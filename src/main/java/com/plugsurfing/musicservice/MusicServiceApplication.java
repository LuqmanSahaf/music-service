package com.plugsurfing.musicservice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class MusicServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(MusicServiceApplication.class, args);
		log.info("Sample request: http://localhost:8080/musify/music-artist/details/f27ec8db-af05-4f36-916e-3d57f91ecf5e");
	}
}
