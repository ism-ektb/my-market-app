INSERT INTO my_shop.carts (name) VALUES ('my_cart')
                                 ON CONFLICT (name) DO NOTHING;

INSERT INTO my_shop.users (email, password, roles) values ('admin',
                                                           '{bcrypt}$2a$10$BRy5xjkyJ2n9j2nG8O3C9ebTDVTKBG6W2XsiLjutsKwGkyP7M45mG', 'ADMIN')
ON CONFLICT (email) DO NOTHING;

SET SEARCH_PATH = my_shop;