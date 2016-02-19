package com.cse403.matchonthestreet;

import android.content.res.Resources;
import android.location.Location;

import java.util.ArrayList;
import java.util.Arrays;
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

    public Set<Event> getEventSet() {
        return eventSet;
    }

    public boolean updateEventList(Set<Event> updatedSet) {
        boolean changed = !this.eventSet.equals(updatedSet);
        this.eventSet = updatedSet;
        return changed;
    }


    public List<Event> populateDummyData() {
        // Sample string values to store in list
        List<String> sampleVal = new ArrayList<>();
        Random rand = new Random();
        String largeStr = Resources.getSystem().getString(R.string.large_text);

        String[] values = new String[]{"Tennis match @ Denny",
                "Casual pool play",
                "Team Potato needs a goalkeeper",
                "Basket ball IMA 5v5",
                "Tennis match @ Denny",
                "Casual pool play",
                "Team Potato needs a goalkeeper",
                "Basket ball IMA 5v5",
                "Tennis match @ Denny",
                "Casual pool play",
                "Team Potato needs a goalkeeper",
                "Basket ball IMA 5v5"};

        String[] sports = new String[]{"basketball", "tennis", "soccer", "football",
                "badminton", "table tennis", "pool", "running", "swimming", "racket ball", "baseball",
                ""};
        sampleVal.addAll(Arrays.asList(values));
        for (int i = 0; i < 233; i++) {
            String randStr = "";
            for (int j = 0; j < rand.nextInt(9); j++) {
                randStr += (char)('A' + rand.nextInt(48));
            }
            sampleVal.add(randStr + values[rand.nextInt(values.length)] +
                    sports[rand.nextInt(sports.length)]);
        }

        List<Event> Events = new ArrayList<>();

        // Hardcoded population of list items
        for (String s : sampleVal) {
            Location l = new Location("dummy");
            l.setLatitude(rand.nextDouble() * 90);
            l.setLongitude(rand.nextDouble() * 90);
            l.setAltitude(rand.nextDouble() * 90);

            int start = rand.nextInt(largeStr.length() - s.length());
            String d = largeStr.substring(start, start + s.length());

            Event e = new Event(s.hashCode(), s, l, new Date(2000 + rand.nextInt(17), rand.nextInt(12) + 1, rand.nextInt(28) + 1), d);

            Events.add(e);
        }

        return Events;
    }

    protected static String[] generateRandomWords(int numberOfWords) {
        String[] randomStrings = new String[numberOfWords];
        Random random = new Random();
        for(int i = 0; i < numberOfWords; i++)
        {
            char[] word = new char[random.nextInt(8)+3]; // words of length 3 through 10. (1 and 2 letter words are boring.)
            for(int j = 0; j < word.length; j++)
            {
                word[j] = (char)('a' + random.nextInt(26));
            }
            randomStrings[i] = new String(word);
        }
        return randomStrings;
    }

}
