package com.cse403.matchonthestreet.models;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import com.cse403.matchonthestreet.R;
import com.cse403.matchonthestreet.controller.MOTSApp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Iris on 2/7/16.
 *
 * Represents a sporting Event.
 */
public class Event implements Parcelable {
    /**
     * Representation Invariant:
     *      eid is in the database of events
     *      location is on earth
     *
     * Abstraction Function:
     *      Event e represents an event called e.title, taking place at the coordinates marked by
     *      e.location, at the time denoted by e.time, taking the number of hours of e.duration,
     *      created at the time denoted by e.timeCreated, described by the user in the way denoted
     *      by e.description, which is attended by the list of accounts contained in e.attending.
     */

    // The id number of the event
    public int eid;

    // The title of the event
    public String title;

    // Where the event is held.
    public Location location;

    // The time of the event
    public Date time;

    // The duration of the event, in minutes
    public int duration;

    // The time stamp of the event creation
    public Date timeCreated;

    // A description of the event.
    public String description = "";

    // A list of accounts who have said they will be attending the event.
    public List<Account> attending;

    private static Random rand = new Random();

    public Event(int eid, String title, Location location, Date time,
                 int duration, Date timeCreated, String description) {
        this.eid = eid;
        this.title = title;
        this.location = location;
        this.time = time;
        this.duration = duration;
        this.timeCreated = timeCreated;
        this.description = description;
        this.attending = new ArrayList<>();

    }

    public Event(String title, Location location, Date time, int duration,
                 Date timeCreated, String description) {
        this.eid = title.hashCode();
        this.title = title;
        this.location = location;
        this.time = time;
        this.duration = duration;
        this.timeCreated = timeCreated;
        this.description = description;
        this.attending = new ArrayList<>();
    }

    public Event(String title, Location location, Date time, int duration, String description) {
        this.eid = title.hashCode();
        this.title = title;
        this.location = location;
        this.time = time;
        this.duration = duration;
        this.timeCreated = new Date();
        this.description = description;
        this.attending = new ArrayList<>();
    }

    public Event(boolean random) {
        if (random) {
            // Sample string values to store in list
            Calendar cal = Calendar.getInstance();
            String largeStr = MOTSApp.getContext().getResources().getString(R.string.large_text);

            String[] values = new String[]{"## @ IMA",
                    "Casual play of ##",
                    "Team Potato needs a skillful ## player",
                    "IMA 5v5 ##",
                    "Come and play ##",
                    "Competitive ## match",
                    "2 hours of ##",
                    "Needs a little ##",
                    "Feeling like playing ##?",
                    "Great weather! Play ##",
                    "##. 3 yrs or experience required",
                    "Group ##",
                    "## -- don't bail!",
                    "## after lunch",
                    "looking for ## players",
                    "Would someone teach me ##?",
                    "I dont really know what to put in the title",
                    "Play ## like a boss",
                    "Get down tonight and do some ##",
                    "I miss my girlfriend but let's play ##",
                    "This is another randomly generated event containing the keyword ##"
            };

            String[] sports = new String[]{"basketball", "tennis", "soccer", "football",
                    "badminton", "ping pong", "snooker", "billiard", "running", "swimming",
                    "squash", "baseball", "rowing", "sailing", "climbing", "skating",
                    "chess", "boating", "arch shooting", "skiing", "surfing"};

            String randStr = "";
            for (int j = 0; j < rand.nextInt(9); j++) {
                randStr += (char) ('A' + rand.nextInt(48));
            }
            String randTitle = values[rand.nextInt(values.length)] + " " + randStr;
            randTitle = randTitle.replace("##", sports[rand.nextInt(sports.length)]);
            this.title = randTitle;

            Location l = new Location("dummy");
            l.setLatitude(46 + rand.nextInt(2) + rand.nextDouble());      // center at 47
            l.setLongitude(-123 + rand.nextInt(2) + rand.nextDouble());    // center at 122

            int start = rand.nextInt(largeStr.length() - randTitle.length());
            String d = largeStr.substring(start, start + randTitle.length() + rand.nextInt(10));

            cal.set(2000 + rand.nextInt(17), rand.nextInt(12), 1 + rand.nextInt(28),
                    rand.nextInt(24), rand.nextInt(60));
            Date dateStart = cal.getTime();
            cal.add(Calendar.HOUR, -1 * rand.nextInt(240));
            cal.add(Calendar.MINUTE, -1 * rand.nextInt(60));
            Date dateCreate = cal.getTime();

            this.location = l;
            this.time = dateStart;
            this.duration = rand.nextInt(600) + 20;
            this.timeCreated = dateCreate;
            this.description = d;
        }
    }

