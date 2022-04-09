package com.gmy.gulimall.product.api;

import java.util.Arrays;
import java.util.Map;

import com.gmy.common.validator.group.AddGroup;
import com.gmy.common.validator.group.UpdateGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gmy.gulimall.product.entity.BrandEntity;
import com.gmy.gulimall.product.service.BrandService;
import com.gmy.common.utils.PageUtils;
import com.gmy.common.utils.R;


/**
 * 品牌
 *
 * @author gmy
 * @email guanmengyang528@gmail.com
 * @date 2022-03-16 22:46:40
 */
@RestController
@RequestMapping("product/brand")
public class BrandController {
    @Autowired
    private BrandService brandService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    // @RequiresPermissions("product:brand:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = brandService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{brandId}")
    // @RequiresPermissions("product:brand:info")
    public R info(@PathVariable("brandId") Long brandId){
		BrandEntity brand = brandService.getById(brandId);

        return R.ok().put("brand", brand);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    // @RequiresPermissions("product:brand:save")
    // 校验注解 @Valid
    public R save(@Validated(AddGroup.class) @RequestBody BrandEntity brand /*BindingResult result */){
//        if (result.hasErrors()) {
//
//            final HashMap<String, String> message = new HashMap<>();
//            // 获取错误的字段
//            result.getFieldErrors().forEach( (item) ->{
//                final String defaultMessage = item.getDefaultMessage();
//                // 获取字段的名字
//                final String field = item.getField();
//                message.put(field, defaultMessage);
//            });
//
//            R.error(400, "提交的数据不合法").put("data", message);
//        }else {
//        }
        brandService.save(brand);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    // @RequiresPermissions("product:brand:update")
    public R update(@Validated(UpdateGroup.class) @RequestBody BrandEntity brand){
		brandService.updateById(brand);
        brandService.updateDetail(brand);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    // @RequiresPermissions("product:brand:delete")
    public R delete(@RequestBody Long[] brandIds){
		brandService.removeByIds(Arrays.asList(brandIds));

        return R.ok();
    }

}
