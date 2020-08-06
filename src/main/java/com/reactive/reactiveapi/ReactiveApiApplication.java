package com.reactive.reactiveapi;

import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Arrays;

public class ReactiveApiApplication {

    public static void main(String[] args) throws InterruptedException {
        Mono.empty();
        Flux.empty();
        Mono<Integer> mono = Mono.just(1);
        Flux<Integer> flux = Flux.just(1, 2, 3);

        Flux<Integer> fluxFromMono = mono.flux();
        Mono<Boolean> monoFromFlux = flux.any(v -> v.equals(1));
        Mono<Integer> integerMono = flux.elementAt(1);

        Flux.range(1, 5);
        Flux.fromIterable(Arrays.asList(2, 3, 4, 5));

        Flux.<String>generate(sink -> {
            sink.next("hello");
        })
                .delayElements(Duration.ofMillis(500))
                .take(4);
                //.subscribe(System.out::println);
        //Thread.sleep(4001);

        Flux<Object> producer = Flux
                .generate(
                        () -> 2354,
                        (state, sink) -> {
                            if (state > 2366) {
                                sink.complete();
                            } else {
                                sink.next("Step: " + state);
                            }
                            return state + 3;
                        }
                );

        Flux
                .create(
                        sink -> {
                            producer.subscribe(
                                    new BaseSubscriber<Object>() {
                                        @Override
                                        protected void hookOnNext(Object value) {
                                            sink.next(value);
                                        }

                                        @Override
                                        protected void hookOnComplete() {
                                            sink.complete();
                                        }
                                    }
                            );

                            sink.onRequest(r -> {
                                sink.next("db returns" + producer.blockFirst());
                            });
                        }
                ); //.subscribe(System.out::println);

        Flux<String> second = Flux.just("world", "coder").repeat();
        Flux<String> sumFlux = Flux
                .just("hello", "dru", "java", "linus", "java")
                .zipWith(second, (f, s) -> String.format("%s %s", f, s));

        Flux<String> string = sumFlux
                .delayElements(Duration.ofMillis(1300));
                //.timeout(Duration.ofSeconds(1))
                //.onErrorReturn("Too slow")
//                .onErrorResume(thr ->
//                    //return Flux.just("one", "thwo");
//                    Flux
//                            .interval(Duration.ofMillis(300))
//                            .map(String::valueOf)
//                );

        string.subscribe(
            v -> So.pln(v),
                e -> Se.pLn(e),
                () -> So.pln("Complete")
        );

        Thread.sleep(5001);



    }
}
