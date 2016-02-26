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
 * <p/>
 * Every test name has to start with test!!
 * It is some bull.
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

        for (Event e : events) {
            DBManager.removeEvent(e);
            Event e3 = DBManager.getEventById(e.eid);
            assertNull(e3);
        }

        for (Account a : accounts) {
            DBManager.removeAccount(a);
            Account a3 = DBManager.getAccountById(a.getUid());
            assertNull(a3);
        }

        for (Attending pair : attending) {
            DBManager.removeAttendance(pair.a, pair.e);
        }
    }

    @Test
    public void testAddGetRemoveCheckNullEventsAccounts() throws SQLException, ClassNotFoundException {
        // Everything is done in the setup and teardown.
    }

    @Test
    public void testAddEventToAccount() throws SQLException, ClassNotFoundException {
        Event e = events.get(0);
        Account a = accounts.get(0);
        DBManager.addAccountToEvent(a, e);
        attending.add(new Attending(a, e));
    }

    @Test
    public void testGetCountOfAccountsAttendingEvent() throws SQLException, ClassNotFoundException {
        Event e = events.get(0);
        int c0 = DBManager.getCountOfAccountsAttendingEvent(e);
        assertEquals(0, c0);

        Account a = accounts.get(0);
        DBManager.addAccountToEvent(a, e);
        attending.add(new Attending(a, e));

        int c1 = DBManager.getCountOfAccountsAttendingEvent(e);
        assertEquals(1, c1);
    }

    @Test
    public void testGetAccountsAttendingEvent() throws SQLException, ClassNotFoundException {
        Event e = events.get(0);
        List<Account> al = DBManager.getAccountsAttendingEvent(e);
        assertEquals(0, al.size());

        Account a = accounts.get(0);
        DBManager.addAccountToEvent(a, e);
        attending.add(new Attending(a, e));
        al = DBManager.getAccountsAttendingEvent(e);
        assertEquals(1, al.size());
        assertTrue(a.equals(al.get(0)));

        Account a2 = accounts.get(1);
        DBManager.addAccountToEvent(a2, e);
        attending.add(new Attending(a2, e));
        al = DBManager.getAccountsAttendingEvent(e);
        assertEquals(2, al.size());
        int count = 0;
        for (Account account : al) {
            if (account.equals(a)) {
                count++;
            }
        }
        assertEquals(1, count);
        count = 0;
        for (Account account : al) {
            if (account.equals(a2)) {
                count++;
            }
        }
        assertEquals(1, count);
    }

    @Test
    public void testGetEventsAttendedByAccount() throws SQLException, ClassNotFoundException {
        Event e0 = events.get(0);
        Event e1 = events.get(1);
        Account a0 = accounts.get(0);

        DBManager.addAccountToEvent(a0, e0);
        attending.add(new Attending(a0, e0));
        DBManager.addAccountToEvent(a0, e1);
        attending.add(new Attending(a0, e1));

        List<Event> el = DBManager.getEventsAttendedByAccount(a0);
        Event[] arr = {e0, e1};
        for (Event exp : arr) {
            int count = 0;
            for (Event res : el) {
                if (exp.equals(res)) {
                    count++;
                }
            }
            assertEquals(1, count);
        }
    }

    @Test
    public void testGetEventsInRadius() throws SQLException, ClassNotFoundException {
        Event e1 = makeRandomEvent();
        Event e2 = makeRandomEvent();
        Event e3 = makeRandomEvent();

        e1.location.setLatitude(0);
        e2.location.setLatitude(0);
        e3.location.setLatitude(20);

        e1.location.setLongitude(0);
        e2.location.setLongitude(20);
        e3.location.setLongitude(10);


        Location center = new Location("");
        center.setLatitude(10);
        center.setLongitude(10);

        Location origin = new Location("");
        origin.setLatitude(0);
        origin.setLongitude(0);

        // Add the events to the database.
        Event[] eventsArr = {e1, e2, e3};
        for (Event e : eventsArr) {
            DBManager.addEvent(e);
            Event er = DBManager.getEventById(e.eid);
            assertTrue(e.equals(er));
            events.add(e);
        }

        List<Event> all = DBManager.getEventsInRadius(center, 20);
        for (Event e : eventsArr) {
            int count = 0;
            for (Event er : all) {
                if (e.equals(er)) {
                    count++;
                }
            }
            assertEquals(1, count);
        }

        List<Event> none = DBManager.getEventsInRadius(center, 2);
        for (Event e : eventsArr) {
            int count = 0;
            for (Event er : none) {
                if (e.equals(er)) {
                    count++;
                }
            }
            assertEquals(0, count);
        }

        List<Event> one = DBManager.getEventsInRadius(origin, 11);
        int count = 0;
        for (Event er : none) {
            if (e1.equals(er)) {
                count++;
            }
        }
        assertEquals(0, count);
    }
}
