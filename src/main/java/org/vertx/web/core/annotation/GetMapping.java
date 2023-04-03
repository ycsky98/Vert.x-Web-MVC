package org.vertx.web.core.annotation;

import java.lang.annotation.*;

/**
 * @author yangcong
 *
 * get请求
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GetMapping {

    String value() default "";
}
