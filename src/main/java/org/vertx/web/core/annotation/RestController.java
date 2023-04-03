package org.vertx.web.core.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * @author yangcong
 *
 * Controller总控制器(通过RestController可以获取到method)
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface RestController {

    String url();
}
