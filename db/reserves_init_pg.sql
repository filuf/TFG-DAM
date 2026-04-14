-- PostgreSQL script converted from MySQL reserves_init.sql
CREATE SCHEMA IF NOT EXISTS reserves;
SET search_path TO reserves;

-- Bloque para crear TIPOS de forma segura (Postgres no soporta IF NOT EXISTS en tipos)
DO $$ 
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type t JOIN pg_namespace n ON n.oid = t.typnamespace WHERE t.typname = 'sender_rol' AND n.nspname = 'reserves') THEN
        CREATE TYPE reserves.sender_rol AS ENUM ('USER', 'SYSTEM');
    END IF;
    
    IF NOT EXISTS (SELECT 1 FROM pg_type t JOIN pg_namespace n ON n.oid = t.typnamespace WHERE t.typname = 'notification_sender' AND n.nspname = 'reserves') THEN
        CREATE TYPE reserves.notification_sender AS ENUM ('USER', 'COMPANY');
    END IF;
END $$;

CREATE TABLE IF NOT EXISTS reserves.users (
  user_id UUID NOT NULL,
  username VARCHAR(70) NOT NULL,
  s3_image_key VARCHAR(200),
  phone_number VARCHAR(15),
  email_address VARCHAR(320) UNIQUE,
  created_at TIMESTAMP NOT NULL,
  created_by_company_id UUID,
  PRIMARY KEY (user_id)
);

CREATE TABLE IF NOT EXISTS reserves.companies (
  user_id UUID NOT NULL,
  default_max_concurrent_services INT NOT NULL,
  company_name VARCHAR(200) NOT NULL,
  phone_number VARCHAR(15),
  email_address VARCHAR(320) NOT NULL UNIQUE,
  physical_address VARCHAR(300) NOT NULL,
  s3_image_key VARCHAR(200),
  description TEXT,
  ratting_avg SMALLINT,
  PRIMARY KEY (user_id)
);

CREATE TABLE IF NOT EXISTS reserves.services (
  service_id UUID NOT NULL,
  company_id UUID NOT NULL,
  service_name VARCHAR(45) NOT NULL,
  service_minutes_duration INT NOT NULL,
  s3_image_key VARCHAR(200),
  service_price_cent INT NOT NULL,
  PRIMARY KEY (service_id),
  CONSTRAINT company_id_fk FOREIGN KEY (company_id)
    REFERENCES reserves.companies (user_id)
);

CREATE INDEX IF NOT EXISTS company_id_idx ON reserves.services (company_id);

CREATE TABLE IF NOT EXISTS reserves.reserves (
  reserve_id UUID NOT NULL,
  user_id UUID NOT NULL,
  service_id UUID NOT NULL,
  service_time TIMESTAMP NOT NULL,
  created_at TIMESTAMP NOT NULL,
  is_canceled BOOLEAN NOT NULL,
  PRIMARY KEY (reserve_id),
  CONSTRAINT fk_reserves_users FOREIGN KEY (user_id)
    REFERENCES reserves.users (user_id),
  CONSTRAINT fk_reserves_services FOREIGN KEY (service_id)
    REFERENCES reserves.services (service_id)
);

CREATE INDEX IF NOT EXISTS fk_users_has_services_services1_idx ON reserves.reserves (service_id);
CREATE INDEX IF NOT EXISTS fk_users_has_services_users1_idx ON reserves.reserves (user_id);

CREATE TABLE IF NOT EXISTS reserves.conversations (
  conversation_id UUID NOT NULL,
  user_id UUID NOT NULL,
  created_at TIMESTAMP NOT NULL,
  modified_at TIMESTAMP NOT NULL,
  title VARCHAR(255) NOT NULL,
  summary TEXT,
  PRIMARY KEY (conversation_id),
  CONSTRAINT fk_conversations_users1 FOREIGN KEY (user_id)
    REFERENCES reserves.users (user_id)
);

CREATE INDEX IF NOT EXISTS fk_conversations_users1_idx ON reserves.conversations (user_id);

CREATE TABLE IF NOT EXISTS reserves.message (
  message_id UUID NOT NULL,
  conversation_id UUID NOT NULL,
  text_content TEXT NOT NULL,
  created_at TIMESTAMP NOT NULL,
  sender_rol reserves.sender_rol NOT NULL,
  PRIMARY KEY (message_id),
  CONSTRAINT fk_message_conversations1 FOREIGN KEY (conversation_id)
    REFERENCES reserves.conversations (conversation_id)
);

CREATE INDEX IF NOT EXISTS fk_message_conversations1_idx ON reserves.message (conversation_id);

CREATE TABLE IF NOT EXISTS reserves.notifications (
  notification_id UUID NOT NULL,
  companies_id UUID NOT NULL,
  user_id UUID NOT NULL,
  notification_sender reserves.notification_sender NOT NULL,
  text_content TEXT NOT NULL,
  created_at TIMESTAMP NOT NULL,
  PRIMARY KEY (notification_id),
  CONSTRAINT fk_notifications_companies1 FOREIGN KEY (companies_id)
    REFERENCES reserves.companies (user_id),
  CONSTRAINT fk_notifications_users1 FOREIGN KEY (user_id)
    REFERENCES reserves.users (user_id)
);

CREATE TABLE IF NOT EXISTS reserves.max_concurrent_services_interval (
  interval_id UUID NOT NULL,
  company_id UUID NOT NULL,
  start_datetime TIMESTAMP NOT NULL,
  end_datetime TIMESTAMP NOT NULL,
  max_concurrent_services INT NOT NULL,
  created_at TIMESTAMP NOT NULL,
  PRIMARY KEY (interval_id),
  CONSTRAINT fk_max_concurrent_services_interval_companies1 FOREIGN KEY (company_id)
    REFERENCES reserves.companies (user_id)
);

CREATE TABLE IF NOT EXISTS reserves.service_schedule (
  service_schedule_id UUID NOT NULL,
  service_id UUID NOT NULL,
  day_of_week INT NOT NULL,
  start_time TIME NOT NULL,
  end_time TIME NOT NULL,
  PRIMARY KEY (service_schedule_id),
  CONSTRAINT fk_service_schedule_services1 FOREIGN KEY (service_id)
    REFERENCES reserves.services (service_id)
);

CREATE TABLE IF NOT EXISTS reserves.reviews (
  review_id UUID NOT NULL,
  user_id UUID NOT NULL,
  company_id UUID NOT NULL,
  stars INT NOT NULL,
  text_content TEXT,
  reserve_id UUID NOT NULL,
  PRIMARY KEY (review_id),
  CONSTRAINT fk_users_has_companies_users1 FOREIGN KEY (user_id)
    REFERENCES reserves.users (user_id),
  CONSTRAINT fk_users_has_companies_companies1 FOREIGN KEY (company_id)
    REFERENCES reserves.companies (user_id),
  CONSTRAINT fk_reviews_reserves1 FOREIGN KEY (reserve_id)
    REFERENCES reserves.reserves (reserve_id)
);