package top.okay3r.spring.framework.bean.registry;

import top.okay3r.spring.framework.beandefinition.BeanDefinition;

import java.util.List;

/**
 * Created By okay3r.top
 * Author: okay3r
 * Date: 2020/2/7
 * Time: 11:51
 * Explain:
 */
public interface BeanDefinitionRegistry {
    BeanDefinition getBeanDefinition(String name);

    void registerBeanDefinition(String name, BeanDefinition beanDefinition);

    List<BeanDefinition> getBeanDefinitions();

}
