//package com.pavan.apigateway.routes;
//
//import com.netflix.discovery.converters.Auto;
//import com.pavan.apigateway.config.AuthenticationFilter;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.cloud.gateway.route.RouteLocator;
//import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class GatewayRoutes {
//
//
//    @Autowired
//    private AuthenticationFilter authenticationFilter;
//
//    @Bean
//    public RouteLocator routeLocator(RouteLocatorBuilder builder) {
//        return builder.routes()
//                .route("quiz-service", r -> r.path("/quiz/**")
//                        .uri("lb://QUIZ-SERVICE"))
//                .route("order-service", r -> r.path("/question/**")
//                        .filters(f -> f.filter(authenticationFilter))
//                        .uri("lb://QUESTION-SERVICE"))
//                .route("auth-service", r -> r.path("/auth/**")
//                        .filters(f -> f.filter(authenticationFilter))
//                        .uri("lb://AUTH-SERVICE"))
//                .build();
//    }
//}
//
