package com.rhythmix.coreservice.dto.create;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
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
public class AlbumCreateDto implements Serializable {
    @NotNull
    @NotBlank
    @Size(max = 255)
    private String title;
    private String description;
    private String coverUrl;
    private MultipartFile coverFile;
    private LocalDate releaseDate;
    private UUID artistId;
}