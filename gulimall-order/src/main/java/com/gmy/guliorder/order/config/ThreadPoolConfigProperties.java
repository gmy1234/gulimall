package com.gmy.guliorder.order.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Description:
 * @Created: with IntelliJ IDEA.
 * @author: gmy
 * @createTime: 2020-06-23 20:28
 **/

@ConfigurationProperties(prefix = "gulimall.thread")
//@Component
@Data
public class ThreadPoolConfigProperties {

    private Integer coreSize = 5;

    private Integer maxSize = 15;

    private Integer keepAliveTime = 20;


}
