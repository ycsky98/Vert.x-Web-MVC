package org.vertx.web.core.annotation;

import java.lang.annotation.*;

/**
 * @author yangcong
 *
 * 请求参数
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Param {

    String value();
}
