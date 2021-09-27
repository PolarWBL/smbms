package com.ctgu.dao;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

public class BaseDao {
    private static String driver=null;
    private static String url=null;
    private static String username=null;
    private static String password=null;

    //静态代码块,类加载的时候就初始化了
     static {

        Properties properties = new Properties();
         //通过类加载器读取资源
        InputStream inputStream = BaseDao.class.getClassLoader().getResourceAsStream("db.properties");

        try {
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        driver = properties.getProperty("driver");
        url = properties.getProperty("url");
        username = properties.getProperty("user");
        password = properties.getProperty("password");

    }

    //获得数据库的连接
    public static Connection getConnection(){
        Connection connection = null;
        try {
            Class.forName(driver);
            connection = DriverManager.getConnection(url, username, password);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connection;
    }

    //编写查询公共方法
    public static ResultSet execute(Connection connection,PreparedStatement preparedStatement, ResultSet resultSet, String sql, Object[] params ) throws SQLException {
        preparedStatement = connection.prepareStatement(sql);

        for (int i = 0; i < params.length; i++) {
            //setobject 占位符从1开始,但是数组是从0开始
            preparedStatement.setObject(i+1,params[i]);
        }
        resultSet = preparedStatement.executeQuery();
        return resultSet;

    }
    //编写增删改公共方法
    public static int execute(Connection connection,  PreparedStatement preparedStatement,String sql, Object[] params ) throws SQLException {
        preparedStatement = connection.prepareStatement(sql);

        for (int i = 0; i < params.length; i++) {
            //setobject 占位符从1开始,但是数组是从0开始
            preparedStatement.setObject(i+1,params[i]);
        }
        return preparedStatement.executeUpdate();
    }


    //释放资源
    public static boolean closeResource(Connection connection, ResultSet resultSet, PreparedStatement preparedStatement){
         boolean flag = true;
         if (resultSet!=null) {
             try {
                 resultSet.close();
             } catch (SQLException throwables) {
                 throwables.printStackTrace();
                 flag = false;
             }
         }
        if (preparedStatement!=null) {
            try {
                preparedStatement.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                flag = false;
            }
        }
        if (connection!=null) {
            try {
                connection.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                flag = false;
            }
        }

        return flag;

    }

}
