package com.ctgu.servlet.user;

import com.ctgu.pojo.User;
import com.ctgu.service.user.UserService;
import com.ctgu.service.user.UserServiceImpl;
import com.ctgu.util.Constants;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

public class LoginServlet extends HttpServlet {
    //控制层调用业务层代码



    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("进入了LoginServlet");
        //获取用户名和密码
        String userCode = req.getParameter("userCode");
        String userPassword = req.getParameter("userPassword");
        //对比
        UserService userService = new UserServiceImpl();
        User user = userService.login(userCode, userPassword);

        if (user != null){//匹配成功
            //将用户信息放入session中
            req.getSession().setAttribute(Constants.USER_SESSION,user);
            //跳转到内部主页
            resp.sendRedirect("jsp/frame.jsp");
        }else {//验证失败无法登录
            //转发到登录界面,提示用户名密码错误
            req.setAttribute("error", "用户名或密码错误!");
            req.getRequestDispatcher("login.jsp").forward(req,resp);
        }


    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req,resp);
    }
}
