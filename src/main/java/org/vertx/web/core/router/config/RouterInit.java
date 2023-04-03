package org.vertx.web.core.router.config;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.AbstractRefreshableConfigApplicationContext;
import org.vertx.web.core.annotation.Blocking;
import org.vertx.web.core.annotation.GetMapping;
import org.vertx.web.core.annotation.PostMapping;
import org.vertx.web.core.annotation.RestController;
import org.vertx.web.core.handler.RouterHandler;
import org.vertx.web.core.router.RouterCreate;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

/**
 * @author yangcong
 *
 * 路由初始化
 */
public class RouterInit {

    private AbstractApplicationContext abstractApplicationContext;

    private Map<String, Object> beans;

    private Vertx vertx;

    private RouterCreate routerCreate;

    public RouterInit(AbstractApplicationContext abstractApplicationContext, Vertx vertx){
        this.abstractApplicationContext = abstractApplicationContext;
        this.abstractApplicationContext.start();//启动自动装配
        this.beans = this.abstractApplicationContext.getBeansWithAnnotation(RestController.class);
        this.vertx = vertx;
        this.routerCreate = new RouterCreate(vertx);
        this.init();
    }

    /**
     * 初始化
     */
    private void init(){
        Collection<Object> beans = this.beans();
        String prefix;//前置url
        Method[] methods;
        for (Object obj : beans) {
            prefix = this.prefixUrl(obj);
            methods = obj.getClass().getDeclaredMethods();
            for (int i = 0; i < methods.length; i++) {
                //无论是post还是get,都进行后缀拼接
                if (methods[i].isAnnotationPresent(PostMapping.class)){//post
                    prefix+=this.methodUrl(methods[i], PostMapping.class);
                } else if (methods[i].isAnnotationPresent(GetMapping.class)) {//get
                    prefix+=this.methodUrl(methods[i], GetMapping.class);
                }
                //需要去判定方法是否式耗时方法(由注解告诉框架)选择的路由是耗时路由还是异步路由
                if (methods[i].isAnnotationPresent(Blocking.class)){
                    this.routerCreate.urlBlocking(prefix, new RouterHandler(obj, methods[i]));
                }else {
                    this.routerCreate.url(prefix, new RouterHandler(obj, methods[i]));
                }
                prefix = this.prefixUrl(obj);
            }
        }
    }

    /**
     * 获取当前类
     * @return
     */
    private Collection<Object> beans(){
        return this.beans.values();
    }

    /**
     * 获取前置url (RestController)
     * @param object
     * @return
     */
    private String prefixUrl(Object object){
        RestController restController = object.getClass().getAnnotation(RestController.class);
        return restController.url();
    }

    /**
     * 获取方法上注解的url
     * @param method
     * @param annotation
     * @return
     */
    private String methodUrl(Method method, Class annotation){
        if (annotation.equals(PostMapping.class)){
            return method.getAnnotation(PostMapping.class).value();
        } else if (annotation.equals(GetMapping.class)) {
            return method.getAnnotation(GetMapping.class).value();
        }
        throw new RuntimeException("Annotation not find");
    }

    /**
     * 获取当前路由
     *
     * @return
     */
    public Router getRouter(){
        return this.routerCreate.getRouter();
    }
}
