package io.gitee.hefren.hhgateway.plugin.impl;

import com.hhrpc.hhrpc.core.api.LoadBalance;
import com.hhrpc.hhrpc.core.api.RegisterCenter;
import com.hhrpc.hhrpc.core.api.Router;
import com.hhrpc.hhrpc.core.meta.InstanceMeta;
import com.hhrpc.hhrpc.core.meta.ServiceMeta;
import io.gitee.hefren.hhgateway.plugin.AbstractGatewayPlugin;
import io.gitee.hefren.hhgateway.plugin.GatewayPluginChain;
import jakarta.annotation.Resource;
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

import java.util.List;

/**
 * @Date 2024/6/22
 * @Author lifei
 */
@Component(value = "hhRpcGatewayPlugin")
public class HhRpcGatewayPlugin extends AbstractGatewayPlugin {

    private static final Logger log = LoggerFactory.getLogger(HhRpcGatewayPlugin.class);

    private static final String NAME = "hhrpc";

    @Resource
    private RegisterCenter registerCenter;

    @Resource
    private Router<InstanceMeta> router;

    @Resource
    private LoadBalance<InstanceMeta> loadBalance;

    @Override
    public Mono<Void> doHandle(ServerWebExchange exchange, GatewayPluginChain chain) {
        log.debug("====> HhRpcGatewayPlugin doHandle");
        // 1. 从请求路径中获取service
        String service = exchange.getRequest().getPath().value().substring(getGatewayPrefix().length());
        // 2. 从注册中心获取服务实例
        ServiceMeta serviceMeta = ServiceMeta.builder().name(service).app("hhrpc").env("dev").namespace("public").build();
        List<InstanceMeta> instanceMetaList = registerCenter.findAll(serviceMeta);
        // 3. 通过负载均衡获取到一个实例
        InstanceMeta instanceMeta = loadBalance.choose(router.rout(instanceMetaList));
        String url = instanceMeta.toUrl();
        // 4. 从request 中获取请求参数
        Flux<DataBuffer> body = exchange.getRequest().getBody();
        Mono<ResponseEntity<String>> entity = WebClient.create(url).post().contentType(MediaType.APPLICATION_JSON)
                .body(body, DataBuffer.class).retrieve().toEntity(String.class);
        Mono<String> responseBody = entity.map(ResponseEntity::getBody);
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
