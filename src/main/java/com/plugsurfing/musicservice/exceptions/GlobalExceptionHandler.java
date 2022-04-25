package com.plugsurfing.musicservice.exceptions;

import static org.springframework.http.HttpStatus.BAD_GATEWAY;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;
import static org.springframework.web.reactive.function.server.ServerResponse.badRequest;

import java.util.Map;
import java.util.concurrent.TimeoutException;

import com.plugsurfing.musicservice.dto.ErrorResponse;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Slf4j
@Configuration
@Order(-2)
public class GlobalExceptionHandler extends AbstractErrorWebExceptionHandler {
    public GlobalExceptionHandler(ErrorAttributes errorAttributes, ApplicationContext applicationContext,
                                  ServerCodecConfigurer configurer) {
        super(errorAttributes, new WebProperties.Resources(), applicationContext);
        super.setMessageWriters(configurer.getWriters());
        super.setMessageReaders(configurer.getReaders());
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(
            RequestPredicates.all() , request -> {
            var error = errorAttributes.getError(request);
            log.error("Error: " + error.getMessage());

            if (error instanceof WebClientResponseException.NotFound || error instanceof WebClientResponseException.BadRequest) {
                return badRequest().body(Mono.just(new ErrorResponse(BAD_REQUEST.toString())), ErrorResponse.class);
            }

            if (error instanceof WebClientResponseException.ServiceUnavailable || error instanceof RequestNotPermitted ||
                error instanceof TimeoutException || error instanceof CallNotPermittedException) {
                return ServerResponse.status(SERVICE_UNAVAILABLE).body(Mono.just(new ErrorResponse(BAD_GATEWAY.toString())), ErrorResponse.class);
            }

            return ServerResponse.status(INTERNAL_SERVER_ERROR).body(Mono.just(new ErrorResponse(INTERNAL_SERVER_ERROR.toString())), ErrorResponse.class);
        });
    }
}
