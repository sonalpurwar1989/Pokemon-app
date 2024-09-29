BEGIN TRANSACTION;

DROP TABLE IF EXISTS pokemon, users, users_pokemon;

CREATE TABLE users (
  id serial PRIMARY KEY,
  username varchar(255) NOT NULL UNIQUE,     -- Username
  password varchar(32) NOT NULL,      -- Password (hashed, not plain-text)
  salt varchar(256) NOT NULL		  -- Password Salt
);

CREATE TABLE pokemon (
    id serial PRIMARY KEY, -- database id, pk
    api_id int NOT NULL,
    name varchar(56) NOT NULL UNIQUE,
    base_experience int,
    height int,
    weight int,
    back_url varchar(256),
    front_url varchar(256)
);

CREATE TABLE users_pokemon ( -- many to many relationship
    pokemon_id int NOT NULL,
    users_id int NOT NULL,
    CONSTRAINT fk_pokemon FOREIGN KEY (pokemon_id) REFERENCES pokemon(id),
    CONSTRAINT fk_users FOREIGN KEY (users_id) REFERENCES users(id)
);

ALTER TABLE users_pokemon
  ADD PRIMARY KEY (pokemon_id, users_id);

COMMIT;