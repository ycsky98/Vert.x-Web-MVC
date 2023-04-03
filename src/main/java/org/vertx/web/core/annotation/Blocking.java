package org.vertx.web.core.annotation;

import java.lang.annotation.*;

/**
 * @author yangcong
 *
 * 是否为耗时请求,可以补充该注解加入额外的线程池
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Blocking {
}
