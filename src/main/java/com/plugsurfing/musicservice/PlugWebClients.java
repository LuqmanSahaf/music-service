package com.plugsurfing.musicservice;

import java.time.Duration;
import javax.annotation.PostConstruct;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

@Component
@NoArgsConstructor
public class PlugWebClients {
    @Value("${plug.base-urls.music-brainz}") private String musicBrainzUrl;
    @Value("${plug.base-urls.cover-art}") private String coverArtUrl;
    @Value("${plug.base-urls.wikidata}") private String wikidataUrl;
    @Value("${plug.base-urls.wikipedia}") private String wikipediaUrl;

    @Getter @Setter
    protected WebClient musicBrainzClient,
        coverArtClient,
        wikidataClient,
        wikipediaClient;

    @PostConstruct
    public void init() {
        musicBrainzClient = createWebClient(musicBrainzUrl, false, 100);

        coverArtClient = createWebClient(coverArtUrl, true, 500);

        wikidataClient = createWebClient(wikidataUrl, true, 100);

        wikipediaClient = createWebClient(wikipediaUrl, false, 100);
    }

    private WebClient createWebClient(String url, boolean followRedirect, int maxConnections) {
        ConnectionProvider provider =
            ConnectionProvider.builder("custom")
                .maxConnections(maxConnections)
                .maxIdleTime(Duration.ofMillis(300))
                .maxLifeTime(Duration.ofMillis(1500))
                .build();
        return WebClient.builder()
            .clientConnector(new ReactorClientHttpConnector(
                HttpClient.create(provider)
                    .responseTimeout(Duration.ofSeconds(10))
                    .followRedirect(followRedirect)
            ))
            .baseUrl(url)
            .build();
    }
}
