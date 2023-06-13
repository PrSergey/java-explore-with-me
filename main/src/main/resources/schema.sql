DROP TABLE IF EXISTS users, categories, locations, events, requests, compilations CASCADE;

CREATE TABLE IF NOT EXISTS users (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(512) NOT NULL,
    CONSTRAINT pk_user PRIMARY KEY (id),
    CONSTRAINT uq_user_email UNIQUE (email),
    CONSTRAINT user_name_unique UNIQUE (name)
    );

CREATE TABLE IF NOT EXISTS categories (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name VARCHAR(50) NOT NULL,
    CONSTRAINT pk_cat PRIMARY KEY (id),
    CONSTRAINT CATEGORY_NAME_UNIQUE UNIQUE (name)
    );

CREATE TABLE IF NOT EXISTS locations (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    lat DOUBLE PRECISION NOT NULL,
    lon DOUBLE PRECISION NOT NULL,
    CONSTRAINT pk_loc PRIMARY KEY (id)
    );

CREATE TABLE IF NOT EXISTS events (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    annotation VARCHAR,
    cat_id INTEGER NOT NULL,
    confirmed_requests BIGINT,
    created_on TIMESTAMP,
    description TEXT,
    event_date TIMESTAMP NOT NULL,
    initiator_id BIGINT NOT NULL,
    loc_id BIGINT NOT NULL,
    paid BOOLEAN NOT NULL,
    participant_limit INTEGER,
    published_on TIMESTAMP,
    request_moderation BOOLEAN,
    state_event VARCHAR,
    title VARCHAR NOT NULL,
    views BIGINT,
    CONSTRAINT pk_event PRIMARY KEY (id),
    CONSTRAINT fk_events_to_users FOREIGN KEY (initiator_id) REFERENCES users(id),
    CONSTRAINT fk_events_to_categories FOREIGN KEY (cat_id) REFERENCES categories(id),
    CONSTRAINT fk_events_to_location FOREIGN KEY (loc_id) REFERENCES locations(id)
    );

CREATE TABLE IF NOT EXISTS requests (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    created TIMESTAMP,
    event_id BIGINT,
    requester_id BIGINT,
    status VARCHAR(10),
    CONSTRAINT pk_request PRIMARY KEY (id),
    CONSTRAINT fk_request_to_event FOREIGN KEY (event_id) REFERENCES events(id),
    CONSTRAINT fk_request_to_user FOREIGN KEY (requester_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS compilations (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    pinned BOOLEAN,
    title VARCHAR,
    CONSTRAINT pk_compilation PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS compilations_events (
    compilation_id BIGINT CONSTRAINT compilations_events_compilations_id_fk REFERENCES compilations,
    event_id BIGINT CONSTRAINT compilations_events_events_id_fk REFERENCES events
);

