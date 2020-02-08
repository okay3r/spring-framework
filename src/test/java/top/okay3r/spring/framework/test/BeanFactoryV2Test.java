package top.okay3r.spring.framework.test;

import org.junit.Test;
import top.okay3r.spring.framework.bean.factory.support.DefaultListableBeanFactory;
import top.okay3r.spring.framework.reader.XmlBeanDefinitionReader;
import top.okay3r.spring.framework.resource.ClasspathResource;
import top.okay3r.spring.framework.resource.Resource;
import top.okay3r.spring.framework.test.pjc.pojo.User;
import top.okay3r.spring.framework.test.pjc.service.UserService;

import java.io.InputStream;
import java.util.List;

/**
 * Created By okay3r.top
 * Author: okay3r
 * Date: 2020/2/7
 * Time: 12:19
 * Explain:
 */
public class BeanFactoryV2Test {
    @Test
    public void test() {
        Resource resource = new ClasspathResource("beans.xml");
        InputStream inputStream = resource.getResource();

        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();

        XmlBeanDefinitionReader xmlBeanDefinitionReader = new XmlBeanDefinitionReader(beanFactory);
        xmlBeanDefinitionReader.loadBeanDefinitions(inputStream);
        
        UserService userService = (UserService) beanFactory.getBean("userService");
        User user = new User();
        user.setUsername("李逵");
        List<User> userList = userService.queryUserList(user);
        for (User u : userList) {
            System.out.println(u);
        }
    }
}
