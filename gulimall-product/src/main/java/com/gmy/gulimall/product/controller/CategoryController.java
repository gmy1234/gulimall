package com.gmy.gulimall.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.gmy.gulimall.product.entity.CategoryEntity;
import com.gmy.gulimall.product.service.CategoryService;
import com.gmy.common.utils.PageUtils;
import com.gmy.common.utils.R;



/**
 * 商品三级分类
 *
 * @author gmy
 * @email guanmengyang528@gmail.com
 * @date 2022-03-16 22:46:40
 */
@RestController
@RequestMapping("product/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 查询出所有的商品分类，并且封装成树形结构
     */
    @RequestMapping("/getAllCategory")
    // @RequiresPermissions("product:category:list")
    public R getAllCategoryList( ){
        List<CategoryEntity> categoryList = categoryService.getAllCategoryWithTree();

        return R.ok().put("allCategory", categoryList);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{catId}")
    // @RequiresPermissions("product:category:info")
    public R info(@PathVariable("catId") Long catId){
		CategoryEntity category = categoryService.getById(catId);

        return R.ok().put("category", category);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    // @RequiresPermissions("product:category:save")
    public R save(@RequestBody CategoryEntity category){
		categoryService.save(category);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    // @RequiresPermissions("product:category:update")
    public R update(@RequestBody CategoryEntity category){
		categoryService.updateById(category);

        return R.ok();
    }

    /**
     * 删除
     * @RequestBody 获取请求体：只能使用 POST 请求。
     * 将 springMVC 自动将 请求体的数据：json 转换为 对应的对象。
     */
    @RequestMapping("/delete")
    // @RequiresPermissions("product:category:delete")
    public R delete(@RequestBody Long[] catIds){
        // 1、菜单是否被使用
        categoryService.removeMenuByIds(Arrays.asList(catIds));
		// categoryService.removeByIds(Arrays.asList(catIds));

        return R.ok();
    }

}
