package com.rhythmix.coreservice.dto.create;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.URL;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

/**
 * DTO for {@link com.rhythmix.coreservice.entity.Track}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class TrackCreateDto implements Serializable {
    @NotNull
    @Size(max = 255)
    private String title;
    private String description;
    @NotNull
    private MultipartFile audioFile;
    @URL(message = "Cover URL must be a valid URL")
    private String coverUrl;
    private MultipartFile coverFile;
    @NotNull
    private Boolean explicit = false;
    private LocalDate releaseDate;
    @NotNull
    private UUID artistId;
    private UUID albumId;
}