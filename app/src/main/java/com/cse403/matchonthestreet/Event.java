package com.cse403.matchonthestreet;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Iris on 2/7/16.
 *
 * Represents a sporting Event.
 */
public class Event implements Parcelable {

    // The id number of the event
    public int eid;

    // The title of the event
    public String title;

    // Where the event is held.
    protected Location location;

    // The time of the event
    protected Date time;

    // The duration of the event, in minutes
    protected int duration;

    // The time stamp of the event creation
    protected Date timeCreated;

    // A description of the event.
    protected String description = "";

    // A list of accounts who have said they will be attending the event.
    protected List<Account> attending;

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
        String lowerCaseTitle = title.toLowerCase();
        String lowerCaseDesc = description.toLowerCase();

        for(String s : stringList) {
            boolean containsString = containsString(lowerCaseTitle, lowerCaseDesc, s.toLowerCase());
            if (!containsString) {
                return false;
            }
        }

        return true;
    }

    public boolean containsString(String lowerCaseTitle, String lowerCaseDesc, String lowerCaseString) {

        boolean titleContainsString = lowerCaseTitle.contains(lowerCaseString);
        if (titleContainsString) {
            return true;
        }

        boolean descriptionContainsString = lowerCaseDesc.contains(lowerCaseString);
        if (descriptionContainsString) {
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
