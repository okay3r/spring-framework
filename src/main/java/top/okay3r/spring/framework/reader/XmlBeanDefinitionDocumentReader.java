package top.okay3r.spring.framework.reader;

import org.dom4j.Element;
import top.okay3r.spring.framework.bean.registry.BeanDefinitionRegistry;
import top.okay3r.spring.framework.beandefinition.BeanDefinition;
import top.okay3r.spring.framework.beandefinition.PropertyValue;
import top.okay3r.spring.framework.beandefinition.RuntimeBeanReference;
import top.okay3r.spring.framework.beandefinition.TypedStringValue;
import top.okay3r.spring.framework.utils.ReflectUtils;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Created By okay3r.top
 * Author: okay3r
 * Date: 2020/2/7
 * Time: 12:04
 * Explain:
 */
public class XmlBeanDefinitionDocumentReader {
    private BeanDefinitionRegistry beanDefinitionRegistry;

    public XmlBeanDefinitionDocumentReader(BeanDefinitionRegistry beanDefinitionRegistry) {
        this.beanDefinitionRegistry = beanDefinitionRegistry;
    }

    public void registerBeanDefinitions(Element rootElement) {
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
        this.beanDefinitionRegistry.registerBeanDefinition(beanName, beanDefinition);
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
            Class<?> targetType = ReflectUtils.getTypeByFieldName(beanDefinition.getClazzName(), name);
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

}
