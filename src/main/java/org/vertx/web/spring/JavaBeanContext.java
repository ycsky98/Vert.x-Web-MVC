package org.vertx.web.spring;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yangcong
 *
 * 加载bean容器
 */
public class JavaBeanContext extends ClassPathXmlApplicationContext {

    public JavaBeanContext(String ... configLocations){
        super(configLocations);
        super.start();
    }

    /**
     * 获取带某个注解的bean
     *
     * @param type method | class
     * @return
     */
    public Map<String, Object> getBeansWithAnnotation(Class cls, String type){
        //处理方法上的注解
        if ("method".equals(type)){
            return super.getBeansWithAnnotation(cls);
        } else if ("class".equals(type)) {
            return super.getBeansWithAnnotation(cls);
        }
        return new HashMap<>();
    }
}
