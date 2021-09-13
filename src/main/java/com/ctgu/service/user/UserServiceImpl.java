package com.ctgu.service.user;

import com.ctgu.dao.BaseDao;
import com.ctgu.dao.user.UserDao;
import com.ctgu.dao.user.UserDaoImpl;
import com.ctgu.pojo.User;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

public class UserServiceImpl implements UserService {
    //业务层调用dao层,引入dao层
    private UserDao userDao;
    public UserServiceImpl(){
        userDao = new UserDaoImpl();
    }


    @Override//登录判断
    public User login(String userCode, String password){
        Connection connection;
        User user=null;
        connection = BaseDao.getConnection();
        //通过业务层调用dao层
        try {
            user = userDao.getUser(userCode,connection);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }finally {
            BaseDao.closeResource(connection,null,null);
        }

        //密码匹配
        if(user!=null&&password.equals(user.getUserPassword())){
            return user;
        }else {
            return null;
        }
    }

    @Override//修改密码
    public boolean updatePwd(String userCode, String password){
        Connection connection = null;
        connection = BaseDao.getConnection();
        boolean flag = false;
        try {
            if(userDao.updatePwd(connection,userCode,password)>0){
                flag = true;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }finally {
            BaseDao.closeResource(connection,null,null);
        }
        return flag;
    }

    @Override//获得用户总数
    public int getUserCount(String userName, int userRole) {
        Connection connection = null;
        connection = BaseDao.getConnection();
        int count = 0;

        try {
            count = userDao.getUserCount(connection, userName, userRole);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }finally {
            BaseDao.closeResource(connection,null,null);
        }

        return count;
    }

    @Override//获取用户信息
    public List<User> getUserList(String userName, int userRole, int currentPageNo, int pageSize) {
        Connection connection = null;
        connection = BaseDao.getConnection();
        List<User> userList;
        userList = userDao.getUserList(connection,userName,userRole,currentPageNo,pageSize);
        BaseDao.closeResource(connection,null,null);

        return userList;
    }

    @Override//查询对应用户编号的用户信息
    public User getUserListByUserCode(String userCode) {
        Connection connection = null;
        connection = BaseDao.getConnection();
        User user = null;
        try {
            user = userDao.getUser(userCode,connection);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        BaseDao.closeResource(connection,null,null);
        return user;
    }

    @Override//添加用户
    public boolean addUser(User user) throws SQLException {
        Connection connection = null;
        connection = BaseDao.getConnection();
        boolean result = false;
        result = userDao.addUser(connection,user);
        BaseDao.closeResource(connection,null,null);
        return result;
    }

    @Override//删除用户
    public boolean deluser(int id) {
        Connection connection = null;
        connection = BaseDao.getConnection();
        boolean result = false;
        result = userDao.deluser(connection,id);
        BaseDao.closeResource(connection,null,null);
        return result;
    }

    @Override//修改用户
    public boolean modifyUser(int id, int modifyBy,
                              String userName, int gender, Date birthday,
                              String phone, String address, int userRole) throws SQLException {
        Connection connection = null;
        connection = BaseDao.getConnection();
        boolean result = false;
        result = userDao.modifyUser(connection,id,modifyBy,userName,gender,birthday,phone,address,userRole);
        BaseDao.closeResource(connection,null,null);
        return result;
    }

    @Test
    public void test() throws ParseException, SQLException {
        UserService userService = new UserServiceImpl();
        boolean result =  userService.modifyUser(15,1,null,
                2, null,"12345678900", "CTGU", 1);

        if (result){
            System.out.println("success");
        }else {
            System.out.println("false");
        }
    }

}


