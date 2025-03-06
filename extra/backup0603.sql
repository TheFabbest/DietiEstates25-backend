--
-- PostgreSQL database dump
--

-- Dumped from database version 16.1
-- Dumped by pg_dump version 16.1

-- Started on 2025-03-06 22:49:42

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- TOC entry 7 (class 2615 OID 82539)
-- Name: DietiEstates2025; Type: SCHEMA; Schema: -; Owner: postgres
--

CREATE SCHEMA "DietiEstates2025";


ALTER SCHEMA "DietiEstates2025" OWNER TO postgres;

--
-- TOC entry 5076 (class 0 OID 0)
-- Dependencies: 7
-- Name: SCHEMA "DietiEstates2025"; Type: COMMENT; Schema: -; Owner: postgres
--

COMMENT ON SCHEMA "DietiEstates2025" IS 'Database for DietiEstates2025 real estate platform';


--
-- TOC entry 956 (class 1247 OID 82602)
-- Name: classe_energetica; Type: TYPE; Schema: DietiEstates2025; Owner: postgres
--

CREATE TYPE "DietiEstates2025".classe_energetica AS ENUM (
    'A4',
    'A3',
    'A2',
    'A1',
    'B',
    'C',
    'D',
    'E',
    'F',
    'G',
    'Non applicabile'
);


ALTER TYPE "DietiEstates2025".classe_energetica OWNER TO postgres;

--
-- TOC entry 968 (class 1247 OID 82682)
-- Name: giardino; Type: TYPE; Schema: DietiEstates2025; Owner: postgres
--

CREATE TYPE "DietiEstates2025".giardino AS ENUM (
    'Privato',
    'Comune',
    'Non presente'
);


ALTER TYPE "DietiEstates2025".giardino OWNER TO postgres;

--
-- TOC entry 953 (class 1247 OID 82588)
-- Name: stato_immobile; Type: TYPE; Schema: DietiEstates2025; Owner: postgres
--

CREATE TYPE "DietiEstates2025".stato_immobile AS ENUM (
    'Nuovo',
    'In buono stato',
    'Ristrutturato',
    'Da ristrutturare',
    'In cattivo stato',
    'In costruzione'
);


ALTER TYPE "DietiEstates2025".stato_immobile OWNER TO postgres;

--
-- TOC entry 962 (class 1247 OID 82642)
-- Name: stato_offerta; Type: TYPE; Schema: DietiEstates2025; Owner: postgres
--

CREATE TYPE "DietiEstates2025".stato_offerta AS ENUM (
    'In attesa',
    'Accettata',
    'Rifiutata',
    'Ribattuta',
    'Ritirata'
);


ALTER TYPE "DietiEstates2025".stato_offerta OWNER TO postgres;

--
-- TOC entry 959 (class 1247 OID 82626)
-- Name: tipologia_proprietà; Type: TYPE; Schema: DietiEstates2025; Owner: postgres
--

CREATE TYPE "DietiEstates2025"."tipologia_proprietà" AS ENUM (
    'Intera proprietà',
    'Nuda proprietà',
    'Parziale proprietà',
    'Usufrutto',
    'Multiproprietà',
    'Superficiaria',
    'A reddito'
);


ALTER TYPE "DietiEstates2025"."tipologia_proprietà" OWNER TO postgres;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 238 (class 1259 OID 82551)
-- Name: agenzia; Type: TABLE; Schema: DietiEstates2025; Owner: postgres
--

CREATE TABLE "DietiEstates2025".agenzia (
    id integer NOT NULL,
    nome character varying NOT NULL,
    "città" character varying NOT NULL,
    via character varying NOT NULL,
    civico character varying,
    edificio character varying,
    provincia character varying NOT NULL
);


ALTER TABLE "DietiEstates2025".agenzia OWNER TO postgres;

--
-- TOC entry 237 (class 1259 OID 82550)
-- Name: Agenzia_idagenzia_seq; Type: SEQUENCE; Schema: DietiEstates2025; Owner: postgres
--

