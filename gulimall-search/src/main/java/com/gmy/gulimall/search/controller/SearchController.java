package com.gmy.gulimall.search.controller;

import com.gmy.gulimall.search.service.MallSearchService;
import com.gmy.gulimall.search.vo.SearchParam;
import com.gmy.gulimall.search.vo.SearchRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;


@Controller
public class SearchController {

    @Autowired
    MallSearchService mallSearchService;

    @GetMapping("/list.html")
    public String listPage(SearchParam searchKey, Model model, HttpServletRequest request){

//        SearchRes result = mallSearchService.search(searchKey);
//        String queryString = request.getQueryString();
//        searchKey.setQueryString(queryString);

        //model.addAttribute("result", result);
        return "list";
    }
}
