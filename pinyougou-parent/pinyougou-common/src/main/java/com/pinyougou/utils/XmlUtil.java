package com.pinyougou.utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class XmlUtil {
	
	 /**
	 * @description 将xml字符串转换成map
	 * @param xml
	 * @return Map
	 */
	 public static Map convertXmlToMap(String xml) {
	         Map map = new HashMap();
	         Document doc = null;
	          
             try {
				// 将字符串转为XML
				 doc = DocumentHelper.parseText(xml); 
				 // 获取根节点
				 Element rootElt = doc.getRootElement(); 				 
				 // 获取根节点下的子节点head
				 List<Element> elements = rootElt.elements();
				 for(Element element:elements){
					 map.put(element.getName(), element.getStringValue());	            	 
				 }
			} catch (DocumentException e) {
				
				e.printStackTrace();
			}	            
	        return map;
	 }
	 
	 /**
	  * 将Map集合转换为xml字符串
	  * @param map
	  * @return
	  */
	 public static String convertMapToXml(Map<String,String> map){
		 StringBuilder xml=new StringBuilder("<xml>");
		 for(String key:map.keySet()){
			 xml.append("<"+key + ">"+map.get(key)+"</"+ key + ">"   );
		 }
		 xml.append("</xml>");
		 return xml.toString();
	 }
	 

}
