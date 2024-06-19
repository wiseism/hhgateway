package io.gitee.hefren.hhgateway.filter;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Objects;

/**
 * @Date 2024/6/20
 * @Author lifei
 */
@Component
public class GatewayPreWebFilter implements WebFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        // 1. 执行当前过滤
        String mock = exchange.getRequest().getQueryParams().getFirst("mock");
        if (Objects.nonNull(mock)) {
            // {"result": "mock result"}
            String result = "{\"result\": \"mock result\"}";
            exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
            return exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(result.getBytes())));
        }
        // 2. 继续执行链条中其它的过滤
        return chain.filter(exchange);
    }
}
