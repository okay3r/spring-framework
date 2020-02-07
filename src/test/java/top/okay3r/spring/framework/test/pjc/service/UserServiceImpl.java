package top.okay3r.spring.framework.test.pjc.service;

import top.okay3r.spring.framework.test.pjc.dao.UserDao;
import top.okay3r.spring.framework.test.pjc.pojo.User;

import java.util.List;

/**
 * Created By okay3r.top
 * Author: okay3r
 * Date: 2020/2/5
 * Time: 00:48
 * Explain:
 */
public class UserServiceImpl implements UserService {
    private UserDao userDao;

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public List<User> queryUserList(Object param) {
        return userDao.queryUserList("queryUserById", param);
    }
}
