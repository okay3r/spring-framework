package top.okay3r.spring.framework.beandefinition;

/**
 * Created By okay3r.top
 * Author: okay3r
 * Date: 2020/2/5
 * Time: 12:23
 * Explain:
 */
public class TypedStringValue {
    private String value;
    private Class<?> targetType;

    public TypedStringValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Class<?> getTargetType() {
        return targetType;
    }

    public void setTargetType(Class<?> targetType) {
        this.targetType = targetType;
    }
}
