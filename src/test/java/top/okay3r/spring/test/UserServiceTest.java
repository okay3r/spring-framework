package top.okay3r.spring.test;

import org.junit.Test;
import top.okay3r.spring.framework.pjc.dao.UserDaoImpl;
import top.okay3r.spring.framework.pjc.pojo.User;
import top.okay3r.spring.framework.pjc.service.UserServiceImpl;

import java.util.List;

public class UserServiceTest {

    @Test
    public void queryUserList() {
        UserDaoImpl userDao = new UserDaoImpl();
        userDao.init();
        UserServiceImpl userService = new UserServiceImpl();
        userService.setUserDao(userDao);
        List<User> userList = userService.queryUserList("赵云");
        for (User user : userList) {
            System.out.println(user);
        }
    }
}