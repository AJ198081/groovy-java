package dev.aj.groovyjava.warehouse

import dev.aj.groovyjava.db.DBConnectionProperties
import groovy.sql.Sql

import java.text.NumberFormat

def credentials = DBConnectionProperties.getConnectionCredentials()

def usersData = DataBase.getUserData()
def productsData = new PrepareData().getProductData()
def ordersData = DataBase.getOrders()

def integerFormat = NumberFormat.getIntegerInstance(Locale.ENGLISH)

void tableEncapsulate(String textContents, Integer characterWidth, String paddingCharacter) {
	def textBody = new StringBuffer().append(' ').append(textContents).append(' ')
	println(textBody.center(characterWidth, paddingCharacter))
}
def sql = Sql.newInstance(credentials.url.concat('?currentSchema=warehouse'), credentials.userName, credentials.password, credentials.driverClassName)
println('Connected')

sql.execute("""
	Drop TABLE if exists users cascade;
	Drop TABLE if exists products cascade;
	Drop TABLE if exists orders;

	create table users (
	    id SERIAL PRIMARY KEY,
	    first_name varchar(50),
	    last_name varchar(50)
	);

	CREATE TABLE products (
	  	id SERIAL PRIMARY KEY,
  		name VARCHAR(100),
  		department VARCHAR(75),
  		price INTEGER,
  		weight INTEGER
	);
	CREATE TABLE orders (
  		id SERIAL PRIMARY KEY,
  		user_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
  		product_id INTEGER REFERENCES products(id) ON DELETE CASCADE,
  		paid BOOLEAN
	);
""")

productsData.forEach {
	sql.executeInsert("""
INSERT INTO products(name, department, price, weight) values (${it.name}, ${it.department}, ${Double.parseDouble(it.price)}, ${Double.parseDouble(it.weight)})
""")
}

usersData.forEach {
	sql.executeInsert("""
	INSERT INTO users (first_name, last_name) values (${it.first_name}, ${it.last_name})
""")
}

ordersData.forEach {
	sql.executeInsert("""
	INSERT INTO orders (user_id, product_id, paid) values (${it.user_id}, ${it.product_id}, ${it.paid})
""")
}

println(" Start of a new table ".center(50, '='))
sql.eachRow("""
	SELECT paid, count(*) as numbers FROM warehouse.orders Group BY paid;
""", {
	println("Paid status of ${it.paid} for a total of ${it.numbers} orders.")
})
println(" End of table ".center(50, "+"))

println(" Start of a new table ".center(50, '='))
sql.eachRow("""
	SELECT u.first_name, u.last_name, o.paid from warehouse.users u left join warehouse.orders o on u.id = o.user_id;
""", {
	println("User Name ${it.first_name} ${it.last_name} has paid? ${it.paid}")
})
println(" End of table ".center(50, "+"))

println(" Start of a new table ".center(50, '='))
sql.eachRow("""
	SELECT distinct * from warehouse.products order by price;
""", {
	println("| ${it.id} |\t ${it.name} \t|\t ${it.department} \t|\t ${it.price} \t|\t ${it.weight} \t|")
})
println(" End of table ".center(50, "+"))

println(" Start of a new table ".center(50, '='))
sql.eachRow("""
	SELECT * from warehouse.products order by id limit 10 offset 50;
""", {
	println("| ${it.id} |\t ${it.name} \t\t\t\t| ${it.department} \t|\t ${it.price} \t|\t ${it.weight} \t|")
})
println(" End of table ".center(50, "+"))

println(" Start of a new table ".center(50, '='))
sql.eachRow("""
	SELECT id, name, (products.price / products.weight) as PWRatio from warehouse.products order by (products.price / products.weight) desc limit 4;
""", {
	println("| ${it.id} |\t ${it.name} \t\t\t\t| ${it.PWRatio} \t|")
})
println(" End of table ".center(50, "+"))

//Union - removes duplicated rows from the final result set, where as Union All just concatenates the two result sets
//Union requires the result set to strictly have same column names and column types
println(" Start of a new table ".center(50, '='))
sql.eachRow("""
(SELECT id, name, price as PWRatio from warehouse.products order by price desc limit 4)
UNION
(SELECT id, name, (products.price / products.weight) as PWRatio from warehouse.products order by (products.price / products.weight) desc limit 4)
""", {
	println("| ${it.id} |\t ${it.name} \t\t\t\t| ${it.PWRatio} \t|")
})
println(" End of table ".center(50, "+"))

//Intersect - shows the 'intersection' of the two result sets, same type and name conditions of columns apply
println(" Start of a new table ".center(50, '='))
sql.eachRow("""
(SELECT * from warehouse.products order by price desc limit 4)
INTERSECT ALL
(SELECT * from warehouse.products order by (products.price / products.weight) desc limit 4)
""", {
	println("| ${it.id} |\t ${it.name} \t\t\t\t| ${it.department} \t|\t ${it.price} \t|\t ${it.weight} \t|")
})
println(" End of table ".center(50, "+"))

println(" Start of a new table ".center(50, '='))
sql.eachRow("""
(SELECT * from warehouse.products order by (products.price / products.weight) desc limit 6)
Except
(SELECT * from warehouse.products order by price desc limit 4)
""", {
	println("| ${it.id} |\t ${it.name} \t\t\t\t| ${it.department} \t|\t ${it.price} \t|\t ${it.weight} \t|")
})
println(" End of table ".center(50, "+"))

