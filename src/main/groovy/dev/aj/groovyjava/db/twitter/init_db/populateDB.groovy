package dev.aj.groovyjava.db.twitter.init_db

import dev.aj.groovyjava.db.DBConnectionProperties
import groovy.sql.Sql

def credentials = DBConnectionProperties.getConnectionCredentials("twitter", "twitter_schema")

def sql = Sql.newInstance(credentials.url, credentials.userName, credentials.password, credentials.driverClassName)
println('Connected!!!')


