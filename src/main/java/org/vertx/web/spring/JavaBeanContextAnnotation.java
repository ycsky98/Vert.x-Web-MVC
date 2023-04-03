package org.vertx.web.spring;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yangcong
 *
 * spring注解扫描
 */
public class JavaBeanContextAnnotation extends AnnotationConfigApplicationContext {

    private Class<?>[] scanner;

    public JavaBeanContextAnnotation(Class ... cs){
        super(cs);
        this.scanner = cs;
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
