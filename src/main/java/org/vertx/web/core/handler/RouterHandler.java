package org.vertx.web.core.handler;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;
import org.vertx.web.core.annotation.GetMapping;
import org.vertx.web.core.annotation.Param;
import org.vertx.web.core.annotation.PostMapping;
import org.vertx.web.core.annotation.RequestBody;
import org.vertx.web.json.JSONPrint;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;
import java.util.Objects;

/**
 * @author yangcong
 *
 * 路由映射器
 */
public class RouterHandler implements Handler<RoutingContext> {

    /**
     * 目标源
     */
    private Object targ;

    /**
     * 执行的方法
     */
    private Method method;

    public RouterHandler(Object targ, Method method){
        this.targ = targ;
        this.method = method;
    }

    /**
     * Something has happened, so handle it.
     *
     * @param event the event to handle
     */
    @Override
    public void handle(RoutingContext event) {
        HttpServerRequest httpServerRequest = event.request();
        HttpServerResponse httpServerResponse = event.response();
        if (this.method.isAnnotationPresent(PostMapping.class) &&
                httpServerRequest.method().name().toLowerCase().equals("get")){
            event.end("Request Method ERROR");
            return;
        } else if (this.method.isAnnotationPresent(GetMapping.class) &&
                httpServerRequest.method().name().toLowerCase().equals("post")) {
            event.end("Request Method ERROR");
            return;
        }

        // 拿到json数据
        String requestData = event.getBodyAsString("UTF-8");
        if (Objects.isNull(requestData) || "".equals(requestData)){
            requestData = "{}";
        }

        Parameter[] parameters = this.findParameter();

        Object[] objects = new Object[parameters.length];

        Object result = null;
        try {
            int count = 0;
            for (Parameter parameter : parameters) {
                if (parameter.getType().equals(httpServerRequest.getClass())){
                    objects[count] = httpServerRequest;
                } else if (parameter.getType().equals(httpServerResponse.getClass())) {
                    objects[count] = httpServerResponse;
                }else if (parameter.isAnnotationPresent(RequestBody.class)){//RequestBody 获取请求体,并转换
                    objects[count] = JSONPrint.parseJSON(requestData, parameter.getType());
                } else if (parameter.isAnnotationPresent(Param.class)) {//取出里面的某一项
                    objects[count] = JSONPrint.parseJSON(
                            JSONPrint.toJSON(
                                    JSONPrint.parseJSON(requestData, Map.class).get(parameter.getName())
                            ),
                            parameter.getType()
                    );
                } else {
                    objects[count] = JSONPrint.parseJSON(requestData, parameter.getType());
                }
                count++;
            }

            result = this.method.invoke(this.targ, objects);
            //没错误的话直接输出结果集
            event.end(JSONPrint.toJSON(result));
        } catch (Exception e){
            event.end(e.getMessage());
        }
    }

    /**
     * 获取方法参数类型
     *
     * @return
     */
    private Parameter[] findParameter(){
        return this.method.getParameters();
    }

}
