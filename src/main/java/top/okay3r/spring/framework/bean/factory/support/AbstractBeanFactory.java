package top.okay3r.spring.framework.bean.factory.support;

import top.okay3r.spring.framework.bean.factory.BeanFactory;
import top.okay3r.spring.framework.bean.registry.support.DefaultSingletonBeanRegistry;
import top.okay3r.spring.framework.beandefinition.BeanDefinition;

/**
 * Created By okay3r.top
 * Author: okay3r
 * Date: 2020/2/7
 * Time: 11:35
 * Explain:
 */
public abstract class AbstractBeanFactory extends DefaultSingletonBeanRegistry implements BeanFactory {
    @Override
    public Object getBean(String name) {
        return getBean(null, name);
    }

    @Override
    public Object getBean(Class type, String name) {
        return doGetBean(type, name);
    }

    public Object doGetBean(Class type, String name) {
        Object singleton = getSingleton(name);
        if (singleton != null) {
            return singleton;
        }
        BeanDefinition beanDefinition = getBeanDefinition(name);
        if (beanDefinition == null) {
            return null;
        }
        singleton = createBean(beanDefinition);
        if (beanDefinition.isSingleton()) {
            this.addSingleton(name, singleton);
        }
        return singleton;
    }

    public abstract Object createBean(BeanDefinition beanDefinition);

    public abstract BeanDefinition getBeanDefinition(String name);
}
