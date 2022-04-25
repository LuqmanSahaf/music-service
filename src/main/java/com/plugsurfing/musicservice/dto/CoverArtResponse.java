package com.plugsurfing.musicservice.dto;


import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    public Image getFrontImage() {
        return this.images == null ? null :
            this.images.stream()
                .filter(Image::isFront).findFirst()
                .orElse(this.images.stream().filter(x -> x.getUrl() != null).findFirst().orElse(null));
    }
}
