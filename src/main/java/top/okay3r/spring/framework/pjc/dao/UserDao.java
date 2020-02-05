package top.okay3r.spring.framework.pjc.dao;

import top.okay3r.spring.framework.pjc.pojo.User;

import java.util.List;

/**
 * Created By okay3r.top
 * Author: okay3r
 * Date: 2020/2/5
 * Time: 00:52
 * Explain:
 */
public interface UserDao {
    List<User> queryUserList(String sqlId, Object param);
}
