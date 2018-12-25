package com.pinyougou.page.service.impl;


import com.pinyougou.mapper.TbGoodsDescMapper;
import com.pinyougou.mapper.TbGoodsMapper;
import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.page.service.ItemPageService;
import com.pinyougou.pojo.*;
import freemarker.template.Configuration;
import freemarker.template.Template;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;


import java.io.File;
import java.io.PrintWriter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ItemPageServiceImpl implements ItemPageService {
    @Value("${pageDir}")
    private String pageDir;
    @Autowired
    private FreeMarkerConfig freeMarkerConfig;

    @Autowired
    private TbGoodsMapper goodsMapper;
    @Autowired
    private TbGoodsDescMapper goodsDescMapper;
    @Autowired
    private TbItemCatMapper catMapper;
    @Autowired
    private TbItemMapper itemMapper;

    @Override
    public boolean genItemHtml(Long goodsId) {
        try {
            //构建freeMaker配置
            Configuration configuration = freeMarkerConfig.getConfiguration();
            //获取模板
            Template template = configuration.getTemplate("item.ftl");
            //创建数据模型
            Map dataModel = new HashMap<>();
            //1.加载商品表数据
            TbGoods goods = goodsMapper.selectByPrimaryKey(goodsId);
            dataModel.put("goods", goods);
            //2.加载商品扩展表数据
            TbGoodsDesc goodsDesc = goodsDescMapper.selectByPrimaryKey(goodsId);
            dataModel.put("goodsDesc", goodsDesc);
            //3.读取商品分类,获取分类名称
            String itemCat1 = catMapper.selectByPrimaryKey(goods.getCategory1Id()).getName();
            String itemCat2 = catMapper.selectByPrimaryKey(goods.getCategory2Id()).getName();
            String itemCat3 = catMapper.selectByPrimaryKey(goods.getCategory3Id()).getName();

            dataModel.put("itemCat1", itemCat1);
            dataModel.put("itemCat2", itemCat2);
            dataModel.put("itemCat3", itemCat3);
            //4.读取sku列表数据
            TbItemExample example = new TbItemExample();
            TbItemExample.Criteria criteria = example.createCriteria();
            //添加条件
            criteria.andGoodsIdEqualTo(goodsId);//SKU id
            criteria.andStatusEqualTo("1");//商品状态要有效
            example.setOrderByClause("is_default desc");//按照是否默认进行降序，保证第一个为默认SKU
            List<TbItem> tbItems = itemMapper.selectByExample(example);
            dataModel.put("itemList", tbItems);
            //输出流
            //Writer out=new FileWriter(pageDir+goodsId+".html");
            //可能会遇到乱码问题
            PrintWriter out = new PrintWriter(pageDir + goodsId + ".html", "utf-8");
            //模板输出
            template.process(dataModel, out);
            out.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean delItemHtml(Long[] goodsIds) {
        try {
            for (Long goodsId : goodsIds) {
                new File(pageDir + goodsId + ".html").delete();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}


