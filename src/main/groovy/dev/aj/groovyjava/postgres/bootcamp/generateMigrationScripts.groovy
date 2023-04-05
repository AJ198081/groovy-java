package dev.aj.groovyjava.postgres.bootcamp
// GENERATE MIGRATION SCRIPTS.groovy file
import dev.aj.groovyjava.db.DBConnectionProperties
import groovy.sql.GroovyRowResult
import groovy.sql.Sql

def forwardDB = new File("./migrateDatabase.sql")
def backwardDB = new File("./rollbackDatabase.sql")
forwardDB.write('')
backwardDB.write('')

def credentials = DBConnectionProperties.getConnectionCredentials("postgres-bootcamp", "business")
def sql = Sql.newInstance(credentials.url, credentials.userName, credentials.password, credentials.driverClassName)

sql.execute("""
drop table if exists business.trades_test;
create table business.trades_test (
    id      serial primary key,
    region  varchar(40),
    country varchar(40),
    year    integer,
    imports numeric,
    exports numeric
);
""")

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
	INSERT INTO business.trades_test (region, country, year, imports, exports) values ('${row.region}', '${row.country}', ${row.year}, ${row.imports}, ${row.exports});
"""
}

static String deleteStatement(GroovyRowResult row) {
	return """
	DELETE FROM business.trades_test where region='${row.region}' AND country='${row.country}' AND year=${row.year} AND imports=${row.imports} AND exports=${row.exports};
"""
}
