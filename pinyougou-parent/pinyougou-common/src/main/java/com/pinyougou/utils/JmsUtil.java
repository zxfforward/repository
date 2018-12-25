package com.pinyougou.utils;
import java.util.Map;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.Session;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

/**
 * JMS工具类
 * @author Administrator
 *
 */
@Component
public class JmsUtil {

	@Autowired
	private JmsTemplate jmsTemplate;
	
	/**
	 * 发送消息
	 * @param destination
	 * @param map
	 */
	public void send(Destination destination, final Map<String ,String> map){
		System.out.println("---------------生产者发送消息-----------------");   
		jmsTemplate.send(destination, new MessageCreator() {			
			public Message createMessage(Session session) throws JMSException {
				
				MapMessage mapMessage = session.createMapMessage();
				
				for(String key:map.keySet()){
					mapMessage.setString(key, map.get(key));			
				}				
				
				return mapMessage;
			}			
		} );			
	}
	
	
	/**
	 * 发送消息（文本）
	 * @param destination
	 * @param text
	 */
	public void send(Destination destination, final String text){
		System.out.println("---------------生产者发送消息(Text)-----------------");   
		jmsTemplate.send(destination, new MessageCreator() {			
			public Message createMessage(Session session) throws JMSException {
				return session.createTextMessage(text);
			}			
		} );			
	}
	
}
