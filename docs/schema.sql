-- Таблица колонок
CREATE TABLE hplc_column(
id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
manufacturer VARCHAR(100) NOT NULL,
serial_number VARCHAR(100) NOT NULL,
part_number VARCHAR(100) NOT NULL,
length INTEGER NOT NULL, -- мм
diameter NUMERIC(4,2) NOT NULL, -- мм
particle_size NUMERIC(4,2) NOT NULL, -- мкм
installation_date DATE NOT NULL,
ph_min DOUBLE PRECISION NOT NULL,
ph_max DOUBLE PRECISION NOT NULL,
stationary_phase VARCHAR(100) NOT NULL,
max_pressure INTEGER NOT NULL, -- bar
owner_organization VARCHAR(100) NULL, -- NULL = наша колонка
return_date DATE NULL, -- NULL = ещё не вернули
status VARCHAR(20) NOT NULL, -- AVAILABLE, IN_USE, RETURNED
internal_code VARCHAR(100) NOT NULL UNIQUE -- ID для внутренней работы сотрудников
);

-- Таблица пользователей
CREATE TABLE users(
id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
first_name VARCHAR(100) NOT NULL,
last_name VARCHAR(100) NOT NULL,
middle_name VARCHAR(100) NULL,
login VARCHAR(50) NOT NULL UNIQUE,
password VARCHAR(100) NOT NULL,
role VARCHAR(20) NOT NULL,
is_active BOOLEAN NOT NULL DEFAULT TRUE -- Статус пользователя: АКТИВЕН, НЕАКТИВЕН
);

-- Таблица использования колонки
CREATE TABLE column_usage_log(
id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
user_id BIGINT NOT NULL, -- Пользователь
column_id BIGINT NOT NULL, -- Колонка
task_number VARCHAR(100) NOT NULL, -- Номер задания
drug_name VARCHAR(255) NOT NULL, -- Наименование препарата
analysis_parameters TEXT NULL, -- Описание ключевых данных анализа
storage_phase TEXT NULL, -- Хранение
min_pressure INTEGER NULL, -- Минимальное давление при анализе
max_pressure INTEGER NULL, -- Максимальное давление при анализе
start_date DATE NOT NULL,
end_date DATE NULL,
rejection_reason TEXT NULL, -- NULL = анализ завершен штатно
CONSTRAINT fk_log_user FOREIGN KEY (user_id) REFERENCES users(id),
CONSTRAINT fk_log_column FOREIGN KEY (column_id) REFERENCES hplc_column(id)
);