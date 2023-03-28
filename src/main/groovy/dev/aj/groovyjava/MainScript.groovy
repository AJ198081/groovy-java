package dev.aj.groovyjava

import groovy.sql.GroovyRowResult
import org.apache.groovy.datetime.extensions.DateTimeExtensions
import org.apache.groovy.datetime.extensions.DateTimeStaticExtensions

import java.time.Instant
import java.time.LocalDateTime
import java.util.concurrent.LinkedBlockingQueue

import groovy.sql.Sql

String name = "Zoravar Jang Singh bhandal"

/*def sharedQueue = [] as LinkedBlockingQueue

def newThread1 = Thread.start('newThread2'){
    Thread.sleep(200)
    for (i in 0..10) {
        println("${Thread.currentThread().name} \t: ${i}")
        sharedQueue.add(i)
    }
}

def thread = Thread.start('push'){
    10.times {
        println("${Thread.currentThread().name} \t: ${it}")
        sharedQueue.add(it)
    }
}

def thread1 = Thread.start('push1') {
    Thread.sleep(500)

    10.times {
        println("${Thread.currentThread().name} \t: ${it}")
        sharedQueue.add(it)
    }
}


Thread.start('pop'){
    while (true){
        sleep(200)
        println("${Thread.currentThread().name} \t: ${sharedQueue.take()}")

    }
}*/

def sql = Sql.newInstance("jdbc:postgresql://localhost:5432/postgres", "admin", "password", "org.postgresql.Driver")
println("Connected!!")

/*sql.eachRow('select * from city', {
    println(it)
})

def results = sql.rows('select * from city where CountryCode = "IND" and District="Punjab"')
results.forEach {print(it)}

def file = new File('db.csv')
file.write("")
def result = results[0]
def headerLine = result.keySet().join(",")
file.write(headerLine)

results.forEach {
    file.append(System.lineSeparator())
    file.append(it.values().join(","))
}*/

sql.execute("""
drop table if exists cities;
    create table cities (
        name varchar(50),
        country varchar(50),
        population numeric,
        area numeric
    );
""")

def tokyo = [name: 'Tokyo', country: 'Japan', population: 38505000, area: 8223]
def delhi = [name: 'Delhi', country: 'India', population: 28125000, area: 2240]
def shanghai = [name: 'Shanghai', country: 'China', population: 22125000, area: 4015]
def saoPaulo = [name: 'Sao Paulo', country: 'Brazil', population: 20935000, area: 3043]
def dummy = [name: 'dummy', population: 23423423]

def data = [tokyo, delhi, shanghai, saoPaulo, dummy]

data.forEach {
   sql.execute("""
insert into cities (name, country, population, area) values (${it.name}, ${it.country}, ${it.population}, ${it.area})
""")
    if (sql.updateCount == 1) {
        println "${it.name} has been persisted"
    } else {
        println("Unable to persist ${it.name}, please have a look")
    }
}

/*def list = sql.executeInsert("""
insert into cities (name, country, population, area) values (${data.name}, ${data.country}, ${data.population}, ${data.area})
""")*/

sql.eachRow("select name, country, population, area from cities", {
    println("${it.name} : ${it.country} : ${it.population} : ${it.area}")
})

sql.eachRow("select name, round(population/area, 2) as density from cities", {
    println("${it.name} has a population density of ${it.density}")
})

sql.close()

/*def formattedDate = DateTimeExtensions.format(LocalDateTime.now(), "d/M/y")
println(formattedDate)*/




