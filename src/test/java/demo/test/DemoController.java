package demo.test;

import org.vertx.web.core.annotation.Blocking;
import org.vertx.web.core.annotation.GetMapping;
import org.vertx.web.core.annotation.RestController;

@RestController(url = "/demo")
public class DemoController {

    /**
     * 测试耗时方法
     * @return
     */
    @GetMapping("/1")
    @Blocking
    public Object test(){
        return Thread.currentThread().getName();
    }

    @GetMapping("/2")
    public Object test2(){
        return Thread.currentThread().getName();
    }
}
