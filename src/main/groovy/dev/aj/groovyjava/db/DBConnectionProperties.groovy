package dev.aj.groovyjava.db

class DBConnectionProperties {

    static LinkedHashMap<String, String> getConnectionCredentials() {
        return [url: 'jdbc:postgresql://localhost:5432/postgres', userName: 'admin', password: 'password', driverClassName: 'org.postgresql.Driver']
    }

}
