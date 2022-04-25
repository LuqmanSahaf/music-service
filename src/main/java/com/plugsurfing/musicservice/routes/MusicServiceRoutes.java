package com.plugsurfing.musicservice.routes;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.badRequest;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

import java.util.regex.Pattern;

import com.plugsurfing.musicservice.dto.ArtistDetailsResponse;
import com.plugsurfing.musicservice.dto.ErrorResponse;
import com.plugsurfing.musicservice.services.ArtistDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class MusicServiceRoutes {
    private final ArtistDetailsService artistDetailsService;

    private final static Pattern UUID_REGEX_PATTERN =
        Pattern.compile("^[{]?[0-9a-fA-F]{8}-([0-9a-fA-F]{4}-){3}[0-9a-fA-F]{12}[}]?$");

    public static boolean isValidUUID(String str) {
        if (str == null) {
            return false;
        }
        return UUID_REGEX_PATTERN.matcher(str).matches();
    }

    @Bean
    public RouterFunction<ServerResponse> getArtistDetailsRoute() {
        return route(GET("/musify/music-artist/details/{mbid}"),
            this::getArtistDetailsHandler);
    }

    Mono<ServerResponse> getArtistDetailsHandler(ServerRequest request) {
        var mbid = request.pathVariable("mbid");
        log.info("Received request: GET " + request.path());

        if (!isValidUUID(mbid)) {
            return badRequest().body(Mono.just(new ErrorResponse("Invalid MBID")), ErrorResponse.class);
        }

        return artistDetailsService.getArtistDetails(mbid)
            .flatMap(artist -> ok().body(Mono.just(artist), ArtistDetailsResponse.class));
    }
}
