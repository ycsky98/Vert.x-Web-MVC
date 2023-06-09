package demo.test;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import org.springframework.context.annotation.ComponentScan;
import org.vertx.web.core.router.config.RouterInit;
import org.vertx.web.spring.JavaBeanContextAnnotation;

import java.io.IOException;

@ComponentScan("demo.test.**")
public class Test {

    public static void main(String[] args) throws IOException {
        Vertx vertx = Vertx.vertx();
        HttpServer httpServer = vertx.createHttpServer();
        httpServer.requestHandler(new RouterInit(new JavaBeanContextAnnotation(Test.class), vertx).getRouter())
                .listen(80);

    }
}
