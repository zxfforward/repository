import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.TbGoodsMapper;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbGoodsExample;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemExample;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.result.ScoredPage;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created by crowndint on 2018/10/22.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
        locations = {"classpath*:spring/applicationContext-solr.xml","classpath:spring/applicationContext-dao.xml"})
public class TestSearch {

    @Autowired
    private SolrTemplate sorlTemplate;


    @Autowired
    private TbGoodsMapper goodsMapper;
    @Autowired
    private TbItemMapper itemMapper;

    //删除索引库
    @Test
    public void testDelete() {

        sorlTemplate.delete(new SimpleQuery("*:*"));
        sorlTemplate.commit();
    }

    @Test
    public void testDelteByCri() {
        Long ids[] = {149187842867976L};
        List<Long> goodsId = Arrays.asList();
        SimpleQuery query = new SimpleQuery();
        Criteria criteria = new Criteria("item_goodsid").in(goodsId);
        query.addCriteria(criteria);
        sorlTemplate.delete(query);
        sorlTemplate.commit();
    }

    //手动的设置商品的审核状态为审核通过
    @Test
    public void testSetGoodsAudioStatus() {
        List<TbGoods> goodsList = goodsMapper.selectByExample(null);
        for (TbGoods goods : goodsList) {
            goods.setAuditStatus("2");
            goodsMapper.updateByPrimaryKey(goods);
        }
    }

    //设置商品项目的状态为启用状态，启用状态：1，非启用状态：0
    @Test
    public void testSetGoodsItemStatus() {

        //TbItemExample itemExample = new TbItemExample();
        //itemExample.createCriteria().andStatusEqualTo("0");
        List<TbItem> items = itemMapper.selectByExample(null);
        for (TbItem item : items) {
            item.setStatus("1");
            itemMapper.updateByPrimaryKey(item);
        }
    }

    //查询已经审核通过的商品并且商品项为启用状态项批量导入到索引库

    @Test
    public void testImportBeans() {
        //定义一个集合存放审核通过的商品id
       /* List<Long> goodsId = new ArrayList<>();
        TbGoodsExample goodsExample = new TbGoodsExample();
        goodsExample.createCriteria().andAuditStatusEqualTo("2");//审核通过的商品
        List<TbGoods> goodsList = goodsMapper.selectByExample(goodsExample);
        //SELECT COUNT(*) FROM tb_goods;
        System.out.println("goodsList--->"+goodsList.size());
        for (TbGoods goods : goodsList) {
            goodsId.add(goods.getId());
        }*/

        //获取审核通过的商品对应的商品项并且商品项的状态为启用状态
        TbItemExample itemExample = new TbItemExample();
        //itemExample.createCriteria().andGoodsIdIn(goodsId);//获取审核通过的商品对应的商品项
        itemExample.createCriteria().andStatusEqualTo("1");//商品项的状态为启用状态
        List<TbItem> items = itemMapper.selectByExample(itemExample);
        //SELECT COUNT(*) FROM tb_item;
        System.out.println("items--->"+items.size()+" ");

        for (TbItem item : items) {
            String spec = item.getSpec();
            Map map = JSON.parseObject(spec, Map.class);
            item.setSpecMap(map);
        }

        sorlTemplate.saveBeans(items);
        sorlTemplate.commit();
    }

    /*
        分页查询
     */
    @Test
    public void testPageQuery(){
        Query query=new SimpleQuery("*:*");
        query.setOffset(0);//开始索引（默认0）
        query.setRows(20);//每页记录数(默认10)
        ScoredPage<TbItem> page = sorlTemplate.queryForPage(query, TbItem.class);
        System.out.println("总记录数："+page.getTotalElements());
        List<TbItem> itemList = page.getContent();
        for (TbItem item : itemList) {
            System.out.println(item);
        }
    }
    /*
        条件查询
     */
    @Test
    public void testPageCriteriaQuery() {
        Query query = new SimpleQuery("*:*");
        Criteria criteria = new Criteria("item_category").contains("手机");
        criteria = criteria.and("item_title").contains("5");
        query.addCriteria(criteria);
        query.setOffset(0);// 开始索引（默认0）
        query.setRows(20);// 每页记录数(默认10)
        ScoredPage<TbItem> page = sorlTemplate.queryForPage(query, TbItem.class);
        System.out.println("总记录数：" + page.getTotalElements());
        List<TbItem> itemList = page.getContent();
        for (TbItem item : itemList) {
            System.out.println(item);
        }
    }


    @Test
    public void testDeleteById() {
        sorlTemplate.deleteById("1369295");
        //sorlTemplate.commit();
    }

    @Test
    public void testFindById() {
        TbItem item = sorlTemplate.getById("1369295", TbItem.class);
        System.out.println(item);
    }


























    @Autowired
    private HttpSolrServer httpSolrServer;

    @Test
    public void testSearch() {

        Map<String, Object> searchMap = new HashMap<>();
        searchMap.put("keywords", "手机");
        Map<String, Object> resultMap = search(searchMap);
        List<Object> rows = (List<Object>) resultMap.get("rows");
        for (Object row : rows) {
            System.out.println(row);
        }

    }


    public Map<String, Object> search(Map<String, Object> searchMap)  {
        HashMap<String, Object> searchResult = new HashMap<>();
        try {
            SolrQuery query = new SolrQuery("item_keywords:"+searchMap.get("keywords"));

            //高亮显示
            query.setHighlight(true);
            //高亮显示的域
            query.addHighlightField("product_name");
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

        } catch (SolrServerException e) {
            e.printStackTrace();
        }
        return searchResult;
    }


}
