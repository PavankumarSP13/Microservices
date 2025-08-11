package com.pavan.apigateway.config;

import com.pavan.apigateway.exceptions.UnauthorizedException;
import com.pavan.apigateway.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    @Autowired
    private RouteValidator routeValidator;

    @Autowired
    private JwtUtil jwtUtil;

    public AuthenticationFilter() {
        super(Config.class);
    }
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationFilter.class);

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            if (routeValidator.isSecured.test(exchange.getRequest())) {
                try {
                    String token = extractToken(exchange.getRequest());
                    jwtUtil.validateToken(token);
                } catch (UnauthorizedException e) {
                    logger.warn("Unauthorized access: {}", e.getMessage());
                    return onError(exchange.getResponse(), e.getMessage(), HttpStatus.UNAUTHORIZED);
                } catch (Exception e) {
                    logger.error("Unexpected error in authentication filter", e);
                    return onError(exchange.getResponse(), "Unauthorized access", HttpStatus.UNAUTHORIZED);
                }
            }
            return chain.filter(exchange);
        };
    }


    private Mono<Void> onError(ServerHttpResponse response, String message, HttpStatus status) {
        response.setStatusCode(status);
        response.getHeaders().add("Content-Type", "application/json");
        DataBuffer buffer = response.bufferFactory().wrap(("{\"error\":\"" + message + "\"}").getBytes());
        return response.writeWith(Mono.just(buffer));
    }


    /** validates headers contains token or not
     *
     * @param serverHttpRequest
     * @return bearer token
     * @throws UnauthorizedException
     */
    public String extractToken(ServerHttpRequest serverHttpRequest) {
        List<String> authHeaders = serverHttpRequest.getHeaders().getOrEmpty(HttpHeaders.AUTHORIZATION);
        Optional<String> bearerToken = authHeaders.stream()
                .filter(h -> h.startsWith("Bearer "))
                .findFirst();
        if (!bearerToken.isPresent()) {
            throw new UnauthorizedException("Missing or invalid Authorization header");
        }
        return bearerToken.get().substring(7);

    }


    public static class Config {

    }
}
