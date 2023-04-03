package demo.test;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import org.vertx.web.core.router.config.RouterInit;
import org.vertx.web.spring.JavaBeanContext;

public class Test {

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        HttpServer httpServer = vertx.createHttpServer();
        httpServer.requestHandler(new RouterInit(new JavaBeanContext("spring.xml"), vertx).getRouter())
                .listen(80);

    }
}
