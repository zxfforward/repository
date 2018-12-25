package com.pinyougou.page.service;

public interface ItemPageService {
    /**
     * 是否生成商品详情页
     * @return
     */
    public boolean genItemHtml(Long goodsId);

    /**
     * 是否删除商品详情页
     * @param goodsIds
     * @return
     */
    public boolean delItemHtml(Long[] goodsIds);
}
