package io.gitee.hefren.hhgateway.handler;

import com.hhrpc.hhrpc.core.api.LoadBalance;
import com.hhrpc.hhrpc.core.api.RegisterCenter;
import com.hhrpc.hhrpc.core.api.Router;
import com.hhrpc.hhrpc.core.meta.InstanceMeta;
import com.hhrpc.hhrpc.core.meta.ServiceMeta;
import jakarta.annotation.Resource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebHandler;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @Date 2024/6/20
 * @Author lifei
 */
@Component(value = "gatewayWebHandler")
public class GatewayWebHandler implements WebHandler {

    @Resource
    private RegisterCenter registerCenter;

    @Resource
    private Router<InstanceMeta> router;

    @Resource
    private LoadBalance<InstanceMeta> loadBalance;

    @Override
    public Mono<Void> handle(ServerWebExchange exchange) {
        String path = exchange.getRequest().getPath().value().substring(1);
        if (path.indexOf("/")>0) {
            // 1. 从请求路径中获取service
            String service = path.substring(path.indexOf("/")+1);
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
            return responseBody.flatMap(result->exchange.getResponse().writeWith(Mono.just(exchange.getResponse()
                    .bufferFactory().wrap(result.getBytes()))));
        }else {
            return exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap("hello gateway web handler".getBytes())));
        }
    }
}
