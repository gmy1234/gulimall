package com.gmy.gulimall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gmy.common.utils.PageUtils;
import com.gmy.gulimall.member.entity.IntegrationChangeHistoryEntity;

import java.util.Map;

/**
 * 积分变化历史记录
 *
 * @author gmy
 * @email guanmengyang528@gmail.com
 * @date 2022-03-17 11:33:13
 */
public interface IntegrationChangeHistoryService extends IService<IntegrationChangeHistoryEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

