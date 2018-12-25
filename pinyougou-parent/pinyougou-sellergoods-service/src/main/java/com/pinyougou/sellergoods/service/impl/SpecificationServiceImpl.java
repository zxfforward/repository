package com.pinyougou.sellergoods.service.impl;
import java.util.List;
import java.util.Map;

import com.pinyougou.mapper.TbSpecificationOptionMapper;
import com.pinyougou.pojo.TbSpecificationOption;
import com.pinyougou.pojo.TbSpecificationOptionExample;
import entity.Specification;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbSpecificationMapper;
import com.pinyougou.pojo.TbSpecification;
import com.pinyougou.pojo.TbSpecificationExample;
import com.pinyougou.pojo.TbSpecificationExample.Criteria;
import com.pinyougou.sellergoods.service.SpecificationService;

import entity.PageResult;
import org.springframework.transaction.annotation.Transactional;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
@Transactional
public class SpecificationServiceImpl implements SpecificationService {

	@Autowired
	private TbSpecificationMapper specificationMapper;
	@Autowired
	private TbSpecificationOptionMapper specificationOptionMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbSpecification> findAll() {
		return specificationMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbSpecification> page=   (Page<TbSpecification>) specificationMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(Specification specification) {

		TbSpecification spec = specification.getSpec();
		specificationMapper.insert(spec);

		List<TbSpecificationOption> specOptions = specification.getSpecOptions();
		for (TbSpecificationOption specOption : specOptions) {
			specOption.setSpecId(spec.getId());
			specificationOptionMapper.insert(specOption);
		}
	}
	
	/**
	 * 修改
	 */
	@Override
	public void update(Specification specification){

		TbSpecification spec = specification.getSpec();
		specificationMapper.updateByPrimaryKey(spec);

		//删除规格选项在添加
		//删除原来的规格选项
		TbSpecificationOptionExample specificationOptionExample = new TbSpecificationOptionExample();
		specificationOptionExample.createCriteria().andSpecIdEqualTo(spec.getId());
		specificationOptionMapper.deleteByExample(specificationOptionExample);
		//添加新的的规格选项
		List<TbSpecificationOption> specOptions = specification.getSpecOptions();
		for (TbSpecificationOption specOption : specOptions) {
			specOption.setSpecId(spec.getId());
			specificationOptionMapper.insert(specOption);
		}
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public Specification findOne(Long id){

		Specification specification = new Specification();
		TbSpecification spec = specificationMapper.selectByPrimaryKey(id);
		specification.setSpec(spec);

		TbSpecificationOptionExample specificationOptionExample = new TbSpecificationOptionExample();
		TbSpecificationOptionExample.Criteria criteria = specificationOptionExample.createCriteria();
		criteria.andSpecIdEqualTo(id);
		List<TbSpecificationOption> specificationOptions = specificationOptionMapper.selectByExample(specificationOptionExample);

		specification.setSpecOptions(specificationOptions);
		return specification;
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			TbSpecificationOptionExample specificationOptionExample = new TbSpecificationOptionExample();
			specificationOptionExample.createCriteria().andSpecIdEqualTo(id);
			specificationOptionMapper.deleteByExample(specificationOptionExample);
			specificationMapper.deleteByPrimaryKey(id);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbSpecification specification, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbSpecificationExample example=new TbSpecificationExample();
		Criteria criteria = example.createCriteria();
		
		if(specification!=null){			
						if(specification.getSpecName()!=null && specification.getSpecName().length()>0){
				criteria.andSpecNameLike("%"+specification.getSpecName()+"%");
			}
	
		}
		
		Page<TbSpecification> page= (Page<TbSpecification>)specificationMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Override
	public List<Map> selectOptionsList() {
		return specificationMapper.selectOptionsList();
	}

}
