package com.plugsurfing.musicservice.services;

import static java.lang.String.format;

import com.plugsurfing.musicservice.PlugWebClients;
import com.plugsurfing.musicservice.dto.WikiDataResponse;
import com.plugsurfing.musicservice.dto.WikipediaResponse;
import lombok.RequiredArgsConstructor;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class WikiService {
    private final PlugWebClients webClients;

    public Mono<String> fetchWikipediaExtract(String title) {
        return webClients.getWikipediaClient()
            .get()
            .uri(title)
            .retrieve()
            .bodyToMono(WikipediaResponse.class)
            .map(WikipediaResponse::getExtract);
    }

    public Publisher<String> fetchDescriptionFromWiki(String resourceId) {
        var wikiPageTitleMono = webClients.getWikidataClient()
            .get()
            .uri(getWikidataUri(resourceId))
            .retrieve()
            .bodyToMono(WikiDataResponse.class)
            .map(response -> response.getWikiPageTitle(resourceId));

        return wikiPageTitleMono
            .flatMap(title -> title.isEmpty() || title.isBlank() ?
                Mono.just(title) :
                Mono.from(fetchWikipediaExtract(title)));
    }

    private String getWikidataUri(String resourceId) {
        return format("?action=wbgetentities&props=sitelinks/urls&format=json&ids=%s", resourceId);
    }
}
