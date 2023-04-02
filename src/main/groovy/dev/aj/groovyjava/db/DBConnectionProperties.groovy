package dev.aj.groovyjava.db

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
    
}
