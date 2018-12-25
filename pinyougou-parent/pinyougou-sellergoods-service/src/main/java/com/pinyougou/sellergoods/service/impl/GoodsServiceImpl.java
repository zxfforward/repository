package com.pinyougou.sellergoods.service.impl;
import java.util.*;

import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.*;
import com.pinyougou.pojo.*;
import entity.Goods;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.pojo.TbGoodsExample.Criteria;
import com.pinyougou.sellergoods.service.GoodsService;

import entity.PageResult;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.transaction.annotation.Transactional;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
@Transactional
public class GoodsServiceImpl implements GoodsService {

	@Autowired
	private TbGoodsMapper goodsMapper;
	@Autowired
	private TbGoodsDescMapper goodsDescMapper;
	@Autowired
	private TbItemMapper itemMapper;
	@Autowired
	private TbSellerMapper sellerMapper;
	@Autowired
	private TbItemCatMapper itemCatMapper;
	@Autowired
	private TbBrandMapper brandMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbGoods> findAll() {
		return goodsMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbGoods> page=   (Page<TbGoods>) goodsMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(Goods goods) {

		TbGoods tbGoods = goods.getGoods();
		tbGoods.setAuditStatus("0");//为待提交审核状态
		goodsMapper.insert(tbGoods);

		TbGoodsDesc goodsDesc = goods.getGoodsDesc();
		goodsDesc.setGoodsId(tbGoods.getId());
		goodsDescMapper.insert(goodsDesc);

		//保存itemList
		List<TbItem> itemList = goods.getItemList();//没有启用规格$scope.entity.itemList = [{spec:{},num:99999,price:99999,status:1,isDefault:1}]
		saveItems(goods, itemList);
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(Goods goods){

		goods.getGoods().setAuditStatus("0");//更新商品之后设置为为申请状态
		goodsMapper.updateByPrimaryKey(goods.getGoods());
		goodsDescMapper.updateByPrimaryKey(goods.getGoodsDesc());

		//更新item,删除在添加
		TbItemExample itemExample = new TbItemExample();
		itemExample.createCriteria().andGoodsIdEqualTo(goods.getGoods().getId());
		itemMapper.deleteByExample(itemExample);
		TbGoods tbGoods = goods.getGoods();
		List<TbItem> itemList = goods.getItemList();
		//保存sku列表
		saveItems(goods, itemList);
	}

	private void saveItems(Goods goods, List<TbItem> itemList) {
		for (TbItem item : itemList) {

			TbGoods tbGoods = goods.getGoods();
			TbGoodsDesc goodsDesc = goods.getGoodsDesc();
			item.setGoodsId(tbGoods.getId());
			String title = tbGoods.getGoodsName();
			Map spec = JSON.parseObject(item.getSpec(), Map.class);
			if (spec!=null&& !spec.isEmpty()) {
				Set<Map.Entry<String, String>> set = spec.entrySet();
				for (Map.Entry<String, String> entry : set) {
					title+= "   " +entry.getValue();
				}
			}
			//标题: 手机 移动4G 32
			item.setTitle(title);
			//设置商家信息
			String sellerId = tbGoods.getSellerId();
			item.setSellerId(sellerId);
			String nickName = sellerMapper.selectByPrimaryKey(sellerId).getNickName();
			item.setSeller(nickName);

			//设置分类
			item.setCategoryid(tbGoods.getCategory3Id());
			String categoryName = itemCatMapper.selectByPrimaryKey(tbGoods.getCategory3Id()).getName();
			item.setCategory(categoryName);
			//设置品牌
			item.setBrand(brandMapper.selectByPrimaryKey(tbGoods.getBrandId()).getName());
			//获取图片
			List<Map> itemImages = JSON.parseArray(goodsDesc.getItemImages(), Map.class);
			if (itemImages!=null&&itemImages.size()>0) {
				String url = (String) itemImages.get(0).get("url");
				item.setImage(url);
			}
			item.setCreateTime(new Date());
			item.setUpdateTime(new Date());

			itemMapper.insert(item);
		}
	}

	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public Goods findOne(Long id){

		Goods goods = new Goods();
		TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
		TbGoodsDesc tbGoodsDesc = goodsDescMapper.selectByPrimaryKey(tbGoods.getId());
		goods.setGoods(tbGoods);
		goods.setGoodsDesc(tbGoodsDesc);

		//获取sku列表数据
		TbItemExample tbItemExample = new TbItemExample();
		tbItemExample.createCriteria().andGoodsIdEqualTo(id);
		List<TbItem> items = itemMapper.selectByExample(tbItemExample);
		goods.setItemList(items);

		return goods;
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {

		for(Long id:ids){
			TbGoods goods = goodsMapper.selectByPrimaryKey(id);
			goods.setIsDelete("1");
			goodsMapper.updateByPrimaryKey(goods);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbGoods goods, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbGoodsExample example=new TbGoodsExample();
		Criteria criteria = example.createCriteria();
		//查询没有删除的数据
		criteria.andIsDeleteIsNull();

		if(goods!=null){
			if(goods.getSellerId()!=null && goods.getSellerId().length()>0){
				//criteria.andSellerIdLike("%"+goods.getSellerId()+"%");
				//查询自己家的商品
				criteria.andSellerIdEqualTo(goods.getSellerId());
			}
			if(goods.getGoodsName()!=null && goods.getGoodsName().length()>0){
				criteria.andGoodsNameLike("%"+goods.getGoodsName()+"%");
			}
			if(goods.getAuditStatus()!=null && goods.getAuditStatus().length()>0){
				criteria.andAuditStatusLike("%"+goods.getAuditStatus()+"%");
			}
			if(goods.getIsMarketable()!=null && goods.getIsMarketable().length()>0){
				criteria.andIsMarketableLike("%"+goods.getIsMarketable()+"%");
			}
			if(goods.getCaption()!=null && goods.getCaption().length()>0){
				criteria.andCaptionLike("%"+goods.getCaption()+"%");
			}
			if(goods.getSmallPic()!=null && goods.getSmallPic().length()>0){
				criteria.andSmallPicLike("%"+goods.getSmallPic()+"%");
			}
			if(goods.getIsEnableSpec()!=null && goods.getIsEnableSpec().length()>0){
				criteria.andIsEnableSpecLike("%"+goods.getIsEnableSpec()+"%");
			}
			if(goods.getIsDelete()!=null && goods.getIsDelete().length()>0){
				criteria.andIsDeleteLike("%"+goods.getIsDelete()+"%");
			}
	
		}
		
		Page<TbGoods> page= (Page<TbGoods>)goodsMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

	//更新商品审核状态
	@Override
	public void updateStatus(Long[] ids, String status) {

//		TbGoodsExample goodsExample = new TbGoodsExample();
//		goodsExample.createCriteria().andIdIn(Arrays.asList(ids));
//		List<TbGoods> goodsList = goodsMapper.selectByExample(goodsExample);
//		for (TbGoods goods : goodsList) {
//			goods.setAuditStatus(status);
//		}
		for (Long id : ids) {
			TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
			tbGoods.setAuditStatus(status);
			goodsMapper.updateByPrimaryKey(tbGoods);
		}

	}

	@Override
	public List<TbItem> findAuditedStatusItems(Long ids[]) {

		TbItemExample itemExample = new TbItemExample();
		TbItemExample.Criteria criteria = itemExample.createCriteria();
		criteria.andGoodsIdIn(Arrays.asList(ids));
		//criteria.andStatusEqualTo("1");//启动状态的商品
		List<TbItem> itemList = itemMapper.selectByExample(itemExample);
		return itemList;
	}

	/**
	 * 根据SPU的ID集合查询SKU列表
	 * @param goodsIds
	 * @param status
	 * @return
	 */
	public List<TbItem>	findItemListByGoodsIdListAndStatus(Long []goodsIds,String status){

		TbItemExample example=new TbItemExample();
		com.pinyougou.pojo.TbItemExample.Criteria criteria = example.createCriteria();
		criteria.andStatusEqualTo(status);//状态
		criteria.andGoodsIdIn( Arrays.asList(goodsIds));//指定条件：SPUID集合

		return itemMapper.selectByExample(example);
	}
}
