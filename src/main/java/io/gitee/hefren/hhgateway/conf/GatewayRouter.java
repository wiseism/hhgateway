package io.gitee.hefren.hhgateway.conf;

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
public class GatewayRouter {

    @Resource
    private GatewayHandler gatewayHandler;

    @Bean
    public RouterFunction<?> gwRouterFunction() {
        return RouterFunctions.route()
                .GET("/gw", gatewayHandler::getHandle)
                .POST("/gw/**", contentType(MediaType.APPLICATION_JSON), gatewayHandler::gwHandle)
                .build();
    }
}
