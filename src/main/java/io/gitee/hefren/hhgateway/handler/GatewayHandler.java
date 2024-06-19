package io.gitee.hefren.hhgateway.handler;

import com.hhrpc.hhrpc.core.api.LoadBalance;
import com.hhrpc.hhrpc.core.api.RegisterCenter;
import com.hhrpc.hhrpc.core.api.Router;
import com.hhrpc.hhrpc.core.meta.InstanceMeta;
import com.hhrpc.hhrpc.core.meta.ServiceMeta;
import jakarta.annotation.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @Date 2024/6/15
 * @Author lifei
 */
@Component
public class GatewayHandler {

    @Resource
    private RegisterCenter registerCenter;

    @Resource
    private Router<InstanceMeta> router;

    @Resource
    private LoadBalance<InstanceMeta> loadBalance;

    public Mono<ServerResponse> getHandle(ServerRequest request) {
        return ServerResponse.ok().body(Mono.just("hello gateway!"), String.class);
    }

    public Mono<ServerResponse> gwHandle(ServerRequest request) {
        // /gw/com.hhrpc.hhrpc.demo.api.UserService
        String path = request.path().substring(1);
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
            return request.bodyToMono(String.class).flatMap(requestBody->{
                Mono<ResponseEntity<String>> entity = WebClient.create(url).post().contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(requestBody).retrieve().toEntity(String.class);
                Mono<String> responseBody = entity.map(ResponseEntity::getBody);
                return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(responseBody, String.class);
            });
        }else {
            return ServerResponse.ok().body(Mono.just("hello gateway!"), String.class);
        }
    }
}
