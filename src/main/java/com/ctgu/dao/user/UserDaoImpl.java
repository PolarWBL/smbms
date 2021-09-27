package com.ctgu.dao.user;

import com.ctgu.dao.BaseDao;
import com.ctgu.pojo.User;
import com.mysql.cj.util.StringUtils;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;



public class UserDaoImpl implements UserDao {
    @Override//查询单个用户信息
    public User getUser(String userCode, Connection connection) throws SQLException {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        connection =  BaseDao.getConnection();
        User user = new User();
        if(connection!=null){
            String sql = "select * from smbms_user where userCode=?";
            Object[] p = {userCode};
            resultSet = BaseDao.execute(connection,preparedStatement,resultSet,sql,p);
            if(resultSet.next()){
                user.setId(resultSet.getInt("id"));
                user.setUserCode(resultSet.getString("userCode"));
                user.setUserName(resultSet.getString("userName"));
                user.setUserPassword(resultSet.getString("userPassword"));
                user.setGender(resultSet.getInt("gender"));
                user.setBirthday(resultSet.getDate("birthday"));
                user.setPhone(resultSet.getString("phone"));
                user.setAddress(resultSet.getString("address"));
                user.setUserRole(resultSet.getInt("userRole"));
                user.setCreatedBy(resultSet.getInt("createdBy"));
                user.setCreationDate(resultSet.getDate("creationDate"));
                user.setModifyBy(resultSet.getInt("modifyBy"));
                user.setModifyDate(resultSet.getDate("modifyDate"));
            }
            BaseDao.closeResource(null,resultSet,preparedStatement);
        }
    return user;
    }

    public User getUser(int id, Connection connection) throws SQLException {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        connection =  BaseDao.getConnection();
        User user = new User();
        if(connection!=null){
            String sql = "select * from smbms_user where id=?";
            Object[] p = {id};
            resultSet = BaseDao.execute(connection,preparedStatement,resultSet,sql,p);
            if(resultSet.next()){
                user.setId(resultSet.getInt("id"));
                user.setUserCode(resultSet.getString("userCode"));
                user.setUserName(resultSet.getString("userName"));
                user.setUserPassword(resultSet.getString("userPassword"));
                user.setGender(resultSet.getInt("gender"));
                user.setBirthday(resultSet.getDate("birthday"));
                user.setPhone(resultSet.getString("phone"));
                user.setAddress(resultSet.getString("address"));
                user.setUserRole(resultSet.getInt("userRole"));
                user.setCreatedBy(resultSet.getInt("createdBy"));
                user.setCreationDate(resultSet.getDate("creationDate"));
                user.setModifyBy(resultSet.getInt("modifyBy"));
                user.setModifyDate(resultSet.getDate("modifyDate"));
            }
            BaseDao.closeResource(null,resultSet,preparedStatement);
        }
        return user;
    }

    @Override//修改用户密码
    public int updatePwd(Connection connection, String userCode, String password) throws SQLException {
        PreparedStatement preparedStatement = null;
        int i = 0;
        if (connection!=null){
            connection = BaseDao.getConnection();
            Object[] params = {password,userCode};
            String sql = "update smbms_user set userPassword = ? where userCode = ?";
            i = BaseDao.execute(connection, preparedStatement, sql, params);
            BaseDao.closeResource(null, null, preparedStatement);
        }
        return i;
    }

    @Override//查询用户总数, 同时包含 用户名查询和用户分类查询
    public int getUserCount(Connection connection, String userName, int userRole) throws SQLException {
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        int count = 0;

        if (connection != null) {
            connection = BaseDao.getConnection();
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT count(*) as count from smbms_user, smbms_role where smbms_user.userRole = smbms_role.id");
            ArrayList<Object> list = new ArrayList<Object>();

            if(!StringUtils.isNullOrEmpty(userName)){
                sql.append(" and userName like ?");
                list.add("%"+userName+"%");
            }

            if(userRole>0){
                sql.append(" and userRole = ?");
                list.add(userRole);
            }


            System.out.println("UserDaoInpl:sql---->" +  sql.toString());

            //将list转为数组
            Object[] params = list.toArray();
            rs = BaseDao.execute(connection, preparedStatement,rs,sql.toString(), params);

            if(rs.next()){
                count = rs.getInt("count");
            }

            BaseDao.closeResource(null,rs,preparedStatement);

        }
        return count;
    }

