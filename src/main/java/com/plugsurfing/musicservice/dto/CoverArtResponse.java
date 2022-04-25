package com.plugsurfing.musicservice.dto;


import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Data
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class CoverArtResponse {
    private List<Image> images;

    @Data
    @Getter @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Image {
        @JsonProperty("image") private String url;
        private boolean front; // we will prefer front image.
    }

    public Mono<Image> getFrontImage() {
        return this.images == null || this.images.size() == 0 ? Mono.empty() :
            Flux.fromIterable(this.images)
                .takeUntil(Image::isFront)
                .takeLast(1).single();
    }
}
