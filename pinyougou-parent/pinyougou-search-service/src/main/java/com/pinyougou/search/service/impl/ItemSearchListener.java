package com.pinyougou.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.pinyougou.pojo.TbItem;

import com.pinyougou.search.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.List;

@Component
public class ItemSearchListener implements MessageListener {
    @Autowired
    private SearchService itemSearchService;
    //建立监听类
    @Override
    public void onMessage(Message message) {

        TextMessage textMessage = (TextMessage)message;
        try {
            String text = textMessage.getText();//提取消息文本，即json字符串
            System.out.println("监听到消息"+text);
            List<TbItem> itemList = JSON.parseArray(text,TbItem.class);//获取List集合
            itemSearchService.importItemList(itemList);//导入到solr索引库
            System.out.println("导入到solr索引库");
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
