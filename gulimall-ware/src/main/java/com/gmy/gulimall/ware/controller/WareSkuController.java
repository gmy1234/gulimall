package com.gmy.gulimall.ware.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.serializer.BigDecimalCodec;
import com.gmy.common.exception.BizCodeEnume;
import com.gmy.common.to.SkuHasStockVo;
import com.gmy.gulimall.ware.vo.LockStockResultVo;
import com.gmy.gulimall.ware.vo.WareSkuLockVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.gmy.gulimall.ware.entity.WareSkuEntity;
import com.gmy.gulimall.ware.service.WareSkuService;
import com.gmy.common.utils.PageUtils;
import com.gmy.common.utils.R;



/**
 * 商品库存
 *
 * @author gmy
 * @email guanmengyang528@gmail.com
 * @date 2022-03-17 11:48:48
 */
@RestController
@RequestMapping("ware/waresku")
public class WareSkuController {
    @Autowired
    private WareSkuService wareSkuService;

    /**
     * 锁库存
     * @param vo 需要的数据
     * @return
     */
    @PostMapping("/lock")
    public R orderLock(WareSkuLockVo vo){
        boolean res = wareSkuService.lockCount(vo);
        if (res) {
            return R.ok();
        }
        return R.error(BizCodeEnume.NO_STOCK_EXCEPTION.getCode(),
                BizCodeEnume.NO_STOCK_EXCEPTION.getMsg());
    }


    /**
     * 检查sku 是否有库存
     */
    @PostMapping("/hasstock")
    // @RequiresPermissions("ware:waresku:list")
    public R getSkuHasStock(@RequestBody List<Long> skuIds){
        List<SkuHasStockVo> data = wareSkuService.getSkuHasStock(skuIds);

        return R.ok().put("data", data);
    }


    /**
     * 列表
     */
    @RequestMapping("/list")
    // @RequiresPermissions("ware:waresku:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = wareSkuService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    // @RequiresPermissions("ware:waresku:info")
    public R info(@PathVariable("id") Long id){
		WareSkuEntity wareSku = wareSkuService.getById(id);

        return R.ok().put("wareSku", wareSku);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    // @RequiresPermissions("ware:waresku:save")
    public R save(@RequestBody WareSkuEntity wareSku){
		wareSkuService.save(wareSku);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    // @RequiresPermissions("ware:waresku:update")
    public R update(@RequestBody WareSkuEntity wareSku){
		wareSkuService.updateById(wareSku);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    // @RequiresPermissions("ware:waresku:delete")
    public R delete(@RequestBody Long[] ids){
		wareSkuService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
