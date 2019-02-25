package com.example.demodynamicroutinggateway.predicate;

import org.springframework.cloud.gateway.handler.predicate.AbstractRoutePredicateFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import java.util.function.Predicate;

@Component
public class AlwaysTrueRoutePredicateFactory extends AbstractRoutePredicateFactory<Object> {

    public AlwaysTrueRoutePredicateFactory() {
        super(Object.class);
    }

    @Override
    public Predicate<ServerWebExchange> apply(Object config) {
        return exchange -> true;
    }

}
