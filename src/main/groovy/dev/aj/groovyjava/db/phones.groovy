package dev.aj.groovyjava.db

import groovy.sql.Sql

import java.text.DecimalFormat

def decimalFormat = new DecimalFormat("#,###.00")

def dbConnection = [url: 'jdbc:postgresql://localhost:5432/postgres', userName: 'admin', password: 'password', driverClassName: 'org.postgresql.Driver']

def sql = Sql.newInstance(dbConnection.url, dbConnection.userName, dbConnection.password, dbConnection.driverClassName)

println('Connected to db')

sql.execute("""
drop table if exists phones;
create table phones(
    name varchar(25),
    manufacturer varchar(25),
    price integer,
    units_sold integer
);
""")

def data = [
        [name: 'N1280', manufacturer: 'Nokia', price: 199, units_sold: 1925],
        [name: 'iPhone 4', manufacturer: 'Apple', price: 399, units_sold: 9436],
        [name: 'Galaxy S', manufacturer: 'Samsung', price: 299, units_sold: 2359],
        [name: 'S5620', manufacturer: 'Samsung', price: 250, units_sold: 2385],
]

data.forEach {
    sql.executeInsert("""
    insert into phones (name, manufacturer, price, units_sold) values (${it.name}, ${it.manufacturer}, ${it.price}, ${it.units_sold});
""")
}
final PADDING_CHARACTERS = 45
println(' Phone sale revenue '.center(PADDING_CHARACTERS, '='))
println(System.lineSeparator())


def itemCount = 0

//sql.eachRow("select concat_ws(' - ', name, manufacturer) as model, price * phones.units_sold as revenue from phones where manufacturer in ('Nokia', 'Apple');", {
sql.eachRow("""
select concat_ws(' - ', name, manufacturer) as model, price * phones.units_sold as revenue from phones
where units_sold between 2000 and 2400 
   or manufacturer in ('Apple', 'Nokia') 
order by revenue desc""",
        {
            itemCount++
            println(" Item ${itemCount} ".center(PADDING_CHARACTERS, '-'))
            println(it.model + " made a total of AUD " + decimalFormat.format(it.revenue))
            //    println("${it.name} - ${it.manufacturer} made a total of ${decimalFormat.format(it.revenue)}")
        })
println(System.lineSeparator())
println(' End of revenue table '.center(PADDING_CHARACTERS, '='))

sql.executeUpdate("""
update phones
set manufacturer = :name
where manufacturer = :oldName
""", [oldName: 'Apple', name: 'APPLE'])

def dataToBeDeleted = [
        [manufacturer: 'Samsung'],
        [manufacturer: 'APPLE']
]

dataToBeDeleted.forEach {
    sql.execute("""
        delete from phones where manufacturer = ${it.manufacturer}
    """)
}

sql.execute("""
    delete from phones where manufacturer = :manufacturer;
    """, [manufacturer: 'Samsung'])

sql.eachRow("""select * from phones;""", {
    println "${it.name} ${it.manufacturer}"
})

sql.close()
