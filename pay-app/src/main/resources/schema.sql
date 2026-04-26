CREATE SCHEMA IF NOT EXISTS my_bank;
CREATE TABLE IF NOT EXISTS my_bank.accounts(
    id BIGSERIAL PRIMARY KEY ,
    user_id BIGINT UNIQUE ,
    sum BIGINT,
    CONSTRAINT sum_more_null check ( sum >= 0 )
);