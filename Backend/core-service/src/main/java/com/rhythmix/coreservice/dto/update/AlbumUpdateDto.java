package com.rhythmix.coreservice.dto.update;

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
 * DTO for {@link com.rhythmix.coreservice.entity.Album}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class AlbumUpdateDto implements Serializable {
    @NotNull
    private UUID id;
    @NotNull
    @Size(max = 255)
    private String title;
    private String description;
    @URL(message = "Not valid URL")
    private String coverUrl;
    private MultipartFile coverFile;
    private LocalDate releaseDate;
}