CREATE SEQUENCE "DietiEstates2025"."Agenzia_idagenzia_seq"
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE "DietiEstates2025"."Agenzia_idagenzia_seq" OWNER TO postgres;

--
-- TOC entry 5077 (class 0 OID 0)
-- Dependencies: 237
-- Name: Agenzia_idagenzia_seq; Type: SEQUENCE OWNED BY; Schema: DietiEstates2025; Owner: postgres
--

ALTER SEQUENCE "DietiEstates2025"."Agenzia_idagenzia_seq" OWNED BY "DietiEstates2025".agenzia.id;


--
-- TOC entry 252 (class 1259 OID 83135)
-- Name: autorimessa; Type: TABLE; Schema: DietiEstates2025; Owner: postgres
--

CREATE TABLE "DietiEstates2025".autorimessa (
    id integer NOT NULL,
    ha_sorveglianza boolean DEFAULT false,
    piani character varying[] NOT NULL,
    numero_piani integer DEFAULT 1,
    CONSTRAINT valid_numero_piani CHECK ((numero_piani > 0))
);


ALTER TABLE "DietiEstates2025".autorimessa OWNER TO postgres;

--
-- TOC entry 244 (class 1259 OID 82851)
-- Name: categoria_immobile; Type: TABLE; Schema: DietiEstates2025; Owner: postgres
--

CREATE TABLE "DietiEstates2025".categoria_immobile (
    id integer NOT NULL,
    categoria character varying NOT NULL,
    sottocategoria character varying NOT NULL,
    "è_attivo" boolean DEFAULT true
);


ALTER TABLE "DietiEstates2025".categoria_immobile OWNER TO postgres;

--
-- TOC entry 243 (class 1259 OID 82850)
-- Name: categoria_immobile_id_seq; Type: SEQUENCE; Schema: DietiEstates2025; Owner: postgres
--

CREATE SEQUENCE "DietiEstates2025".categoria_immobile_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE "DietiEstates2025".categoria_immobile_id_seq OWNER TO postgres;

--
-- TOC entry 5078 (class 0 OID 0)
-- Dependencies: 243
-- Name: categoria_immobile_id_seq; Type: SEQUENCE OWNED BY; Schema: DietiEstates2025; Owner: postgres
--

ALTER SEQUENCE "DietiEstates2025".categoria_immobile_id_seq OWNED BY "DietiEstates2025".categoria_immobile.id;


--
-- TOC entry 242 (class 1259 OID 82671)
-- Name: contratto; Type: TABLE; Schema: DietiEstates2025; Owner: postgres
--

CREATE TABLE "DietiEstates2025".contratto (
    id integer NOT NULL,
    nome character varying(255) NOT NULL,
    "è_attivo" boolean DEFAULT true
);


ALTER TABLE "DietiEstates2025".contratto OWNER TO postgres;

--
-- TOC entry 241 (class 1259 OID 82670)
-- Name: contratto_idcontratto_seq; Type: SEQUENCE; Schema: DietiEstates2025; Owner: postgres
--

CREATE SEQUENCE "DietiEstates2025".contratto_idcontratto_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE "DietiEstates2025".contratto_idcontratto_seq OWNER TO postgres;

--
-- TOC entry 5079 (class 0 OID 0)
-- Dependencies: 241
-- Name: contratto_idcontratto_seq; Type: SEQUENCE OWNED BY; Schema: DietiEstates2025; Owner: postgres
--

ALTER SEQUENCE "DietiEstates2025".contratto_idcontratto_seq OWNED BY "DietiEstates2025".contratto.id;


--
-- TOC entry 248 (class 1259 OID 82875)
-- Name: immobile; Type: TABLE; Schema: DietiEstates2025; Owner: postgres
--

CREATE TABLE "DietiEstates2025".immobile (
    id integer NOT NULL,
    description text,
    prezzo numeric(12,2) NOT NULL,
    superficie integer NOT NULL,
    id_contratto integer NOT NULL,
    id_categoria_immobile integer NOT NULL,
    stato_immobile "DietiEstates2025".stato_immobile NOT NULL,
    classe_energetica "DietiEstates2025".classe_energetica NOT NULL,
    "tipologia_proprietà" "DietiEstates2025"."tipologia_proprietà",
    caratteristiche_addizionali text[],
    id_agente_immobiliare integer NOT NULL,
    id_indirizzo integer NOT NULL,
    immagini character varying[] NOT NULL,
    ultima_modifica timestamp with time zone DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT valid_prezzo CHECK ((prezzo > (0)::numeric)),
    CONSTRAINT valid_superficie CHECK ((superficie > 0))
);


