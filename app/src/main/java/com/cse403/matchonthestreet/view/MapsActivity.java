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


package com.cse403.matchonthestreet.view;

import android.Manifest;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;


import com.cse403.matchonthestreet.R;
import com.cse403.matchonthestreet.backend.DBManager;
import com.cse403.matchonthestreet.controller.MOTSApp;
import com.cse403.matchonthestreet.controller.SportsIconFinder;
import com.cse403.matchonthestreet.controller.ViewController;
import com.cse403.matchonthestreet.models.Account;
import com.cse403.matchonthestreet.models.Event;
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
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.VisibleRegion;

import java.io.IOException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class MapsActivity extends NavActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener,
        GoogleMap.OnMapLongClickListener {

    public static final int ADD_EVENT_REQUEST_CODE = 1;
    public static final int LIST_VIEW_REQUEST_CODE = 2;

    public static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 10;
    public static final int NO_SELECTED_EVENT = -1;
    public static final int ZOOM_IN_MAGNITUDE = 15;


    private boolean FROM_LIST;
    private boolean FROM_LIST_ITEM;
    private static boolean FIRST_LAUNCH = true;
    private int selectedEventID = NO_SELECTED_EVENT;

    private boolean centerOnLocation = true;

    /** Tag used for printing to debugger */
    private static final String TAG = "MainActivity";

    /** Google Maps object */
    private GoogleMap mMap;

    /** Provides the entry point to Google Play services.*/
    private GoogleApiClient mGoogleApiClient;

    /** Location Request sent to google play services*/
    private LocationRequest mLocationRequest;

    /** Current location of the user (updated when position is changed) */
    private Location mCurrentLocation;

    /** If continuous location updates are needed */
    private boolean mRequestingLocationUpdates = true;

    private Map<Marker, Event> mapMarkerEvent = new HashMap<>();

    private ViewController viewController;

    private Event passedEvent;
    /**
     *
     * @param savedInstanceState
     *
     * When the view is created, initialize the map, locaiton requests, and buttons
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize the google api features
        buildGoogleApiClient();
        // Set the view to the xml layout file
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

        // Starts location requests to the google api
        checkLocationPermission();
        createLocationRequest();


        Log.d(TAG, "onCreate");
        Intent intent = getIntent();

        FROM_LIST = intent.getBooleanExtra(ListViewActivity.EXTRA_MESSAGE, false);
        selectedEventID = intent.getIntExtra(ListViewActivity.class.toString() + ".VIEW_EVENT",
                NO_SELECTED_EVENT);
        FROM_LIST_ITEM = intent.getBooleanExtra("fromListItem", false);


        // Creates the buttons that look like floating action buttons
        createButtons();

        if (FROM_LIST || FROM_LIST_ITEM) {
            Log.d(TAG, "From List");
            centerOnLocation = false;
        }


        // Obtain the current instance of ViewController
        viewController = ((MOTSApp) getApplicationContext()).getViewController();
        // TODO: Here the dummy data is used in the ViewController, when the activity
        // is first launched.
        if (FIRST_LAUNCH) {
            viewController.populateDummyData();
            FIRST_LAUNCH = false;
        }



        // Set the DetailFragment to be invisible
        FrameLayout fl = (FrameLayout)findViewById(R.id.fragment_container);
        fl.setVisibility(View.GONE);

        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(findViewById(R.id.map_search_bar).getWindowToken(), 0);

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
     * Checks whether the location service is permitted by the user,
     * if not, request the permission.
     */
    public void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay!
                    // Do tasks that needs location services.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
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
            Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLastLocation != null && centerOnLocation) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()),ZOOM_IN_MAGNITUDE));
            }
        } else {
            Log.d(TAG, "Do not have COARSE LOCATION permission");
        }

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            Log.d(TAG, "Have FINE LOCATION permission");
            Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLastLocation != null && centerOnLocation) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), ZOOM_IN_MAGNITUDE));
            }
        } else {
            Log.d(TAG, "do not have FINE LOCATION permission");
        }

        // Start requesting location updates
        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }


        Intent intent = getIntent();

        FROM_LIST = intent.getBooleanExtra(ListViewActivity.EXTRA_MESSAGE, false);
        selectedEventID = intent.getIntExtra(ListViewActivity.class.toString() + ".VIEW_EVENT",
                NO_SELECTED_EVENT);
        FROM_LIST_ITEM = intent.getBooleanExtra("fromListItem", false);


        // Creates the buttons that look like floating action buttons
        createButtons();

        if (FROM_LIST || FROM_LIST_ITEM) {
            Log.d(TAG, "From List");
            centerOnLocation = false;
            Event event = intent.getParcelableExtra("selectedEvent");
            Log.d(TAG, "Maps was passed the event: " + event.title);
            Log.d(TAG, "passed event location " + event.location.getLatitude() + " " + event.location.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(event.location.getLatitude(), event.location.getLongitude()), ZOOM_IN_MAGNITUDE));
            displayMarkerInfo(event);
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

        Log.d(TAG, "Location Changed ");

        // Set the users current location
        mCurrentLocation = location;

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
        if (mGoogleApiClient.isConnected())
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

        Set<Event> currentEvents = viewController.getEventSet();
        addEventsToMap(new ArrayList<>(currentEvents));


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
                    Log.d(TAG, "" + mCurrentLocation.getLatitude() + " " + mCurrentLocation.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()), ZOOM_IN_MAGNITUDE));
                    viewController.setUserLocation(mCurrentLocation);

                } else {
                    Log.d(TAG, "No last known location");
                    Toast noLocationToast = Toast.makeText(getApplicationContext(), "No location available now", Toast.LENGTH_SHORT);
                    noLocationToast.show();

                }
            }
        });
