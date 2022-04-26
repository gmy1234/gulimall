package com.gmy.guliorder.order.config;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author UnityAlvin
 * @date 2021/7/17 9:52
 */
@Configuration
public class FeignConfig {
    /**
     * 解决远程服务丢失请求头的问题
     *
     * @return
     */
    @Bean("requestInterceptor")
    public RequestInterceptor requestInterceptor() {
        return template -> {
            /*
                通过请求的上下文环境，拿到刚进来的请求数据
             */
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                if (request != null) {
                    // 将老请求的请求头放到新请求的请求头里面
                    template.header("Cookie", request.getHeader("Cookie"));
                }
            }
        };
    }
}
