-- Table: public.sessions

-- DROP TABLE IF EXISTS public.sessions;

CREATE TABLE IF NOT EXISTS public.sessions
(
    id SERIAL NOT NULL,
    guild character varying COLLATE pg_catalog."default" NOT NULL,
	member character varying COLLATE pg_catalog."default" NOT NULL,
    starttime character varying COLLATE pg_catalog."default" NOT NULL,
    endtime character varying COLLATE pg_catalog."default" NOT NULL,
   
    CONSTRAINT sessions_pkey PRIMARY KEY (id)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.sessions
    OWNER to postgres;
    
-- Table: public.session_exp

-- DROP TABLE IF EXISTS session_exp;

CREATE TABLE IF NOT EXISTS public.session_exp
(
    id SERIAL NOT NULL,
    session integer  NOT NULL,
	member character varying  NOT NULL,
	exp  integer   NOT NULL,
   
    CONSTRAINT session_exp_pkey PRIMARY KEY (id)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.session_exp
    OWNER to postgres;