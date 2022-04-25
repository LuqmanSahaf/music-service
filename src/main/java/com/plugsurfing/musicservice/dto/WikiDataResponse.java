package com.plugsurfing.musicservice.dto;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WikiDataResponse {
    private Map<String, Entity> entities;

    @Data
    @Getter @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Entity {
        private SiteLinks sitelinks;
    }

    @Data
    @Getter @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SiteLinks {
        private EnWiki enwiki;
    }

    @Data
    @Getter @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EnWiki {
        private String title;
        private String url;
    }

    public String getWikiPageTitle(String resourceId) {
        String[] tokens;

        try {
            tokens = this.getEntities().get(resourceId).getSitelinks().getEnwiki().getUrl().split("/");
            if (tokens.length != 0)
                return tokens[tokens.length - 1];
        } catch (NullPointerException e) {
            return "";
        }
        return "";
    }
}
