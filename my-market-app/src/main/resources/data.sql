INSERT INTO my_shop.carts (name) VALUES ('my_cart')
                                 ON CONFLICT (name) DO NOTHING;

SET SEARCH_PATH = my_shop;