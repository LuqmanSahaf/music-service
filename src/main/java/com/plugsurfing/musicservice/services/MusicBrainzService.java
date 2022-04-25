package com.plugsurfing.musicservice.services;

import static java.lang.String.format;
import static reactor.core.scheduler.Schedulers.immediate;

import com.plugsurfing.musicservice.PlugWebClients;
import com.plugsurfing.musicservice.dto.MusicBrainzArtistResponse;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MusicBrainzService {
    private final PlugWebClients webClients;

    @RateLimiter(name = "musicBrainz")
    public Publisher<MusicBrainzArtistResponse> fetchArtist(String mbid) {
        return webClients.getMusicBrainzClient()
            .get()
            .uri(getMusicBrainzArtistUri(mbid))
            .retrieve()
            .bodyToMono(MusicBrainzArtistResponse.class)
            .subscribeOn(immediate());
    }

    private String getMusicBrainzArtistUri(String mbid) {
        return format("/artist/%s?&fmt=json&inc=url-rels+release-groups", mbid);
    }
}
