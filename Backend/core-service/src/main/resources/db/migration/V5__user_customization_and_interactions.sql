ALTER TABLE rhythmix_user
    ADD COLUMN avatar_seed    VARCHAR(100),
    ADD COLUMN background_url TEXT;

CREATE TABLE entity_likes
(
    id          UUID PRIMARY KEY,
    entity_type VARCHAR(50) NOT NULL,
    entity_id   UUID        NOT NULL,
    user_id     UUID        NOT NULL,
    created_at  TIMESTAMP WITHOUT TIME ZONE DEFAULT now(),
    CONSTRAINT uq_entity_like UNIQUE (entity_type, entity_id, user_id),
    CONSTRAINT fk_entity_like_user FOREIGN KEY (user_id) REFERENCES rhythmix_user (id) ON DELETE CASCADE
);

CREATE INDEX idx_entity_likes_entity ON entity_likes (entity_type, entity_id);

CREATE TABLE playlist_ratings
(
    id          UUID PRIMARY KEY,
    playlist_id UUID NOT NULL,
    user_id     UUID NOT NULL,
    rating      INT  NOT NULL CHECK (rating BETWEEN 1 AND 5),
    created_at  TIMESTAMP WITHOUT TIME ZONE DEFAULT now(),
    CONSTRAINT uq_playlist_rating UNIQUE (playlist_id, user_id),
    CONSTRAINT fk_rating_playlist FOREIGN KEY (playlist_id) REFERENCES playlists (id) ON DELETE CASCADE,
    CONSTRAINT fk_rating_user FOREIGN KEY (user_id) REFERENCES rhythmix_user (id) ON DELETE CASCADE
);

CREATE INDEX idx_ratings_playlist ON playlist_ratings (playlist_id);

CREATE TABLE track_listens
(
    id          UUID PRIMARY KEY,
    track_id    UUID NOT NULL,
    user_id     UUID,
    listened_at TIMESTAMP WITHOUT TIME ZONE DEFAULT now(),
    CONSTRAINT fk_listen_track FOREIGN KEY (track_id) REFERENCES tracks (id) ON DELETE CASCADE,
    CONSTRAINT fk_listen_user FOREIGN KEY (user_id) REFERENCES rhythmix_user (id) ON DELETE SET NULL
);

CREATE INDEX idx_listens_track ON track_listens (track_id);