ALTER TABLE "DietiEstates2025".immobile OWNER TO postgres;

--
-- TOC entry 250 (class 1259 OID 83112)
-- Name: immobile_commerciale; Type: TABLE; Schema: DietiEstates2025; Owner: postgres
--

CREATE TABLE "DietiEstates2025".immobile_commerciale (
    id integer NOT NULL,
    numero_locali integer NOT NULL,
    piani character varying[] NOT NULL,
    numero_bagni integer NOT NULL,
    numero_piani_totali integer NOT NULL,
    ha_accesso_disabili boolean DEFAULT false,
    numero_vetrine integer DEFAULT 0,
    CONSTRAINT valid_numero_bagni CHECK ((numero_bagni > 0)),
    CONSTRAINT valid_numero_locali CHECK ((numero_locali > 0)),
    CONSTRAINT valid_numero_piani CHECK ((numero_piani_totali > 0))
);


ALTER TABLE "DietiEstates2025".immobile_commerciale OWNER TO postgres;

--
-- TOC entry 247 (class 1259 OID 82874)
-- Name: immobile_id_seq; Type: SEQUENCE; Schema: DietiEstates2025; Owner: postgres
--

CREATE SEQUENCE "DietiEstates2025".immobile_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE "DietiEstates2025".immobile_id_seq OWNER TO postgres;

--
-- TOC entry 5080 (class 0 OID 0)
-- Dependencies: 247
-- Name: immobile_id_seq; Type: SEQUENCE OWNED BY; Schema: DietiEstates2025; Owner: postgres
--

ALTER SEQUENCE "DietiEstates2025".immobile_id_seq OWNED BY "DietiEstates2025".immobile.id;


--
-- TOC entry 249 (class 1259 OID 83070)
-- Name: immobile_residenziale; Type: TABLE; Schema: DietiEstates2025; Owner: postgres
--

CREATE TABLE "DietiEstates2025".immobile_residenziale (
    id integer NOT NULL,
    numero_locali integer NOT NULL,
    numero_bagni integer NOT NULL,
    posti_auto integer DEFAULT 0,
    id_riscaldamento integer NOT NULL,
    giardino "DietiEstates2025".giardino NOT NULL,
    "è_arredato" boolean DEFAULT false,
    piani character varying[] NOT NULL,
    numero_piani_totali integer NOT NULL,
    ha_ascensore boolean DEFAULT false,
    CONSTRAINT valid_numero_bagni CHECK ((numero_bagni > 0)),
    CONSTRAINT valid_numero_locali CHECK ((numero_locali > 0)),
    CONSTRAINT valid_posti_auto CHECK ((posti_auto >= 0))
);


ALTER TABLE "DietiEstates2025".immobile_residenziale OWNER TO postgres;

--
-- TOC entry 256 (class 1259 OID 90787)
-- Name: indirizzo; Type: TABLE; Schema: DietiEstates2025; Owner: postgres
--

CREATE TABLE "DietiEstates2025".indirizzo (
    id integer NOT NULL,
    paese character varying NOT NULL,
    provincia character varying NOT NULL,
    "città" character varying NOT NULL,
    via character varying NOT NULL,
    civico character varying,
    edificio character varying,
    latitudine numeric(10,8) NOT NULL,
    longitudine numeric(11,8) NOT NULL
);


ALTER TABLE "DietiEstates2025".indirizzo OWNER TO postgres;

--
-- TOC entry 255 (class 1259 OID 90786)
-- Name: indirizzo_id_seq; Type: SEQUENCE; Schema: DietiEstates2025; Owner: postgres
--

