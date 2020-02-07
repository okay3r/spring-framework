package top.okay3r.spring.framework.beandefinition;

/**
 * Created By okay3r.top
 * Author: okay3r
 * Date: 2020/2/5
 * Time: 12:20
 * Explain:
 */
public class PropertyValue {
    private String name;
    private Object value;

    public PropertyValue(String name, Object value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
