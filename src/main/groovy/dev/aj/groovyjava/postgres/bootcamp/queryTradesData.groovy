package dev.aj.groovyjava.postgres.bootcamp

import dev.aj.groovyjava.db.DBConnectionProperties
import groovy.sql.GroovyRowResult
import groovy.sql.Sql

def forwardDB = new File("./forward.sql")
def backwardDB = new File("./backward.sql")
forwardDB.write('')
backwardDB.write('')

def credentials = DBConnectionProperties.getConnectionCredentials("postgres-bootcamp", "business")
def sql = Sql.newInstance(credentials.url, credentials.userName, credentials.password, credentials.driverClassName)

def data = sql.rows("""
	select * from business.trades;
""")

data.stream()
		.limit(10)
		.forEach {
			{
				forwardDB.append(insertStatement(it))
				backwardDB.append(deleteStatement(it))
			}
		}

static String insertStatement(GroovyRowResult row) {
	"""
	INSERT INTO business.trades (region, country, year, imports, exports) values ('${row.region}', '${row.country}', ${row.year}, ${row.imports}, ${row.exports});
"""
}

static String deleteStatement(GroovyRowResult row) {
	return """
	DELETE FROM business.trades where region='${row.region}' AND country='${row.country}' AND year=${row.year} AND imports=${row.imports} AND exports=${row.exports};
"""
}