    @Override
    public boolean equals(Object o) {
        // If the object is compared with itself then return true
        if (o == this) {
            return true;
        }

        /* Check if o is an instance of Event or not
          "null instanceof [type]" also returns false */
        if (!(o instanceof Event)) {
            return false;
        }

        // typecast o to Event so that we can compare data members
        Event e = (Event) o;

        // Compare the data members and return accordingly
        return eid == e.eid
                && title.equals(e.title)
                && time.equals(e.time)
                && duration == e.duration
                && timeCreated.equals(e.timeCreated)
                && description.equals(e.description)
                && Double.compare(location.getLatitude(), e.location.getLatitude()) == 0
                && Double.compare(location.getLongitude(), e.location.getLongitude()) == 0;
    }

    /*
    public Event(int eid, String title, Location location, Date time, String description) {
        this.eid = eid;
        this.title = title;
        this.location = location;
        this.time = time;
        this.description = description;
        this.attending = new ArrayList<>();
    }

    public Event(String title, Location location, Date time, String description) {
        this.eid = title.hashCode();
        this.title = title;
        this.location = location;
        this.time = time;
        this.description = description;
        this.attending = new ArrayList<>();
    }*/

    /**
     * Tests if an account is attending this event.
     *
     * @account The account to test is attending
     * @return True if the account is attending this event, false otherwise.
     */
    public boolean isAttendedBy(Account account) {
        for (Account attendee : attending) {
            if (attendee.equals(account)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Marks an event as being attended by the given account.
     *
     * @param account The account attending this event
     * @return true if the account was added as attending, false otherwise. If the account
     * was already attending, returns false.
     */
    public boolean addAttendee(Account account) {
        if (!this.isAttendedBy(account)) {
            this.attending.add(account);
            return true;
        }

        return false;
    }

    public String getTitle() { return this.title; }

    public String getDescription() { return this.description; }

    public boolean isAfter(Date time) {
        return this.time.after(time) || this.time.equals(time);
    }

    public boolean isBefore(Date time) {
        return this.time.before(time);
    }

    public boolean containsStrings(List<String> stringList) {
        for(String s : stringList) {
            if (!containsString(s)) {
                return false;
            }
        }
        return true;
    }

    public boolean containsString(String string) {
        if (string == null) {
            return false;
        }
        String lower = string.toLowerCase();
        if (title != null && title.toLowerCase().contains(lower)) {
            return true;
        }
        if (description != null && description.toLowerCase().contains(lower)) {
            return true;
        }
        return false;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(title);
        parcel.writeString(description);
        double lat = location.getLatitude();
        double lon = location.getLongitude();
        parcel.writeDouble(lat);
        parcel.writeDouble(lon);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yy HH:mm", Locale.US);

        String datetime = dateFormat.format(time);

        parcel.writeString(datetime);

        parcel.writeString(dateFormat.format(timeCreated));

        parcel.writeInt(eid);

        parcel.writeInt(duration);


    }

    private Event(Parcel in) {
        this.title = in.readString();
        this.description = in.readString();
        double lat = in.readDouble();
        double lon = in.readDouble();
        Location location = new Location("created in parcel");
        location.setLatitude(lat);
        location.setLongitude(lon);
        this.location = location;
        String dateString = in.readString();
        String timestampString = in.readString();

        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yy HH:mm", Locale.US);
        try {
            this.time = format.parse(dateString);
            this.timeCreated = format.parse(timestampString);
        } catch (ParseException e) {
            this.time = new Date();
            e.printStackTrace();
        }

        this.eid = in.readInt();
        this.duration = in.readInt();


        this.attending = new ArrayList<>();

    }

    public static final Parcelable.Creator<Event> CREATOR
            = new Parcelable.Creator<Event>() {

        // This simply calls our new constructor (typically private) and
        // passes along the unmarshalled `Parcel`, and then returns the new object!
        @Override
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        // We just need to copy this and change the type to match our class.
        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };
}
