package com.cse403.matchonthestreet;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Iris on 2/7/16.
 *
 * Represents a user's account.
 */
public class Account {

    // User ID given by facebook.
    private String uid;

    // The name of the user whose account this is.
    private String name;

    // A list of events the user is planning on attending.
    private List<Event> events;

    public Account(String uid) {
        new Account(uid, null);
    }

    public Account(String uid, String name) {
        this.uid = uid;
        this.name = name;
        this.events = null;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Account)) {
            return false;
        }

        Account a = (Account) o;
        return a.uid.equals(this.uid);
    }
}
