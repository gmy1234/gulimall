package com.gmy.gulimall.product.api;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.gmy.gulimall.product.entity.AttrEntity;
import com.gmy.gulimall.product.service.AttrAttrgroupRelationService;
import com.gmy.gulimall.product.service.AttrService;
import com.gmy.gulimall.product.service.CategoryService;
import com.gmy.gulimall.product.vo.AttrGroupRelationVo;
import com.gmy.gulimall.product.vo.AttrGroupWithAttrsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gmy.gulimall.product.entity.AttrGroupEntity;
import com.gmy.gulimall.product.service.AttrGroupService;
import com.gmy.common.utils.PageUtils;
import com.gmy.common.utils.R;



/**
 * 属性分组
 *
 * @author gmy
 * @email guanmengyang528@gmail.com
 * @date 2022-03-16 22:46:40
 */
@RestController
@RequestMapping("product/attrgroup")
public class AttrGroupController {
    @Autowired
    private AttrGroupService attrGroupService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private AttrService attrService;

    @Autowired
    private AttrAttrgroupRelationService relationService;


     /* 获取分类下所分组和属性 */
    @RequestMapping("/{catelogId}/withattr")
    // @RequiresPermissions("product:attrgroup:list")
    public R getAttrGroupWithAttrs(@PathVariable("catelogId") Long catelogId){

        List<AttrGroupWithAttrsVo> res = attrGroupService.getAttrGroupWithAttrsByCatelogId(catelogId);
        return R.ok().put("data", res);
    }



    // 查询分组关联属性
    @RequestMapping("/{attrgroupId}/attr/relation")
    // @RequiresPermissions("product:attrgroup:list")
    public R attrRelation(@PathVariable("attrgroupId") Long attrgroupId){

        List<AttrEntity> entities = attrService.getRelationAttr(attrgroupId);
        return R.ok().put("data", entities);
    }

    // 查询分组没有关联的属性
    @RequestMapping("/{attrgroupId}/noattr/relation")
    // @RequiresPermissions("product:attrgroup:list")
    public R attrNoRelation(@RequestParam Map<String, Object> params,
                            @PathVariable("attrgroupId") Long attrgroupId){

        PageUtils page = attrService.getNoRelationAttr(params, attrgroupId);
        return R.ok().put("data", page);
    }


    @RequestMapping("/attr/relation")
    // @RequiresPermissions("product:attrgroup:list")
    public R addRelation(@RequestBody() AttrGroupRelationVo[] vos){

        relationService.addRelations(vos);
        return R.ok();
    }

    @RequestMapping("/attr/relation/delete")
    // @RequiresPermissions("product:attrgroup:list")
    public R deleteRelation(@RequestBody() AttrGroupRelationVo[] vos){

        attrService.deleteRelationAttr(vos);
        return R.ok();
    }


    /**
     * 列表
     */
    @RequestMapping("/list/{categoryId}")
    // @RequiresPermissions("product:attrgroup:list")
    public R list(@RequestParam Map<String, Object> params,
                  @PathVariable("categoryId") Long categoryId){
        // PageUtils page = attrGroupService.queryPage(params);

        PageUtils page = attrGroupService.queryPage(params, categoryId);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{attrGroupId}")
    // @RequiresPermissions("product:attrgroup:info")
    public R info(@PathVariable("attrGroupId") Long attrGroupId){
		AttrGroupEntity attrGroup = attrGroupService.getById(attrGroupId);
        final Long catelogId = attrGroup.getCatelogId();
        Long[] path = categoryService.findCatalogPath(catelogId);
        attrGroup.setCatelogPath(path);
        return R.ok().put("attrGroup", attrGroup);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    // @RequiresPermissions("product:attrgroup:save")
    public R save(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.save(attrGroup);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    // @RequiresPermissions("product:attrgroup:update")
    public R update(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.updateById(attrGroup);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    // @RequiresPermissions("product:attrgroup:delete")
    public R delete(@RequestBody Long[] attrGroupIds){
		attrGroupService.removeByIds(Arrays.asList(attrGroupIds));

        return R.ok();
    }

}
