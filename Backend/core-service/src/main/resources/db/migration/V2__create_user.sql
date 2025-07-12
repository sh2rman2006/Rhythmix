CREATE TABLE rhythmix_user
(
    id          UUID PRIMARY KEY,
    keycloak_id UUID UNIQUE  NOT NULL,
    username    VARCHAR(50)  NOT NULL,
    email       VARCHAR(100) NOT NULL,
    first_name  VARCHAR(50),
    last_name   VARCHAR(50),
    created_at  TIMESTAMP    NOT NULL DEFAULT now(),
    updated_at  TIMESTAMP    NOT NULL DEFAULT now()
);
