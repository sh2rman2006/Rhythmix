package com.rhythmix.coreservice.dto.create;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.URL;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

/**
 * DTO for {@link com.rhythmix.coreservice.entity.Playlist}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class PlaylistCreateDto implements Serializable {
    @NotNull
    @Size(max = 255)
    @NotBlank
    private String name;
    private String description;
    @URL(message = "Cover URL must be a valid URL")
    private String coverUrl;
    private MultipartFile coverFile;
    @NotNull
    private Boolean isPublic = false;
}