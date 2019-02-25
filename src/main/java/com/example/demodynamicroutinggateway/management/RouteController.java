package com.example.demodynamicroutinggateway.management;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.Resource;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
public class RouteController {

    private final Resource routesConfig;

    private final ObjectMapper objectMapper = new Jackson2ObjectMapperBuilder().factory(new YAMLFactory()).build();

    private final RouteDefinitionRepository routeDefinitionRepository;

    private final ApplicationEventPublisher eventPublisher;

    public RouteController(@Value("classpath:routes.yml") Resource routesConfig, RouteDefinitionRepository routeDefinitionRepository, ApplicationEventPublisher eventPublisher) {
        this.routesConfig = routesConfig;
        this.routeDefinitionRepository = routeDefinitionRepository;
        this.eventPublisher = eventPublisher;
    }

    @PostMapping(path = "routes")
    public Flux<RouteDefinition> updateRoutes(@RequestBody String yaml) throws Exception {
        List<RouteDefinition> routes = this.objectMapper.readValue(yaml, new TypeReference<List<RouteDefinition>>() {

        });
        return Flux.fromIterable(routes).flatMap(route -> this.routeDefinitionRepository.save(Mono.just(route)))
            .doOnComplete(() -> this.eventPublisher.publishEvent(new RefreshRoutesEvent(this)))
            .thenMany(this.routeDefinitionRepository.getRouteDefinitions());
    }

    @PostConstruct
    void init() throws Exception {
        try (InputStream inputStream = this.routesConfig.getInputStream()) {
            String yaml = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
            this.updateRoutes(yaml).blockLast(); // block only for initializing ...!
        }
    }
}
