package com.plugsurfing.musicservice.services;

import java.util.List;

import com.plugsurfing.musicservice.PlugWebClients;
import com.plugsurfing.musicservice.dto.Album;
import com.plugsurfing.musicservice.dto.CoverArtResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class CoverArtService {
    private final PlugWebClients webClients;

    public Publisher<Album> fetchAlbumCoverArt(Album album) {
        var imagesMono = webClients.getCoverArtClient()
            .get()
            .uri(album.getId())
            .retrieve()
            .bodyToMono(CoverArtResponse.class);

        return imagesMono
            .flatMap(coverArt -> coverArt.getFrontImage().map(image -> {
                album.setImageUrl(image.getUrl());
                return album;
            }))
            .onErrorResume(e -> {
                // Don't set Image Url if it's not found.
                if (e instanceof WebClientResponseException.NotFound || e instanceof WebClientResponseException.BadRequest)
                    return Mono.just(album);
                else return Mono.error(e);
            });
    }

    public Mono<List<Album>> fetchAllCoverArtsForAlbums(List<Album> albums) {
        return Flux.fromStream(albums.stream())
            .flatMap(this::fetchAlbumCoverArt)
            .collectList();
    }
}
