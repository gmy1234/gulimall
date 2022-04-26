package com.gmy.guliorder.order.interceptor;

import com.gmy.common.constant.AuthServerConstant;
import com.gmy.common.vo.MemberResponseVo;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class LoginUserInterceptor implements HandlerInterceptor {

    public static ThreadLocal<MemberResponseVo> loginUser = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        // 此类型的请求，直接放行，无需登录，否则会报错
        String uri = request.getRequestURI();
        AntPathMatcher antPathMatcher = new AntPathMatcher();

        boolean match = antPathMatcher.match("/web/order/order-by-order-sn/**", uri);
        boolean match1 = antPathMatcher.match("/alipay/paid/notify", uri);
        boolean match2 = antPathMatcher.match("/doc.html", uri);
        if (match || match1 || match2) return true;

        MemberResponseVo memberTO = (MemberResponseVo) request.getSession().getAttribute(AuthServerConstant.SESSION_ATTR_NAME);
        if (memberTO != null) {
            loginUser.set(memberTO);
            return true;
        } else {
            request.getSession().setAttribute("msg", "请先进行登录！");
            response.sendRedirect("http://auth.gulimall.com/login.html");
            return false;
        }
    }
}
