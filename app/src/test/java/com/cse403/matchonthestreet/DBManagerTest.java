package com.cse403.matchonthestreet;

import android.location.Location;

import com.cse403.matchonthestreet.backend.DBManager;
import com.cse403.matchonthestreet.models.Account;
import com.cse403.matchonthestreet.models.Event;

import junit.framework.TestCase;

import org.junit.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Created by larioj on 2/25/16.
 *
 * Every test name has to start with test!!
 * It is some bull.
 *
 */
public class DBManagerTest extends TestCase {
    private Event makeRandomEvent() {
        Random rand = new Random(System.currentTimeMillis());
        String title = "randomEvent" + rand.nextInt();
        Location loc = new Location("randloc");
        loc.setLatitude(rand.nextInt(180) - 89);
        loc.setLongitude(rand.nextInt(360) - 179);
        Date date = new Date();
        int duration = rand.nextInt(240) + 1;
        Date timeCreated = new Date();
        String description = "This is a random event created for the purpose of testing."
                + rand.nextInt();
        return new Event(title, loc, date, duration, timeCreated, description);
    }

    private Account makeRandomAccount() {
        Random rand = new Random(System.currentTimeMillis());
        int uid = rand.nextInt();
        String name = "randname" + rand.nextInt();
        return new Account(uid, name);
    }

    private class Attending {
        public Account a;
        public Event e;

        public Attending(Account a, Event e) {
            this.a = a;
            this.e = e;
        }
    }

    private List<Event> events;
    private List<Account> accounts;
    private List<Attending> attending;

    private static final int NUM = 3;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        events = new ArrayList<>();
        accounts = new ArrayList<>();
        attending = new ArrayList<>();

        for (int i = 0; i < NUM; i++) {
            Event e = makeRandomEvent();
            DBManager.addEvent(e);
            Event e2 = DBManager.getEventById(e.eid);
            assertTrue(e.equals(e2));
            events.add(e);

            Account a = makeRandomAccount();
            DBManager.addAccount(a);
            Account a2 = DBManager.getAccountById(a.getUid());
            assertTrue(a.equals(a2));
            accounts.add(a);
        }
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();

        for(Event e: events) {
            DBManager.removeEvent(e);
            Event e3 = DBManager.getEventById(e.eid);
            assertNull(e3);
        }

        for(Account a: accounts) {
            DBManager.removeAccount(a);
            Account a3 = DBManager.getAccountById(a.getUid());
            assertNull(a3);
        }

        for(Attending pair: attending) {
            DBManager.removeAttendance(pair.a, pair.e);
        }
    }

    @Test
    public void testAddGetRemoveCheckNullEventsAccounts() throws SQLException, ClassNotFoundException {
        // Everything is done in the setup and teardown.
    }

    @Test
    public void testAddEventToAccount() throws SQLException, ClassNotFoundException {
        // Everything is done in the setup and teardown.
        Event e = events.get(0);
        Account a = accounts.get(0);
        DBManager.addAccountToEvent(a, e);
        attending.add(new Attending(a, e));
    }

    @Test
    public void testGetCountOfAccountsAttendingEvent() throws SQLException, ClassNotFoundException {
        // Everything is done in the setup and teardown.
        Event e = events.get(0);
        int c0 = DBManager.getCountOfAccountsAttendingEvent(e);
        assertEquals(0, c0);

        Account a = accounts.get(0);
        DBManager.addAccountToEvent(a, e);
        attending.add(new Attending(a, e));

        int c1 = DBManager.getCountOfAccountsAttendingEvent(e);
        assertEquals(1, c1);
    }
}
