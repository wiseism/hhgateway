package io.gitee.hefren.hhgateway.plugin;

import com.google.common.base.Strings;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @Date 2024/6/22
 * @Author lifei
 */
public abstract class AbstractGatewayPlugin implements GatewayPlugin{

    @Override
    public String getGatewayPrefix() {
        return Strings.lenientFormat("%s/%s/", GATEWAY_PREFIX, getName());
    }

    @Override
    public boolean support(ServerWebExchange exchange) {
        return exchange.getRequest().getPath().value().startsWith(getGatewayPrefix());
    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange) {
        return support(exchange)? doHandle(exchange): Mono.empty();
    }

    public abstract Mono<Void> doHandle(ServerWebExchange exchange);
}
