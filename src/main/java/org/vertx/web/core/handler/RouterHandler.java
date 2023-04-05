package org.vertx.web.core.handler;

import com.google.gson.JsonObject;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.FileUpload;
import io.vertx.ext.web.RoutingContext;
import org.vertx.web.core.annotation.*;
import org.vertx.web.core.file.FileUploadDetails;
import org.vertx.web.json.JSONPrint;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

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

    /**
     * Vertx
     */
    private Vertx vertx;

    public RouterHandler(Object targ, Method method, Vertx vertx){
        this.targ = targ;
        this.method = method;
        this.vertx = vertx;
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

        String contentType = httpServerRequest.getHeader("Content-Type");
        if (contentType != null && contentType.contains("multipart/form-data")) {
            // 这是一个文件上传的HTTP请求
            this.isFromRequest(event, httpServerRequest, httpServerResponse);
        } else if (contentType != null && contentType.contains("application/json")){
            // 这是一个json请求
            this.isJsonRequest(event, httpServerRequest, httpServerResponse);
        } else {
            httpServerResponse.end("暂无该请求类型");
        }
    }

    /**
     * 处理请求头为application/json
     * @param event
     * @param httpServerRequest
     * @param httpServerResponse
     */
    private void isJsonRequest(RoutingContext event, HttpServerRequest httpServerRequest,
                               HttpServerResponse httpServerResponse){
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
                }else if (parameter.getType().equals(this.vertx.getClass())) {
                    objects[count] = this.vertx;
                } else if (parameter.isAnnotationPresent(RequestBody.class)){//RequestBody 获取请求体,并转换
                    if (parameter.getType().equals(String.class)){
                        objects[count] = JSONPrint.parseJSON(requestData, JsonObject.class).toString();
                    }else {
                        objects[count] = JSONPrint.parseJSON(requestData, parameter.getType());
                    }
                } else if (parameter.isAnnotationPresent(Param.class)) {//取出里面的某一项
                    String key = parameter.getAnnotation(Param.class).value();

                    Map<String, Object> map = JSONPrint.parseJSON(requestData, Map.class);
                    String v = JSONPrint.toJSON(map.get(key));//拿到对应的value,转成json
                    //如果是字符串,直接赋予
                    if (parameter.getType().equals(String.class)){
                        objects[count] = v;
                    }else {//如果不是字符串,进行转换
                        objects[count] = JSONPrint.parseJSON(v, parameter.getType());
                    }
                } else {
                    String key = parameter.getName();
                    Map<String, Object> map = JSONPrint.parseJSON(requestData, Map.class);
                    String v = JSONPrint.toJSON(map.get(key));//拿到对应的value,转成json
                    //如果是字符串,直接赋予
                    if (parameter.getType().equals(String.class)){
                        objects[count] = v;
                    }else {//如果不是字符串,进行转换
                        objects[count] = JSONPrint.parseJSON(v, parameter.getType());
                    }
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
     * 处理from表单请求
     * @param event
     * @param httpServerRequest
     * @param httpServerResponse
     */
    private void isFromRequest(RoutingContext event, HttpServerRequest httpServerRequest,
                               HttpServerResponse httpServerResponse) {
        MultiMap requestKey = httpServerRequest.formAttributes();
        Set<String> names = requestKey.names();

        Map<String, Object> data = new HashMap<>();

        for (String name:names) {
            data.put(name, httpServerRequest.getParam(name));
        }

        String requestData = "{}";
        try {
            requestData = JSONPrint.toJSON(data);
        } catch (Exception e){
            e.printStackTrace();
        }

        //获取文件
        List<FileUpload> fileUploads = event.fileUploads();
        Map<String, FileUploadDetails> fileUploadDetailsMap = new HashMap<>();
        FileUpload fileUpload = null;
        try {
            for (int i = 0; i < fileUploads.size(); i++) {
                fileUpload = fileUploads.get(i);
                fileUploadDetailsMap.put(fileUpload.name(),
                        new FileUploadDetails()
                                .setFilename(fileUpload.fileName().substring(0, fileUpload.fileName().lastIndexOf(".")))
                                .setSuffix(fileUpload.fileName().substring(fileUpload.fileName().lastIndexOf("."), fileUpload.fileName().length()))
                                .setBytes(Files.readAllBytes(Path.of(fileUpload.uploadedFileName())))
                );
                //传完后删除
                Files.delete(Path.of(fileUpload.uploadedFileName()));
            }
        } catch (IOException e){
            e.printStackTrace();
        }

        Parameter[] parameters = this.findParameter();

        Object[] objects = new Object[parameters.length];

        Object result = null;
        try {
            int count = 0;
            for (Parameter parameter : parameters) {
                if (parameter.getType().equals(httpServerRequest.getClass())){//内置组件request
                    objects[count] = httpServerRequest;
                } else if (parameter.getType().equals(httpServerResponse.getClass())) {//内置组件response
                    objects[count] = httpServerResponse;
                }else if (parameter.getType().equals(this.vertx.getClass())) {//内置组件vertx
                    objects[count] = this.vertx;
                } else if (parameter.isAnnotationPresent(RequestBody.class)){//RequestBody 获取请求体,并转换
                    if (parameter.getType().equals(String.class)){
                        objects[count] = JSONPrint.parseJSON(requestData, JsonObject.class).toString();
                    }else {
                        objects[count] = JSONPrint.parseJSON(requestData, parameter.getType());
                    }
                } else if (parameter.isAnnotationPresent(Param.class)) {//取出里面的某一项
                    String key = parameter.getAnnotation(Param.class).value();

                    Map<String, Object> map = JSONPrint.parseJSON(requestData, Map.class);
                    String v = JSONPrint.toJSON(map.get(key));//拿到对应的value,转成json
                    //如果是字符串,直接赋予
                    if (parameter.getType().equals(String.class)){
                        objects[count] = v;
                    }else {//如果不是字符串,进行转换
                        objects[count] = JSONPrint.parseJSON(v, parameter.getType());
                    }
                } else if (parameter.isAnnotationPresent(File.class) && parameter.getType().equals(FileUploadDetails.class)) {
                    objects[count] = fileUploadDetailsMap.get(parameter.getAnnotation(File.class).name());
                } else if (parameter.getType().equals(FileUploadDetails[].class)) {//如果上传的是一组文件
                    if (fileUploadDetailsMap.isEmpty()){
                        objects[count] = new FileUploadDetails[0];
                    }else {
                        objects[count] = fileUploadDetailsMap.values().stream().toArray();
                    }
                } else {
                    //以参数key作为标准
                    String key = parameter.getName();
                    Map<String, Object> map = JSONPrint.parseJSON(requestData, Map.class);
                    String v = JSONPrint.toJSON(map.get(key));//拿到对应的value,转成json
                    //如果是字符串,直接赋予
                    if (parameter.getType().equals(String.class)){
                        objects[count] = v;
                    }else {//如果不是字符串,进行转换
                        objects[count] = JSONPrint.parseJSON(v, parameter.getType());
                    }
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
