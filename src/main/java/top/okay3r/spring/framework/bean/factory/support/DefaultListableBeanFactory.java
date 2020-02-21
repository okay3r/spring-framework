package top.okay3r.spring.framework.bean.factory.support;


import top.okay3r.spring.framework.bean.factory.ListableBeanFactory;
import top.okay3r.spring.framework.bean.registry.BeanDefinitionRegistry;
import top.okay3r.spring.framework.beandefinition.BeanDefinition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created By okay3r.top
 * Author: okay3r
 * Date: 2020/2/7
 * Time: 11:50
 * Explain:
 */
public class DefaultListableBeanFactory extends AbstractAutowireCapableBeanFactory implements BeanDefinitionRegistry,
        ListableBeanFactory {
    private Map<String, BeanDefinition> beanDefinitions = new HashMap<>();

    @Override
    public BeanDefinition getBeanDefinition(String name) {
        return this.beanDefinitions.get(name);
    }

    @Override
    public void registerBeanDefinition(String name, BeanDefinition beanDefinition) {
        this.beanDefinitions.put(name, beanDefinition);
    }

    @Override
    public List<BeanDefinition> getBeanDefinitions() {
        List<BeanDefinition> beanDefinitionList = new ArrayList<>();
        for (BeanDefinition beanDefinition : beanDefinitions.values()) {
            beanDefinitionList.add(beanDefinition);
        }
        return beanDefinitionList;
    }

    //根据类型获取bean，获取到的bean为该类型及其子类/实现类
    @Override
    public <T> List<T> getBeansByType(Class<T> clazz) {
        List<T> res = new ArrayList<>();
        //遍历所有的BeanDefinition
        for (BeanDefinition bd : beanDefinitions.values()) {
            String clazzName = bd.getClazzName();
            //反射获取当前遍历到的类型
            Class<?> type = resolveClassName(clazzName);
            //判断是否是其子类、实现类
            if (clazz.isAssignableFrom(type)) {
                //获取该bean
                T bean = (T) getBean(bd.getBeanName());
                res.add(bean);
            }
        }
        return res;
    }

    private Class<?> resolveClassName(String clazzName) {
        try {
            Class<?> type = Class.forName(clazzName);
            return type;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}