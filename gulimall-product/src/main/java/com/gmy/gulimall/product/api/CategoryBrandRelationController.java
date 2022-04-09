package com.gmy.gulimall.product.api;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.gmy.gulimall.product.entity.BrandEntity;
import com.gmy.gulimall.product.vo.BrandVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.gmy.gulimall.product.entity.CategoryBrandRelationEntity;
import com.gmy.gulimall.product.service.CategoryBrandRelationService;
import com.gmy.common.utils.PageUtils;
import com.gmy.common.utils.R;



/**
 * 品牌与分类关联
 *
 * @author gmy
 * @email guanmengyang528@gmail.com
 * @date 2022-03-16 22:46:40
 */
@RestController
@RequestMapping("product/categorybrandrelation")
public class CategoryBrandRelationController {
    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;

    /* 获取当前品牌关联的所有分类 */
    @RequestMapping(value = "catelog/list", method = RequestMethod.GET)
    // @RequiresPermissions("product:categorybrandrelation:list")
    public R catelogList(@RequestParam("brandId") Long brandId){

        List<CategoryBrandRelationEntity> data = categoryBrandRelationService.getRelationCategory(brandId);

        return R.ok().put("data", data);
    }

    /* 获取当前分类关联的的品牌
    * 分类Id；catId
    * */
    @RequestMapping(value = "/brands/list", method = RequestMethod.GET)
    // @RequiresPermissions("product:categorybrandrelation:list")
    public R relationBrands(@RequestParam(value = "catId", required = true) Long catId){

        // 业务处理
        List<BrandEntity> data = categoryBrandRelationService.getBrandsByCatId(catId);

        // 处理数据, 返回页面指定的 vo
        final List<BrandVo> collect = data.stream().map(item -> {
            final BrandVo brandVo = new BrandVo();
            brandVo.setBrandId(item.getBrandId());
            brandVo.setBrandName(item.getName());
            return brandVo;
        }).collect(Collectors.toList());


        return R.ok().put("data", collect);
    }


    /**
     * 列表
     */
    @RequestMapping("/list")
    // @RequiresPermissions("product:categorybrandrelation:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = categoryBrandRelationService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    // @RequiresPermissions("product:categorybrandrelation:info")
    public R info(@PathVariable("id") Long id){
		CategoryBrandRelationEntity categoryBrandRelation = categoryBrandRelationService.getById(id);

        return R.ok().put("categoryBrandRelation", categoryBrandRelation);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    // @RequiresPermissions("product:categorybrandrelation:save")
    public R save(@RequestBody CategoryBrandRelationEntity categoryBrandRelation){

        categoryBrandRelationService.saveDetail(categoryBrandRelation);

		categoryBrandRelationService.save(categoryBrandRelation);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    // @RequiresPermissions("product:categorybrandrelation:update")
    public R update(@RequestBody CategoryBrandRelationEntity categoryBrandRelation){
		categoryBrandRelationService.updateById(categoryBrandRelation);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    // @RequiresPermissions("product:categorybrandrelation:delete")
    public R delete(@RequestBody Long[] ids){
		categoryBrandRelationService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
