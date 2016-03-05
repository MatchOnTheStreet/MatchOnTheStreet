package com.cse403.matchonthestreet.models;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.cse403.matchonthestreet.R;
import com.cse403.matchonthestreet.controller.MOTSApp;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Iris on 2/7/16.
 *
 * Represents a sporting Event.
 */
public class Event implements Parcelable, ClusterItem {
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
    private int eid;

    // The title of the event
    private String title;

    // Where the event is held.
    private Location location;

    // The time of the event
    private Date time;

    // The duration of the event, in minutes
    private int duration;

    // The time stamp of the event creation
    private Date timeCreated;

    // A description of the event.
    private String description = "";

    // A list of accounts who have said they will be attending the event.
    private List<Account> attending = new ArrayList<>();

    private static Random rand = new Random();

    public Event(int eid, String title, Location location, Date time,
                 int duration, Date timeCreated, String description) {

        this(title, location, time, duration, description);
        this.eid = eid;
        this.timeCreated = timeCreated;
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

    public Event() {
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


    public int getEid() {
        return eid;
    }

    public void setEid(int eid) {
        this.eid = eid;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public Date getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(Date timeCreated) {
        this.timeCreated = timeCreated;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Account> getAttending() {
        return attending;
    }

    public void setAttending(List<Account> attending) {
        this.attending = attending;
    }

    @Override
    public LatLng getPosition() {
        return new LatLng(this.location.getLatitude(), this.location.getLongitude());
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

    /**
     * Removes the passed account from the list of attendees
     * @param account the account to remove
     * @return if the account was in the list of attendees
     */
    public boolean removeAttendee(Account account) {
        if (isAttendedBy(account)) {
            this.attending.remove(account);
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
        return title != null && title.toLowerCase().contains(lower) || description != null && description.toLowerCase().contains(lower);
    }




    // The following are used for the parcelable interface which allows events to be passed through intents
    // Serializable would be cleaner, but wasn't working for some reason

    /**
     * Required for the parcelable interface
     * @return a bitmask indicating the set of special object types marshalled by the Parcelable
     */
    public int describeContents() {
        // We don't have child classes so return the default 0
        return 0;
    }

    /**
     * Writes the data of an event into primitive data types
     * @param parcel the parcel to write to
     * @param flags any special flags
     */
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

        parcel.writeSerializable((Serializable)attending);

    }

    /**
     * The constructor when reconstructing from a parcel
     * @param in the event as a Parcel object
     */
    @SuppressWarnings("unchecked")
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

        List<Account> accnts = (List<Account>)in.readSerializable();
        if (accnts != null) {
            Log.d("Event", "List of accounts converted to serialiable and back");
            this.attending = accnts;
        } else {
            Log.d("Event", "Failed to serialize list of accounts");
            this.attending = new ArrayList<>();
        }
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
