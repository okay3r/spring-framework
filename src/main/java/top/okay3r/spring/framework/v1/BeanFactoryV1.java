package top.okay3r.spring.framework.v1;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.junit.Test;
import top.okay3r.spring.framework.ioc.BeanDefinition;
import top.okay3r.spring.framework.ioc.PropertyValue;
import top.okay3r.spring.framework.ioc.RuntimeBeanReference;
import top.okay3r.spring.framework.ioc.TypedStringValue;
import top.okay3r.spring.framework.pjc.pojo.User;
import top.okay3r.spring.framework.pjc.service.UserService;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created By okay3r.top
 * Author: okay3r
 * Date: 2020/2/5
 * Time: 12:26
 * Explain:
 */
public class BeanFactoryV1 {
    private Map<String, Object> singletonObjects = new HashMap<>();
    private Map<String, BeanDefinition> beanDefinitions = new HashMap<>();

    public BeanFactoryV1(String resource) {
        loadAndRegisterBeanDefinitions(resource);
    }

    private void loadAndRegisterBeanDefinitions(String resource) {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(resource);
        Document document = createDocument(inputStream);
        registerBeanDefinitions(document.getRootElement());
    }

    private void registerBeanDefinitions(Element rootElement) {
        List<Element> elements = rootElement.elements();
        for (Element element : elements) {
            if ("bean".equals(element.getName())) {
                parseBeanElement(element);
            } else {
                // TODO 解析自定义标签
                // parseCustomElement(element);
            }
        }
    }

    private void parseBeanElement(Element beanElement) {
        String id = beanElement.attributeValue("id");
        String name = beanElement.attributeValue("name");
        String clazzName = beanElement.attributeValue("class");
        Class<?> clazzType = null;
        if (clazzName == null || clazzName.equals("")) {
            return;
        }
        try {
            clazzType = Class.forName(clazzName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        String initMethod = beanElement.attributeValue("init-method");
        String scope = beanElement.attributeValue("scope");
        scope = scope != null && !scope.equals("") ? scope : "singleton";
        String beanName = id == null ? name : id;
        beanName = beanName != null && !beanName.equals("") ? beanName : clazzType.getSimpleName();
        BeanDefinition beanDefinition = new BeanDefinition(beanName, clazzName);
        beanDefinition.setInitMethod(initMethod);
        beanDefinition.setScope(scope);
        List<Element> propertyElements = beanElement.elements();
        for (Element propertyElement : propertyElements) {
            parsePropertyElement(propertyElement, beanDefinition);
        }
        this.beanDefinitions.put(beanName, beanDefinition);
    }

    private void parsePropertyElement(Element propertyElement, BeanDefinition beanDefinition) {
        if (propertyElement == null) {
            return;
        }
        String name = propertyElement.attributeValue("name");
        String value = propertyElement.attributeValue("value");
        String ref = propertyElement.attributeValue("ref");
        if (value != null && !value.equals("") && ref != null && !ref.equals("")) {
            return;
        }
        PropertyValue propertyValue = null;
        if (value != null && !value.equals("")) {
            TypedStringValue typedStringValue = new TypedStringValue(value);
            Class targetType = getTypeByFieldName(beanDefinition.getClazzName(), name);
            typedStringValue.setTargetType(targetType);
            propertyValue = new PropertyValue(name, typedStringValue);
        } else if (ref != null && !ref.equals("")) {
            RuntimeBeanReference runtimeBeanReference = new RuntimeBeanReference(ref);
            propertyValue = new PropertyValue(name, runtimeBeanReference);
        } else {
            return;
        }
        beanDefinition.addProperty(propertyValue);
    }

    private Class getTypeByFieldName(String clazzName, String name) {
        Class<?> beanClass = null;
        try {
            beanClass = Class.forName(clazzName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Field field = null;
        try {
            field = beanClass.getDeclaredField(name);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return field.getType();
    }

    private Document createDocument(InputStream inputStream) {
        Document document = null;
        try {
            SAXReader reader = new SAXReader();
            document = reader.read(inputStream);
            return document;
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Object getBean(String beanName) {
        Object bean = this.singletonObjects.get(beanName);
        if (bean == null) {
            BeanDefinition beanDefinition = this.beanDefinitions.get(beanName);
            if (beanDefinition == null) {
                return null;
            }
            bean = createBean(beanDefinition);
        }
        return bean;
    }

    private Object createBean(BeanDefinition beanDefinition) {
        Object bean = createInstance(beanDefinition);
        if (bean == null) {
            return null;
        }
        populateBean(bean, beanDefinition);
        initBean(bean, beanDefinition);
        return bean;
    }

    private void initBean(Object bean, BeanDefinition beanDefinition) {
        String initMethod = beanDefinition.getInitMethod();
        if (initMethod == null || initMethod.equals("")) {
            return;
        }
        Class<?> clazz = bean.getClass();
        try {
            Method method = clazz.getDeclaredMethod(initMethod);
            method.invoke(bean);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private void populateBean(Object bean, BeanDefinition beanDefinition) {
        List<PropertyValue> propertyValues = beanDefinition.getPropertyValues();
        for (PropertyValue propertyValue : propertyValues) {
            String name = propertyValue.getName();
            Object value = propertyValue.getValue();
            Object valueToUse = null;
            if (value instanceof TypedStringValue) {
                TypedStringValue typedStringValue = (TypedStringValue) value;
                String stringValue = typedStringValue.getValue();
                Class<?> targetType = typedStringValue.getTargetType();
                if (targetType == String.class) {
                    valueToUse = stringValue;
                } else if (targetType == Integer.class) {
                    valueToUse = Integer.parseInt(stringValue);
                }
            } else if (value instanceof RuntimeBeanReference) {
                RuntimeBeanReference runtimeBeanReference = new RuntimeBeanReference(name);
                String ref = runtimeBeanReference.getRef();
                valueToUse = getBean(ref);
            }
            setProperty(bean, name, valueToUse);
        }
    }

    private void setProperty(Object bean, String name, Object valueToUse) {
        Class<?> clazz = bean.getClass();
        try {
            Field field = clazz.getDeclaredField(name);
            field.setAccessible(true);
            field.set(bean, valueToUse);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private Object createInstance(BeanDefinition beanDefinition) {
        String clazzName = beanDefinition.getClazzName();
        Object bean = null;
        try {
            bean = Class.forName(clazzName).newInstance();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return bean;
    }
}
