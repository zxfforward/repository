import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.TbGoodsMapper;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemExample;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.Group;
import org.apache.solr.client.solrj.response.GroupCommand;
import org.apache.solr.client.solrj.response.GroupResponse;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.QueryParser;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.TermsQueryParser;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.result.ScoredPage;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by crowndint on 2018/10/22.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
        locations = {"classpath*:spring/applicationContext-solr.xml","classpath:spring/applicationContext-dao.xml"})
public class TestSearchByFilter {

    @Autowired
    private SolrTemplate sorlTemplate;
    @Autowired
    private HttpSolrServer httpSolrServer;
    @Autowired
    private TbGoodsMapper goodsMapper;
    @Autowired
    private TbItemMapper itemMapper;


    @Test
    public void testSearch() {

        Map<String, Object> searchMap = new HashMap<>();
        searchMap.put("keywords", "皮鞭");
        Map<String, Object> resultMap = search(searchMap);
        List<Object> rows = (List<Object>) resultMap.get("rows");
        for (Object row : rows) {
            System.out.println(row);
        }

        System.out.println("---------------------");
        List<String> categoryList = (List<String>) resultMap.get("categoryList");
        for (String category : categoryList) {
            System.out.println(category);
        }
    }


    public Map<String, Object> search(Map<String, Object> searchMap)  {
        HashMap<String, Object> searchResult = new HashMap<>();
        try {

            String keywords = (String) searchMap.get("keywords");
            if (StringUtils.isEmpty(keywords)) {
                keywords = "*";
            }

            SolrQuery query = new SolrQuery("item_keywords:"+keywords);

            //关键字索索高亮显示及分页
            searchByHighlightAndPage(searchResult, query);

            //查询按照关键字进行分组,把分组结果存放到searchResult ，searchResult.put("categoryList", categoryList);
            groupByKeywords(searchResult, query);


        } catch (SolrServerException e) {
            e.printStackTrace();
        }
        return searchResult;
    }

    /*
        根据关键词的搜索结果，按照category域进行数组

        1.根据关键字进行查询
        SELECT * FROM tb_item WHERE title LIKE '%皮鞭%';
        2.然后对查询的结果集按照category进行分类
		SELECT COUNT(1),category FROM (SELECT * FROM tb_item WHERE title LIKE '%皮鞭%') ret GROUP BY category;
     */
    private void groupByKeywords(HashMap<String, Object> searchResult, SolrQuery query) throws SolrServerException {
        //执行分组查询
        query.setParam("group", true);//是否分组
        query.setParam("group.field", "item_category");//分组的域
        List<String> categoryList = new ArrayList<>();
        //这里的结果就是按照关键字搜索的结果集,相当于SELECT * FROM tb_item WHERE title LIKE '%皮鞭%';
        QueryResponse response = httpSolrServer.query(query);
        //下面就是对搜索到的结果集按照item_category域进行分组，
        // 相当于SELECT COUNT(1),category FROM (SELECT * FROM tb_item WHERE title LIKE '%皮鞭%') ret GROUP BY category;
        GroupResponse groupResponse = response.getGroupResponse();
        List<GroupCommand> values = groupResponse.getValues();
        if (values != null) {
            for (GroupCommand groupCommand : values) {
                for (Group group : groupCommand.getValues()) {
                    SolrDocumentList hits = group.getResult();
                    for (SolrDocument document : hits) {
                        String category = (String) document.getFieldValue("item_category");
                        categoryList.add(category);
                    }
                }
            }
        }
        //把分组结果存放到categoryList集合，然后在把这个集合存放到searchResult这个Map
        searchResult.put("categoryList", categoryList);
    }

    private void searchByHighlightAndPage(HashMap<String, Object> searchResult, SolrQuery query) throws SolrServerException {
        //高亮显示
        query.setHighlight(true);
        //高亮显示的域
        query.addHighlightField("item_title");
        //高亮显示的前缀
        query.setHighlightSimplePre("<em style='color:red'>");
        //高亮显示的后缀
        query.setHighlightSimplePost("</em>");


        QueryResponse queryResponse = httpSolrServer.query(query);
        SolrDocumentList solrDocumentList = queryResponse.getResults();
        long numFound = solrDocumentList.getNumFound();

        ArrayList<TbItem> itemList = new ArrayList<>();
        for (SolrDocument solrDocument : solrDocumentList) {
            TbItem item = new TbItem();
            String id = (String) solrDocument.get("id");
            String title = (String) solrDocument.get("item_title");
            Double price = (Double) solrDocument.get("item_price");
            String image = (String) solrDocument.get("item_image");
            String category = (String) solrDocument.get("item_category");
            String brand = (String) solrDocument.get("item_brand");

            //取高亮显示
            Map<String, Map<String, List<String>>> highlighting = queryResponse.getHighlighting();
            List<String> list = highlighting.get(solrDocument.get("id")).get("item_title");
            //判断是否有高亮内容
            if (null != list) {
                title = list.get(0);
            }


            item.setId(Long.parseLong(id));
            item.setTitle(title);
            item.setPrice(new BigDecimal(price));
            item.setImage(image);
            item.setCategory(category);
            item.setBrand(brand);

            itemList.add(item);
        }

        searchResult.put("total", numFound);
        searchResult.put("rows", itemList);
    }


}
