package com.gmy.guliorder.order.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class Hello {

    @GetMapping("/{page}.html")
    public String list(@PathVariable("page") String page){

        return page;
    }
}
