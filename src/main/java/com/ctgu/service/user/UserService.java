package com.ctgu.service.user;

import com.ctgu.pojo.User;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public interface UserService {
    //登录实现
    public User login(String userCode, String password);

    //根据userCode修改密码
    public boolean updatePwd(String userCode, String password);

    //查询用户总数
    public int getUserCount(String userName, int userRole);

    //查询用户
    public List<User> getUserList(String userName, int userRole, int currentPageNo, int pageSize);

    //查询用户通过userCode
    public User getUserListByUserCode(String userCode);

    //查询用户通过id
    public User getUserListByid(int id);

    //添加用户
    public boolean addUser(User user) throws SQLException;

    //删除用户
    public boolean deluser(int id);

    //修改用户
    public boolean modifyUser(int id, int modifyBy, String userName,
                              int gender, Date birthday, String phone, String address,
                              int userRole) throws SQLException;

}
