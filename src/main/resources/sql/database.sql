-- run these commands before starting the project, in order to setup the database and the connecting user
CREATE USER order_service_admin WITH PASSWORD 'kvL9MxNPbRx2ZYn';

CREATE DATABASE order_service;
GRANT ALL PRIVILEGES ON DATABASE order_service TO order_service_admin;

\c order_service
GRANT ALL ON SCHEMA public TO order_service_admin;