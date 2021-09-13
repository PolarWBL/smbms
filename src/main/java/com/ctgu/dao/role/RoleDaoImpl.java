package com.ctgu.dao.role;

import com.ctgu.dao.BaseDao;
import com.ctgu.pojo.Role;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RoleDaoImpl implements RoleDao{
    @Override//获取角色信息
    public List<Role> getRoles(Connection connection) {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Object[] params = {};
        ArrayList<Role> roleList = new ArrayList<Role>();

        if(connection != null){
            connection = BaseDao.getConnection();
            String sql = ("SELECT * from smbms_role");
            try {
                resultSet = BaseDao.execute(connection,preparedStatement,resultSet,sql,params);
                while(resultSet.next()) {
                    Role role = new Role();
                    role.setId(resultSet.getInt("id"));
                    role.setRoleCode(resultSet.getString("roleCode"));
                    role.setRoleName(resultSet.getString("roleName"));
                    roleList.add(role);
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }finally {
                BaseDao.closeResource(null,resultSet,preparedStatement);
            }
        }

        return roleList;
    }
}
