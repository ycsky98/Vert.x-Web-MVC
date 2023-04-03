package org.vertx.web.core.annotation;

import java.lang.annotation.*;

/**
 * @author yangcong
 *
 * post请求
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PostMapping {

    String value() default "";
}
