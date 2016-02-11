package com.cse403.matchonthestreet;

import android.location.Location;

import java.util.*;

/**
 * Created by hopecrandall on 2/11/16.
 */
public class ListViewFilteredSearch {
    // Current search/filter result list
    private List<Event> currentResults;

    // Current Search
    private ListViewFilteredSearchData currentSearch;


    public List<Event> FilterAndSearch(ListViewFilteredSearchData newSearch) {
        int changeInSearch = currentSearch.compareTo(newSearch);

        if (searchIsTheSame(changeInSearch)) {
            return currentResults;
        }

        if (searchHasBeenNarrowed(changeInSearch)) {
            SearchWithinCurrentResults();
        } else {
            SearchInDatabase();
        }

        return currentResults;
    }

    /* iterate through current results and remove anything that does not
     * fit the new criteria*/
    private void SearchWithinCurrentResults() {
        for(Event e : currentResults) {
            if (!meetsFilterAndSearchCriteria(e)) {
                currentResults.remove(e);
            }
        }
    }

    private void SearchInDatabase() {
    }

    private boolean meetsFilterAndSearchCriteria(Event event) {
        return isAfterStartTime(event) && isBeforeEndTime(event) && containsAllTags(event)
                && containsQueryString(event);
    }

    private boolean isAfterStartTime(Event event) {
        return event.time.after(currentSearch.startTime) || event.time.equals(currentSearch.startTime);
    }

    private boolean isBeforeEndTime(Event event) {
        return event.time.before(currentSearch.endTime);
    }

    private boolean containsAllTags(Event event) {
        return true;
    }

    private boolean containsQueryString(Event event) {
        return true;
    }

    private boolean searchIsTheSame(int changeInSearch) {
        return changeInSearch == 0;
    }

    private boolean searchHasBeenNarrowed(int changeInSearch) {
        return  changeInSearch == -1;
    }
}
