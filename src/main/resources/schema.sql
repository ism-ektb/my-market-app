CREATE SCHEMA IF NOt EXISTS my_shop;

CREATE TABLE IF NOT EXISTS my_shop.items
(
    id     BIGSERIAL PRIMARY KEY,
    title       VARCHAR NOT NULL UNIQUE,
    description VARCHAR NOT NULL,
    price       BIGINT  NOT NULL
);

CREATE TABLE IF NOT EXISTS my_shop.item_with_quantity
(
    item_with_quantity_id BIGSERIAL PRIMARY KEY,
    item_id               BIGINT,
    quantity              INT NOT NULL,
    FOREIGN KEY (item_id) REFERENCES my_shop.items (id)
);

CREATE TABLE IF NOT EXISTS my_shop.carts
(
    cart_id BIGSERIAL PRIMARY KEY,
    name    varchar UNIQUE
);

CREATE TABLE IF NOT EXISTS my_shop.carts_item_with_quantity
(
    cart_id               BIGINT,
    item_with_quantity_id BIGINT,
    number BIGINT UNIQUE,
    PRIMARY KEY (cart_id, item_with_quantity_id),
    FOREIGN KEY (cart_id) REFERENCES my_shop.carts (cart_id),
    FOREIGN KEY (item_with_quantity_id) REFERENCES my_shop.item_with_quantity (item_with_quantity_id) ON
        DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS my_shop.images
(
    image_id BIGSERIAL PRIMARY KEY,
    number  BIGINT,
    image    BYTEA,
    CONSTRAINT fk_image_post foreign key (number) references my_shop.items (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS my_shop.orders
(
    order_id BIGSERIAL PRIMARY KEY,
    total BIGINT
);

CREATE TABLE IF NOT EXISTS my_shop.order_item_with_quantity(
    id BIGINT,
    item_with_quantity_id BIGINT,
    number BIGINT,
    total BIGINT,
    PRIMARY KEY (id, item_with_quantity_id),
    FOREIGN KEY (id) REFERENCES my_shop.orders (order_id),
    FOREIGN KEY (item_with_quantity_id) REFERENCES my_shop.item_with_quantity (item_with_quantity_id) ON
        DELETE CASCADE
);