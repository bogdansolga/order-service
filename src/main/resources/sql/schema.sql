BEGIN TRANSACTION;

-- Drop tables if they exist
DROP TABLE IF EXISTS orders CASCADE;
DROP TABLE IF EXISTS order_item CASCADE;

-- Drop sequences if they exist
DROP SEQUENCE IF EXISTS orders_seq CASCADE;
DROP SEQUENCE IF EXISTS order_item_seq CASCADE;

-- Create tables
CREATE TABLE IF NOT EXISTS orders (
  id BIGINT PRIMARY KEY DEFAULT nextval('orders_seq'),
  customer_id BIGINT,
  total DOUBLE PRECISION,
  status VARCHAR(50),
  creation_date TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS order_item (
  id BIGINT PRIMARY KEY DEFAULT nextval('order_item_seq'),
  order_id BIGINT,
  restaurant_id BIGINT,
  food_id BIGINT,
  price DOUBLE PRECISION,
  name VARCHAR(50),
  description VARCHAR(50),
  creation_date TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (order_id) REFERENCES orders(id)
);

COMMIT;
