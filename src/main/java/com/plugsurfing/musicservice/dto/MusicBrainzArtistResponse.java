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
public class MusicBrainzArtistResponse {
    @JsonProperty("id") private String mbid;
    private String name;
    private String gender;
    private String country;
    private String disambiguation;
    private String error;
    private List<Relation> relations;
    @JsonProperty("release-groups") private List<Album> albums;

    @Data
    @Getter @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Relation {
        private RelationUrl url;
        private String type;
    }

    @Data
    @Getter @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RelationUrl {
        private String resource;
    }

    public String getWikiDataResourceId() {
        return this.relations.stream().filter(x -> x.type.equals("wikidata")).findFirst()
            .map(x -> {
                if (x.url.resource == null) return null;
                String[] tokens = x.url.resource.split("/");
                return tokens[tokens.length-1];
            } ).orElse(null);
    }
}

