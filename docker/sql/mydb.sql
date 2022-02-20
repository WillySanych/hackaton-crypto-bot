-- Database
CREATE DATABASE db;
\c db;

CREATE TABLE IF NOT EXISTS candle
        (
          figi character varying(100) NOT NULL,
          interval character varying(100),
          low character varying(100),
          high character varying(100),
          open character varying(100),
          close character varying(100),
          open_time character varying(100),
          PRIMARY KEY (figi)
        );