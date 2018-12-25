package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.pojo.TbBrandExample;
import com.pinyougou.sellergoods.service.BrandService;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by crowndint on 2018/10/13.
 */
@Service
public class BrandServiceImpl implements BrandService {

    @Autowired
    private TbBrandMapper brandMapper;

    @Override
    public List<TbBrand> findAll() {
        return brandMapper.selectByExample(null);
    }

    @Override
    public PageResult findByPage(int pageNum, int pageSize) {

        PageHelper.startPage(pageNum, pageSize);
        Page<TbBrand> page = (Page<TbBrand>) brandMapper.selectByExample(null);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public PageResult findByPage(TbBrand brand, int pageNum, int pageSize) {

        PageHelper.startPage(pageNum, pageSize);
        TbBrandExample brandExample = new TbBrandExample();
        TbBrandExample.Criteria criteria = brandExample.createCriteria();
        if (StringUtils.isNotEmpty(brand.getName())) {
            criteria.andNameLike("%"+brand.getName()+"%");
        }

        if (StringUtils.isNotEmpty(brand.getFirstChar())) {
            criteria.andFirstCharEqualTo(brand.getFirstChar());
        }

        Page<TbBrand> page = (Page<TbBrand>) brandMapper.selectByExample(brandExample);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public void update(TbBrand brand) {

        Long id = brand.getId();
        if (id!=null && id>0) {
            brandMapper.updateByPrimaryKey(brand);
        } else {
            brandMapper.insert(brand);
        }
    }

    @Override
    public TbBrand findOne(Long id) {

        return brandMapper.selectByPrimaryKey(id);
    }

    @Override
    public void delete(Long[] ids) {

        TbBrandExample brandExample = new TbBrandExample();
        TbBrandExample.Criteria criteria = brandExample.createCriteria();
        criteria.andIdIn(Arrays.asList(ids));
        brandMapper.deleteByExample(brandExample);
    }

    @Override
    public List<Map> selectOptionsList() {
        return brandMapper.selectOptionsList();
    }


}
