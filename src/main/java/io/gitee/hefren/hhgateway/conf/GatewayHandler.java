package io.gitee.hefren.hhgateway.conf;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

/**
 * @Date 2024/6/15
 * @Author lifei
 */
@Component
public class GatewayHandler {

    public Mono<ServerResponse> getHandle(ServerRequest request) {
        return ServerResponse.ok().body(Mono.just("hello gateway!"), String.class);
    }

    public Mono<ServerResponse> gwHandle(ServerRequest request) {
       String url = "http://localhost:8081/hhrpc";
       String requestBody = "{\n" +
               "  \"serviceName\": \"com.hhrpc.hhrpc.demo.api.UserService\",\n" +
               "  \"methodSign\": \"findById#java.lang.Integer\",\n" +
               "  \"args\": [100]\n" +
               "}";
        Mono<ResponseEntity<String>> entity = WebClient.create(url).post().contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody).retrieve().toEntity(String.class);
        Mono<String> responseBody = entity.map(ResponseEntity::getBody);
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(responseBody, String.class);
    }
}
