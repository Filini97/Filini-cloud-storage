INSERT INTO users (username, password)
VALUES
    ('user@one.user', '$2a$10$h7QmnlSEXLF2eQS4lIlRX.FXGZ0U8tpcLYi8IPkgLG/sqQiDA/HlG'), -- pass: user_one
    ('user@two.user', '$2a$10$kNOJuKpknuUJPnOinunYDOEyJq2v3qXvyiaCMoS2CKOobT8Mfi8Da')  -- pass: user_two
    ON CONFLICT (username) DO NOTHING;
