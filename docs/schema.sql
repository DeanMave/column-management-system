create TABLE hplc_column(
id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
manufacturer VARCHAR(100) NOT NULL,
serial_number VARCHAR(100) NOT NULL,
part_number VARCHAR(100) NOT NULL,
length INTEGER NOT NULL, -- мм
diameter INTEGER NOT NULL, -- мм
particle_size NUMERIC(4,2) NOT NULL, -- мкм
installation_date DATE,
ph_min INTEGER NOT NULL,
ph_max INTEGER NOT NULL,
stationary_phase VARCHAR(100) NOT NULL,
max_pressure INTEGER NOT NULL, -- bar
owner_organization VARCHAR(100) NULL, -- NULL = наша колонка
return_Date DATE NULL, -- NULL = ещё не вернули
status VARCHAR(20) NOT NULL -- AVAILABLE, IN_USE, RETURNED
);