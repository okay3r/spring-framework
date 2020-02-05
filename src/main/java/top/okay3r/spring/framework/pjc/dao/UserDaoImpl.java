package top.okay3r.spring.framework.pjc.dao;

import org.apache.commons.dbcp.BasicDataSource;
import top.okay3r.spring.framework.pjc.pojo.User;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created By okay3r.top
 * Author: okay3r
 * Date: 2020/2/5
 * Time: 00:53
 * Explain:
 */
public class UserDaoImpl implements UserDao {
    private DataSource dataSource;

    private Properties properties = new Properties();

    public void init() {
        String dbResource = "db.properties";
        String sqlmappingResource = "sqlmapping.properties";
        loadProperties(dbResource);
        loadProperties(sqlmappingResource);
        BasicDataSource basicDataSource = new BasicDataSource();
        basicDataSource.setDriverClassName(properties.getProperty("db.datasource.driver"));
        basicDataSource.setUrl(properties.getProperty("db.datasource.url"));
        basicDataSource.setUsername(properties.getProperty("db.datasource.username"));
        basicDataSource.setPassword(properties.getProperty("db.datasource.password"));
        dataSource = basicDataSource;
    }

    private void loadProperties(String resource) {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(resource);
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<User> queryUserList(String sqlId, Object param) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = dataSource.getConnection();
            String sql = properties.getProperty("db.sql." + sqlId);
            preparedStatement = connection.prepareStatement(sql);
            if (param instanceof Integer) {
                preparedStatement.setObject(1, ((Integer) param).intValue());
            } else if (param instanceof String) {
                preparedStatement.setObject(1, param.toString());
            } else {
                Class<?> clazz = param.getClass();
                String paramnames = properties.getProperty("db.sql." + sqlId + ".paramnames");
                String[] paramArr = paramnames.split(",");
                for (int i = 0; i < paramArr.length; i++) {
                    String name = paramArr[i];
                    Field field = clazz.getDeclaredField(name);
                    field.setAccessible(true);
                    Object o = field.get(param);
                    preparedStatement.setObject(i + 1, o);
                }
            }
            resultSet = preparedStatement.executeQuery();
            List resList = new ArrayList<>();
            String resultClassName = properties.getProperty("db.sql." + sqlId + ".resultclassname");
            Class<?> resClass = Class.forName(resultClassName);
            while (resultSet.next()) {
                Object res = resClass.newInstance();
                ResultSetMetaData metaData = resultSet.getMetaData();
                int columnCount = metaData.getColumnCount();
                for (int i = 0; i < columnCount; i++) {
                    String columnName = metaData.getColumnName(i + 1);

                    Field field = resClass.getDeclaredField(columnName);
                    field.setAccessible(true);

                    field.set(res, resultSet.getObject(i + 1));
                }

                resList.add(res);
            }
            return resList;
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return null;
    }
}
