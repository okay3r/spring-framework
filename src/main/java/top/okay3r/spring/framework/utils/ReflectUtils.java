package top.okay3r.spring.framework.utils;

import top.okay3r.spring.framework.beandefinition.BeanDefinition;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created By okay3r.top
 * Author: okay3r
 * Date: 2020/2/7
 * Time: 12:25
 * Explain:
 */
public class ReflectUtils {
    //使用反射创建bean的实例，但创建完成的bean没有人属性值
    public static Object createInstance(String clazzName) {
        Object bean = null;
        try {
            bean = Class.forName(clazzName).newInstance();
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
        return bean;
    }

    //将属性值设置到bean中
    public static void setProperty(Object bean, String name, Object valueToUse) {
        //获取bean的类型
        Class<?> clazz = bean.getClass();
        try {
            //通过反射将值设置到bean中
            Field field = clazz.getDeclaredField(name);
            field.setAccessible(true);
            field.set(bean, valueToUse);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    //反射调用方法
    public static void initMethod(Object bean, String initMethod) {
        if (initMethod == null || initMethod.equals("")) {
            return;
        }
        Class<?> clazz = bean.getClass();
        try {
            Method method = clazz.getDeclaredMethod(initMethod);
            method.setAccessible(true);
            method.invoke(bean);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public static Class<?> getTypeByFieldName(String clazzName, String name) {
        //bean的所属类
        Class<?> beanClass = null;
        try {
            beanClass = Class.forName(clazzName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Field field = null;
        try {
            //获取在bean的所属类名称为name的属性
            field = beanClass.getDeclaredField(name);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        //返回属性的类型
        return field.getType();
    }
}
