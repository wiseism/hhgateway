package io.gitee.hefren.hhgateway.plugin;

import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public interface GatewayPlugin {

    String GATEWAY_PREFIX = "/gw";

    String getName();

    String getGatewayPrefix();

    Mono<Void> handle(ServerWebExchange exchange);

    boolean support(ServerWebExchange exchange);
}
