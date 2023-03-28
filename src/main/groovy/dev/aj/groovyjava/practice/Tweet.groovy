package dev.aj.groovyjava.practice

import groovy.transform.Canonical
import groovy.transform.EqualsAndHashCode
import groovy.transform.Immutable
import groovy.transform.ToString

@ToString
//@Immutable
class Tweet {
    String username
    String text
    Integer retweets
    int favourites
    Date createdOn

    public Tweet(String user, String tweet) {
        this.username = user
        this.text = tweet
        retweets = 0
        favourites = 0
        createdOn = new Date()
    }

    void addToRetweets() {
        retweets += 1
    }

    void addToFavourites() {
        favourites += 1
    }
}

@Immutable
@Canonical
class Customer {
    String firstname, lastname
    int age
    Date since
    Collection favItems
}