package io.gitee.hefren.hhgateway.handler;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

/**
 * @Date 2024/6/15
 * @Author lifei
 */
@Component
public class HelloHandler {

    public Mono<ServerResponse> handle(ServerRequest request) {
        return ServerResponse.ok().body(Mono.just("hello demo!"), String.class);
    }

    public Mono<ServerResponse> postHandle(ServerRequest request) {
        Mono<String> requestBody = request.bodyToMono(String.class);
        return ServerResponse.ok().body(requestBody, String.class);
    }
}
