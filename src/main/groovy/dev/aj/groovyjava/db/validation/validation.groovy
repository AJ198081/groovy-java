package dev.aj.groovyjava.db.validation

import dev.aj.groovyjava.db.DBConnectionProperties
import groovy.sql.Sql

def credentials = DBConnectionProperties.getConnectionCredentials("validation", "inventory")

def sql = Sql.newInstance(credentials.url, credentials.userName, credentials.password, credentials.driverClassName)

println('Connected')

sql.execute("""
	DROP TABLE IF EXISTS products;

	CREATE TABLE products (
	    id SERIAL PRIMARY KEY,
	    name VARCHAR(40) NOT NULL UNIQUE DEFAULT 'Default',
	    department VARCHAR(40) NOT NULL DEFAULT 'Default',
	    price NUMERIC NOT NULL DEFAULT 9999 CHECK ( price > 0 AND price * products.weight >= 1),
	    weight NUMERIC NOT NULL DEFAULT 10 CHECK ( weight > 0 )
	)
""")

//Alternatively approach to set Unique Key

sql.execute("""
	ALTER TABLE products
	DROP CONSTRAINT products_name_key;
	
ALTER TABLE products
 ADD UNIQUE (name, department);

""")

sql.executeInsert("""
	INSERT INTO products (name, department, price, weight)
	VALUES ('Shirt', 'Clothes', 20.99, 1.5);
""")

sql.executeInsert("""
	INSERT INTO products (name, department, weight)
	VALUES ('Pant', 'Clothes', 2.5);
""")

sql.execute("""
	INSERT INTO products (name)
	values ('Shirt Coloured');
""")

sql.execute("""
	INSERT INTO products (name, department, price, weight)
	values ('Belt', 'Clothes', 2, 1);
""")

sql.execute("""
	INSERT INTO products (name, department)
	values ('Belt', 'House');
""")

sql.execute("""
	DROP TABLE IF EXISTS orders;

	CREATE TABLE orders(
	 id SERIAL PRIMARY KEY,
	 name VARCHAR(50) NOT NULL,
	 created_at TIMESTAMP NOT NULL,
	 est_delivery TIMESTAMP NOT NULL,
	 CHECK ( created_at < orders.est_delivery )
	);
""")

sql.execute("""
	INSERT INTO orders (name, created_at, est_delivery)
	VALUES ('Shirt', '20 Mar 23 01:00AM', '03 Apr 23 09:00AM');
""")


