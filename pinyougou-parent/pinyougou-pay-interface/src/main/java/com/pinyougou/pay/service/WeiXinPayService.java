package com.pinyougou.pay.service;



import java.util.Map;

public interface WeiXinPayService {
    /**
     *  生成微信支付二维码
     * @param out_trade_no 商户订单号
     * @param total_fee 金额（单位为分）
     * @return
     */
    public Map createNative(String out_trade_no ,String total_fee);

    /**
     * 根据商家订单号查询商品支付状态
     * @param out_trade_no
     * @return
     */
    public Map queryStatus(String out_trade_no );
    /**
     * 关闭订单
     * @param out_trade_no
     * @return
     */
    public Map closePay(String out_trade_no);
}
