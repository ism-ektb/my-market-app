INSERT INTO my_bank.accounts (user_id, sum) VALUES (1, 10000)
                                 ON CONFLICT (user_id) DO NOTHING;

