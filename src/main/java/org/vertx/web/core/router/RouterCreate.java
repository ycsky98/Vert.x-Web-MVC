package org.vertx.web.core.router;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

/**
 * @author yangcong
 *
 * Vert.x Web 路由构建
 */
public class RouterCreate {

    private Vertx vertx;

    private Router router;

    public RouterCreate(Vertx vertx) {
        this.vertx = vertx;
        this.router = Router.router(this.vertx);
    }

    /**
     * 构建url路由
     * @param url
     * @return
     */
    public RouterCreate url(String url, Handler<io.vertx.ext.web.RoutingContext> handler){
        this.router.route(url).handler(BodyHandler.create()).handler(handler);
        return this;
    }

    /**
     * 构建url路由
     * @param url
     * @return
     */
    public RouterCreate urlBlocking(String url, Handler<io.vertx.ext.web.RoutingContext> handler){
        this.router.route(url).handler(BodyHandler.create()).blockingHandler(handler);
        return this;
    }

    /**
     * 构建带正则表达式的路由
     * @param regex
     * @return
     */
    public RouterCreate urlRegex(String regex, Handler<io.vertx.ext.web.RoutingContext> handler){
        this.router.routeWithRegex(regex).handler(BodyHandler.create()).handler(handler);
        return this;
    }

    /**
     * 构建带正则表达式的路由(阻塞式)
     * @param regex
     * @return
     */
    public RouterCreate urlBlockingRegex(String regex, Handler<io.vertx.ext.web.RoutingContext> handler){
        this.router.routeWithRegex(regex).handler(BodyHandler.create()).blockingHandler(handler);
        return this;
    }

    public RouterCreate error(int code, Handler<io.vertx.ext.web.RoutingContext> handler){
        this.router.errorHandler(code, handler);
        return this;
    }

    public Router getRouter() {
        return router;
    }
}
