CREATE TABLE tb_tweets (
    tweet_id BIGSERIAL PRIMARY KEY,
    user_id UUID NOT NULL,
    content TEXT NOT NULL,
    creation_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_tweet_user FOREIGN KEY (user_id) REFERENCES tb_users(user_id) ON DELETE CASCADE
);