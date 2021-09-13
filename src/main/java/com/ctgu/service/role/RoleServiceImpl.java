package com.ctgu.service.role;

import com.ctgu.dao.BaseDao;
import com.ctgu.dao.role.RoleDao;
import com.ctgu.dao.role.RoleDaoImpl;
import com.ctgu.pojo.Role;
import org.junit.jupiter.api.Test;


import java.sql.Connection;
import java.util.List;

public class RoleServiceImpl implements RoleService{

    //业务层调用dao层,引入dao层
    private RoleDao roleDao;
    public RoleServiceImpl(){
        roleDao = new RoleDaoImpl();
    }

    //获取所有角色信息
    @Override
    public List<Role> getRoles() {
        Connection connection = null;
        connection = BaseDao.getConnection();
        List<Role> roles = roleDao.getRoles(connection);
        BaseDao.closeResource(connection,null,null);
        return roles;
    }

    @Test
    public void test(){
        RoleServiceImpl roleService = new RoleServiceImpl();
        List<Role> roles = roleService.getRoles();
        for(Role role : roles){
            System.out.println(role.getId()+" "+ role.getRoleName());
        }
    }

}
