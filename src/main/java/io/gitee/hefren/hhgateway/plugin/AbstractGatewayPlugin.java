package io.gitee.hefren.hhgateway.plugin;

import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @Date 2024/6/22
 * @Author lifei
 */
public abstract class AbstractGatewayPlugin implements GatewayPlugin{

    private static final Logger log = LoggerFactory.getLogger(AbstractGatewayPlugin.class);
    @Override
    public String getGatewayPrefix() {
        return Strings.lenientFormat("%s/%s/", GATEWAY_PREFIX, getName());
    }

    @Override
    public boolean support(ServerWebExchange exchange) {
        return exchange.getRequest().getPath().value().startsWith(getGatewayPrefix());
    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, GatewayPluginChain chain) {
        boolean isSupport = support(exchange);
        log.debug("====> gateway plugin [{}], support: {}", getName(), isSupport);
        return support(exchange)? doHandle(exchange, chain): chain.handle(exchange);
    }

    public abstract Mono<Void> doHandle(ServerWebExchange exchange, GatewayPluginChain chain);
}
