package com.ctgu.dao.user;

import com.ctgu.pojo.User;

import java.sql.Connection;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public interface UserDao {
    //得到用户信息
    public User getUser(String usercode, Connection connection) throws SQLException;

    //修改用户密码
    public int updatePwd(Connection connection, String userCode, String password) throws SQLException;

    //查询用户总数, 同时包含 用户查询和用户分类查询
    public int getUserCount(Connection connection, String userName, int userRole) throws SQLException;

    //查询用户信息
    public List<User> getUserList(Connection connection, String userName, int userRole, int currentPageNo,  int pageSize);

    //添加用户
    public boolean addUser(Connection connection, User user) throws SQLException;

    //删除用户
    public boolean deluser(Connection connection, int id);

    //修改用户
    public boolean modifyUser(Connection connection, int id, int modifyBy, String userName,
                              int gender, Date birthday, String phone, String address,
                              int userRole) throws SQLException;
}
