package com.rhythmix.coreservice.service;

import java.io.InputStream;

public interface MinioService {

    String uploadMusicImage(InputStream stream, String filename, String contentType);

    String uploadUserImage(InputStream stream, String filename, String contentType);

    String uploadMusicAudio(InputStream stream, String filename, String contentType);
}
