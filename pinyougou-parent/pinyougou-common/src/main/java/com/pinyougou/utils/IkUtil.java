package com.pinyougou.utils;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.wltea.analyzer.lucene.IKAnalyzer;

public class IkUtil {
	public static Set<String> analyzer(String analyzerString) {
		Set<String> set = new HashSet<String>();
		TokenStream stream;
		try {
			// 创建分词的io流,将title进行分词
			stream = new IKAnalyzer(true).tokenStream("myfield", analyzerString);
			stream.reset();
			// 创建结果集封装类
			CharTermAttribute offsetAtt = stream.getAttribute(CharTermAttribute.class);
			// 判断是否有下一个分词

			while (stream.incrementToken()) {
				set.add(offsetAtt.toString());
			}
			// 关闭流
			stream.end();
			stream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return set;
	}
}
