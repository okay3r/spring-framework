package top.okay3r.spring.framework.bean.registry.support;

import top.okay3r.spring.framework.bean.registry.SingletonBeanRegistry;

import java.util.HashMap;
import java.util.Map;

/**
 * Created By okay3r.top
 * Author: okay3r
 * Date: 2020/2/7
 * Time: 11:39
 * Explain:
 */
public class DefaultSingletonBeanRegistry implements SingletonBeanRegistry {
    private Map<String, Object> singletonObjects = new HashMap<>();

    @Override
    public Object getSingleton(String name) {
        return this.singletonObjects.get(name);
    }

    @Override
    public void addSingleton(String name, Object singleton) {
        this.singletonObjects.put(name, singleton);
    }
}
