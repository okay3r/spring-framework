package top.okay3r.spring.framework.v1;

import org.junit.Before;
import org.junit.Test;
import top.okay3r.spring.framework.pjc.pojo.User;
import top.okay3r.spring.framework.pjc.service.UserService;

import java.util.List;

import static org.junit.Assert.*;

public class BeanFactoryV1Test {
    private BeanFactoryV1 beanFactoryV1;

    @Before
    public void setUp() throws Exception {
        beanFactoryV1 = new BeanFactoryV1("beans.xml");
    }

    @Test
    public void test() {
        UserService userService = (UserService) beanFactoryV1.getBean("userService");
        User user = new User();
        user.setUsername("李逵");
        List<User> userList = userService.queryUserList(user);
        for (User u : userList) {
            System.out.println(u);
        }
    }

}