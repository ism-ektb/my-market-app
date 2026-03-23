CREATE SCHEMA IF NOt EXISTS my_shop;

CREATE TABLE IF NOT EXISTS my_shop.items
(
    item_id     BIGSERIAL PRIMARY KEY,
    title       VARCHAR NOT NULL UNIQUE,
    description VARCHAR NOT NULL,
    price       BIGINT  NOT NULL
);

CREATE TABLE IF NOT EXISTS my_shop.item_with_quantity
(
    item_with_quantity_id BIGSERIAL PRIMARY KEY,
    item_id               BIGINT,
    quantity              INT NOT NULL,
    FOREIGN KEY (item_id) REFERENCES my_shop.items (item_id)
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
    CONSTRAINT fk_image_post foreign key (number) references my_shop.items (item_id) ON DELETE CASCADE
);