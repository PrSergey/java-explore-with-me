DROP TABLE IF EXISTS endpoint_hits;


CREATE TABLE IF NOT EXISTS endpoint_hits (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    app VARCHAR(25) NOT NULL,
    uri VARCHAR(30) NOT NULL,
    ip VARCHAR(50) NOT NULL,
    time_add TIMESTAMP
);