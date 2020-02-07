package top.okay3r.spring.framework.bean.factory;

/**
 * Created By okay3r.top
 * Author: okay3r
 * Date: 2020/2/7
 * Time: 11:32
 * Explain:
 */
public interface BeanFactory {
    Object getBean(String name);

    Object getBean(Class type, String name);
}
