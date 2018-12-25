package com.pinyougou.cart.service;

import entity.Cart;

import java.util.List;

public interface CartService {
    /**
     * 添加商品到购物车
     * @param cartList 购物车列表
     * @param itemId 商品id
     * @param num 商品数量
     * @return
     */
    public List<Cart> addGoodsToCartList(List<Cart> cartList ,Long itemId, Integer num);

    /**
     * 从缓存中提取购物车列表
     * @param username
     * @return
     */
    public List<Cart> findCartListFromRedis(String username);

    /**
     * 保存购物车列表到缓存中
     * @param cartList
     * @param username
     */
    public void saveCartListToRedis(String username,List<Cart> cartList );

    /**
     * 合并购物车
     * @param cartList1
     * @param cartList2
     * @return
     */
    public List<Cart> mergeCartList(List<Cart> cartList1, List<Cart> cartList2);
}
