package top.okay3r.spring.framework.v1;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import top.okay3r.spring.framework.beandefinition.BeanDefinition;
import top.okay3r.spring.framework.beandefinition.PropertyValue;
import top.okay3r.spring.framework.beandefinition.RuntimeBeanReference;
import top.okay3r.spring.framework.beandefinition.TypedStringValue;

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
    //用于存储单例bean实例
    private Map<String, Object> singletonObjects = new HashMap<>();

    //用于存储BeanDefinition
    private Map<String, BeanDefinition> beanDefinitions = new HashMap<>();

    //创建BeanFactory时首先将xml文件中的bean信息读取到beanDefinitions中
    public BeanFactoryV1(String resource) {
        //加载并注册xml中所有的BeanDefinition
        loadAndRegisterBeanDefinitions(resource);
    }

    private void loadAndRegisterBeanDefinitions(String resource) {
        //获取xml文件的输入流
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(resource);
        //通过输入流获取Document
        Document document = createDocument(inputStream);
        //注册xml中的标签信息
        registerBeanDefinitions(document.getRootElement());
    }

    private void registerBeanDefinitions(Element rootElement) {
        List<Element> elements = rootElement.elements();
        for (Element element : elements) {
            //解析bean标签
            if ("bean".equals(element.getName())) {
                parseBeanElement(element);
            } else {
                // TODO 解析自定义标签
                // parseCustomElement(element);
            }
        }
    }

    //解析bean标签
    private void parseBeanElement(Element beanElement) {
        //获取标签中的信息
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

        //如果没有填写scope的值，则默认为singleton
        scope = scope != null && !scope.equals("") ? scope : "singleton";

        //beanName为每个bean的唯一标识，即singletonObjects中的key
        //如果id、name都用，则默认使用id作为beanName
        String beanName = id == null ? name : id;
        //如果没有指定id、name，则beanName为类名
        beanName = beanName != null && !beanName.equals("") ? beanName : clazzType.getSimpleName();

        //每个BeanDefinition都必须有beanName和clazzName，scope和initMethod并不是必须的
        BeanDefinition beanDefinition = new BeanDefinition(beanName, clazzName);
        //将scope、initMethod设置到beanDefinition中
        beanDefinition.setInitMethod(initMethod);
        beanDefinition.setScope(scope);
        List<Element> propertyElements = beanElement.elements();
        for (Element propertyElement : propertyElements) {
            //解析property标签
            parsePropertyElement(propertyElement, beanDefinition);
        }
        //将新解析完成的beanDefinition存放到BeanDefinitions中
        this.beanDefinitions.put(beanName, beanDefinition);
    }

    //解析property标签
    private void parsePropertyElement(Element propertyElement, BeanDefinition beanDefinition) {
        if (propertyElement == null) {
            return;
        }
        //获取property标签中的信息
        String name = propertyElement.attributeValue("name");
        String value = propertyElement.attributeValue("value");
        String ref = propertyElement.attributeValue("ref");
        //如果value、ref同时存在，则直接返回
        if (value != null && !value.equals("") && ref != null && !ref.equals("")) {
            return;
        }
        PropertyValue propertyValue = null;
        //如果property中的放的是value类型，即直接的值，而不是引用其他的bean
        if (value != null && !value.equals("")) {
            TypedStringValue typedStringValue = new TypedStringValue(value);
            //获取该属性在bean中的类型
            Class<?> targetType = getTypeByFieldName(beanDefinition.getClazzName(), name);
            //设置属性
            typedStringValue.setTargetType(targetType);
            propertyValue = new PropertyValue(name, typedStringValue);
        } else if (ref != null && !ref.equals("")) {
            //如果是引用类型，指向了其他的bean
            RuntimeBeanReference runtimeBeanReference = new RuntimeBeanReference(ref);
            propertyValue = new PropertyValue(name, runtimeBeanReference);
        } else {
            return;
        }
        //将该property保存到beanDefinition中
        beanDefinition.addProperty(propertyValue);
    }

    private Class<?> getTypeByFieldName(String clazzName, String name) {
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

    //从容器中获取bean
    public Object getBean(String beanName) {
        //首先从singletonObjects中获取
        Object bean = this.singletonObjects.get(beanName);
        //如果没有，则根据对应的beanDefinition创建该bean
        if (bean == null) {
            //从beanDefinitions中根据beanName获取beanDefinition
            BeanDefinition beanDefinition = this.beanDefinitions.get(beanName);
            if (beanDefinition == null) {
                return null;
            }
            //根据beanDefinition创建bean
            bean = createBean(beanDefinition);
        }
        return bean;
    }

    private Object createBean(BeanDefinition beanDefinition) {
        //首先创建bean的实例
        Object bean = createInstance(beanDefinition);
        if (bean == null) {
            return null;
        }

        //将属性值等信息填充到bean中，在此之前该bean是空的，即没有设置任何的属性的值等
        populateBean(bean, beanDefinition);
        initBean(bean, beanDefinition);
        return bean;
    }

    //通过反射调用bean的初始化方法
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
            setProperty(bean, name, valueToUse);
        }
    }

    //将属性值设置到bean中
    private void setProperty(Object bean, String name, Object valueToUse) {
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

    //使用反射创建bean的实例，但创建完成的bean没有人属性值
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
