package io.gitee.hefren.hhgateway.plugin;

import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @Date 2024/6/22
 * @Author lifei
 */
public interface GatewayPluginChain {

    Mono<Void> handle(ServerWebExchange exchange);

}
