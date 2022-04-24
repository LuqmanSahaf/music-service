package com.plugsurfing.musicservice.dto.util;

import static org.springframework.beans.BeanUtils.copyProperties;

import com.plugsurfing.musicservice.dto.ArtistDetailsResponse;
import com.plugsurfing.musicservice.dto.MusicBrainzArtistResponse;
import org.springframework.stereotype.Component;

@Component
public class ResponseMapper {
    public ArtistDetailsResponse toArtistDetailsResponse(MusicBrainzArtistResponse response) {
        var artistDetails = new ArtistDetailsResponse();
        copyProperties(response,artistDetails);
        return artistDetails;
    }
}
