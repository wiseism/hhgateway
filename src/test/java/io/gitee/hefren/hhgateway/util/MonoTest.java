package io.gitee.hefren.hhgateway.util;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @Date 2024/6/22
 * @Author lifei
 */
public class MonoTest {

    @Test
    public void test01() {

//        Mono<String> mono = Mono.just(createData());
//        mono.subscribe(System.out::println);

        Mono<String> deferMono = Mono.defer(() -> Mono.just(createData()));
        deferMono.subscribe(System.out::println);
    }
    
    @Test
    public void test02() {
        Flux<String> flux = Flux.just(createDataList());
        flux.subscribe(System.out::println);
        Mono<Void> then = flux.then();
    }
    
    private String[] createDataList() {
        System.out.println("=====> createDataList");
        return new String[]{"a", "b", "c"};
    }
    

    private String createData() {
        System.out.println("====> createData.....");
        return "hello mono test!";
    }
}
