package com.ctgu.dao.role;

import com.ctgu.pojo.Role;

import java.sql.Connection;
import java.util.List;

public interface RoleDao {
    //获取所有角色信息
    public List<Role> getRoles(Connection connection);

}
