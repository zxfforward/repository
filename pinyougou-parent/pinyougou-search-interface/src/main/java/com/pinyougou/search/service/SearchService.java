package com.pinyougou.search.service;

import com.pinyougou.pojo.TbItem;

import java.util.List;
import java.util.Map;

/**
 * Created by crowndint on 2018/10/22.
 */
public interface SearchService {

    public Map<String, Object> search(Map<String, Object> searchMap);

    public void deleteByGoodsIds(List<Long> goodsIds);

    public void importItemList(List<TbItem> itemList);
}