CREATE SEQUENCE "DietiEstates2025".indirizzo_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE "DietiEstates2025".indirizzo_id_seq OWNER TO postgres;

--
-- TOC entry 5081 (class 0 OID 0)
-- Dependencies: 255
-- Name: indirizzo_id_seq; Type: SEQUENCE OWNED BY; Schema: DietiEstates2025; Owner: postgres
--

ALTER SEQUENCE "DietiEstates2025".indirizzo_id_seq OWNED BY "DietiEstates2025".indirizzo.id;


--
-- TOC entry 254 (class 1259 OID 83149)
-- Name: offerta; Type: TABLE; Schema: DietiEstates2025; Owner: postgres
--

CREATE TABLE "DietiEstates2025".offerta (
    id integer NOT NULL,
    id_immobile integer NOT NULL,
    id_utente integer NOT NULL,
    prezzo numeric(12,2) NOT NULL,
    data date NOT NULL,
    stato "DietiEstates2025".stato_offerta DEFAULT 'In attesa'::"DietiEstates2025".stato_offerta NOT NULL,
    CONSTRAINT valid_prezzo CHECK ((prezzo > (0)::numeric))
);


ALTER TABLE "DietiEstates2025".offerta OWNER TO postgres;

--
-- TOC entry 253 (class 1259 OID 83148)
-- Name: offerta_id_seq; Type: SEQUENCE; Schema: DietiEstates2025; Owner: postgres
--

CREATE SEQUENCE "DietiEstates2025".offerta_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE "DietiEstates2025".offerta_id_seq OWNER TO postgres;

--
-- TOC entry 5082 (class 0 OID 0)
-- Dependencies: 253
-- Name: offerta_id_seq; Type: SEQUENCE OWNED BY; Schema: DietiEstates2025; Owner: postgres
--

ALTER SEQUENCE "DietiEstates2025".offerta_id_seq OWNED BY "DietiEstates2025".offerta.id;


--
-- TOC entry 246 (class 1259 OID 82863)
-- Name: riscaldamento; Type: TABLE; Schema: DietiEstates2025; Owner: postgres
--

CREATE TABLE "DietiEstates2025".riscaldamento (
    id integer NOT NULL,
    nome character varying NOT NULL,
    "è_attivo" boolean DEFAULT true
);


ALTER TABLE "DietiEstates2025".riscaldamento OWNER TO postgres;

--
-- TOC entry 245 (class 1259 OID 82862)
-- Name: riscaldamento_id_seq; Type: SEQUENCE; Schema: DietiEstates2025; Owner: postgres
--

CREATE SEQUENCE "DietiEstates2025".riscaldamento_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE "DietiEstates2025".riscaldamento_id_seq OWNER TO postgres;

--
-- TOC entry 5083 (class 0 OID 0)
-- Dependencies: 245
-- Name: riscaldamento_id_seq; Type: SEQUENCE OWNED BY; Schema: DietiEstates2025; Owner: postgres
--

ALTER SEQUENCE "DietiEstates2025".riscaldamento_id_seq OWNED BY "DietiEstates2025".riscaldamento.id;


--
-- TOC entry 251 (class 1259 OID 83124)
-- Name: terreno; Type: TABLE; Schema: DietiEstates2025; Owner: postgres
--

CREATE TABLE "DietiEstates2025".terreno (
    id integer NOT NULL,
    ha_ingresso_dalla_strada boolean DEFAULT true
);


ALTER TABLE "DietiEstates2025".terreno OWNER TO postgres;

--
-- TOC entry 240 (class 1259 OID 82563)
-- Name: utente; Type: TABLE; Schema: DietiEstates2025; Owner: postgres
--

CREATE TABLE "DietiEstates2025".utente (
    id integer NOT NULL,
    email character varying NOT NULL,
    password character varying NOT NULL,
    username character varying NOT NULL,
    nome character varying NOT NULL,
    cognome character varying NOT NULL,
    "è_agente" boolean DEFAULT false,
    licenza character varying(8),
    "è_gestore" boolean DEFAULT false,
    id_agenzia integer,
    CONSTRAINT valid_email CHECK (((email)::text ~ '^[a-z0-9._-]+@[a-z0-9]+(\.[a-z]{2,})+$'::text)),
    CONSTRAINT valid_licenza CHECK (((licenza)::text ~ '^[A-Z]{2}[0-9]{6}$'::text)),
    CONSTRAINT valid_username CHECK (((username)::text ~ '^[A-Za-z0-9._-]+$'::text))
);


