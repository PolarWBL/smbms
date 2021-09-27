package com.ctgu.servlet.user;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.ctgu.pojo.Role;
import com.ctgu.pojo.User;
import com.ctgu.service.role.RoleService;
import com.ctgu.service.role.RoleServiceImpl;
import com.ctgu.service.user.UserService;
import com.ctgu.service.user.UserServiceImpl;
import com.ctgu.util.Constants;
import com.ctgu.util.PageSupport;
import com.mysql.cj.util.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class UserServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String method = req.getParameter("method");
        if (method.equals("savepwd")){
            this.updatePassword(req,resp);
        }else if (method.equals("pwdmodify")){
            this.checkOldPassword(req,resp);
        }else if (method.equals("query")){
            this.checkQuery(req,resp);
        }else if (method.equals("getrolelist")){
            this.getrolelist(req,resp);
        }else if (method.equals("ucexist")){
            this.ucexist(req,resp);
        }else if (method.equals("add")){
            this.add(req,resp);
        }else if (method.equals("deluser")){
            this.deluser(req,resp);
        }else if (method.equals("modify")){
            this.modify(req,resp);
        }else if (method.equals("modifyexe")){
            this.modifyexe(req,resp);
        }else if (method.equals("view")){
            this.view(req,resp);
        }


    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }


    //修改密码
    public void updatePassword(HttpServletRequest req, HttpServletResponse resp){
        //从session中获取信息
        Object attribute = req.getSession().getAttribute(Constants.USER_SESSION);
        String newpassword = req.getParameter("newpassword");
        boolean flag = false;
        if (attribute!=null && !StringUtils.isNullOrEmpty(newpassword)){
            User user = (User) attribute;
            UserService userService = new UserServiceImpl();
            flag = userService.updatePwd(user.getUserCode(), newpassword);
            if (flag){
                req.setAttribute(Constants.MESSAGE, "修改成功, 请退出重新登陆!");
                //修改成功移除session
                req.getSession().removeAttribute(Constants.USER_SESSION);
            }else {
                req.setAttribute(Constants.MESSAGE, "密码修改失败!");
            }
        }else {
            req.setAttribute(Constants.MESSAGE,"新密码出现问题");
        }
        try {
            resp.sendRedirect(req.getContextPath()+"/jsp/pwdmodify.jsp");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //验证旧密码,对比session中的密码
    public void checkOldPassword(HttpServletRequest req, HttpServletResponse resp){
        Object attribute = req.getSession().getAttribute(Constants.USER_SESSION);
        String oldpassword = req.getParameter("oldpassword");

        //使用万能的Map存数据
        HashMap<String, String> resultMap = new HashMap<>();

        if (attribute==null){//session失效
            resultMap.put("result", "sessionerror");
        }else if (StringUtils.isNullOrEmpty(oldpassword)){
            resultMap.put("result", "error");
        }else {
            User user = (User) attribute;
            if (user.getUserPassword().equals(oldpassword)){
                resultMap.put("result", "true");
            }else {
                resultMap.put("result", "false");
            }
        }
        resp.setContentType("application/json");

        try {
            PrintWriter writer = resp.getWriter();
            writer.write(JSONArray.toJSONString(resultMap));
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //查询用户列表
    public void checkQuery(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //从前端获取数据
        String queryUserName = req.getParameter("queryname");
        String queryUserRole = req.getParameter("queryUserRole");
        String pageIndex = req.getParameter("pageIndex");

        if (queryUserRole==null){
            queryUserName = "";
        }

        //创建需要使用的数据和函数
        List<User> userList = null;
        List<Role> roleList = null;
        PageSupport pageSupport = new PageSupport();
        UserService userService = new UserServiceImpl();
        RoleService roleService = new RoleServiceImpl();
        //防止queryUserRole为空,传值报错
        int queryUserRoleNumber = 0;
        if (queryUserRole != null && !queryUserRole.equals("")){
            queryUserRoleNumber = Integer.parseInt(queryUserRole);
        }
        //防止pageIndex为空,传值报错
        int pageIndexNumber = 0;
        if (pageIndex != null && !pageIndex.equals("")){
            pageIndexNumber = Integer.parseInt(pageIndex);
        }
        //获取用户总数
        int userCount = userService.getUserCount(queryUserName, queryUserRoleNumber);
        //设置分页参数
        pageSupport.setPageSize(Constants.PAGESIZE);
        pageSupport.setTotalCount(userCount);
        pageSupport.setCurrentPageNo(pageIndexNumber);
        //获取分页数据
        int totalPageCount = pageSupport.getTotalPageCount();
        int currentPageNo = pageSupport.getCurrentPageNo();
        int pageSize = pageSupport.getPageSize();
        //获取用户列表和角色列表
        userList = userService.getUserList(queryUserName, queryUserRoleNumber, currentPageNo, pageSize);
        roleList = roleService.getRoles();
        //返回值给前端
        req.setAttribute("roleList",roleList);
        req.setAttribute("userList",userList);
        req.setAttribute("totalPageCount",totalPageCount);
        req.setAttribute("totalCount",userCount);
        req.setAttribute("currentPageNo",currentPageNo);
        req.setAttribute("queryUserName",queryUserName);
        req.setAttribute("queryUserRole",queryUserRole);
        //返回前端页面
        req.getRequestDispatcher("userlist.jsp").forward(req,resp);

    }


    //获取角色信息
    private void getrolelist(HttpServletRequest req, HttpServletResponse resp) {
        List<Role> roleList;
        RoleService roleService = new RoleServiceImpl();
        roleList = roleService.getRoles();
        String str = JSON.toJSONString(roleList);

        resp.setContentType("application/json");
        try {
            PrintWriter writer = resp.getWriter();
            writer.write(str);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //检查用户编号是否重复
    private void ucexist(HttpServletRequest req, HttpServletResponse resp) {
        User user = null;
        HashMap<String, String> resultMap = new HashMap<>();
        String userCode = req.getParameter("userCode");
        UserService userService = new UserServiceImpl();

        user = userService.getUserListByUserCode(userCode);

        if (userCode !=null && !userCode.equals("")) {
           if(user != null){
               if (userCode.equals(user.getUserCode())){
                   resultMap.put("userCode", "exist");
               }
           }else {
               resultMap.put("userCode", "empty");
           }
        }else {
            resultMap.put("userCode", "empty");
        }

        String str = JSON.toJSONString(resultMap);

        resp.setContentType("application/json");
        try {
            PrintWriter writer = resp.getWriter();
            writer.write(str);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //添加用户
    private void add(HttpServletRequest req, HttpServletResponse resp) {
        System.out.println("调用了add的servlet");

        //从前端获取当前用户的session
        Object attribute = req.getSession().getAttribute(Constants.USER_SESSION);
        boolean result = false;
        //如果session存在
        if (attribute!=null){
            //从前端获取数据
            UserService userService = new UserServiceImpl();
            User user = (User) attribute;
            SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
            String userCode = req.getParameter("userCode");
            String userName = req.getParameter("userName");
            String userPassword = req.getParameter("userPassword");
            int gender =Integer.parseInt(req.getParameter("gender"));
            String phone = req.getParameter("phone");
            String address = req.getParameter("address");
            int userRole = Integer.parseInt(req.getParameter("userRole"));
            int createBy = user.getId();
            Date birthday = null;
            try {
                birthday = dateformat.parse(req.getParameter("birthday"));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            //将数据打包到User类实体中
            User newUser = new User();
            newUser.setUserCode(userCode);
            newUser.setUserName(userName);
            newUser.setUserPassword(userPassword);
            newUser.setGender(gender);
            newUser.setBirthday(birthday);
            newUser.setPhone(phone);
            newUser.setAddress(address);
            newUser.setUserRole(userRole);
            newUser.setCreatedBy(createBy);

            //调用service添加用户
            try {
                result = userService.addUser(newUser);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            //判断结果. 返回信息
            if (result){
                req.setAttribute(Constants.MESSAGE, "添加成功!");
                try {
                    resp.sendRedirect(req.getContextPath()+"/jsp/user.do?method=query");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else {
                req.setAttribute(Constants.MESSAGE, "添加失败!");
            }

        }else {
            req.setAttribute(Constants.MESSAGE, "添加失败!");
        }


    }

    //删除用户
    private void deluser(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        System.out.println("调用了deluser方法");
        //从前端获取要删除的用户id, session中的用户信息
        int id = Integer.parseInt(req.getParameter("uid"));
        Object attribute = req.getSession().getAttribute(Constants.USER_SESSION);
        HashMap<String, String> resultMap = new HashMap<>();
        //如果session存在,调用service方法
        if (attribute!=null){
            if (id >0){
                UserService userService = new UserServiceImpl();
                User user = (User) attribute;
                if (user.getId() != id){
                    if (userService.deluser(id)){
                        resultMap.put("delResult", "true");
                    }else{
                        resultMap.put("delResult", "false");
                        resp.sendRedirect(req.getContextPath()+"/jsp/user.do?method=query");
                    }
                }else {
                    resultMap.put("delResult", "cannotdel");
                    resp.sendRedirect(req.getContextPath()+"/jsp/user.do?method=query");
                }
            }else{
                resultMap.put("delResult", "notexist");
                resp.sendRedirect(req.getContextPath()+"/jsp/user.do?method=query");
            }
        }

        resp.setContentType("application/json");
        String str =  JSONArray.toJSONString(resultMap);

        System.out.println(resultMap.get("delResult"));
        System.out.println(str);

        try {
            PrintWriter writer = resp.getWriter();
            writer.write(str);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //进入修改页面
    private void modify(HttpServletRequest req, HttpServletResponse resp) {
        System.out.println("调用了modify方法");
        //从前端获取要修改的用户id
        int id = Integer.parseInt(req.getParameter("uid"));
        //查询对应用户编号的用户信息
        UserService userService = new UserServiceImpl();
        User user = userService.getUserListByid(id);
        //返回值给前端
        req.setAttribute("user",user);
        //返回前端页面
        try {
            req.getRequestDispatcher("usermodify.jsp").forward(req,resp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //修改用户信息
    private void modifyexe(HttpServletRequest req, HttpServletResponse resp) {
        System.out.println("调用了modifyexe方法");
        //从前端获取要修改的用户id
        int id = Integer.parseInt(req.getParameter("uid"));
        //从前端获取当前用户的session
        Object attribute = req.getSession().getAttribute(Constants.USER_SESSION);

        //如果session存在
        if (attribute!=null){
            //从前端获取数据
            //将attribute转为user类型
            UserService userService = new UserServiceImpl();
            User user = (User) attribute;
            //修改者
            int modifyBy = user.getId();
            //用户名
            String userName = req.getParameter("userName");
            //性别
            int gender =Integer.parseInt(req.getParameter("gender"));
            //出生日期
            SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
            Date birthday = null;
            try {
                birthday = dateformat.parse(req.getParameter("birthday"));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            //电话
            String phone = req.getParameter("phone");
            //地址
            String address = req.getParameter("address");
            //角色
            int userRole = Integer.parseInt(req.getParameter("userRole"));

            //将数据发送给service
            boolean result = false;
            try {
                result =  userService.modifyUser(id,modifyBy,userName,gender,birthday,phone,address,userRole);
            } catch (SQLException e) {
                e.printStackTrace();
            }

            if (result){
                req.setAttribute(Constants.MESSAGE, "修改成功!");
                try {
                    resp.sendRedirect(req.getContextPath()+"/jsp/user.do?method=query");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else {
                req.setAttribute(Constants.MESSAGE, "添加失败!");
            }

        }else {
            req.setAttribute(Constants.MESSAGE, "添加失败!");
        }
    }

    //进入用户查看界面
    private void view(HttpServletRequest req, HttpServletResponse resp) {
        System.out.println("调用了view方法");
        //从前端获取要查看的用户id
        int id = Integer.parseInt(req.getParameter("uid"));
        //查询对应用户编号的用户信息
        UserService userService = new UserServiceImpl();
        User user = userService.getUserListByid(id);
        //返回值给前端
        req.setAttribute("user",user);
        //返回前端页面
        try {
            req.getRequestDispatcher("userview.jsp").forward(req,resp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
