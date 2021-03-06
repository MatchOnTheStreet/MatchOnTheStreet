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

import dalvik.annotation.TestTarget;

/**
 * Created by larioj on 2/25/16.
 * <p/>
 * Exercises the functionality found in DBManager. This is a "system test" since it mocks the
 * flow of data from database to application.
 * <p/>
 * Note that every test name has to start with test!!
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

    /*
     * Adds NUM events to the database and verifies that they have been added. It
     * also adds NUM accounts to the database and verifies they have been added.
     */
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

    /*
     * Removes all accounts, events, and attending relationships added in the tests and setup,
     * and verifies they have been removed.
     */
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
            assertTrue(DBManager.checkAttendance(pair.a, pair.e));
            DBManager.removeAttendance(pair.a, pair.e);
            assertFalse(DBManager.checkAttendance(pair.a, pair.e));
        }
    }

    /*
     * In the setup method we add a known set of elements to the database, and verify that they
     * exist. We need this for the rest of the tests. In tearDown() we remove the events we added
     * and verify that they have been removed. This function merely tests the setup and teardown
     * although it uses a lot of DBMethods.
     *
     */
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

        for (int i = 1; i <= NUM; i++) {
            Account a = accounts.get(i - 1);
            DBManager.addAccountToEvent(a, e);
            attending.add(new Attending(a, e));

            int c1 = DBManager.getCountOfAccountsAttendingEvent(e);
            assertEquals(i, c1);
        }
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
    public void testGetEventsInRadiusSimple() throws SQLException, ClassNotFoundException {
        Location loc = new Location("");
        DBManager.getEventsInRadius(loc, 20);
    }

    @Test
    public void testGetEventsInRadiusOne() throws SQLException, ClassNotFoundException {
        Event e1 = events.get(0);
        Location loc = new Location("");
        loc.setLatitude(e1.location.getLatitude());
        loc.setLongitude(e1.location.getLongitude());
        List<Event> el = DBManager.getEventsInRadius(loc, 20);
        int count = 0;
        for (Event e : el) {
            if (e.equals(e1)) {
                count++;
            }
        }
        assertEquals(1, count);
    }

    @Test
    public void testGetEventsInRadiusTWo() throws SQLException, ClassNotFoundException {
        Event e1 = events.get(0);
        Event e2 = events.get(1);

        double latdiff = Math.abs(e1.location.getLatitude() - e2.location.getLatitude());
        double londiff = Math.abs(e1.location.getLongitude() - e2.location.getLongitude());
        double radius = Math.max(latdiff, londiff) + 2;

        Location loc = new Location("");
        loc.setLatitude(e1.location.getLatitude());
        loc.setLongitude(e1.location.getLongitude());
        List<Event> el = DBManager.getEventsInRadius(loc, radius);

        Event[] events12 = {e1, e2};
        for (Event e : events12) {
            int count = 0;
            for (Event er : el) {
                if (er.equals(e)) {
                    count++;
                }
            }
            assertEquals(1, count);
        }
    }

    @Test
    public void testGetEventByIdWithAttendance() throws SQLException, ClassNotFoundException {
        Event e1 = events.get(0);
        Account a1 = accounts.get(0);
        Account a2 = accounts.get(1);

        DBManager.addAccountToEvent(a1, e1);
        attending.add(new Attending(a1, e1));
        DBManager.addAccountToEvent(a2, e1);
        attending.add(new Attending(a2, e1));

        Event er = DBManager.getEventByIdWithAttendance(e1.eid);
        assertNotNull(er);
        assertEquals(2, er.attending.size());

        Account[] accounts12 = {a1, a2};
        for (Account a : accounts12) {
            int count = 0;
            for (Account ar: er.attending) {
                if (a.equals(ar)) {
                    count++;
                }
            }
            assertEquals(1, count);
        }
    }

    @Test
    public void testGetEventsInRadiusWithAttendingTWo() throws SQLException, ClassNotFoundException {
        Event e1 = events.get(0);
        Event e2 = events.get(1);
        Account a1 = accounts.get(0);
        Account a2 = accounts.get(1);

        DBManager.addAccountToEvent(a1, e1);
        attending.add(new Attending(a1, e1));
        DBManager.addAccountToEvent(a2, e1);
        attending.add(new Attending(a2, e1));

        DBManager.addAccountToEvent(a1, e2);
        attending.add(new Attending(a1, e2));
        DBManager.addAccountToEvent(a2, e2);
        attending.add(new Attending(a2, e2));


        double latdiff = Math.abs(e1.location.getLatitude() - e2.location.getLatitude());
        double londiff = Math.abs(e1.location.getLongitude() - e2.location.getLongitude());
        double radius = Math.max(latdiff, londiff) + 2;

        Location loc = new Location("");
        loc.setLatitude(e1.location.getLatitude());
        loc.setLongitude(e1.location.getLongitude());
        List<Event> el = DBManager.getEventsInRadiusWithAttendance(loc, radius);

        Event[] events12 = {e1, e2};
        for (Event e : events12) {
            int count = 0;
            for (Event er : el) {
                if (er.equals(e)) {
                    count++;
                }
            }
            assertEquals(1, count);
        }

        Account[] accounts12 = {a1, a2};
        for (Event e : el) {
            if (e.equals(e1) || e.equals(e2)) {
                for (Account a : accounts12) {
                    int count = 0;
                    for (Account ar : e.attending) {
                        if (ar.equals(a)) {
                            count++;
                        }
                    }
                    assertEquals(1, count);
                }
            }
        }
    }
}