ALTER TABLE "DietiEstates2025".utente OWNER TO postgres;

--
-- TOC entry 239 (class 1259 OID 82562)
-- Name: utente_idutente_seq; Type: SEQUENCE; Schema: DietiEstates2025; Owner: postgres
--

CREATE SEQUENCE "DietiEstates2025".utente_idutente_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE "DietiEstates2025".utente_idutente_seq OWNER TO postgres;

--
-- TOC entry 5084 (class 0 OID 0)
-- Dependencies: 239
-- Name: utente_idutente_seq; Type: SEQUENCE OWNED BY; Schema: DietiEstates2025; Owner: postgres
--

ALTER SEQUENCE "DietiEstates2025".utente_idutente_seq OWNED BY "DietiEstates2025".utente.id;


--
-- TOC entry 4842 (class 2604 OID 82554)
-- Name: agenzia id; Type: DEFAULT; Schema: DietiEstates2025; Owner: postgres
--

ALTER TABLE ONLY "DietiEstates2025".agenzia ALTER COLUMN id SET DEFAULT nextval('"DietiEstates2025"."Agenzia_idagenzia_seq"'::regclass);


--
-- TOC entry 4848 (class 2604 OID 82854)
-- Name: categoria_immobile id; Type: DEFAULT; Schema: DietiEstates2025; Owner: postgres
--

ALTER TABLE ONLY "DietiEstates2025".categoria_immobile ALTER COLUMN id SET DEFAULT nextval('"DietiEstates2025".categoria_immobile_id_seq'::regclass);


--
-- TOC entry 4846 (class 2604 OID 82674)
-- Name: contratto id; Type: DEFAULT; Schema: DietiEstates2025; Owner: postgres
--

ALTER TABLE ONLY "DietiEstates2025".contratto ALTER COLUMN id SET DEFAULT nextval('"DietiEstates2025".contratto_idcontratto_seq'::regclass);


--
-- TOC entry 4852 (class 2604 OID 82878)
-- Name: immobile id; Type: DEFAULT; Schema: DietiEstates2025; Owner: postgres
--

ALTER TABLE ONLY "DietiEstates2025".immobile ALTER COLUMN id SET DEFAULT nextval('"DietiEstates2025".immobile_id_seq'::regclass);


--
-- TOC entry 4864 (class 2604 OID 90790)
-- Name: indirizzo id; Type: DEFAULT; Schema: DietiEstates2025; Owner: postgres
--

ALTER TABLE ONLY "DietiEstates2025".indirizzo ALTER COLUMN id SET DEFAULT nextval('"DietiEstates2025".indirizzo_id_seq'::regclass);


--
-- TOC entry 4862 (class 2604 OID 83152)
-- Name: offerta id; Type: DEFAULT; Schema: DietiEstates2025; Owner: postgres
--

ALTER TABLE ONLY "DietiEstates2025".offerta ALTER COLUMN id SET DEFAULT nextval('"DietiEstates2025".offerta_id_seq'::regclass);


--
-- TOC entry 4850 (class 2604 OID 82866)
-- Name: riscaldamento id; Type: DEFAULT; Schema: DietiEstates2025; Owner: postgres
--

ALTER TABLE ONLY "DietiEstates2025".riscaldamento ALTER COLUMN id SET DEFAULT nextval('"DietiEstates2025".riscaldamento_id_seq'::regclass);


--
-- TOC entry 4843 (class 2604 OID 82566)
-- Name: utente id; Type: DEFAULT; Schema: DietiEstates2025; Owner: postgres
--

ALTER TABLE ONLY "DietiEstates2025".utente ALTER COLUMN id SET DEFAULT nextval('"DietiEstates2025".utente_idutente_seq'::regclass);


