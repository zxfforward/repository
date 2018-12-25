package com.pinyougou.cart.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pay.service.WeiXinPayService;

import com.pinyougou.pojo.TbPayLog;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/pay")
public class PayController {
    @Reference
    private WeiXinPayService weiXinPayService;
    @Reference
    private OrderService orderService;
    @RequestMapping("/createNative")
    public Map createNative(){
        //获取当前登录的用户名
        String username= SecurityContextHolder.getContext().getAuthentication().getName();
        TbPayLog tbPayLog = orderService.searchPayLogFromRedis(username);//从缓存中读取支付日志
        if(tbPayLog!= null){
            return weiXinPayService.createNative(tbPayLog.getOutTradeNo(),tbPayLog.getTotalFee()+"");
        }
          return new HashMap();
    }
    @RequestMapping("/queryStatus")
    public Result queryStatus(String out_trade_no){
        Result result=null;
        int time = 0;
        while(true){
            Map map = weiXinPayService.queryStatus(out_trade_no);
            //判断返回结果
            if(map==null){
                //支付失败
                result=new Result(false, "支付出错");
                break;
            }
            if(map.get("trade_state").equals("SUCCESS")){
                //支付成功
                result=new Result(true, "支付成功");
                orderService.updateStatus(out_trade_no, (String) map.get("transaction_id"));
                break;
            }
            try {
                Thread.sleep(3000);//间隔
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            time++;
            if(time>=100){
                result = new Result(false,"二维码请求超时");
                break;
            }
        }
        return  result;
    }
}
