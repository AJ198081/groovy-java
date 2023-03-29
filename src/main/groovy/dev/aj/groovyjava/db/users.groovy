package dev.aj.groovyjava.db

import groovy.sql.Sql

def credentials = DBConnectionProperties.getConnectionCredentials()

def sql = Sql.newInstance(credentials.url, credentials.userName, credentials.password, credentials.driverClassName)
println("Connected to ${credentials.url}")

sql.execute("""
    drop table if exists users;

    CREATE TABLE users (
        id SERIAL PRIMARY KEY NOT NULL,
        username varchar(50)
 	   );
 	   """
)

def usernames = ['AJ', 'Dippi', 'Reet', 'Zoravar']

usernames.forEach {
	sql.executeInsert("""
        INSERT INTO users (username) values (${it})
    """)
}


