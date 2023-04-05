package dev.aj.groovyjava.postgres.bootcamp

import dev.aj.groovyjava.db.DBConnectionProperties
import groovy.sql.GroovyRowResult
import groovy.sql.Sql
import org.apache.commons.lang3.time.StopWatch
import java.util.concurrent.TimeUnit

def credentials = DBConnectionProperties.getConnectionCredentials("postgres-bootcamp", "business")
def sql = Sql.newInstance(credentials.url, credentials.userName, credentials.password, credentials.driverClassName)

def watch = new StopWatch()

sql.execute("""
	truncate business.trades;
""")

watch.start()

def file = new File("C:/Users/amarj/Downloads/trades.sql")
file.eachLine { sql.execute(it) }

watch.stop()

def rows = sql.rows("select * from business.trades")

println("Data has been updated; ${rows.size()} added. ${TimeUnit.MILLISECONDS.convert(watch.nanoTime, TimeUnit.NANOSECONDS)} milli-seconds taken.")







