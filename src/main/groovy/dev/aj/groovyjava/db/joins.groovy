package dev.aj.groovyjava.db

import groovy.sql.Sql

def credentials = DBConnectionProperties.getConnectionCredentials()

def sql = Sql.newInstance(credentials.url, credentials.userName, credentials.password, credentials.driverClassName)
assert sql != null

println(" Contents by Users ".center(90, '-'))
sql.eachRow("SELECT contents, username from comments c JOIN users u on c.user_id = u.id", {
	println("Content: ${it.contents} left by User: ${it.username}")
})
printEndOfTable(90, "=")

println(" Contents on Photos ".center(90, "-"))
sql.eachRow("SELECT contents, url from comments join photos p on p.id = comments.photo_id", {
	println("Conent: ${it.contents} left on the photo: ${it.url}")
})
printEndOfTable(90, "=")

println(" Photo Urls by Username ".center(50, "-"))
sql.eachRow("SELECT url, username from photos left join users u on photos.user_id = u.id", {
	def displayUserInfo = it.username ? " of User ${it.username}" : ""
	println("Photo Url: ${it.url} ${displayUserInfo}")
})
printEndOfTable(50, "=")

println(" Full join - Photo Urls by Username ".center(50, "-"))
sql.eachRow("SELECT url, username from photos as p FULL JOIN users u on p.user_id = u.id", {
	def displayUserInfo = it.username ? " of User ${it.username}" : ""
	println("Photo Url: ${it.url} ${displayUserInfo}")
})
printEndOfTable(50, "=")

sql.eachRow("SELECT url, contents, username from comments join photos p on p.id = comments.photo_id join users u on u.id = comments.user_id AND u.id = p.user_id", {
	println("URL: ${it.url} - Contents: ${it.contents} - Username: ${it.username}")
})

private printEndOfTable(int numberOfChars, String padding) {
	println(" End of Table ".center(numberOfChars, padding))
}

println(" Group By Example ".center(60, '+'))

sql.eachRow("SELECT user_id, count(user_id) as numberOfUsers from photos GROUP BY user_id order by user_id", {
	// if user_id is null then the count will be '0'
	println("User ID ${it.user_id} has \t: ${it.numberOfUsers} instance/s in the photos table".center(50))
})

println(" Better Version ".center(60, '-'))
// Better version to ensure we count all instances is count(*)
sql.eachRow("SELECT user_id, username, count(*) as numberOfUsers from photos left join users u on u.id = photos.user_id where user_id > 2 GROUP BY user_id, username HAVING user_id is not null AND count(*) >= 2 order by username desc", {
	// if user_id is null then the count will be '0'
	println("User ${it.username} with ID ${it.user_id} has \t: ${it.numberOfUsers} instance/s in the photos table".center(50))
})

printEndOfTable(60, '+')
