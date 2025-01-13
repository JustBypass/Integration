CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- SEQUENCE: public.role__role_id_seq

-- DROP SEQUENCE IF EXISTS public.role__role_id_seq;

CREATE SEQUENCE IF NOT EXISTS public.role__role_id_seq
    INCREMENT 1
    START 1
    MINVALUE 1
    MAXVALUE 2147483647
    CACHE 1;

ALTER SEQUENCE public.role__role_id_seq
    OWNER TO admin;

-- Table: public.roleфф

-- DROP TABLE IF EXISTS public.role;

CREATE TABLE IF NOT EXISTS public.role
(
    id integer NOT NULL DEFAULT nextval('role__role_id_seq'::regclass),
    name_role text COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT role_pkey PRIMARY KEY (id)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.role
    OWNER to admin;

INSERT INTO public.role (name_role)
    VALUES 
        ('Руководитель проекта'),
        ('Участник');

-- Table: public.user_app

-- DROP TABLE IF EXISTS public.user_app;

CREATE TABLE IF NOT EXISTS public.users
(
    id uuid PRIMARY KEY,
    first_name text COLLATE pg_catalog."default" NOT NULL,
    middle_name text COLLATE pg_catalog."default" NOT NULL,
    last_name text COLLATE pg_catalog."default" NOT NULL,
    "position" text COLLATE pg_catalog."default" NOT NULL,
    is_admin boolean NOT NULL,
    login text COLLATE pg_catalog."default" NOT NULL,
    password bytea NOT NULL,
    created_at timestamptz NOT NULL,
    updated_at timestamptz NOT NULL
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.users
    OWNER to admin;

-- Table: public.project

-- DROP TABLE IF EXISTS public.project;

CREATE TABLE IF NOT EXISTS public.project
(
    id uuid PRIMARY KEY,
    name text COLLATE pg_catalog."default" NOT NULL,
    description text COLLATE pg_catalog."default" NOT NULL,
    is_active boolean NOT NULL default true,
    created_at timestamptz NOT NULL,
    updated_at timestamptz NOT NULL
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.project
    OWNER to admin;

-- Table: public.participants_project

-- DROP TABLE IF EXISTS public.participants_project;

CREATE TABLE IF NOT EXISTS public.participants_project
(
    user_id uuid NOT NULL,
    project_id uuid NOT NULL,
    role_id integer NOT NULL,
    is_admin_project boolean NOT NULL,
    
    CONSTRAINT user_in_project_pk PRIMARY KEY (user_id, project_id),
    CONSTRAINT user_in_project_user_fk FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT user_in_project_project_fk FOREIGN KEY (project_id) REFERENCES project(id) ON DELETE CASCADE
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.participants_project
    OWNER to admin;

CREATE TYPE status_task AS ENUM ('Открыта', 'Отложено', 'В работе', 'На тестировании', 'На ревью', 'Завершена');

-- Table: public.task

-- DROP TABLE IF EXISTS public.task;

CREATE TABLE IF NOT EXISTS public.task
(
    id uuid PRIMARY KEY,
    name text COLLATE pg_catalog."default" NOT NULL,
    description text COLLATE pg_catalog."default" NOT NULL,
    status status_task NOT NULL,
    project_id uuid NOT NULL,
    created_at timestamptz NOT NULL,
    deadline timestamptz NOT NULL,
    author_id uuid NOT NULL,
    executor_id uuid NOT NULL,
    tester_id uuid NOT NULL,
    CONSTRAINT fkey_task_project FOREIGN KEY (project_id) REFERENCES project(id) ON DELETE CASCADE,
    CONSTRAINT fkey_task_author FOREIGN KEY (author_id) REFERENCES users(id),
    CONSTRAINT fkey_task_executor FOREIGN KEY (executor_id) REFERENCES users(id),
    CONSTRAINT fkey_task_tester FOREIGN KEY (tester_id) REFERENCES users(id)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.task
    OWNER to admin;




CREATE VIEW public.testers_view AS
SELECT DISTINCT
    u.first_name AS name,
    u.last_name AS surname,
    u.middle_name AS patronymic,
    u.is_admin AS is_active,
    r.name_role AS role,
    u.login
FROM public.task t
         JOIN public.users u ON t.tester_id = u.id
         JOIN public.participants_project pp ON pp.user_id = u.id
         JOIN public.role r ON r.id = pp.role_id
WHERE t.tester_id IS NOT NULL;


INSERT INTO public.role (id, name_role)
VALUES
    (1, 'Руководитель проекта'),
    (2, 'Участник')
    ON CONFLICT DO NOTHING;


INSERT INTO public.users (id, first_name, middle_name, last_name, position, is_admin, login, password, created_at, updated_at)
VALUES
    ('1f8b9c10-d79b-11ed-afa1-0242ac120002', 'Иван', 'Иванович', 'Петров', 'Разработчик', false, 'ivan.petrov', '\\x73656372657431', NOW(), NOW()),
    ('2a1c8d20-d79b-11ed-afa1-0242ac120002', 'Петр', 'Петрович', 'Сидоров', 'Тестировщик', false, 'petr.sidorov', '\\x73656372657432', NOW(), NOW())
    ON CONFLICT DO NOTHING;


INSERT INTO public.project (id, name, description, is_active, created_at, updated_at)
VALUES
    ('3b2d9e30-d79b-11ed-afa1-0242ac120002', 'Проект А', 'Описание проекта А', true, NOW(), NOW())
    ON CONFLICT DO NOTHING;

INSERT INTO public.participants_project (user_id, project_id, role_id, is_admin_project)
VALUES
    ('2a1c8d20-d79b-11ed-afa1-0242ac120002', '3b2d9e30-d79b-11ed-afa1-0242ac120002', 2, false)
    ON CONFLICT DO NOTHING;

INSERT INTO public.task (id, name, description, status, project_id, created_at, deadline, author_id, executor_id, tester_id)
VALUES
    ('4c3f7f40-d79b-11ed-afa1-0242ac120002', 'Тестовая задача', 'Описание задачи', 'На тестировании', '3b2d9e30-d79b-11ed-afa1-0242ac120002', NOW(), NOW() + INTERVAL '7 days', '1f8b9c10-d79b-11ed-afa1-0242ac120002', '1f8b9c10-d79b-11ed-afa1-0242ac120002', '2a1c8d20-d79b-11ed-afa1-0242ac120002')
    ON CONFLICT DO NOTHING;

SELECT * FROM public.testers_view;
