create table if not exists USERS
(
    id           serial primary key,
    login        varchar not null unique,
    password     varchar not null,
    "auth-token" varchar
);



INSERT INTO USERS (login, password)
VALUES ('yurec', '2212')
ON CONFLICT (login) DO NOTHING;


INSERT INTO USERS (login, password)
VALUES ('admin', 'admin')
ON CONFLICT (login) DO NOTHING;


CREATE TABLE IF NOT EXISTS FILES
(
    id       SERIAL PRIMARY KEY,
    filename VARCHAR NOT NULL,
    size     INT8,
    content  bytea  NOT NULL
);

-- drop table files

