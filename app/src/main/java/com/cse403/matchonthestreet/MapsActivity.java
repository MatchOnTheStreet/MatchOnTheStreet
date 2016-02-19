/**
 *
 * This is the Maps view for MOTS, this is the first screen the app will open to.
 * The first time a user runs the app, they will be prompted with the Facebook login screen.
 * Currently in the developer release this can be bypassed by clicking the cancel button upon loggin in.
 * The Maps view provides the user with a high level overview of all the events nearby them or
 * near a specified location.
 *
 * The search bar is used to find events around the searched location.
 * There are 4 buttons, and 3 are used to navigate the app:
 *   * The target icon will center the map on the users current location, provided MOTS has the permissions
 *   * The plus icon will change to the add event view
 *   * The square icon will change to the list view where you can sort and filter events
 *   * The face icon will change to the user profile view where you can view your events
 *
 *
 * Note: to get the location services working, you will need to follow these steps first
 * Open the settings app within the emulator
 * Under the 'Device' subheading click the 'Apps'
 * Find 'Match on the Street'
 * Click 'Permissions'
 * Turn location services on
 *
 * This should get changed later to be turned on through a popup dialogue
 *
 *
 *
 *
 * NOTE: Emulator will not emulate location services automatically, you must open a terminal and
 * send it coordinates:
 *  telnet localhost 5554
 *  The format is: geo fix longitude latitude
 *  so doing 'geo fix -122.31544733 47.6528135881'  will show your location on UW campus
 *
 * Lance Ogoshi
 */


package com.cse403.matchonthestreet;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MapsActivity extends NavActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener {

    public static final int ADD_EVENT_REQUEST_CODE = 1;
    public static final int LIST_VIEW_REQUEST_CODE = 2;

    /** Tag used for printing to debugger */
    private static final String TAG = "MainActivity";

    /** Google Maps object */
    private GoogleMap mMap;

    /** Provides the entry point to Google Play services.*/
    private GoogleApiClient mGoogleApiClient;

    /** Represents a geographical location */
    private Location mLastLocation;
    /** Location Request sent to google play services*/
    private LocationRequest mLocationRequest;
    /** Current location of the user (updated when position is changed) */
    private Location mCurrentLocation;
    /** If continuous location updates are needed */
    private boolean mRequestingLocationUpdates = true;

    private Map<Marker, Event> mapMarkerEvent;
    /**
     *
     * @param savedInstanceState
     *
     * When the view is created, initialize the map, locaiton requests, and buttons
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the view to the xml layout file
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);
        // Initialize the google api features
        buildGoogleApiClient();
        // Starts location requests to the google api
        createLocationRequest();
        // Creates the buttons that look like floating action buttons
        createButtons();

        // Set the DetailFragment to be invisible
        FrameLayout fl = (FrameLayout)findViewById(R.id.fragment_container);
        fl.setVisibility(View.GONE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            Log.d(TAG,"Toolbar Found");
        } else {
            Log.d(TAG,"Toolbar Not Found");
        }

        mapMarkerEvent = new HashMap<>();


        // Database Test
        DBManager db = new DBManager();
        System.out.println("-------------------------------- Connecting to Database");
        db.openConnection().execute();
        db.printStatus().execute();
        db.closeConnection().execute();
        System.out.println("-------------------------------- Done connecting to Database");
    }

    /**
     * Builds a GoogleApiClient. Uses the addApi() method to request the LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }


    /**
     * When starting location updates
     */
    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    /**
     * When stopping location updates
     */
    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected(Bundle connectionHint) {

        Log.d(TAG, "onConnected");

        // Setup callbacks for interactions with the map. Primarily for the MapDetailFragment
        mMap.setOnMarkerClickListener(this);
        mMap.setOnMapClickListener(this);
        mMap.setOnMapLongClickListener(this);
        // Check permissions both Coarse and Fine
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Have COARSE LOCATION permission");
        } else {
            Log.d(TAG, "Do not have COARSE LOCATION permission");
        }

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            Log.d(TAG, "Have FINE LOCATION permission");
        } else {
            Log.d(TAG, "do not have FINE LOCATION permission");
        }

        // Checking for the users last known location and if there is non then creates a
        // sample pin in sydney
        if (mLastLocation != null) {
            LatLng sydney = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            mMap.addMarker(new MarkerOptions().position(sydney).title("Marker"));
            Log.d(TAG, "" + mLastLocation.getLatitude() + ", " + mLastLocation.getLongitude());
        } else {
            Log.d(TAG, "No last known location");
        }

        // Start requesting location updates
        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }

        // Display the Facebook login activity if the user has not logged in before
        SharedPreferences mPrefs = getSharedPreferences("userPrefs", 0);
        String mString = mPrefs.getString("userID", "not found");
        if (mString.equals("not found")) {
            Intent intent = new Intent(MapsActivity.this, LoginActivity.class);
            startActivity(intent);
        }

    }

    /**
     * Starts the location updates from the google maps api
     */
    protected void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            Log.d(TAG, "Starting Location Updates");
        } else {
            // display error message
            Log.d(TAG, "do not have permission");
        }
    }


    /**
     *
     * @param location  the new location of the device
     *
     */
    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "Location Changed");

        // Set the users current location
        mCurrentLocation = location;
        // Move the view over this new location
       // mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));

    }

    /**
     * If location updates are paused, stop the location updates
     */
    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    /**
     * Stop location updates
     */
    protected void stopLocationUpdates() {
        Log.d(TAG, "Stop location updates");
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    /**
     * If location updates are resumed from a pause
     */
    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected() && !mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    /**
     * Called when the connection to the google maps api fails
     * @param result error message from a failed connection
     *
     */
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }


    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        UiSettings mapSettings = mMap.getUiSettings();
        mapSettings.setZoomControlsEnabled(true);
        mapSettings.setMyLocationButtonEnabled(false);
    }

    /**
     * Creates a pin at the specified location
     * @param lat the latitude of the pin
     * @param lon the longitude of the pin
     */
    private void createPin(double lat, double lon) {
        String date = DateFormat.getTimeInstance().format(new Date());
        mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lon)).title(date));
    }

    /**
     * Creates the location request at the specific intervals and with a high priority on the accuracy
     * check api for more details. Can use different priority to prioritize battery life instead
     */
    protected void createLocationRequest() {
        Log.d(TAG, "Create Location Request");
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /**
     * Creates the floating buttons and sets up their callback methods
     */
    protected void createButtons() {

        // The button that pans to the users current location
        FloatingActionButton fabImageButton = (FloatingActionButton) findViewById(R.id.fab_update_location);
        fabImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Location button pressed");

                if (mCurrentLocation != null) {
                    Log.d(TAG, "" + mCurrentLocation.getLatitude() + mCurrentLocation.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude())));
                    // TODO: add zooming here

                    addEventsToMap(new ArrayList<Event>());
                } else {
                    Log.d(TAG, "No last known location");
                }
            }
        });

        // The button that adds a pin to the current location
        // Should redirect to add event screen
        FloatingActionButton fabAddButton = (FloatingActionButton) findViewById(R.id.fab_add_event);
        fabAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Add button pressed");
                Intent intent = new Intent(MapsActivity.this, AddEventActivity.class);
                startActivity(intent);
                if (mCurrentLocation != null) {
                    Log.d(TAG, "" + mCurrentLocation.getLatitude() + mCurrentLocation.getLongitude());
                    //createPin(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
                } else {
                    Log.d(TAG, "No last known location");
                }
            }
        });

        // The button that moves to the ListViewActivity
        FloatingActionButton fabListMap = (FloatingActionButton) findViewById(R.id.fab_map_to_list);
        fabListMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "map to list pressed");
                Intent intent = new Intent(MapsActivity.this, ListViewActivity.class);

                Event testEvent = new Event(0, "Title", new Location("TestLocation"), new Date(), "Description");
                List<Event> eventList = new ArrayList<Event>();
                eventList.add(testEvent);

