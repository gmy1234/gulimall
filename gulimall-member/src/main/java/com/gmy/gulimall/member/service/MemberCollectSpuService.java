package com.gmy.gulimall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gmy.common.utils.PageUtils;
import com.gmy.gulimall.member.entity.MemberCollectSpuEntity;

import java.util.Map;

/**
 * 会员收藏的商品
 *
 * @author gmy
 * @email guanmengyang528@gmail.com
 * @date 2022-03-17 11:33:13
 */
public interface MemberCollectSpuService extends IService<MemberCollectSpuEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

