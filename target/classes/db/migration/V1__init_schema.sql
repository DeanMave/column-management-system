-- Создание последовательностей для генерации внутренних кодов колонок
CREATE SEQUENCE IF NOT EXISTS column_int_code_seq START WITH 1 INCREMENT BY 1; -- Счётчик внутренних колонок организации
CREATE SEQUENCE IF NOT EXISTS column_ext_code_seq START WITH 1 INCREMENT BY 1; -- Счётчик внешних колонок организации

-- Таблица пользователей
CREATE TABLE IF NOT EXISTS users (
    id          BIGSERIAL PRIMARY KEY,
    first_name  VARCHAR(100) NOT NULL,
    last_name   VARCHAR(100) NOT NULL,
    middle_name VARCHAR(100),
    login       VARCHAR(50)  NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    role        VARCHAR(50)  NOT NULL, -- Роль пользователя
    position    VARCHAR(100), -- Должность сотрудника организации
    is_active   BOOLEAN      NOT NULL DEFAULT TRUE -- Статус пользователя: АКТИВЕН, НЕАКТИВЕН
);

-- Таблица хроматографических колонок (
CREATE TABLE IF NOT EXISTS hplc_column (
    id                 BIGSERIAL PRIMARY KEY,
    manufacturer       VARCHAR(100)  NOT NULL,
    serial_number      VARCHAR(100)  NOT NULL,
    part_number        VARCHAR(100)  NOT NULL,
    length             INT           NOT NULL, --мм
    diameter           NUMERIC(5,2) NOT NULL, --мм
    particle_size      NUMERIC(5,2) NOT NULL, --мкм
    installation_date  DATE          NOT NULL,
    ph_min             DOUBLE PRECISION NOT NULL,
    ph_max             DOUBLE PRECISION NOT NULL,
    stationary_phase   VARCHAR(100)  NOT NULL,
    max_pressure       INT           NOT NULL, --bar
    owner_organization VARCHAR(150), -- NULL = наша колонка
    return_date        DATE, -- NULL = ещё не вернули
    status             VARCHAR(50)   NOT NULL DEFAULT 'AVAILABLE', -- AVAILABLE, IN_USE, RETURNED
    internal_code      VARCHAR(50)   NOT NULL UNIQUE, -- ID для внутренней работы сотрудников
    storage_location   VARCHAR(150) -- Место хранения колонки
);

-- Таблица журнала использования колонок (column_usage_log)
CREATE TABLE IF NOT EXISTS column_usage_log (
    id                  BIGSERIAL PRIMARY KEY,
    user_id             BIGINT       NOT NULL REFERENCES users(id), -- Пользователь
    column_id      BIGINT       NOT NULL REFERENCES hplc_column(id), -- Колонка
    task_number         VARCHAR(100) NOT NULL, -- Номер задания
    drug_name           VARCHAR(150) NOT NULL, -- Наименование препарата
    analysis_parameters TEXT, -- Описание ключевых данных анализа
    storage_phase       VARCHAR(100), -- Хранение
    min_pressure        INT, -- Минимальное давление при анализе
    max_pressure        INT, -- Максимальное давление при анализе
    start_date          DATE         NOT NULL,
    end_date            DATE, -- NULLABLE: маркер открытого/активного анализа
    rejection_reason    TEXT -- NULL = анализ завершен штатно
);

-- Индексы
CREATE INDEX IF NOT EXISTS idx_usage_log_user ON column_usage_log(user_id);
CREATE INDEX IF NOT EXISTS idx_usage_log_column ON column_usage_log(column_id);