println(" Table items expensive than all toys ".center(50, '='))
sql.eachRow("""
SELECT * from warehouse.products where price > (
select max(price) from warehouse.products where department = 'Toys'
);
""", {
	println("| ${it.id} |\t ${it.name} \t\t\t\t| ${it.department} \t|\t ${it.price} \t|\t ${it.weight} \t|")
})
println(" End of table ".center(50, "+"))

//For any subquery in the 'From' clause, always use Alias, no specific use as such, just the language requirement
tableEncapsulate("Table - Subquery", 50, "+")
sql.eachRow("""
	select name, price_weight_ratio from
	(SELECT name, price / weight AS price_weight_ratio from products) AS p
	where price_weight_ratio > 5;
""", {
	println("Name ${it.name} \t| price_weight_ratio ${it.price_weight_ratio} \t|")
})
tableEncapsulate("End Table", 50, "+")

tableEncapsulate("Subquery - Table Demo", 60, "=")
sql.eachRow("""
select AVG(number_of_orders) as average_orders from
(select user_id, count(user_id) AS number_of_orders from orders GROUP BY user_id) as p;
""", {
	println("Average orders placed by users ${integerFormat.format(it.average_orders)}")
})
tableEncapsulate("End Table", 50, "+")

tableEncapsulate("Subquery In A Join - Table", 60, "=")
sql.eachRow("""
select first_name from users
    join (select user_id from orders where product_id = 3) as p
on users.id = p.user_id;
""", {
	println("First Name for product_id 3 orders ${it.first_name}")
})
tableEncapsulate("End Table", 50, "+")

tableEncapsulate("Subquery In A Where Clause - Table", 60, "=")
sql.eachRow("""
select id, count(id) AS countNumber from orders
    where product_id IN (SELECT id from products where price / weight < 5)
group by id;
""", {
	println("ID ${it.id}, total count ${it.countNumber}")
})
tableEncapsulate("End Table", 50, "+")

tableEncapsulate("Subquery In A Where Clause - Table", 60, "=")
sql.eachRow("""
select name from products
 where price > (select avg(price) as average_price from products)
""", {
	println("${it.name}")
})
tableEncapsulate("End Table", 50, "+")

tableEncapsulate("Subquery Where Clause - Greater than ALL", 60, "=")
sql.eachRow("""
select name, department, price from products
 where price > ALL (select price from products where department = 'Industrial')
ORDER BY price;
""", {
	println("${it.name} : ${it.department} : ${it.price}")
})
tableEncapsulate("End Table", 50, "+")

tableEncapsulate("Subquery Where Clause - Greater than SOME", 60, "=")
sql.eachRow("""
select name, department, price from products
 where price > SOME (select price from products where department = 'Industrial')
ORDER BY price DESC;
""", {
	println("${it.name} : ${it.department} : ${it.price}")
})
tableEncapsulate("End Table", 50, "+")

//You can refer to the outside query table in the subquery table
tableEncapsulate("Subquery Where Clause - Another Example", 60, "=")
sql.eachRow("""
select name, department, price from products p1
 where p1.price = (select MAX(price) from products p2 where p1.department = p2.department)
""", {
	println("${it.name} : ${it.department} : ${it.price}")
})
tableEncapsulate("End Table", 50, "+")

tableEncapsulate("Subquery Where Clause - Another Example", 60, "=")
sql.eachRow("""
select p.product_id, products.name as name, p.count from
(select product_id, count(product_id) as count
    from orders
	GROUP BY product_id) as p
left join products on p.product_id = products.id;
""", {
	println("${it.product_id} : ${it.name} : ${it.count}")
})
tableEncapsulate("End Table", 50, "+")

// Above query and the below query will fetch same results, just different ways of fetching the result set
tableEncapsulate("Subquery Where Clause - Another Example", 60, "=")
sql.eachRow("""
select name, id as product_id, (select count(product_id) as count from orders where p1.id = orders.product_id)
       from products as p1
""", {
	println("${it.product_id} : ${it.name} : ${it.count}")
})
tableEncapsulate("End Table", 50, "+")

tableEncapsulate("Distinct function", 60, "=")
sql.eachRow("""
SELECT DISTINCT name FROM products;
""", {
	println("${it.name}")
})
tableEncapsulate("End Table", 50, "+")

// Postgres in-built functions Greatest and Least
tableEncapsulate("Greatest function", 60, "=")
sql.eachRow("""
SELECT DISTINCT name, weight, GREATEST(weight * 2, 30) as shipping_weight FROM products;
""", {
	println("${it.name}: ${it.weight} : ${it.shipping_weight}")
})
tableEncapsulate("End Table", 50, "+")

tableEncapsulate("Least function", 60, "=")
sql.eachRow("""
SELECT DISTINCT name, weight, GREATEST(weight* 2, 30) AS min_shipping_weight, LEAST(GREATEST(weight * 4, 30), 60) as max_shipping_weight FROM products;
""", {
	println("${it.name} : ${it.weight} : ${it.min_shipping_weight} : ${it.max_shipping_weight}")
})
tableEncapsulate("End Table", 50, "+")

tableEncapsulate("Case function", 60, "=")
sql.eachRow("""
SELECT DISTINCT name, weight, CASE
    when weight < 30 then weight * 2
    when weight >= 30 then 60
    END AS calculated_shipping_weight
FROM products order by weight;
""", {
	println("${it.name} : ${it.weight} : ${it.calculated_shipping_weight}")
})
tableEncapsulate("End Table", 50, "+")



sql.close()