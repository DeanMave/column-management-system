-- Таблица колонок
CREATE TABLE hplc_column(
id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
manufacturer VARCHAR(100) NOT NULL,
serial_number VARCHAR(100) NOT NULL,
part_number VARCHAR(100) NOT NULL,
length INTEGER NOT NULL, -- мм
diameter INTEGER NOT NULL, -- мм
particle_size NUMERIC(4,2) NOT NULL, -- мкм
installation_date DATE NOT NULL,
ph_min INTEGER NOT NULL,
ph_max INTEGER NOT NULL,
stationary_phase VARCHAR(100) NOT NULL,
max_pressure INTEGER NOT NULL, -- bar
owner_organization VARCHAR(100) NULL, -- NULL = наша колонка
return_date DATE NULL, -- NULL = ещё не вернули
status VARCHAR(20) NOT NULL -- AVAILABLE, IN_USE, RETURNED
);

-- Таблица пользователей
CREATE TABLE users(
id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
first_name VARCHAR(100) NOT NULL,
last_name VARCHAR(100) NOT NULL,
login VARCHAR(50) NOT NULL,
password VARCHAR(100) NOT NULL,
role VARCHAR(20) NOT NULL
);

-- Таблица использования колонки
CREATE TABLE column_usage_log(
id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
user_id BIGINT NOT NULL, -- Пользователь
column_id BIGINT NOT NULL, -- Колонка
task_number VARCHAR(100) NOT NULL, -- Номер задания
start_date DATE NOT NULL,
end_date DATE NOT NULL,
analysis_parameters TEXT NOT NULL, -- Описание ключевых данных анализа
storage_phase TEXT NOT NULL, -- Хранение
CONSTRAINT fk_log_user FOREIGN KEY (user_id) REFERENCES users(id),
CONSTRAINT fk_log_column FOREIGN KEY (column_id) REFERENCES hplc_column(id)
);