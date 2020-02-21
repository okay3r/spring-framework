package top.okay3r.spring.framework.bean.factory.support;

import top.okay3r.spring.framework.bean.aware.Aware;
import top.okay3r.spring.framework.bean.aware.BeanFactoryAware;
import top.okay3r.spring.framework.bean.factory.AutowireCapableBeanFactory;
import top.okay3r.spring.framework.beandefinition.BeanDefinition;
import top.okay3r.spring.framework.beandefinition.PropertyValue;
import top.okay3r.spring.framework.beandefinition.RuntimeBeanReference;
import top.okay3r.spring.framework.beandefinition.TypedStringValue;
import top.okay3r.spring.framework.utils.ReflectUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Created By okay3r.top
 * Author: okay3r
 * Date: 2020/2/7
 * Time: 11:47
 * Explain:
 */
public abstract class AbstractAutowireCapableBeanFactory extends AbstractBeanFactory implements AutowireCapableBeanFactory {
    public Object createBean(BeanDefinition beanDefinition) {
        //首先创建bean的实例
        Object bean = ReflectUtils.createInstance(beanDefinition.getClazzName());
        if (bean == null) {
            return null;
        }

        //将属性值等信息填充到bean中，在此之前该bean是空的，即没有设置任何的属性的值等
        populateBean(bean, beanDefinition);
        initBean(bean, beanDefinition);
        return bean;
    }

    private void initBean(Object bean, BeanDefinition beanDefinition) {
        // TODO Aware接口会在此时被处理
        if (bean instanceof Aware) {
            if (bean instanceof BeanFactoryAware) {
                ((BeanFactoryAware) bean).setBeanFactory((DefaultListableBeanFactory) this);
            }
            //.....
        }

        initMethod(bean, beanDefinition);
    }

    //通过反射调用bean的初始化方法
    private void initMethod(Object bean, BeanDefinition beanDefinition) {
        String initMethod = beanDefinition.getInitMethod();
        ReflectUtils.initMethod(bean, initMethod);
    }

    //填充bean的属性值
    private void populateBean(Object bean, BeanDefinition beanDefinition) {
        //获取该bean的属性集合
        List<PropertyValue> propertyValues = beanDefinition.getPropertyValues();
        for (PropertyValue propertyValue : propertyValues) {
            //获取属性名称
            String name = propertyValue.getName();
            //获取属性值，可能为直接value类型，可能为ref引用类型指向其他bean
            Object value = propertyValue.getValue();
            //valueToUse为最终可以使用的property值
            Object valueToUse = null;
            if (value instanceof TypedStringValue) {
                //将value转换为TypedStringValue类型
                TypedStringValue typedStringValue = (TypedStringValue) value;
                //获取该值得字符串形式，例如：如果为int类型的7，则stringValue为"7"
                String stringValue = typedStringValue.getValue();
                //获取该值的类型
                Class<?> targetType = typedStringValue.getTargetType();
                //对类型进行转换
                if (targetType == String.class) {
                    valueToUse = stringValue;
                } else if (targetType == Integer.class) {
                    valueToUse = Integer.parseInt(stringValue);
                } else {
                    //    ......
                }
            } else if (value instanceof RuntimeBeanReference) {
                RuntimeBeanReference runtimeBeanReference = new RuntimeBeanReference(name);
                String ref = runtimeBeanReference.getRef();
                //从容器中获取该bean
                valueToUse = getBean(ref);
            }
            //将属性值设置到bean中
            ReflectUtils.setProperty(bean, name, valueToUse);
        }
    }
}
