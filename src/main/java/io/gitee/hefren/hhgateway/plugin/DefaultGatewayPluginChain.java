package io.gitee.hefren.hhgateway.plugin;

import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;

/**
 * @Date 2024/6/22
 * @Author lifei
 */
@Component(value = "defaultGatewayPluginChain")
public class DefaultGatewayPluginChain implements GatewayPluginChain {

    private List<GatewayPlugin> pluginList;
    private int index = 0;

    public DefaultGatewayPluginChain(List<GatewayPlugin> pluginList) {
        this.pluginList = pluginList;
    }

    // 每次调用 handle方法，都执行下一个 GatewayPlugin 的 handle
    @Override
    public Mono<Void> handle(ServerWebExchange exchange) {
        return Mono.defer(()->{
            if (Objects.nonNull(pluginList) && index < pluginList.size()) {
                return pluginList.get(index++).handle(exchange, this);
            }
            return Mono.empty();
        });
    }
}
