package com.plugsurfing.musicservice.services;

import static reactor.core.scheduler.Schedulers.boundedElastic;

import com.plugsurfing.musicservice.dto.ArtistDetailsResponse;
import com.plugsurfing.musicservice.dto.util.ResponseMapper;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArtistDetailsService {
    private final ResponseMapper responseMapper;
    private final MusicBrainzService musicBrainzService;
    private final CoverArtService coverArtService;
    private final WikiService wikiService;

    @TimeLimiter(name="artistDetails")
    @CircuitBreaker(name="artistDetails")
    public Mono<ArtistDetailsResponse> getArtistDetails(String mbid) {

        var artistPub = musicBrainzService.fetchArtist(mbid);

        return Mono.from(artistPub).publishOn(boundedElastic()).flatMap(artist -> {
            var albumsMono = coverArtService.fetchAllCoverArtsForAlbums(artist.getAlbums());
            var descriptionMono = Mono.from(wikiService.fetchDescriptionFromWiki(artist.getWikiDataResourceId()));

            return albumsMono.zipWith(descriptionMono).map(tuple -> {
                var artistDetails = responseMapper.toArtistDetailsResponse(artist);
                artistDetails.setAlbums(tuple.getT1());
                artistDetails.setDescription(tuple.getT2());
                return artistDetails;
            });
        });
    }
}
