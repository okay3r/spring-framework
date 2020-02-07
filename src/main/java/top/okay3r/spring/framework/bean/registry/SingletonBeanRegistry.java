package top.okay3r.spring.framework.bean.registry;

/**
 * Created By okay3r.top
 * Author: okay3r
 * Date: 2020/2/7
 * Time: 11:37
 * Explain:
 */
public interface SingletonBeanRegistry {
    Object getSingleton(String name);

    void addSingleton(String name, Object singleton);
}