/*
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
        }); */

        // The button that moves to the ListViewActivity
        if (!FROM_LIST && !FROM_LIST_ITEM) {
            FloatingActionButton fabListMap = (FloatingActionButton) findViewById(R.id.fab_map_to_list);
            fabListMap.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(TAG, "map to list pressed");
                    Intent intent = new Intent(MapsActivity.this, ListViewActivity.class);

                    startActivity(intent);
                }
            });
        } else {
            FloatingActionButton fabListMap = (FloatingActionButton) findViewById(R.id.fab_map_to_list);
            fabListMap.hide();
        }

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


        FloatingActionButton fabRefresh = (FloatingActionButton) findViewById(R.id.fab_refresh);
        fabRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Refresh button pressed");
                reloadPinsOnScreen();
            }
        });


        fabEvents.hide();

    }

    private boolean reloadPinsOnScreen() {

        VisibleRegion vr = mMap.getProjection().getVisibleRegion();
        double top = vr.latLngBounds.northeast.latitude;
        double bottom = vr.latLngBounds.southwest.latitude;

        LatLng centerScreen = mMap.getCameraPosition().target;
        double cLat = centerScreen.latitude;
        double cLon = centerScreen.longitude;
        AsyncTask<Double, Integer, List<Event>> task = new AsyncTask<Double, Integer, List<Event>>() {
            @Override
            protected List<Event> doInBackground(Double[] params) {
                try {
                    if (params.length == 3) {
                        Log.d(TAG, "getEventsInRadius of " + params[0]);
                        Location loc = new Location("AsyncReloadPins");
                        loc.setLatitude(params[1]);
                        loc.setLongitude(params[2]);
                        List<Event> events = DBManager.getEventsInRadiusWithAttendance(loc, params[0]);
                        ArrayList<Event> eventArrayList = new ArrayList<>(events);
                        Log.d(TAG, "found " + eventArrayList.size() + " events");
                        return events;
                    }
                    return null;
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (SQLException e) {
                    Log.d(TAG, "SQL Exception");
                    e.printStackTrace();
                }
                return null;
            }

            protected void onPostExecute(List<Event> events) {
                Log.d(TAG, "On Post Execute");
                if (events != null) {
                    removeAllMarkers();
                    ArrayList<Event> eventArrayList = new ArrayList<>(events);
                    viewController.updateEventList(new HashSet<>(events));
                    addEventsToMap(eventArrayList);

                }
            }

        };


        task.execute(Math.abs(top - bottom), cLat, cLon);

        return true;
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
                e.printStackTrace();
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
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, ZOOM_IN_MAGNITUDE-2));
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

        if (mapMarkerEvent.containsKey(marker)) {
           displayMarkerInfo(mapMarkerEvent.get(marker));
        }
        return false;
    }

    private void displayMarkerInfo(Event event) {

        FrameLayout fl = (FrameLayout)findViewById(R.id.fragment_container);
        fl.setVisibility(View.VISIBLE);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Bundle args = new Bundle();
        args.putString("detailText", event.getTitle());
        args.putParcelable("eventObject", event);
        if (event.time != null) {
            args.putString("date", event.time.toString() + " for " + event.duration + " minutes");
        } else {
            Log.d(TAG, "Event has null date " + event.title);
        }
        if (event.description != null)
            args.putString("description", event.description);
        if (event.attending != null && event.attending.size() > 0) {
            Log.d(TAG, "Event has attendees");
            List<Account> attendees = event.attending;
            args.putInt("numAttendees", attendees.size());
            Log.d(TAG, attendees.toString());
            Account accnt = ((MOTSApp) getApplication()).getMyAccount();
            if (accnt != null) {
                if (attendees.contains(accnt)) {
                    Log.d(TAG, "I am already attending the event: " + event.title);
                    args.putBoolean("amAttending", true);
                } else {
                    Log.d(TAG, "I am not currently attending the event: " + event.title);
                    args.putBoolean("amAttending", false);
                }
            }

        } else {
            Log.d(TAG, "Event has no attendees");
        }

        MapDetailFragment mapDetailFragment = new MapDetailFragment();
        mapDetailFragment.setArguments(args);
        ft.replace(R.id.fragment_container, mapDetailFragment, "detailFragment");
        ft.commit();

    }
    /**
     * Callback when the map is tapped. Hides the DetailFragment
     * @param latLng the lat and longitude of where the map was pressed
     */
    public void onMapClick(LatLng latLng) {
        Log.d(TAG, "map clicked ");

        FrameLayout fl = (FrameLayout)findViewById(R.id.fragment_container);
        fl.setVisibility(View.GONE);
/*
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
*/
    }

    private void removeAllMarkers() {
        mMap.clear();
        mapMarkerEvent.clear();
    }

    private void addEventsToMap(ArrayList<Event> workingSet) {
        for (int i = 0; i < workingSet.size(); i++) {
            Event temp = workingSet.get(i);
            addEventToMap(temp);
        }
    }

    private void addEventToMap(Event event) {
        Location tLoc = event.location;
        //Log.d(TAG, "The location is: " + tLoc.getLongitude() + " " + tLoc.getLatitude());

        Marker marker = mMap.addMarker(new MarkerOptions().position(new LatLng(tLoc.getLatitude(), tLoc.getLongitude())).title(event.title));

        String iconPath = viewController.getEventIconPath(event);
        if (iconPath != null) {
            BitmapDrawable iconDrawable = (BitmapDrawable) SportsIconFinder.getAssetImage(this, iconPath);
            if (iconDrawable != null) {
                Bitmap iconBitmap = iconDrawable.getBitmap();
                Point screenRes = MOTSApp.getScreenRes();
                int size = Math.max(Math.min(screenRes.x, screenRes.y) / 17, 24);
                iconBitmap = Bitmap.createScaledBitmap(iconBitmap, size, size, false);
                BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(iconBitmap);
                marker.setIcon(icon);
            } else {
                Log.d(TAG, "Icon file not found!");
            }
        }

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

        startActivityForResult(intent, ADD_EVENT_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK && requestCode == ADD_EVENT_REQUEST_CODE) {

            ArrayList<Event> listEvent = data.getParcelableArrayListExtra("eventList");

            if (listEvent != null && listEvent.size() > 0) {
                Log.d(TAG, "event list has: " + listEvent.get(0).title);
                Log.d(TAG, "event coords: " + listEvent.get(0).location.getLatitude() + ", " + listEvent.get(0).location.getLongitude());
                Log.d(TAG, "event date: " + listEvent.get(0).time.toString());
                passedEvent = listEvent.get(0);
                addEventsToMap(listEvent);
                Location loc = listEvent.get(0).location;

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(loc.getLatitude(), loc.getLongitude()), ZOOM_IN_MAGNITUDE));
                centerOnLocation = false;
                Log.d(TAG, "end of onActivityResult");
            } else {
                Log.d(TAG, "eventList is null or no elements");
                centerOnLocation = true;
            }

        } else {
            Log.d(TAG, "not result OK");
            centerOnLocation = false;
        }
    }

    /**
     * Workaround since we cannot call displayMarkerInfo in onActivityResult
     */
    @Override
    protected void onPostResume() {
        super.onPostResume();
        Log.d(TAG, "onPostResume");
        if (passedEvent != null) {
            displayMarkerInfo(passedEvent);
            passedEvent = null;
        }
    }
}