--
-- TOC entry 4879 (class 2606 OID 82559)
-- Name: agenzia Agenzia_pkey; Type: CONSTRAINT; Schema: DietiEstates2025; Owner: postgres
--

ALTER TABLE ONLY "DietiEstates2025".agenzia
    ADD CONSTRAINT "Agenzia_pkey" PRIMARY KEY (id);


--
-- TOC entry 4911 (class 2606 OID 83142)
-- Name: autorimessa autorimessa_pkey; Type: CONSTRAINT; Schema: DietiEstates2025; Owner: postgres
--

ALTER TABLE ONLY "DietiEstates2025".autorimessa
    ADD CONSTRAINT autorimessa_pkey PRIMARY KEY (id);


--
-- TOC entry 4895 (class 2606 OID 82859)
-- Name: categoria_immobile categoria_immobile_pkey; Type: CONSTRAINT; Schema: DietiEstates2025; Owner: postgres
--

ALTER TABLE ONLY "DietiEstates2025".categoria_immobile
    ADD CONSTRAINT categoria_immobile_pkey PRIMARY KEY (id);


--
-- TOC entry 4897 (class 2606 OID 82861)
-- Name: categoria_immobile categoria_immobile_sottocategoria_key; Type: CONSTRAINT; Schema: DietiEstates2025; Owner: postgres
--

ALTER TABLE ONLY "DietiEstates2025".categoria_immobile
    ADD CONSTRAINT categoria_immobile_sottocategoria_key UNIQUE (sottocategoria);


--
-- TOC entry 4891 (class 2606 OID 82679)
-- Name: contratto contratto_nome_key; Type: CONSTRAINT; Schema: DietiEstates2025; Owner: postgres
--

ALTER TABLE ONLY "DietiEstates2025".contratto
    ADD CONSTRAINT contratto_nome_key UNIQUE (nome);


--
-- TOC entry 4893 (class 2606 OID 82677)
-- Name: contratto contratto_pkey; Type: CONSTRAINT; Schema: DietiEstates2025; Owner: postgres
--

ALTER TABLE ONLY "DietiEstates2025".contratto
    ADD CONSTRAINT contratto_pkey PRIMARY KEY (id);


--
-- TOC entry 4907 (class 2606 OID 83118)
-- Name: immobile_commerciale immobile_commerciale_pkey; Type: CONSTRAINT; Schema: DietiEstates2025; Owner: postgres
--

ALTER TABLE ONLY "DietiEstates2025".immobile_commerciale
    ADD CONSTRAINT immobile_commerciale_pkey PRIMARY KEY (id);


--
-- TOC entry 4903 (class 2606 OID 82882)
-- Name: immobile immobile_pkey; Type: CONSTRAINT; Schema: DietiEstates2025; Owner: postgres
--

ALTER TABLE ONLY "DietiEstates2025".immobile
    ADD CONSTRAINT immobile_pkey PRIMARY KEY (id);


--
-- TOC entry 4905 (class 2606 OID 83075)
-- Name: immobile_residenziale immobile_residenziale_pkey; Type: CONSTRAINT; Schema: DietiEstates2025; Owner: postgres
--

ALTER TABLE ONLY "DietiEstates2025".immobile_residenziale
    ADD CONSTRAINT immobile_residenziale_pkey PRIMARY KEY (id);


--
-- TOC entry 4915 (class 2606 OID 90794)
-- Name: indirizzo indirizzo_pkey; Type: CONSTRAINT; Schema: DietiEstates2025; Owner: postgres
--

ALTER TABLE ONLY "DietiEstates2025".indirizzo
    ADD CONSTRAINT indirizzo_pkey PRIMARY KEY (id);


--
-- TOC entry 4913 (class 2606 OID 83155)
-- Name: offerta offerta_pkey; Type: CONSTRAINT; Schema: DietiEstates2025; Owner: postgres
--

ALTER TABLE ONLY "DietiEstates2025".offerta
    ADD CONSTRAINT offerta_pkey PRIMARY KEY (id);


--
-- TOC entry 4899 (class 2606 OID 82873)
-- Name: riscaldamento riscaldamento_nome_key; Type: CONSTRAINT; Schema: DietiEstates2025; Owner: postgres
--

