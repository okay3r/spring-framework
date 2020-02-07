package top.okay3r.spring.framework.resource;

import java.io.InputStream;

/**
 * Created By okay3r.top
 * Author: okay3r
 * Date: 2020/2/7
 * Time: 12:12
 * Explain:
 */
public class ClasspathResource implements Resource {
    private String location;

    public ClasspathResource(String location) {
        this.location = location;
    }

    @Override
    public InputStream getResource() {
        return this.getClass().getClassLoader().getResourceAsStream(location);
    }
}
