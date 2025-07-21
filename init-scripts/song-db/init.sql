CREATE TABLE song (
  id SERIAL PRIMARY KEY,
  name VARCHAR(255),
  artist VARCHAR(255),
  album VARCHAR(255),
  length VARCHAR(10),
  resource_id INTEGER NOT NULL,
  year VARCHAR(10)
);