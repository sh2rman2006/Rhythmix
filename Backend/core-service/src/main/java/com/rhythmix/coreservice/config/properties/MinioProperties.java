package com.rhythmix.coreservice.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "minio")
public class MinioProperties {
    private String url;
    private String accessKey;
    private String secretKey;
    private String bucket;
}
