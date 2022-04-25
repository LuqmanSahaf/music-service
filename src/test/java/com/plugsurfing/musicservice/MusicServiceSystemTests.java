package com.plugsurfing.musicservice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.plugsurfing.musicservice.dto.ArtistDetailsResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class MusicServiceSystemTests {
    @Autowired WebTestClient client;

    /**
     * If imageUrl is not found on http://coverartarchive.org, then resume without it and return the album as is.
     * */
    @Test
    public void shouldPresentAlbumsWithoutCoverArtIfNotFound() {
        String mbid = "8b8a38a9-a290-4560-84f6-3d4466e8d791";

        var response = client.get()
            .uri("/musify/music-artist/details/" + mbid)
            .exchange()
            .expectStatus()
            .isOk() // should return ok
            .expectBody(ArtistDetailsResponse.class)
            .returnResult()
            .getResponseBody();

        assertNotNull(response);
        assertNotNull(response.getName());
        assertEquals("John Williams", response.getName());

        var albums = response.getAlbums();

        assertTrue(albums.stream().anyMatch(album -> album.getImageUrl() == null));
    }
}
