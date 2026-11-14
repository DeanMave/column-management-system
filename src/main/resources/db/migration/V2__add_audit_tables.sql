CREATE SEQUENCE IF NOT EXISTS revinfo_seq START WITH 1 INCREMENT BY 50;

-- 2. Таблица ревизий
CREATE TABLE IF NOT EXISTS revinfo (
    id        BIGINT PRIMARY KEY DEFAULT nextval('revinfo_seq'),
    timestamp BIGINT       NOT NULL,
    username  VARCHAR(255)
);

-- Таблица аудита пользователей
CREATE TABLE IF NOT EXISTS users_aud (
    id          BIGINT   NOT NULL,
    rev         BIGINT   NOT NULL REFERENCES revinfo(id),
    revtype     SMALLINT NOT NULL,
    first_name  VARCHAR(100),
    last_name   VARCHAR(100),
    middle_name VARCHAR(100),
    login       VARCHAR(50),
    password    VARCHAR(255),
    role        VARCHAR(50),
    position    VARCHAR(100),
    is_active   BOOLEAN,
    PRIMARY KEY (id, rev)
);

-- Таблица аудита хроматографических колонок
CREATE TABLE IF NOT EXISTS hplc_column_aud (
    id                 BIGINT   NOT NULL,
    rev                BIGINT   NOT NULL REFERENCES revinfo(id),
    revtype            SMALLINT NOT NULL,
    manufacturer       VARCHAR(100),
    serial_number      VARCHAR(100),
    part_number        VARCHAR(100),
    length             INT,
    diameter           NUMERIC(5,2),
    particle_size      NUMERIC(5,2),
    installation_date  DATE,
    ph_min             DOUBLE PRECISION,
    ph_max             DOUBLE PRECISION,
    stationary_phase   VARCHAR(100),
    max_pressure       INT,
    owner_organization VARCHAR(150),
    return_date        DATE,
    status             VARCHAR(50),
    internal_code      VARCHAR(50),
    storage_location   VARCHAR(150),
    PRIMARY KEY (id, rev)
);

-- Таблица аудита журнала использования колонок
CREATE TABLE IF NOT EXISTS column_usage_log_aud (
    id                  BIGINT   NOT NULL,
    rev                 BIGINT   NOT NULL REFERENCES revinfo(id),
    revtype             SMALLINT NOT NULL,
    user_id             BIGINT,
    column_id           BIGINT,
    task_number         VARCHAR(100),
    drug_name           VARCHAR(150),
    analysis_parameters TEXT,
    storage_phase       VARCHAR(100),
    min_pressure        INT,
    max_pressure        INT,
    start_date          DATE,
    end_date            DATE,
    rejection_reason    TEXT,
    PRIMARY KEY (id, rev)
);