//                intent.putExtra("Event", eventList);

                startActivity(intent);
            }
        });

        // The button that moves to the UserProfileActivity
        FloatingActionButton fabEvents = (FloatingActionButton) findViewById(R.id.fab_map_to_myevents);
        fabEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "map to list pressed");
                Intent intent = new Intent(MapsActivity.this, UserProfileActivity.class);
                startActivity(intent);
            }
        });

    }

    /**
     *  Called when the search button is pressed. Uses Geocoder to search text -> coordinates
     *  So far only good for searching large locations. Ex. Searching ima will not work
     */
    public void onSearch(View view) {
        EditText searchText = (EditText) findViewById(R.id.map_search_bar);
        String search = searchText.getText().toString();

        if (!search.equals("")) {

            Geocoder geocoder = new Geocoder(this);
            List<Address> addressList = null;
            try {
                addressList = geocoder.getFromLocationName(search, 3);

            } catch (IOException e) {
                Log.d(TAG, e.getMessage());
            }

            if (addressList != null && addressList.size() != 0) {
                Address address = addressList.get(0);
                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());

                for (int i = 0; i < addressList.size(); i++) {
                    if (addressList.get(i) != null) {

                        Log.d(TAG, "Coords: " + addressList.get(i).getLatitude() + ", " + addressList.get(i).getLongitude() );
                    }
                }
                /*
                 For a more robust search use a larger list size for the addressList, get the user location
                  compare it with the coordinates of the search results and choose based on what is closer to the user
                  and/or add it to a popup list.
                 */
                createPin(address.getLatitude(), address.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            }
        }
    }

    /**
     * Callback when a marker is pressed. Displays the info on the DetailFragment
     * @param marker the marker object that has been clicked
     * @return whether or not to override the default behavior of the onMarkerClick.
     */
    public boolean onMarkerClick(Marker marker) {
        Log.d(TAG, "marker clicked: " + marker.getTitle());

        // Show the MapDetailFragment when a marker is pressed
        FrameLayout fl = (FrameLayout)findViewById(R.id.fragment_container);
        fl.setVisibility(View.VISIBLE);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        //ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);  // Animation works, but need to find way to remove fragment onMapClick

        // Send the details of the event to the fragment
        Bundle args = new Bundle();
        args.putString("detailText", marker.getTitle());
        if (mapMarkerEvent.containsKey(marker)) {
            Event event = mapMarkerEvent.get(marker);
            args.putString("date", event.time.toString());
            args.putString("description", event.description);
        }

        MapDetailFragment mapDetailFragment = new MapDetailFragment();
        mapDetailFragment.setArguments(args);
        ft.replace(R.id.fragment_container, mapDetailFragment, "detailFragment");
        ft.commit();

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int height = dm.heightPixels;

        SupportMapFragment mMapFragment = (SupportMapFragment) (getSupportFragmentManager()
                .findFragmentById(R.id.map));
        ViewGroup.LayoutParams params;
        try {
            params = mMapFragment.getView().getLayoutParams();
            params.height = height - 150;
            mMapFragment.getView().setLayoutParams(params);
        }catch (NullPointerException e) {
            Log.d(TAG, e.toString());
        }


        return false;
    }

    /**
     * Callback when the map is tapped. Hides the DetailFragment
     * @param latLng the lat and longitude of where the map was pressed
     */
    public void onMapClick(LatLng latLng) {
        Log.d(TAG, "map clicked ");

        FrameLayout fl = (FrameLayout)findViewById(R.id.fragment_container);
        fl.setVisibility(View.GONE);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int height = dm.heightPixels;

        SupportMapFragment mMapFragment = (SupportMapFragment) (getSupportFragmentManager()
                .findFragmentById(R.id.map));
        ViewGroup.LayoutParams params;
        try {
            params = mMapFragment.getView().getLayoutParams();
            params.height = height + 150;
            mMapFragment.getView().setLayoutParams(params);
        }catch (NullPointerException e) {
            Log.d(TAG, e.toString());
        }

    }

    private void removeAllMarkers() {
        mMap.clear();
        mapMarkerEvent.clear();
    }

    private void addEventsToMap(ArrayList<Event> workingSet) {
        //ArrayList<Event> workingSet = EventDBManager.getEvents();

        removeAllMarkers();

        Location loc = new Location("testProvider1");
        loc.setLatitude(47.6543485);
        loc.setLongitude(-122.3155853);

        Location loc2 = new Location("testProvider2");
        loc2.setLatitude(47.7543485);
        loc2.setLongitude(-122.3155853);
        Event e1 = new Event(1, "TestEvent 1", loc, new Date(), "This is a description for TestEvent 1");
        Event e2 = new Event(2, "TestEvent 2", loc2, new Date(), "This is a description for TestEvent 2");

        workingSet.add(e1);
        workingSet.add(e2);

        for (int i = 0; i < workingSet.size(); i++) {
            Event temp = workingSet.get(i);
            addEventToMap(temp);
        }
    }

    private void addEventToMap(Event event) {
        Location tLoc = event.location;
        Log.d(TAG, "The location is: " + tLoc.getLongitude() + " " + tLoc.getLatitude());
        Marker marker = mMap.addMarker(new MarkerOptions().position(new LatLng(tLoc.getLatitude(), tLoc.getLongitude())).title(event.title));
        mapMarkerEvent.put(marker, event);

    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        Log.d(TAG, "onMapLongClick");
        double lat = latLng.latitude;
        double lon = latLng.longitude;

        Intent intent = new Intent(MapsActivity.this, AddEventActivity.class);
        intent.putExtra("latitude", lat);
        intent.putExtra("longitude", lon);

       // stopLocationUpdates();

        startActivityForResult(intent, ADD_EVENT_REQUEST_CODE);
        //startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // data is a list of Events
        if (requestCode == ADD_EVENT_REQUEST_CODE) {
            Log.d(TAG, "add event returned to onActivityResult");
        }

        if (resultCode == RESULT_CANCELED) {
            Log.d(TAG, "add event result: result canceled");
        }

        if (resultCode == RESULT_FIRST_USER) {
            Log.d(TAG, "add event result: result first user");
        }

        if (resultCode == RESULT_OK && requestCode == ADD_EVENT_REQUEST_CODE) {
            //Event newEvents = (Event) data.getSerializableExtra("newEvent"); //(ArrayList<Event>) data.getSerializableExtra("newEvent");  //data.getExtras().getSerializable("newEvent");
            Log.d(TAG, "add event result: ok");
            //addEventsToMap(newEvents);
            Event newEvent = data.getParcelableExtra("event");
            ArrayList<Event> listEvent = data.getParcelableArrayListExtra("eventList");

            if (listEvent != null && listEvent.size() > 0) {
                Log.d(TAG, "event list has: " + listEvent.get(0).title);
                Log.d(TAG, "event coords: " + listEvent.get(0).location.getLatitude() + ", " + listEvent.get(0).location.getLongitude());
            } else {
                Log.d(TAG, "eventList is null or no elements");
            }

            if (newEvent == null) {
                Log.d(TAG, "newEvent is null");
            } else {
                Log.d(TAG, "Received event: " + newEvent.title);
            }

        } else {
            Log.d(TAG, "not result OK");
        }
    }

}

