package com.pinyougou.seckill.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pay.service.WeiXinPayService;
import com.pinyougou.pojo.TbSeckillOrder;
import com.pinyougou.seckill.service.SeckillOrderService;
import entity.Result;
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
    private SeckillOrderService seckillOrderService;

    /**
     * 生成二维码
     *
     * @return
     */
    @RequestMapping("/createNative")
    public Map createNative() {
        //获取当前用户
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        //到 redis 查询秒杀订单
        TbSeckillOrder tbSeckillOrder = seckillOrderService.searchOrderFromRedisByUserId(userId);
        //判断秒杀订单是否存
        if (tbSeckillOrder != null) {
            long fen = (long) (tbSeckillOrder.getMoney().doubleValue() * 100);//金额（分）
            return weiXinPayService.createNative(tbSeckillOrder.getId() + "", +fen + "");
        } else {
            return new HashMap();
        }

    }

    /**
     * 查询支付状态
     *
     * @param out_trade_no
     * @return
     */
    @RequestMapping("/queryPayStatus")
    public Result queryPayStatus(String out_trade_no) {
        //获取当前登录人
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        Result result = null;
        int time = 0;
        while (true) {
            Map map = weiXinPayService.queryStatus(out_trade_no);
            if (map == null) {//出错
                result = new Result(false, "支付出错");
                break;
            }
            if (map.get("trade_state").equals("SUCCESS")) {//如果成功
                result = new Result(true, "支付成功");
                //将交易写入seckillOrder表中
                seckillOrderService.saveOrderFromRedisToDb(userId, Long.valueOf(out_trade_no), (String) map.get("transaction_id"));
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
                // 关闭支付
                Map<String,String> payResult = weiXinPayService.closePay(out_trade_no);
                if(payResult!=null &&  "FAIL".equals( payResult.get("return_code"))){
                    if("ORDERPAID".equals(payResult.get("err_code"))){
                        result=new Result(true, "支付成功");
                        //保存订单
                        seckillOrderService.saveOrderFromRedisToDb(userId, Long.valueOf(out_trade_no) , (String) map.get("transaction_id"));
                    }
                }

                //删除订单
                if(result.getSuccess()==false){
                    seckillOrderService.deleteOrderFromRedis(userId, Long.valueOf(out_trade_no));
                }
            }
        }
        return  result;
        }
    }