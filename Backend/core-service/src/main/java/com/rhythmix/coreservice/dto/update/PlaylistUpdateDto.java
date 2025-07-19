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
import java.util.UUID;

/**
 * DTO for {@link com.rhythmix.coreservice.entity.Playlist}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class PlaylistUpdateDto implements Serializable {
    @NotNull
    private UUID id;
    @Size(max = 255)
    private String name;
    private String description;
    @URL
    private String coverUrl;
    private MultipartFile coverFile;
    @NotNull
    private Boolean isPublic = false;
}