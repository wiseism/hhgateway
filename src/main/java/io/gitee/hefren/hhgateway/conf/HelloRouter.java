package io.gitee.hefren.hhgateway.conf;

import io.gitee.hefren.hhgateway.handler.HelloHandler;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;

/**
 * @Date 2024/6/15
 * @Author lifei
 */
@Configuration
public class HelloRouter {

    @Resource
    private HelloHandler helloHandler;

    @Bean
    public RouterFunction<?> helloRouterFunction() {
        return RouterFunctions.route()
                .GET("/hello", helloHandler::handle)
                .POST("/hello/**", contentType(MediaType.APPLICATION_JSON), helloHandler::postHandle)
                .build();
    }
}
