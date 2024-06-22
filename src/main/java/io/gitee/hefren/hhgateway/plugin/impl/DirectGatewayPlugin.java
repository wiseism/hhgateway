package io.gitee.hefren.hhgateway.plugin.impl;

import com.google.common.base.Strings;
import io.gitee.hefren.hhgateway.plugin.AbstractGatewayPlugin;
import io.gitee.hefren.hhgateway.plugin.GatewayPluginChain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @Date 2024/6/22
 * @Author lifei
 */
@Component(value = "directGatewayPlugin")
public class DirectGatewayPlugin extends AbstractGatewayPlugin {

    private static final Logger log = LoggerFactory.getLogger(DirectGatewayPlugin.class);

    private static final String NAME = "direct";

    @Override
    public Mono<Void> doHandle(ServerWebExchange exchange, GatewayPluginChain chain) {
        log.debug("=====> DirectGatewayPlugin doHandle....");
        // 1. 从请求路径中获取backend参数
        String url = exchange.getRequest().getQueryParams().getFirst("backend");
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        exchange.getResponse().getHeaders().add("hhgateway.version", "v0.0.1");
        exchange.getResponse().getHeaders().add("hhgateway.plugin", getName());
        if (Strings.isNullOrEmpty(url)) {
            // 直接返回它的请求体
            // 第一种写法
//            Flux<DataBuffer> body = exchange.getRequest().getBody();
//            return exchange.getResponse().writeWith(body);
            // 第二种写法
            return exchange.getRequest().getBody().flatMap(body->exchange.getResponse().writeWith(Mono.just(body))).then(chain.handle(exchange));
        }
        // 2. 从request 中获取请求参数
        Flux<DataBuffer> body = exchange.getRequest().getBody();
        // 3. 调用 WebClient 方法
        Mono<ResponseEntity<String>> entity = WebClient.create(url).post().contentType(MediaType.APPLICATION_JSON)
                .body(body, DataBuffer.class).retrieve().toEntity(String.class);
        Mono<String> responseBody = entity.map(ResponseEntity::getBody);
        // 4. 返回
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        exchange.getResponse().getHeaders().add("hhgateway.version", "v0.0.1");
        exchange.getResponse().getHeaders().add("hhgateway.plugin", getName());
        return responseBody.flatMap(result->exchange.getResponse().writeWith(Mono.just(exchange.getResponse()
                .bufferFactory().wrap(result.getBytes())))).then(chain.handle(exchange));
    }

    @Override
    public String getName() {
        return NAME;
    }
}
