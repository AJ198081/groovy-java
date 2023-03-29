package dev.aj.groovyjava.db

import groovy.sql.Sql

def credentials = DBConnectionProperties.getConnectionCredentials()

def sql = Sql.newInstance(credentials.url, credentials.userName, credentials.password, credentials.driverClassName)
println("Connected to ${credentials.url}")

/*sql.execute("""
	drop table if exists photos;

	CREATE TABLE photos (
	    id SERIAL PRIMARY KEY NOT NULL,
	    url varchar(200),
	    user_id INTEGER REFERENCES users(id) ON DELETE CASCADE default null)
""")*/

// ON DELETE CASCADE or ON DELETE SET NULL are two common scenarios
sql.execute("""
	drop table if exists photos;

	CREATE TABLE photos (
	    id SERIAL PRIMARY KEY NOT NULL,
	    url varchar(200),
	    user_id INTEGER REFERENCES users(id) ON DELETE SET NULL default null)
""")

def photosDetails = [
        [url: 'http://AJ.jpg' , user_id: 1],
        [url: 'http://dippi.jpg', user_id: 2],
        [url: 'http://Reet.jpg', user_id: 3],
        [url: 'http://Zoravar.jpg', user_id: 4],
		[url: 'http://dummy.jpg'],
        [url: 'http:/one.jpg', user_id: 4]
]

photosDetails.forEach {
	sql.executeInsert("""
	INSERT INTO photos (url, user_id) values (${it.url}, ${it.user_id})
	""")
}

sql.eachRow("""
	SELECT photos.url, u.username from photos left join users u on u.id = photos.user_id
	""", {
	println("User ${it.username}'s photo can be located at ${it.url}")
})

sql.close()
