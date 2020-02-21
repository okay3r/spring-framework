package top.okay3r.spring.framework.bean.factory;

import java.util.List;

/**
 * Created By okay3r.top
 * Author: okay3r
 * Date: 2020/2/7
 * Time: 11:50
 * Explain:
 */
public interface ListableBeanFactory {
    <T> List<T> getBeansByType(Class<T> clazz);
}