    @Override//查询对应名称和角色的用户列表
    public List<User> getUserList(Connection connection, String userName, int userRole, int currentPageNo, int pageSize) {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        ArrayList<User> userList = new ArrayList<User>();

        if(connection != null){
            connection = BaseDao.getConnection();
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT  * from smbms_user, smbms_role where smbms_user.userRole = smbms_role.id");
            ArrayList<Object> list = new ArrayList<Object>();
            //查询姓名
            if(!StringUtils.isNullOrEmpty(userName)){
                sql.append(" and userName like ?");
                list.add("%"+userName+"%");
            }
            //查询职务
            if(userRole>0){
                sql.append(" and userRole = ?");
                list.add(userRole);
            }

            //分页
            sql.append(" order by smbms_user.id limit ? , ?");
            currentPageNo = (currentPageNo-1)*pageSize;
            list.add(currentPageNo);
            list.add(pageSize);

            System.out.println("UserDaoInpl_getUserList:sql---->" +  sql.toString());

            //将list转为数组
            Object[] params = list.toArray();
            try {
                resultSet = BaseDao.execute(connection,preparedStatement,resultSet, sql.toString(),params);
                while(resultSet.next()){
                    User user = new User();
                    user.setId(resultSet.getInt("smbms_user.id"));
                    user.setUserCode(resultSet.getString("smbms_user.userCode"));
                    user.setUserName(resultSet.getString("smbms_user.userName"));
                    user.setUserPassword(resultSet.getString("smbms_user.userPassword"));
                    user.setGender(resultSet.getInt("smbms_user.gender"));
                    user.setBirthday(resultSet.getDate("smbms_user.birthday"));
                    user.setPhone(resultSet.getString("smbms_user.phone"));
                    user.setAddress(resultSet.getString("smbms_user.address"));
                    user.setUserRole(resultSet.getInt("smbms_user.userRole"));
                    user.setUserRoleName(resultSet.getString("smbms_role.roleName"));
                    user.setCreatedBy(resultSet.getInt("smbms_user.createdBy"));
                    user.setCreationDate(resultSet.getDate("smbms_user.creationDate"));
                    user.setModifyBy(resultSet.getInt("smbms_user.modifyBy"));
                    user.setModifyDate(resultSet.getDate("smbms_user.modifyDate"));
                    userList.add(user);
                }

            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }finally {
                BaseDao.closeResource(null,resultSet,preparedStatement);
            }
        }
        return userList;
    }

    @Override//添加用户
    public boolean addUser(Connection connection, User user) throws SQLException {
        PreparedStatement preparedStatement = null;
        connection = BaseDao.getConnection();
        Object[] params = new Object[9];
        int execute = 0;
        if (connection != null) {
            String sql = "insert into smbms_user (userCode, userName, userPassword, gender, birthday, phone, address, userRole, createdBy, creationDate) values (?, ?, ?, ?, ?, ?, ?, ?, ?, now())";//"    ('用户id', '用户名' ,'密码', 性别(1男,2女), 出生日期, '电话号码', '地址', 角色省份, 创建角色的人的省份, 创建时间now())";
            params[0] = user.getUserCode();
            params[1] = user.getUserName();
            params[2] = user.getUserPassword();
            params[3] = user.getGender();
            params[4] = user.getBirthday();
            params[5] = user.getPhone();
            params[6] = user.getAddress();
            params[7] = user.getUserRole();
            params[8] = user.getCreatedBy();
            execute = BaseDao.execute(connection, preparedStatement, sql, params);
        }
        BaseDao.closeResource(null,null,preparedStatement);
        if (execute == 1){
            return true;
        }else{
            return false;
        }
    }

    @Override//删除用户
    public boolean deluser(Connection connection, int id) {
        //数据库链接, 操作
        PreparedStatement preparedStatement = null;
        connection = BaseDao.getConnection();
        //给params中放入id
        Object[] params = null;
        int execute = 0;
        //操作数据库
        if (connection != null && id > 0){
            String sql = "delete from smbms_user where id = ?";
            params = new Object[]{id};
            try {
                execute = BaseDao.execute(connection,preparedStatement,sql,params);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }finally {
                BaseDao.closeResource(null,null,preparedStatement);
            }
        }
        //返回结果
        if (execute == 1){
            return true;
        }else{
            return false;
        }
    }

    @Override//修改用户
    public boolean modifyUser(Connection connection, int id, int modifyBy, String userName,
                              int gender, Date birthday, String phone, String address,
                              int userRole) throws SQLException {
        PreparedStatement preparedStatement = null;
        int result = 0;

        if (connection != null){
            connection = BaseDao.getConnection();
            StringBuilder sql = new StringBuilder();
            sql.append("update smbms_user set modifyDate = now()");
            ArrayList<Object> list = new ArrayList<Object>();

            if(modifyBy>0){
                sql.append(" , modifyBy = ?");
                list.add(modifyBy);
            }

            if(!StringUtils.isNullOrEmpty(userName)){
                sql.append(" , userName = ?");
                list.add(userName);
            }

            if(gender>0){
                sql.append(" , gender = ?");
                list.add(gender);
            }

            if(birthday!=null){
                sql.append(" , birthday = ?");
                list.add(birthday);
            }

            if (!StringUtils.isNullOrEmpty(phone)){
                sql.append(" , phone = ?");
                list.add(phone);
            }

            if (!StringUtils.isNullOrEmpty(address)){
                sql.append(" , address = ?");
                list.add(address);
            }

            if(userRole>0){
                sql.append(" , userRole = ?");
                list.add(userRole);
            }

            sql.append(" where id = ?");
            list.add(id);

            System.out.println("modifyUser:sql---->" +  sql.toString());
            System.out.println(id);
            //将list转为数组
            Object[] params = list.toArray();

            result = BaseDao.execute(connection, preparedStatement,sql.toString(), params);

            BaseDao.closeResource(null,null,preparedStatement);

        }
        if (result == 1){
            return  true;
        }else {
            return false;
        }
    }

}
