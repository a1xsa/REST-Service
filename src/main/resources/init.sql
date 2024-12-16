CREATE TABLE IF NOT EXISTS customers
(
    id    SERIAL PRIMARY KEY,          -- Уникальный идентификатор пользователя
    name  VARCHAR(100) NOT NULL,       -- Имя пользователя
    email VARCHAR(100) NOT NULL UNIQUE -- Email пользователя
);

CREATE TABLE IF NOT EXISTS orders
(
    id          SERIAL PRIMARY KEY,      -- Уникальный идентификатор заказа
    customer_id     INT            NOT NULL, -- Ссылка на пользователя
    description VARCHAR(100)   NOT NULL,
    amount      NUMERIC(10, 2) NOT NULL, -- Сумма заказа
    CONSTRAINT fk_customer FOREIGN KEY (customer_id) REFERENCES customers (id)
        ON DELETE CASCADE
        ON UPDATE CASCADE               -- Внешний ключ с каскадными операциями
);