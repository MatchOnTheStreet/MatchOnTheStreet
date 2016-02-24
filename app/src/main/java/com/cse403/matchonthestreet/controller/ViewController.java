package com.cse403.matchonthestreet.controller;

import android.location.Location;

import com.cse403.matchonthestreet.R;
import com.cse403.matchonthestreet.controller.MOTSApp;
import com.cse403.matchonthestreet.models.Event;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Created by Hao on 2/18/2016.
 */
public class ViewController {

    protected Set<Event> eventSet = new HashSet<>();

    protected Location userLocation = null;

    public Set<Event> getEventSet() {
        return eventSet;
    }

    public void addEventToSet(Event event) {
        eventSet.add(event);
    }

    public boolean updateEventList(Set<Event> updatedSet) {
        boolean changed = !this.eventSet.equals(updatedSet);
        this.eventSet = updatedSet;
        return changed;
    }

    public void setUserLocation(Location location) {
        this.userLocation = location;
    }

    public Location getUserLocation() {
        return this.userLocation;
    }

    /*
    * Populate some random dummy events into the working set
    *
    * Only for testing
    *
    */
    public void populateDummyData() {
        // Sample string values to store in list
        List<String> sampleVal = new ArrayList<>();
        Random rand = new Random();
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
                "fooball! Yes foobaal! foobar baz biz boz buzzz"
        };

        String[] sports = new String[]{"basketball", "tennis", "soccer", "football",
                "badminton", "ping pong", "snooker", "billiard", "running", "swimming",
                "squash", "baseball", "rowing", "sailing", "climbing", "skating",
                "chess", "boating", "arch shooting", "skiing", "surfing"};

        for (int i = 0; i < 200; i++) {
            String randStr = "";
            for (int j = 0; j < rand.nextInt(9); j++) {
                randStr += (char)('A' + rand.nextInt(48));
            }
            String randTitle = values[rand.nextInt(values.length)] + " " + randStr;
            randTitle = randTitle.replace("##", sports[rand.nextInt(sports.length)]);
            sampleVal.add(randTitle);

        }

        List<Event> Events = new ArrayList<>();

        // Hardcoded population of list items
        for (String s : sampleVal) {
            Location l = new Location("dummy");
            l.setLatitude(46 + rand.nextInt(2) + rand.nextDouble());      // center at 47
            l.setLongitude(-123 + rand.nextInt(2) + rand.nextDouble());    // center at 122

            int start = rand.nextInt(largeStr.length() - s.length());
            String d = largeStr.substring(start, start + s.length());

            cal.set(2000 + rand.nextInt(17), rand.nextInt(12), 1 + rand.nextInt(28),
                    rand.nextInt(24), rand.nextInt(60));
            Date dateStart = cal.getTime();
            cal.add(Calendar.HOUR, -1 * rand.nextInt(240));
            cal.add(Calendar.MINUTE, -1 * rand.nextInt(60));
            Date dateCreate = cal.getTime();

            Event e = new Event(s.hashCode(), s, l, dateStart, rand.nextInt(600) + 20, dateCreate, d);

            Events.add(e);
        }

        eventSet.addAll(Events);
    }
}
