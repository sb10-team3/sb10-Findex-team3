CREATE TABLE IF NOT EXISTS index_infos
(
    id                     UUID           PRIMARY KEY,
    index_classification   VARCHAR(100)   NOT NULL,
    index_name             VARCHAR(100)   NOT NULL,
    employed_items_count   INT            NOT NULL,
    base_point_in_time     DATE           NOT NULL,
    base_index             DECIMAL(18,2)  NOT NULL,
    source_type            VARCHAR(20)    NOT NULL CHECK (source_type IN ('USER','OPEN_API')),
    favorite               BOOLEAN        NOT NULL,
    created_at             TIMESTAMPTZ    NOT NULL DEFAULT CURRENT_TIMESTAMP,

    UNIQUE (index_classification, index_name)
    );

CREATE TABLE IF NOT EXISTS sync_jobs
(
    id            UUID PRIMARY KEY,
    index_info_id UUID        NOT NULL,
    job_type      VARCHAR(10) NOT NULL CHECK (job_type IN ('INDEX_INFO', 'INDEX_DATA')),
    target_date   TIMESTAMPTZ NOT NULL,
    worker        VARCHAR(15) NOT NULL,
    job_time      TIMESTAMPTZ NOT NULL,
    result        VARCHAR(7)  NOT NULL CHECK (job_type IN ('SUCCESS', 'FAILURE')),
    UNIQUE (index_info_id),
    FOREIGN KEY (index_info_id) REFERENCES index_infos (id) ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS index_datas
(
    id                  UUID PRIMARY KEY,
    index_info_id       UUID           NOT NULL,
    base_date           DATE           NOT NULL,
    source_type         VARCHAR(20)    NOT NULL CHECK ( source_type IN ('USER', 'OPEN_API')),
    market_price        DECIMAL(18, 2) NOT NULL,
    closing_price       DECIMAL(18, 2) NOT NULL,
    high_price          DECIMAL(18, 2) NOT NULL,
    low_price           DECIMAL(18, 2) NOT NULL,
    versus              DECIMAL(18, 2) NOT NULL,
    fluctuation_rate    DECIMAL(10, 2) NOT NULL,
    trading_quantity    BIGINT         NOT NULL,
    trading_price       BIGINT         NOT NULL,
    market_total_amount BIGINT         NOT NULL,
    UNIQUE (index_info_id, base_date),
    FOREIGN KEY (index_info_id) REFERENCES index_infos (id) ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS auto_sync_configs
(
    "id"            UUID    NOT NULL,
    "index_info_id" UUID    NOT NULL,
    "enabled"       BOOLEAN NULL,
    UNIQUE (index_info_id),
    FOREIGN KEY (index_info_id) REFERENCES index_infos (id) ON DELETE CASCADE
    );