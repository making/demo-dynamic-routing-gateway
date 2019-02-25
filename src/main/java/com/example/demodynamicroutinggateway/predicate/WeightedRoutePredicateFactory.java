package com.example.demodynamicroutinggateway.predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.handler.predicate.AbstractRoutePredicateFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

@Component
public class WeightedRoutePredicateFactory extends AbstractRoutePredicateFactory<WeightedRoutePredicateFactory.Config> {

    final Random random;

    {
        try {
            random = SecureRandom.getInstance("NativePRNGNonBlocking");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    private static final Logger log = LoggerFactory.getLogger(WeightedRoutePredicateFactory.class);

    public WeightedRoutePredicateFactory() {
        super(Config.class);
    }

    @Override
    public List<String> shortcutFieldOrder() {
        return Arrays.asList("weight");
    }

    @Override
    public Predicate<ServerWebExchange> apply(Config config) {
        return exchange -> config.canRoute(random.nextInt(100));
    }


    static class Config {

        private double weight; // 0.0-1.0

        public double getWeight() {
            return weight;
        }

        public void setWeight(double weight) {
            this.weight = Math.min(Math.max(weight, 0.0), 1.0);
        }

        /**
         * @param n 0-9
         */
        public boolean canRoute(int n) {
            boolean canRoute = n < this.weight * 100;
            log.info("{} < {} => {}", n, this.weight * 100, canRoute);
            return canRoute;
        }
    }
}
