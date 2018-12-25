package com.pinyougou.search.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.search.service.SearchService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Created by crowndint on 2018/10/22.
 */

@RestController
@RequestMapping("itemSearch")
public class SearchController {

    @Reference
    private SearchService searchService;

    @RequestMapping("search")
    public Map search(@RequestBody Map<String, Object> searchMap) {

        return searchService.search(searchMap);
    }



}
