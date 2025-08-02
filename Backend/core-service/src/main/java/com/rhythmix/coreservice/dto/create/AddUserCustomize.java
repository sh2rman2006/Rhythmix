package com.rhythmix.coreservice.dto.create;

import org.hibernate.validator.constraints.URL;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

public record AddUserCustomize(
        @URL(message = "avatarUrl должен быть корректным URL") String avatarUrl,
        MultipartFile avatarFile,
        @URL(message = "backgroundUrl должен быть корректным URL") String backgroundUrl,
        MultipartFile backgroundFile
) implements Serializable {
}
