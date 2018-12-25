package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.sellergoods.service.BrandService;
import entity.PageResult;
import entity.Result;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by crowndint on 2018/10/13.
 */
@RestController
@RequestMapping("brand")
public class BrandController {

    @Reference
    private BrandService brandService;

    @RequestMapping("findAll")
    public List<TbBrand> finDAll() {

        return brandService.findAll();
    }

    @RequestMapping("findByPage")
    public PageResult findByPage(int pageNum, int pageSize) {

        return brandService.findByPage(pageNum, pageSize);
    }

    @RequestMapping("searchByPage")
    public PageResult searchByPage(@RequestBody TbBrand brand, int pageNum, int pageSize) {

        return brandService.findByPage(brand, pageNum, pageSize);
    }

    @RequestMapping("findOne")
    public TbBrand findOne(Long id) {

        return brandService.findOne(id);
    }

    @RequestMapping("update")
    public Result update(@RequestBody TbBrand brand) {
        Result result = new Result();
        try {
            brandService.update(brand);
            result.setSuccess(true);
            result.setMessage("添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            result.setSuccess(false);
            result.setMessage("添加失败");
        }
        return result;
    }

    @RequestMapping("delete")
    public Result delete(Long ids[]) {
        Result result = new Result();
        try {
            brandService.delete(ids);
            result.setSuccess(true);
            result.setMessage("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            result.setSuccess(false);
            result.setMessage("删除失败");
        }
        return result;
    }

    @RequestMapping("selectOptionsList")
    public List<Map> selectOptionsList() {

        return brandService.selectOptionsList();
    }


}
