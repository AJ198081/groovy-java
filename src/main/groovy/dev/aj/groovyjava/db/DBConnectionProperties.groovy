package dev.aj.groovyjava.db

import groovy.sql.Sql

import javax.sql.DataSource

class DBConnectionProperties {
    
    static LinkedHashMap<String, String> getConnectionCredentials() {
        return [url: 'jdbc:postgresql://localhost:5432/postgres', userName: 'admin', password: 'password', driverClassName: 'org.postgresql.Driver']
    }
    
    static LinkedHashMap<String, String> getConnectionCredentials(String databaseName, String schemaName) {
        return [url            : 'jdbc:postgresql://localhost:5432/'.concat(databaseName).concat("?currentSchema=${schemaName}"),
                userName       : 'admin',
                password       : 'password',
                driverClassName: 'org.postgresql.Driver']
    }

    static Sql getConnection() {
        def dataSourceMap = new LinkedHashMap<String, String>()
        dataSourceMap.put("url", 'jdbc:postgresql://localhost:5432/postgres_db')
        dataSourceMap.put("user", 'admin')
        dataSourceMap.put("password", 'password')
        dataSourceMap.put('driverClassName', 'org.postgresql.Driver')
        return Sql.newInstance(dataSourceMap)
    }
    
}
