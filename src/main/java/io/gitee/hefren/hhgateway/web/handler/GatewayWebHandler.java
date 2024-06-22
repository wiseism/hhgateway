package io.gitee.hefren.hhgateway.web.handler;

import com.hhrpc.hhrpc.core.api.LoadBalance;
import com.hhrpc.hhrpc.core.api.RegisterCenter;
import com.hhrpc.hhrpc.core.api.Router;
import com.hhrpc.hhrpc.core.meta.InstanceMeta;
import com.hhrpc.hhrpc.core.meta.ServiceMeta;
import io.gitee.hefren.hhgateway.plugin.GatewayPlugin;
import jakarta.annotation.Resource;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebHandler;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;

/**
 * @Date 2024/6/20
 * @Author lifei
 */
@Component(value = "gatewayWebHandler")
public class GatewayWebHandler implements WebHandler {


    @Resource
    private List<GatewayPlugin> pluginList;

    @Override
    public Mono<Void> handle(ServerWebExchange exchange) {
        if (Objects.isNull(pluginList) || pluginList.isEmpty()) {
            return createOtherResult("{\"result\": \"pluginList is empty\"}", exchange);
        }
        for (GatewayPlugin gatewayPlugin : pluginList) {
            if (gatewayPlugin.support(exchange)) {
                return gatewayPlugin.handle(exchange);
            }
        }
        return createOtherResult("{\"result\": \"none support plugin\"}", exchange);
    }

    private static Mono<Void> createOtherResult(String result, ServerWebExchange exchange) {
        return exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(result.getBytes())));
    }
}
