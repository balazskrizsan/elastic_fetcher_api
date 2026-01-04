CREATE TABLE run_states
(
    index      VARCHAR NOT NULL PRIMARY KEY,
    timestamp  BIGINT  NOT NULL,
    doc        BIGINT  NOT NULL
);
