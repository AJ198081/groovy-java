package dev.aj.groovyjava.db

import groovy.sql.Sql

def credentials = DBConnectionProperties.getConnectionCredentials()

def sql = Sql.newInstance(credentials.url, credentials.userName, credentials.password, credentials.driverClassName)
println("Connected to ${credentials.url}")

sql.execute("""
	DROP TABLE IF EXISTS comments;
	CREATE TABLE comments(
	    id SERIAL PRIMARY KEY,
	    contents VARCHAR(240),
	    user_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
	    photo_id INTEGER REFERENCES photos(id) ON DELETE CASCADE
	)
""")

def commentsData = [
		[comments: 'Hello, welcome to my 1st comments on 1st photo by 1st user', user_id: 1, photo_id: 1],
		[comments: 'Hello, welcome to my 2nd comments on 2nd photo by 2nd user', user_id: 2, photo_id: 2],
		[comments: 'Hello, welcome to my 3rd comments on 3rd photo by 3rd user', user_id: 3, photo_id: 3],
		[comments: 'Hello, welcome to my 4th comments on 4th photo by 4th user', user_id: 4, photo_id: 4],
]

commentsData.forEach {
	sql.executeInsert("""
	INSERT INTO comments (contents, user_id, photo_id) values (${it.comments}, ${it.user_id}, ${it.photo_id})
""")
}

sql.close()