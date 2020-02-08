package top.okay3r.spring.framework.reader;

import org.dom4j.Document;
import top.okay3r.spring.framework.bean.registry.BeanDefinitionRegistry;
import top.okay3r.spring.framework.utils.DocumentReader;

import java.io.InputStream;

/**
 * Created By okay3r.top
 * Author: okay3r
 * Date: 2020/2/7
 * Time: 12:03
 * Explain:
 */
public class XmlBeanDefinitionReader {
    private BeanDefinitionRegistry beanDefinitionRegistry;

    public XmlBeanDefinitionReader(BeanDefinitionRegistry beanDefinitionRegistry) {
        this.beanDefinitionRegistry = beanDefinitionRegistry;
    }

    public void loadBeanDefinitions(InputStream inputStream) {
        Document document = DocumentReader.createDocument(inputStream);
        XmlBeanDefinitionDocumentReader xmlBeanDefinitionDocumentReader =
                new XmlBeanDefinitionDocumentReader(beanDefinitionRegistry);
        xmlBeanDefinitionDocumentReader.registerBeanDefinitions(document.getRootElement());
    }
}
