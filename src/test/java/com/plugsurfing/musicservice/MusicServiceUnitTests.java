package com.plugsurfing.musicservice;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import com.plugsurfing.musicservice.dto.Album;
import com.plugsurfing.musicservice.dto.ErrorResponse;
import com.plugsurfing.musicservice.services.CoverArtService;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.Rule;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext
@AutoConfigureWebTestClient
class MusicServiceUnitTests {
	@Autowired WebTestClient client;
	@Autowired CoverArtService coverArtService;
	@Autowired PlugWebClients webClients;

	@Rule
	private MockWebServer mockWebServer = new MockWebServer();

	@Test
	void shouldReturn400IfInvalidMBID() {
		String invalidMBID = "invalid_mbid";

		client.get()
			.uri("/musify/music-artist/details/" + invalidMBID)
			.exchange()
			.expectStatus()
			.isBadRequest()
			.expectBody(ErrorResponse.class)
			.isEqualTo(new ErrorResponse("Invalid MBID"));
	}

	@Test
	void shouldReturn400IfNonExistentMBID() {
		String fakeMBID = "f27ec8db-af05-4f36-916e-3d57f91ecf3e";
		mockWebServer.enqueue(new MockResponse().setBody("{}").setResponseCode(404));

		var newWebClient = WebClient.builder()
			.baseUrl(mockWebServer.url("/").url().toString())
			.build();

		webClients.setMusicBrainzClient(newWebClient);

		client.get()
			.uri("/musify/music-artist/details/" + fakeMBID)
			.exchange()
			.expectStatus()
			.isBadRequest()
			.expectBody(ErrorResponse.class)
			.isEqualTo(new ErrorResponse(BAD_REQUEST.toString()));
	}


	/**
	 * If imageUrl is not found on http://coverartarchive.org, then resume without it and return the album as is.
	 * */
	@Test
	void resumeWithoutImageUrlIfNotFound() {
		mockWebServer.enqueue(new MockResponse().setBody("{}").setResponseCode(404));

		var mockCoverArtClient = WebClient.builder()
			.baseUrl(mockWebServer.url("/").url().toString())
			.build();
		webClients.setCoverArtClient(mockCoverArtClient);

		var coverArtFakeId = "c31a5e2b-0bf8-32e0-8aeb-ef4ba99739a2";

		Album album = new Album(coverArtFakeId ,"O Mio Babino Carro", null);

		var albumResult = coverArtService.fetchAlbumCoverArt(album);

		StepVerifier.create(albumResult)
			.expectNext(album)
			.verifyComplete();
	}



}
