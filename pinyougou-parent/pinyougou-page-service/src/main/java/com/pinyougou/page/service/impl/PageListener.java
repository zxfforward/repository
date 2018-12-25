package com.pinyougou.page.service.impl;

import com.pinyougou.page.service.ItemPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

@Component
public class PageListener implements MessageListener {
    //监听类（用于生成网页）
    @Autowired
    private ItemPageService itemPageService;
    @Override
    public void onMessage(Message message) {
        TextMessage textMessage = (TextMessage)message;
        try {
            String text = textMessage.getText();//text接收的消息是商品id
            System.out.println("接收消息"+text);
            boolean b = itemPageService.genItemHtml(Long.parseLong(text));
            System.out.println("网页生成结果"+b);
        } catch (JMSException e) {
            e.printStackTrace();
        }

    }
}
