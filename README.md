# Match on the Street :football:
Match on the Street allows users to create, find, and join sports events spontaneously. It targets both casual and avid sports players who want to be able to connect with other players near them, in real time. Currently, sports meetups are formed based on either the luck of the draw for a simple pick-up game or having already formed a group meetup plan. With Match on the Street, you can find people whenever and wherever you want. In other words, you get all the benefits of long term planning, but none of the guesswork of regular pick up sports.

## Product Webpage
http://matchonthestreet.github.io/MatchOnTheStreet/

## Developer Webpage
https://homes.cs.washington.edu/~liuh25/matchonthestreet/

## [Documents](https://github.com/MatchOnTheStreet/Docs)

# Current release: [Feature Complete (v0.5)](https://github.com/MatchOnTheStreet/MatchOnTheStreet/releases/tag/v0.5)
## Features Working
* Creating Events!
* Identifying User Location!
* Displaying Map!
* Location Search
* Location Pins!
* User Athentication!
* Personal Information!
* Joining Events
* Filtering
* Title Search
* Time Search
* Tag Search

## Features Not Working
* Event Garbage Collection

## Major Changes:
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



