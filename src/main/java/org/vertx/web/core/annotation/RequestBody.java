package org.vertx.web.core.annotation;

import java.lang.annotation.*;

/**
 * @author yangcong
 *
 * 直接转换请求体
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestBody {
}
