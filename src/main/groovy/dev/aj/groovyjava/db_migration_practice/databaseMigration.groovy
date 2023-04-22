package dev.aj.groovyjava.db_migration_practice

import dev.aj.groovyjava.db.DBConnectionProperties
import groovy.sql.GroovyRowResult
import groovy.sql.Sql

def sql = DBConnectionProperties.getConnection()

sql.execute ("""
    Drop table if exists athletes;

    Create table athletes(
        athleteId SERIAL primary key,
        firstName varchar(50),
        lastName varchar,
        birthDate varchar
    )
""")

sql.execute("""
    INSERT INTO athletes (firstname, lastname, birthDate) VALUES ('AJ', 'B', '1970-01-01');
""")

def rows = sql.rows("""
    SELECT * from athletes;
""")

rows.forEach ({
    sql.executeInsert(it, """
        INSERT INTO athletes (firstname, lastname, birthDate) values (:firstName, :lastName, :birthDate)
    """)
})

def map = ["firstName": 'Z', "lastName": 'B', "birthDate": "2020-01-01"]
sql.executeInsert("""
    INSERT INTO athletes(firstname, lastname, birthdate) values (:firstName, :lastName, :birthDate)
""", map)



def rowResults = sql.rows("SELECT * FROM athletes")

rowResults.forEach({
    sql.executeInsert("""
    INSERT INTO athletes (firstname, lastname, birthDate) values (:firstName, :lastName, :birthDate)
 """, it)
})

rowResults.forEach {println("${it.firstName} ${it.lastName} ${it.birthDate}")}

rowResults.stream()
        .filter {it.get("firstname") == 'AJ'}
        .forEach {{
            def numberOfRowsUpdated = sql.executeUpdate("""
                    UPDATE athletes SET firstname = 'AJB' WHERE athleteId = :athleteId
                """, it)
            println(numberOfRowsUpdated + ' row(s) has been updated')
        }}

sql.eachRow("""
    SELECT * FROM athletes
""", {
    println("""First Name: ${it.firstname}, Last Name: ${it.lastName}, DOB ${it.birthDate}""")
})

sql.close()
