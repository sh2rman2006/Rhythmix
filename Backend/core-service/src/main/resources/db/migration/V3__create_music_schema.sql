-- artists
CREATE TABLE artists
(
    id                UUID PRIMARY KEY,
    stage_name        VARCHAR(255)                NOT NULL,
    real_name         VARCHAR(255),
    biography         TEXT,
    country           VARCHAR(100),
    city              VARCHAR(100),
    profile_image_url TEXT,
    file_image_url    TEXT,
    created_by        UUID                        NOT NULL,
    created_at        TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at        TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW()
);

-- albums
CREATE TABLE albums
(
    id           UUID PRIMARY KEY,
    title        VARCHAR(255)                NOT NULL,
    description  TEXT,
    cover_url    TEXT,
    release_date DATE,
    artist_id    UUID                        NOT NULL,
    created_at   TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_album_artist FOREIGN KEY (artist_id) REFERENCES artists (id) ON DELETE CASCADE
);

-- tracks
CREATE TABLE tracks
(
    id           UUID PRIMARY KEY,
    title        VARCHAR(255)                NOT NULL,
    description  TEXT,
    file_url     TEXT                        NOT NULL,
    cover_url    TEXT,
    duration     INTEGER, -- длительность в секундах
    explicit     BOOLEAN                     NOT NULL DEFAULT FALSE,
    release_date DATE,
    uploaded_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    uploaded_by  UUID                        NOT NULL,
    artist_id    UUID                        NOT NULL,
    album_id     UUID,
    CONSTRAINT fk_track_artist FOREIGN KEY (artist_id) REFERENCES artists (id) ON DELETE CASCADE,
    CONSTRAINT fk_track_album FOREIGN KEY (album_id) REFERENCES albums (id) ON DELETE SET NULL
);

-- playlists
CREATE TABLE playlists
(
    id          UUID PRIMARY KEY,
    name        VARCHAR(255)                NOT NULL,
    description TEXT,
    cover_url   TEXT,
    owner_id    UUID                        NOT NULL,
    is_public   BOOLEAN                     NOT NULL DEFAULT TRUE,
    is_system   BOOLEAN                     NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW()
);

-- genres
CREATE TABLE genres
(
    id          UUID PRIMARY KEY,
    name        VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    parent_id   UUID,
    CONSTRAINT fk_genre_parent FOREIGN KEY (parent_id) REFERENCES genres (id) ON DELETE SET NULL
);

-- artist_genres
CREATE TABLE artist_genres
(
    artist_id UUID NOT NULL,
    genre_id  UUID NOT NULL,
    PRIMARY KEY (artist_id, genre_id),
    CONSTRAINT fk_artist_genres_artist FOREIGN KEY (artist_id) REFERENCES artists (id) ON DELETE CASCADE,
    CONSTRAINT fk_artist_genres_genre FOREIGN KEY (genre_id) REFERENCES genres (id) ON DELETE CASCADE
);

-- album_genres
CREATE TABLE album_genres
(
    album_id UUID NOT NULL,
    genre_id UUID NOT NULL,
    PRIMARY KEY (album_id, genre_id),
    CONSTRAINT fk_album_genres_album FOREIGN KEY (album_id) REFERENCES albums (id) ON DELETE CASCADE,
    CONSTRAINT fk_album_genres_genre FOREIGN KEY (genre_id) REFERENCES genres (id) ON DELETE CASCADE
);

-- track_genres
CREATE TABLE track_genres
(
    track_id UUID NOT NULL,
    genre_id UUID NOT NULL,
    PRIMARY KEY (track_id, genre_id),
    CONSTRAINT fk_track_genres_track FOREIGN KEY (track_id) REFERENCES tracks (id) ON DELETE CASCADE,
    CONSTRAINT fk_track_genres_genre FOREIGN KEY (genre_id) REFERENCES genres (id) ON DELETE CASCADE
);

-- playlist_tracks
CREATE TABLE playlist_tracks
(
    playlist_id UUID                        NOT NULL,
    track_id    UUID                        NOT NULL,
    added_at    TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    PRIMARY KEY (playlist_id, track_id),
    CONSTRAINT fk_playlist_tracks_playlist FOREIGN KEY (playlist_id) REFERENCES playlists (id) ON DELETE CASCADE,
    CONSTRAINT fk_playlist_tracks_track FOREIGN KEY (track_id) REFERENCES tracks (id) ON DELETE CASCADE
);

-- Индексы
CREATE INDEX idx_tracks_artist_id ON tracks (artist_id);
CREATE INDEX idx_tracks_album_id ON tracks (album_id);
CREATE INDEX idx_albums_artist_id ON albums (artist_id);
CREATE INDEX idx_playlists_owner_id ON playlists (owner_id);
CREATE INDEX idx_artist_genres_genre_id ON artist_genres (genre_id);
CREATE INDEX idx_album_genres_genre_id ON album_genres (genre_id);
CREATE INDEX idx_track_genres_genre_id ON track_genres (genre_id);
CREATE INDEX idx_playlist_tracks_track_id ON playlist_tracks (track_id);
