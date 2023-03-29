package dev.aj.groovyjava.db

import groovy.sql.Sql

def credentials = DBConnectionProperties.getConnectionCredentials()

def sql = Sql.newInstance(credentials.url, credentials.userName, credentials.password, credentials.driverClassName)
println("Connected to ${credentials.url}")

sql.execute("""
    drop table if exists users CASCADE;

    CREATE TABLE users (
        id SERIAL PRIMARY KEY NOT NULL,
        username varchar(50)
 	   );
 	   """
)

Thread.sleep(5000)

def usernames = ['AJ', 'Dippi', 'Reet', 'Zoravar']

usernames.forEach {
	sql.executeInsert("""
        INSERT INTO users (username) values (${it})
    """)
}

// Will throw constraint violation exception as will leave orphaned children, unless ON DELETE constraint is defined
//sql.execute("DELETE FROM users where id = 4")

sql.close()

