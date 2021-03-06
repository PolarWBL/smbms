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


    //????????????
    public void updatePassword(HttpServletRequest req, HttpServletResponse resp){
        //???session???????????????
        Object attribute = req.getSession().getAttribute(Constants.USER_SESSION);
        String newpassword = req.getParameter("newpassword");
        boolean flag = false;
        if (attribute!=null && !StringUtils.isNullOrEmpty(newpassword)){
            User user = (User) attribute;
            UserService userService = new UserServiceImpl();
            flag = userService.updatePwd(user.getUserCode(), newpassword);
            if (flag){
                req.setAttribute(Constants.MESSAGE, "????????????, ?????????????????????!");
                //??????????????????session
                req.getSession().removeAttribute(Constants.USER_SESSION);
            }else {
                req.setAttribute(Constants.MESSAGE, "??????????????????!");
            }
        }else {
            req.setAttribute(Constants.MESSAGE,"?????????????????????");
        }
        try {
            resp.sendRedirect(req.getContextPath()+"/jsp/pwdmodify.jsp");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //???????????????,??????session????????????
    public void checkOldPassword(HttpServletRequest req, HttpServletResponse resp){
        Object attribute = req.getSession().getAttribute(Constants.USER_SESSION);
        String oldpassword = req.getParameter("oldpassword");

        //???????????????Map?????????
        HashMap<String, String> resultMap = new HashMap<>();

        if (attribute==null){//session??????
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

    //??????????????????
    public void checkQuery(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //?????????????????????
        String queryUserName = req.getParameter("queryname");
        String queryUserRole = req.getParameter("queryUserRole");
        String pageIndex = req.getParameter("pageIndex");

        if (queryUserRole==null){
            queryUserName = "";
        }

        //????????????????????????????????????
        List<User> userList = null;
        List<Role> roleList = null;
        PageSupport pageSupport = new PageSupport();
        UserService userService = new UserServiceImpl();
        RoleService roleService = new RoleServiceImpl();
        //??????queryUserRole??????,????????????
        int queryUserRoleNumber = 0;
        if (queryUserRole != null && !queryUserRole.equals("")){
            queryUserRoleNumber = Integer.parseInt(queryUserRole);
        }
        //??????pageIndex??????,????????????
        int pageIndexNumber = 0;
        if (pageIndex != null && !pageIndex.equals("")){
            pageIndexNumber = Integer.parseInt(pageIndex);
        }
        //??????????????????
        int userCount = userService.getUserCount(queryUserName, queryUserRoleNumber);
        //??????????????????
        pageSupport.setPageSize(Constants.PAGESIZE);
        pageSupport.setTotalCount(userCount);
        pageSupport.setCurrentPageNo(pageIndexNumber);
        //??????????????????
        int totalPageCount = pageSupport.getTotalPageCount();
        int currentPageNo = pageSupport.getCurrentPageNo();
        int pageSize = pageSupport.getPageSize();
        //?????????????????????????????????
        userList = userService.getUserList(queryUserName, queryUserRoleNumber, currentPageNo, pageSize);
        roleList = roleService.getRoles();
        //??????????????????
        req.setAttribute("roleList",roleList);
        req.setAttribute("userList",userList);
        req.setAttribute("totalPageCount",totalPageCount);
        req.setAttribute("totalCount",userCount);
        req.setAttribute("currentPageNo",currentPageNo);
        req.setAttribute("queryUserName",queryUserName);
        req.setAttribute("queryUserRole",queryUserRole);
        //??????????????????
        req.getRequestDispatcher("userlist.jsp").forward(req,resp);

    }


    //??????????????????
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

    //??????????????????????????????
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
    //????????????
    private void add(HttpServletRequest req, HttpServletResponse resp) {
        System.out.println("?????????add???servlet");

        //??????????????????????????????session
        Object attribute = req.getSession().getAttribute(Constants.USER_SESSION);
        boolean result = false;
        //??????session??????
        if (attribute!=null){
            //?????????????????????
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

            //??????????????????User????????????
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

            //??????service????????????
            try {
                result = userService.addUser(newUser);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            //????????????. ????????????
            if (result){
                req.setAttribute(Constants.MESSAGE, "????????????!");
                try {
                    resp.sendRedirect(req.getContextPath()+"/jsp/user.do?method=query");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else {
                req.setAttribute(Constants.MESSAGE, "????????????!");
            }

        }else {
            req.setAttribute(Constants.MESSAGE, "????????????!");
        }


    }

    //????????????
    private void deluser(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        System.out.println("?????????deluser??????");
        //?????????????????????????????????id, session??????????????????
        int id = Integer.parseInt(req.getParameter("uid"));
        Object attribute = req.getSession().getAttribute(Constants.USER_SESSION);
        HashMap<String, String> resultMap = new HashMap<>();
        //??????session??????,??????service??????
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

    //??????????????????
    private void modify(HttpServletRequest req, HttpServletResponse resp) {
        System.out.println("?????????modify??????");
        //?????????????????????????????????id
        int id = Integer.parseInt(req.getParameter("uid"));
        //???????????????????????????????????????
        UserService userService = new UserServiceImpl();
        User user = userService.getUserListByid(id);
        //??????????????????
        req.setAttribute("user",user);
        //??????????????????
        try {
            req.getRequestDispatcher("usermodify.jsp").forward(req,resp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //??????????????????
    private void modifyexe(HttpServletRequest req, HttpServletResponse resp) {
        System.out.println("?????????modifyexe??????");
        //?????????????????????????????????id
        int id = Integer.parseInt(req.getParameter("uid"));
        //??????????????????????????????session
        Object attribute = req.getSession().getAttribute(Constants.USER_SESSION);

        //??????session??????
        if (attribute!=null){
            //?????????????????????
            //???attribute??????user??????
            UserService userService = new UserServiceImpl();
            User user = (User) attribute;
            //?????????
            int modifyBy = user.getId();
            //?????????
            String userName = req.getParameter("userName");
            //??????
            int gender =Integer.parseInt(req.getParameter("gender"));
            //????????????
            SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
            Date birthday = null;
            try {
                birthday = dateformat.parse(req.getParameter("birthday"));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            //??????
            String phone = req.getParameter("phone");
            //??????
            String address = req.getParameter("address");
            //??????
            int userRole = Integer.parseInt(req.getParameter("userRole"));

            //??????????????????service
            boolean result = false;
            try {
                result =  userService.modifyUser(id,modifyBy,userName,gender,birthday,phone,address,userRole);
            } catch (SQLException e) {
                e.printStackTrace();
            }

            if (result){
                req.setAttribute(Constants.MESSAGE, "????????????!");
                try {
                    resp.sendRedirect(req.getContextPath()+"/jsp/user.do?method=query");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else {
                req.setAttribute(Constants.MESSAGE, "????????????!");
            }

        }else {
            req.setAttribute(Constants.MESSAGE, "????????????!");
        }
    }

    //????????????????????????
    private void view(HttpServletRequest req, HttpServletResponse resp) {
        System.out.println("?????????view??????");
        //?????????????????????????????????id
        int id = Integer.parseInt(req.getParameter("uid"));
        //???????????????????????????????????????
        UserService userService = new UserServiceImpl();
        User user = userService.getUserListByid(id);
        //??????????????????
        req.setAttribute("user",user);
        //??????????????????
        try {
            req.getRequestDispatcher("userview.jsp").forward(req,resp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
