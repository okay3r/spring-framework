package top.okay3r.spring.framework.test;

import org.junit.Before;
import org.junit.Test;
import top.okay3r.spring.framework.v1.BeanFactoryV1;
import top.okay3r.spring.framework.test.pjc.pojo.User;
import top.okay3r.spring.framework.test.pjc.service.UserService;

import java.util.List;

public class BeanFactoryV1Test {
    /*private BeanFactoryV1 beanFactoryV1;

    @Before
    public void setUp() throws Exception {
        beanFactoryV1 = new BeanFactoryV1("beans.xml");
    }

    @Test
    public void test() {
        UserService userService = (UserService) beanFactoryV1.getBean("userService");
        User user = new User();
        user.setUsername("张飞");
        List<User> userList = userService.queryUserList(user);
        for (User u : userList) {
            System.out.println(u);
        }
    }*/

}