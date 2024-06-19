package io.gitee.hefren.hhgateway.conf;

import com.hhrpc.hhrpc.core.api.LoadBalance;
import com.hhrpc.hhrpc.core.api.RegisterCenter;
import com.hhrpc.hhrpc.core.api.Router;
import com.hhrpc.hhrpc.core.cluster.RoundBionLoadBalance;
import com.hhrpc.hhrpc.core.meta.InstanceMeta;
import com.hhrpc.hhrpc.core.register.HhRegsiterCenter;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;

import java.util.Properties;

/**
 * @Date 2024/6/15
 * @Author lifei
 */
@Configuration
public class GatewayConf {

    @Bean
    public RegisterCenter registerCenter() {
        return new HhRegsiterCenter();
    }

    @Bean
    public Router<InstanceMeta> router() {
        return Router.DEFAULT;
    }

    @Bean
    public LoadBalance<InstanceMeta> loadBalance() {
        return new RoundBionLoadBalance<>();
    }

    @Bean
    public ApplicationRunner runner(ApplicationContext applicationContext) {
        return args->{
            SimpleUrlHandlerMapping simpleUrlHandlerMapping = applicationContext.getBean(SimpleUrlHandlerMapping.class);
            Properties mappings = new Properties();
            mappings.put("/ga/**", "gatewayWebHandler");
            mappings.put("/gw/**", "gatewayWebHandler");
            simpleUrlHandlerMapping.setMappings(mappings);
            simpleUrlHandlerMapping.initApplicationContext();
        };
    }
}
