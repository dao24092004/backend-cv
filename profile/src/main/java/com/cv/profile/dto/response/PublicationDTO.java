package com.cv.profile.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PublicationDTO {
    private Long id;
    private String title;
    private String publisher;
    private String releaseDate;
    private String link;
}