ALTER TABLE ONLY "DietiEstates2025".riscaldamento
    ADD CONSTRAINT riscaldamento_nome_key UNIQUE (nome);


--
-- TOC entry 4901 (class 2606 OID 82871)
-- Name: riscaldamento riscaldamento_pkey; Type: CONSTRAINT; Schema: DietiEstates2025; Owner: postgres
--

ALTER TABLE ONLY "DietiEstates2025".riscaldamento
    ADD CONSTRAINT riscaldamento_pkey PRIMARY KEY (id);


--
-- TOC entry 4909 (class 2606 OID 83129)
-- Name: terreno terreno_pkey; Type: CONSTRAINT; Schema: DietiEstates2025; Owner: postgres
--

ALTER TABLE ONLY "DietiEstates2025".terreno
    ADD CONSTRAINT terreno_pkey PRIMARY KEY (id);


--
-- TOC entry 4881 (class 2606 OID 82561)
-- Name: agenzia unique_agenzia_nome; Type: CONSTRAINT; Schema: DietiEstates2025; Owner: postgres
--

ALTER TABLE ONLY "DietiEstates2025".agenzia
    ADD CONSTRAINT unique_agenzia_nome UNIQUE (nome);


--
-- TOC entry 4883 (class 2606 OID 82577)
-- Name: utente unique_email; Type: CONSTRAINT; Schema: DietiEstates2025; Owner: postgres
--

ALTER TABLE ONLY "DietiEstates2025".utente
    ADD CONSTRAINT unique_email UNIQUE (email);


--
-- TOC entry 4885 (class 2606 OID 90755)
-- Name: utente unique_licenza; Type: CONSTRAINT; Schema: DietiEstates2025; Owner: postgres
--

ALTER TABLE ONLY "DietiEstates2025".utente
    ADD CONSTRAINT unique_licenza UNIQUE (licenza);


--
-- TOC entry 4887 (class 2606 OID 82579)
-- Name: utente unique_username; Type: CONSTRAINT; Schema: DietiEstates2025; Owner: postgres
--

ALTER TABLE ONLY "DietiEstates2025".utente
    ADD CONSTRAINT unique_username UNIQUE (username);


--
-- TOC entry 4889 (class 2606 OID 82575)
-- Name: utente utente_pkey; Type: CONSTRAINT; Schema: DietiEstates2025; Owner: postgres
--

ALTER TABLE ONLY "DietiEstates2025".utente
    ADD CONSTRAINT utente_pkey PRIMARY KEY (id);


--
-- TOC entry 4917 (class 2606 OID 90795)
-- Name: immobile Immobile_id_indirizzo_fkey; Type: FK CONSTRAINT; Schema: DietiEstates2025; Owner: postgres
--

ALTER TABLE ONLY "DietiEstates2025".immobile
    ADD CONSTRAINT "Immobile_id_indirizzo_fkey" FOREIGN KEY (id_indirizzo) REFERENCES "DietiEstates2025".indirizzo(id) NOT VALID;


--
-- TOC entry 4925 (class 2606 OID 83143)
-- Name: autorimessa autorimessa_id_fkey; Type: FK CONSTRAINT; Schema: DietiEstates2025; Owner: postgres
--

ALTER TABLE ONLY "DietiEstates2025".autorimessa
    ADD CONSTRAINT autorimessa_id_fkey FOREIGN KEY (id) REFERENCES "DietiEstates2025".immobile(id);


--
-- TOC entry 4923 (class 2606 OID 83119)
-- Name: immobile_commerciale immobile_commerciale_id_fkey; Type: FK CONSTRAINT; Schema: DietiEstates2025; Owner: postgres
--

ALTER TABLE ONLY "DietiEstates2025".immobile_commerciale
    ADD CONSTRAINT immobile_commerciale_id_fkey FOREIGN KEY (id) REFERENCES "DietiEstates2025".immobile(id);


