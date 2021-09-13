package com.ctgu.filiter;

import com.ctgu.pojo.User;
import com.ctgu.util.Constants;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LoginFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        User user = (User) req.getSession().getAttribute(Constants.USER_SESSION);

        if(user==null){
            //注销或未登录
            resp.sendRedirect(req.getContextPath()+"/login.jsp");
        }

        chain.doFilter(req,resp);
    }

    @Override
    public void destroy() {

    }
}
