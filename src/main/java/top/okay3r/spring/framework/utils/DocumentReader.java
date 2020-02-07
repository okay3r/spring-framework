package top.okay3r.spring.framework.utils;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

import java.io.InputStream;

/**
 * Created By okay3r.top
 * Author: okay3r
 * Date: 2020/2/7
 * Time: 11:58
 * Explain:
 */
public class DocumentReader {
    public static Document createDocument(InputStream inputStream) {
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
}
