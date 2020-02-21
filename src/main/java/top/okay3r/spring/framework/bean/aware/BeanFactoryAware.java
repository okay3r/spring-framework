package top.okay3r.spring.framework.bean.aware;

import top.okay3r.spring.framework.bean.factory.support.DefaultListableBeanFactory;

/**
 * Created By okay3r.top
 * Author: okay3r
 * Date: 2020/2/21
 * Time: 12:20
 * Explain:
 */
public interface BeanFactoryAware extends Aware {

    public void setBeanFactory(DefaultListableBeanFactory beanFactory);
}
