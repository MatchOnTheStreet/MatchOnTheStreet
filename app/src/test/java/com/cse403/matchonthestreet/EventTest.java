package com.cse403.matchonthestreet;

import junit.framework.Assert;

import org.junit.Test;

public class EventTest {

    @Test
    public void Event_GetDescription() {
        //Assert.assertEquals("Test Description", Event_GetDescription("Test Description"));
        //Assert.assertEquals("", Event_GetDescription(""));
        //Assert.assertEquals(null, Event_GetDescription(null));
    }

    private String Event_GetDescription(String testDescription) {
        //Event event = new Event(null, null, null, testDescription);
        //return event.getDescription();
        return null;
    }

    @Test
    public void Event_IsAttendedBy_True() {
        // list with account (size 1)
        // list with account (greater size)
    }


    @Test
    public void Event_IsAttendedBy_False() {
        // null list
        // empty list
        // list without account
    }

    @Test
    public void Event_IsAfter_True() {

    }

    @Test
    public void Event_IsAfter_TimeIsEqual_True() {

    }

    @Test
    public void Event_IsAfter_False() {

    }

    @Test
    public void Event_IsBefore_True() {

    }

    @Test
    public void Event_IsBefore_False() {

    }

    @Test
    public void Event_IsCloser_True() {

    }

    @Test
    public void Event_IsCloser_False() {

    }

    @Test
    public void Event_IsCloser_LocationIsEqual_False() {

    }

    @Test
    public void Event_ContainsAllTags_True() {

    }

    @Test
      public void Event_ContainsAllTags_ContainsDifferentTags_False() {

    }

    @Test
    public void Event_ContainsAllTags_TagsAreMissing_False() {

    }

    @Test
    public void Event_ContainsString_StringInTitle_True() {

    }

    @Test
    public void Event_ContainsString_StringInDescription_True() {

    }

    @Test
    public void Event_ContainsString_False() {

    }

    @Test
    public void Event_MeetsFilterAndSearchCriteria_True() {

    }

    @Test
    public void Event_MeetsFilterAndSearchCriteria_TimeIsBefore_False() {

    }

    @Test
    public void Event_MeetsFilterAndSearchCriteria_TimeIsAfter_False() {

    }

    @Test
    public void Event_MeetsFilterAndSearchCriteria_DoesNotContainAllTags_False() {

    }

    @Test
    public void Event_MeetsFilterAndSearchCriteria_DoesNotContainString_False() {

    }
}