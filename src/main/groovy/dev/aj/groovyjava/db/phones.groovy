package dev.aj.groovyjava.db
import groovy.sql.Sql
import java.text.DecimalFormat
import java.text.NumberFormat

def decimalFormat = new DecimalFormat("#,###.00")
def dbConnection = [url: 'jdbc:postgresql://localhost:5432/postgres', userName: 'admin', password: 'password', driverClassName: 'org.postgresql.Driver']
def sql = Sql.newInstance(dbConnection.url, dbConnection.userName, dbConnection.password, dbConnection.driverClassName)
println('Connected to db')

final PADDING_CHARACTERS = 45

sql.execute("""
drop table if exists phones;
create table phones(
    name varchar(25),
    manufacturer varchar(25),
    price float,
    units_sold integer
);
""")

def data = [
        [name: 'N1280', manufacturer: 'Nokia', price: 199.50, units_sold: 1925],
        [name: 'iPhone 4', manufacturer: 'Apple', price: 399, units_sold: 9436],
        [name: 'Galaxy S', manufacturer: 'Samsung', price: 299, units_sold: 2359],
        [name: 'S5620', manufacturer: 'Samsung', price: 250, units_sold: 2385],
]

data.forEach {
    sql.executeInsert("""
    insert into phones (name, manufacturer, price, units_sold) values (${it.name}, ${it.manufacturer}, ${it.price}, ${it.units_sold});
""")
}

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
        [manufacturer: 'APPLE'],
        [manufacturer: 'Nokia']
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

data.forEach {
    sql.executeInsert("""
    insert into phones (name, manufacturer, price, units_sold) values (${it.name}, ${it.manufacturer}, ${it.price}, ${it.units_sold});
""")
}

println(" Revenue Table for sales greater than a million ".center(60, '+'))
sql.eachRow("""
    SELECT manufacturer, SUM(price * phones.units_sold) as revenue FROM phones GROUP BY manufacturer HAVING SUM(price * phones.units_sold) > 1000000;
""", {
    println("${it.manufacturer} \t: made a total of ${decimalFormat.format(it.revenue)}. ")
})

sql.eachRow("""
    SELECT * FROM phones order by price desc limit 2 offset 1;
""", {
    println("${it.name} : ${it.price}")
})

sql.eachRow("""
    SELECT * FROM phones order by price desc;
""", {
    println("${it.name} : ${it.price}")
})

def percentangeFormat = NumberFormat.getPercentInstance(Locale.ENGLISH)

println(" Table to show price_ratio of the phones ".center(60, "-"))
sql.eachRow("""
    select name, price, ( cast(price as float ) / (select max(price) from phones)) as price_ratio
    from phones
""", {
    println("${it.name} |\t ${it.price} |\t ${percentangeFormat.format(it.price_ratio)}")
})
println " End of Table ".center(60, "-")

println (" Table to show price_ratio of the phones ".center(60, "-"))
sql.eachRow("""
    select name, price, ( price / (select max(price) from phones)) as price_ratio
    from phones
""", {
    println("${it.name} |\t ${it.price} |\t ${percentangeFormat.format(it.price_ratio)}")
})
println " End of Table ".center(60, "-")

sql.close()
