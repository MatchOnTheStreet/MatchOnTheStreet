package com.cse403.matchonthestreet;

import android.location.Location;

import java.util.*;

/**
 * Created by hopecrandall on 2/11/16.
 */
public class ListViewFilteredSearch {
    // Current search/filter result list
    private Set<Event> currentResults;

    // Current Search
    private ListViewFilteredSearchData currentSearch;


    /* Takes in the user's filter and search criteria and returns a list of
       events that fit that criteria */
    public Set<Event> FilterAndSearch(ListViewFilteredSearchData newSearch) {
        Date timeOfLastSearch = currentSearch.timeStamp;

        int changeInSearch = newSearch.compareTo(currentSearch);
        this.currentSearch = newSearch;

        if (searchIsTheSame(changeInSearch)) {
            SearchInDatabase(timeOfLastSearch);
            return null;
        }

        if (searchHasBeenNarrowed(changeInSearch)) {
            SearchWithinCurrentResults();
            SearchInDatabase(timeOfLastSearch);
        } else {
            currentResults = new TreeSet<>();
            SearchInDatabase();
        }

        return currentResults;
    }

    /* Iterate through current results and remove anything that does not
       fit the new search and filter criteria */
    private void SearchWithinCurrentResults() {
        for(Event e : currentResults) {
            if (!e.meetsFilterAndSearchCriteria(this.currentSearch)) {
                currentResults.remove(e);
            }
        }
    }

    /* Search the database for events that meet the new search and filter
       criteria and add them */
    private void SearchInDatabase() {
        SearchInDatabase(new Date(0));
    }

    private void SearchInDatabase(Date createdAfter) {

    }

    /* Determines whether the new search/filtering is the same as the
       current search/filtering. */
    private boolean searchIsTheSame(int changeInSearch) {
        return changeInSearch == 0;
    }

    /* Determines whether the new search/filtering is a narrowing of
       the current search/filtering
       Narrowing is defined as any or all of:
       -The new time interval is within the old time interval
       -The new query string contains the same strings plus
        additional strings
       */
    private boolean searchHasBeenNarrowed(int changeInSearch) {
        return changeInSearch == -1;
    }
}
