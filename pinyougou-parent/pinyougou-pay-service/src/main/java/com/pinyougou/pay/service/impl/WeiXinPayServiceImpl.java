package com.pinyougou.pay.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.wxpay.sdk.WXPayUtil;
import com.pinyougou.pay.service.WeiXinPayService;
import com.pinyougou.utils.HttpClient;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;

@Service(timeout = 10000)
public class WeiXinPayServiceImpl implements WeiXinPayService {
    @Value("${appid}")
    private String appid;
    @Value("${partner}")
    private String partner;
    @Value("${partnerkey}")
    private String partnerkey;//密钥

    @Override
    public Map createNative(String out_trade_no, String total_fee) {
        //1.参数封装
        Map paramMap = new HashMap();//构建参数Map
        paramMap.put("appid", appid);//公众账号id
        paramMap.put("mch_id", partner);//商户号
        paramMap.put("nonce_str", WXPayUtil.generateNonceStr());//随机字符串
        paramMap.put("body", "品优购");//商品描述
        paramMap.put("out_trade_no", out_trade_no);//商户订单号
        paramMap.put("total_fee", total_fee);//标价金额
        paramMap.put("spbill_create_ip", "127.0.0.1");//终端ip
        paramMap.put("notify_url", "http://test.itcast.cn");//回调地址(随便写)
        paramMap.put("trade_type", "NATIVE");//交易类型
        //2.发送请求
        //生成带签名的要发送的xml
        try {
            String xmlParam = WXPayUtil.generateSignedXml(paramMap, partnerkey);
            HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
            httpClient.setHttps(true);
            httpClient.setXmlParam(xmlParam);
            httpClient.post();
            //3.获取结果
            String result = httpClient.getContent();
            Map<String, String> resultMap = WXPayUtil.xmlToMap(result);
            Map<String, String> map = new HashMap<>();
            map.put("code_url", resultMap.get("code_url"));//支付地址
            map.put("total_fee", total_fee);//总金额
            map.put("out_trade_no", out_trade_no);//订单号
            return map;

        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap<>();
        }

    }

    /**
     * 查询商品支付状态
     * @param out_trade_no
     * @return
     */
    @Override
    public Map queryStatus(String out_trade_no) {
        //1.封装参数
        //构建参数map
        Map paramMap = new HashMap();
        paramMap.put("appid", appid);//公众账号 ID
        paramMap.put("mch_id", partner);//商户号
        paramMap.put("out_trade_no", out_trade_no);//订单号
        paramMap.put("nonce_str", WXPayUtil.generateNonceStr());//随机字符串
        try {
            //2.发送xml请求
            String signedXml = WXPayUtil.generateSignedXml(paramMap, partnerkey);
            HttpClient client = new HttpClient("https://api.mch.weixin.qq.com/pay/orderquery");
            client.setHttps(true);
            client.setXmlParam(signedXml);
            client.post();
            //3.返回结果
            String xmlResult = client.getContent();//xml形式的结果
            Map<String, String> resultMap = WXPayUtil.xmlToMap(xmlResult);//将xml形式的结果转化成map形式
            return resultMap;
        } catch (Exception e) {
            e.printStackTrace();
        return null;
        }
    }

    @Override
    public Map closePay(String out_trade_no) {
        //1.封装参数
        //构建参数map
        Map param=new HashMap();
        param.put("appid", appid);//公众账号 ID
        param.put("mch_id", partner);//商户号
        param.put("out_trade_no", out_trade_no);//订单号
        param.put("nonce_str", WXPayUtil.generateNonceStr());//随机字符串
          try {
            //2.发送xml请求
            String signedXml = WXPayUtil.generateSignedXml(param, partnerkey);
            HttpClient client = new HttpClient("https://api.mch.weixin.qq.com/pay/closeorder");
            client.setHttps(true);
            client.setXmlParam(signedXml);
            client.post();
            //3.返回结果
            String xmlResult = client.getContent();//xml形式的结果
            Map<String, String> resultMap = WXPayUtil.xmlToMap(xmlResult);//将xml形式的结果转化成map形式
            return resultMap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
