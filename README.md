# Match on the Street :football:
Match on the Street allows users to create, find, and join sports events spontaneously. It targets both casual and avid sports players who want to be able to connect with other players near them, in real time. Currently, sports meetups are formed based on either the luck of the draw for a simple pick-up game or having already formed a group meetup plan. With Match on the Street, you can find people whenever and wherever you want. In other words, you get all the benefits of long term planning, but none of the guesswork of regular pick up sports.

## Product Webpage
http://matchonthestreet.github.io/MatchOnTheStreet/

## Developer Webpage
https://homes.cs.washington.edu/~liuh25/matchonthestreet/

## [Documents](https://github.com/MatchOnTheStreet/Docs)

# Current release: [Final Release (v1.0)](https://github.com/MatchOnTheStreet/MatchOnTheStreet/releases/tag/v1.0)

## Major Changes:
### v0.8 -> v1.0
* Added tutorial
* Bug fixes

### v0.5 -> v0.8
* Resolved marker clustering issues
* Introduced mechanisms to delete old events automatically and periodically
* Icons are clear and no longer randomly chosen
* Bugs with the filtering have been resolved
* Transitions between the map and the list view are better aligned with what the user would expect.
* Removed the google maps buttons that would appear behind the details of an event
* The names of the people attending an event are now visible 
* Added hours to the event details.
* Keyboard is dismissed when entering the Maps view
* Miscellaneous bug fixes.

### v0.1 -> v0.5
* Name changes:
  * ListActivity ⇒  ListViewActivity
  * EventDetailsFragment ⇒ MapDetailFragment
  * SideBarActivity ⇒ NavActivity
* Added MOTSApp class for cross-activity references.
* Added duration, timestamp, and uniqueID properties to Event class.
* In the map view, “Add event” button was removed. User can tap and hold on the map to call the AddEventActivity.
* In the list view, “Filter” button was removed. User can scroll down from the top of list to access filter/search fields.
* Used a Singleton DBManager instead of separating into EventDBManager and AccountDBManager.
* For the backend we changed our hosting from cubist to AWS, since we found that cubist databases do not support connections from outside the department.

## Features Working
* Creating Events!
* Identifying User Location!
* Displaying Map!
* Location Search
* Location Pins!
* User Athentication!
* Personal Information!
* Joining Events!
* Filtering!
* Title Search!
* Time Search!
* Tag Search!
* Event Garbage Collection!



