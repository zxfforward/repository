package com.pinyougou.sellergoods.service;
import java.util.List;
import java.util.Map;

import com.pinyougou.pojo.TbBrand;
import entity.PageResult;

/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface BrandService {

	/**
	 * 返回全部列表
	 * @return
	 */
	public List<TbBrand> findAll();

	public PageResult findByPage(int pageNum, int pageSize);

	/*
		按照条件查询并分页
	 */
	public PageResult findByPage(TbBrand brand, int pageNum, int pageSize);

	public void update(TbBrand brand);

	public TbBrand findOne(Long id);

	public void delete(Long ids[]);

	List<Map> selectOptionsList();
	
}