--
-- TOC entry 4918 (class 2606 OID 82893)
-- Name: immobile immobile_id_agente_immobiliare_fkey; Type: FK CONSTRAINT; Schema: DietiEstates2025; Owner: postgres
--

ALTER TABLE ONLY "DietiEstates2025".immobile
    ADD CONSTRAINT immobile_id_agente_immobiliare_fkey FOREIGN KEY (id_agente_immobiliare) REFERENCES "DietiEstates2025".utente(id);


--
-- TOC entry 4919 (class 2606 OID 82888)
-- Name: immobile immobile_id_categoria_immobile_fkey; Type: FK CONSTRAINT; Schema: DietiEstates2025; Owner: postgres
--

ALTER TABLE ONLY "DietiEstates2025".immobile
    ADD CONSTRAINT immobile_id_categoria_immobile_fkey FOREIGN KEY (id_categoria_immobile) REFERENCES "DietiEstates2025".categoria_immobile(id);


--
-- TOC entry 4920 (class 2606 OID 82883)
-- Name: immobile immobile_id_contratto_fkey; Type: FK CONSTRAINT; Schema: DietiEstates2025; Owner: postgres
--

ALTER TABLE ONLY "DietiEstates2025".immobile
    ADD CONSTRAINT immobile_id_contratto_fkey FOREIGN KEY (id_contratto) REFERENCES "DietiEstates2025".contratto(id);


--
-- TOC entry 4921 (class 2606 OID 83076)
-- Name: immobile_residenziale immobile_residenziale_id_fkey; Type: FK CONSTRAINT; Schema: DietiEstates2025; Owner: postgres
--

ALTER TABLE ONLY "DietiEstates2025".immobile_residenziale
    ADD CONSTRAINT immobile_residenziale_id_fkey FOREIGN KEY (id) REFERENCES "DietiEstates2025".immobile(id);


--
-- TOC entry 4922 (class 2606 OID 83081)
-- Name: immobile_residenziale immobile_residenziale_id_riscaldamento_fkey; Type: FK CONSTRAINT; Schema: DietiEstates2025; Owner: postgres
--

ALTER TABLE ONLY "DietiEstates2025".immobile_residenziale
    ADD CONSTRAINT immobile_residenziale_id_riscaldamento_fkey FOREIGN KEY (id_riscaldamento) REFERENCES "DietiEstates2025".riscaldamento(id);


--
-- TOC entry 4926 (class 2606 OID 83156)
-- Name: offerta offerta_id_immobile_fkey; Type: FK CONSTRAINT; Schema: DietiEstates2025; Owner: postgres
--

ALTER TABLE ONLY "DietiEstates2025".offerta
    ADD CONSTRAINT offerta_id_immobile_fkey FOREIGN KEY (id_immobile) REFERENCES "DietiEstates2025".immobile(id);


--
-- TOC entry 4927 (class 2606 OID 83161)
-- Name: offerta offerta_id_utente_fkey; Type: FK CONSTRAINT; Schema: DietiEstates2025; Owner: postgres
--

ALTER TABLE ONLY "DietiEstates2025".offerta
    ADD CONSTRAINT offerta_id_utente_fkey FOREIGN KEY (id_utente) REFERENCES "DietiEstates2025".utente(id);


--
-- TOC entry 4924 (class 2606 OID 83130)
-- Name: terreno terreno_id_fkey; Type: FK CONSTRAINT; Schema: DietiEstates2025; Owner: postgres
--

ALTER TABLE ONLY "DietiEstates2025".terreno
    ADD CONSTRAINT terreno_id_fkey FOREIGN KEY (id) REFERENCES "DietiEstates2025".immobile(id);


--
-- TOC entry 4916 (class 2606 OID 82582)
-- Name: utente utente_idagenzia_fkey; Type: FK CONSTRAINT; Schema: DietiEstates2025; Owner: postgres
--

ALTER TABLE ONLY "DietiEstates2025".utente
    ADD CONSTRAINT utente_idagenzia_fkey FOREIGN KEY (id_agenzia) REFERENCES "DietiEstates2025".agenzia(id);


-- Completed on 2025-03-06 22:49:42

--
-- PostgreSQL database dump complete
--

