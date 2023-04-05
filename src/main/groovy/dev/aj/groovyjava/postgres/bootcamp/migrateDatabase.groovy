package dev.aj.groovyjava.postgres.bootcamp

import dev.aj.groovyjava.db.DBConnectionProperties
import groovy.sql.Sql

def credentials = DBConnectionProperties.getConnectionCredentials("postgres-bootcamp", "business")
def sql = Sql.newInstance(credentials.url, credentials.userName, credentials.password, credentials.driverClassName)

new File("./migrateDatabase.sql").eachLine {
	sql.execute(it)
}

sql.eachRow("SELECT * FROM business.trades_test", {
	println("${it.region}, '${it.country}', ${it.year}, ${it.imports}, ${it.exports}")
})

