package top.okay3r.spring.framework.bean.factory.support;


import top.okay3r.spring.framework.bean.registry.BeanDefinitionRegistry;
import top.okay3r.spring.framework.beandefinition.BeanDefinition;

import java.util.HashMap;
import java.util.Map;

/**
 * Created By okay3r.top
 * Author: okay3r
 * Date: 2020/2/7
 * Time: 11:50
 * Explain:
 */
public class DefaultListableBeanFactory extends AbstractAutowireCapableBeanFactory implements BeanDefinitionRegistry {
    private Map<String, BeanDefinition> beanDefinitions = new HashMap<>();

    @Override
    public BeanDefinition getBeanDefinition(String name) {
        return this.beanDefinitions.get(name);
    }

    @Override
    public void registerBeanDefinition(String name, BeanDefinition beanDefinition) {
        this.beanDefinitions.put(name, beanDefinition);
    }
}
