package com.pinyougou.utils;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
/**
 * JSON工具类
 * @author Administrator
 *
 */
public class JsonUtil {

	/**
	 * 将json中的某个key提出来，
	 * @param jsonString
	 * @param key
	 * @return
	 */
	public static String convertJsonToArray(String jsonString,String key){
		String str="";
		List<Map> list = JSON.parseArray(jsonString,Map.class);
		for(int i=0;i<list.size();i++){
			if(i>0){
				str+=",";
			}			
			str+=list.get(i).get(key);
		}
		return str;
	}
	
	/*
	public static void main(String[] args) {
		
		String str="[{id:1,name:'aaa',point:{'e':100,'y':90}},{id:2,name:'bbb',point:{'e':100,'y':96}}]";
		List list = JSON.parseArray(str);
		
		Map map=(Map) list.get(0);
		
		Map map2 = (Map) map.get("point");
		
		System.out.println(map2.get("e"));
		
	}*/
	
}
