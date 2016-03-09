/**
 *
 * This is the Maps view for MOTS, The first time a user runs the app, they will be prompted with the Facebook login screen.
 * The Maps view provides the user with a high level overview of all the events nearby them or
 * near a specified location.
 *
 * The search bar is used to find events around the searched location.
 * There are 3 buttons,
 *   * The target icon will center the map on the users current location, provided MOTS has the permissions
 *   * The list icon will change to the list view where you can sort and filter events
 *   * The refresh icon will reload the list of events based on the view the user sees
 *
 *
 * Note: If you are not prompted to allow location permissions on the first launch
 * to get the location services working, you will need to follow these steps first
 * Open the settings app within the emulator
 * Under the 'Device' subheading click the 'Apps'
 * Find 'Match on the Street'
 * Click 'Permissions'
 * Turn location services on
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
import android.app.Activity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.content.Intent;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;


import com.amulyakhare.textdrawable.TextDrawable;
import com.cse403.matchonthestreet.R;
import com.cse403.matchonthestreet.backend.DBManager;
import com.cse403.matchonthestreet.controller.MOTSApp;
import com.cse403.matchonthestreet.controller.SportsIconFinder;
import com.cse403.matchonthestreet.controller.ViewController;
import com.cse403.matchonthestreet.models.Account;
import com.cse403.matchonthestreet.models.Event;
import com.github.amlcurran.showcaseview.OnShowcaseEventListener;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.VisibleRegion;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

import java.io.IOException;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;


public class MapsActivity extends NavActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener,
        GoogleMap.OnMapLongClickListener {

    /** Check if the intent was passed from AddEventActivity */
    public static final int ADD_EVENT_REQUEST_CODE = 1;

    /** Check if intent is from the listactivity */
    private boolean FROM_LIST;

    /** Check if the intent is from the recycleviewadapter (selecting an item in the listactivity)*/
    private boolean FROM_LIST_ITEM;

    /** Check if the app is being launched for the first time */
    private static boolean FIRST_LAUNCH = true;

    private static final String TUTORIAL_ID = "Map activity tutorial";

    public static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 10;

    /** Default zoom value for the map */
    public static final int ZOOM_IN_MAGNITUDE = 15;

    /** If the map should zoom in on the users current location when loading */
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

    /** Manages the events and their markers on the map */
    private ClusterManager<Event> clusterManager;

    //private List<ShowcaseView> tutorialList;

    /** ViewController used to get the list of working events */
    private ViewController viewController;

    /** The event passed from the AddEventActivity since the event can't be added to the working set
     *  on the initial callback method */
    private Event passedEvent;

    /** Used to show the loading dialog when querying the database */
    public ProgressDialog progress;


    /**
     * @param savedInstanceState
     *
     * When the view is created, initialize the map, location requests, and buttons
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

        // Obtain the current instance of ViewController
        viewController = ((MOTSApp) getApplicationContext()).getViewController();

        // Setup of the progress dialog
        progress = new ProgressDialog(this);
        progress.setTitle("Loading");
        progress.setMessage("Loading events...");


        // Set the DetailFragment to be invisible
        FrameLayout fl = (FrameLayout)findViewById(R.id.fragment_container);
        fl.setVisibility(View.GONE);

    }

    /**
     * Sets up the searchbar so users can search for a location
     * @param menu the menu where the searchView is located
     * @return if the menu should be displayed or not
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.map_search, menu);

        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        final SearchView searchView =
                (SearchView) menu.findItem(R.id.map_search_bar).getActionView();

        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName())
        );

        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                onSearch(searchView);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return true;
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
                Log.d(TAG, "should show reason for location services");
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
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "Permission granted by user");
                    // permission was granted, yay!
                    // Do tasks that needs location services.

                } else {
                    Log.d(TAG, "Permission denied by user");
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
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
        mMap.setOnMapClickListener(this);
        mMap.setOnMapLongClickListener(this);

        // Check permissions both Coarse and Fine and zoom to that location
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
        FROM_LIST_ITEM = intent.getBooleanExtra("fromListItem", false);


        // Creates the buttons that look like floating action buttons
        createButtons();



        if (FROM_LIST || FROM_LIST_ITEM) {
            Log.d(TAG, "From List");
            centerOnLocation = false;
            Event event = intent.getParcelableExtra("selectedEvent");
            if (event != null ) {
                Log.d(TAG, "Maps was passed the event: " + event.getTitle());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(event.getLocation().getLatitude(), event.getLocation().getLongitude()), ZOOM_IN_MAGNITUDE));
                displayMarkerInfo(event);
            } else {
                centerOnLocation = true;
                if (mCurrentLocation != null)
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()), ZOOM_IN_MAGNITUDE));
            }
        }

        // is first launched.
        if (FIRST_LAUNCH) {
            //viewController.populateDummyData();
            reloadPinsOnScreen();
            FIRST_LAUNCH = false;
        }
        // Disable the automatic keyboard popup
        View curView = this.getCurrentFocus();
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        //imm.hideSoftInputFromWindow(findViewById(R.id.map_search_bar).getWindowToken(), 0);
        if (curView != null && imm != null) {
            imm.hideSoftInputFromWindow(curView.getWindowToken(), 0);
        }
        getTutorialSequence().start();
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
     * @param location  the new location of the device
     */
    @Override
    public void onLocationChanged(Location location) {
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

    /**
     * If somehow the connection to the google maps api is suspended try to reconnect
     * @param cause the error code for why the connection was suspended
     */
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
     * This is where we can add markers or lines, add listeners or move the camera.
     *
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        setUpClusterManager();

        Set<Event> currentEvents = viewController.getEventSet();
        addEventsToMap(new ArrayList<>(currentEvents));


        UiSettings mapSettings = mMap.getUiSettings();
        mapSettings.setZoomControlsEnabled(false);
        //mapSettings.setMyLocationButtonEnabled(false);
        mapSettings.setMapToolbarEnabled(false);
    }

    /**
     * Sets up the cluster manager for marker clustering
     */
    public void setUpClusterManager() {
        clusterManager = new ClusterManager<>(this, mMap);

        clusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<Event>() {
            @Override
            public boolean onClusterItemClick(Event event) {
                displayMarkerInfo(event);
                return false;
            }
        });

        clusterManager.setRenderer(new SportsIconRenderer());

        mMap.setOnCameraChangeListener(clusterManager);
        mMap.setOnMarkerClickListener(clusterManager);
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

        // The button that moves to the ListViewActivity
        FloatingActionButton fabListMap = (FloatingActionButton) findViewById(R.id.fab_map_to_list);
        fabListMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "map to list pressed");
                Intent intent = new Intent(MapsActivity.this, ListViewActivity.class);

                startActivity(intent);
            }
        });

        // The button to pull the newest events from the database
        FloatingActionButton fabRefresh = (FloatingActionButton) findViewById(R.id.fab_refresh);
        fabRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Refresh button pressed");
                reloadPinsOnScreen();
            }
        });

    }

    /**
     *  Pulls the newest events within the radius (square) of the screen from the database,
     *  updates the viewController with the list of these events, and displays the events on the map
     */
    private void reloadPinsOnScreen() {
        progress.show(); // show the progress bar

        // The the lat of the top and bottom of the visible map, used to calculate the 'radius'
        VisibleRegion vr = mMap.getProjection().getVisibleRegion();
        double top = vr.latLngBounds.northeast.latitude;
        double bottom = vr.latLngBounds.southwest.latitude;

        // The center of the screen of the center of the 'circle'
        LatLng centerScreen = mMap.getCameraPosition().target;
        double cLat = centerScreen.latitude;
        double cLon = centerScreen.longitude;

        // Create the AsyncTask to query the database, will run in the background
        AsyncTask<Double, Integer, List<Event>> task = new AsyncTask<Double, Integer, List<Event>>() {
            @Override
            protected List<Event> doInBackground(Double[] params) {
                try {
                    if (params.length == 3) { // radius, centerLat, centerLon
                        Log.d(TAG, "getEventsInRadius of " + params[0]);
                        Location loc = new Location("AsyncReloadPins");
                        loc.setLatitude(params[1]);
                        loc.setLongitude(params[2]);
                        List<Event> events = DBManager.getEventsInRadiusWithAttendance(loc, params[0]);
                        ArrayList<Event> eventArrayList = new ArrayList<>(events);
                        Log.d(TAG, "found " + eventArrayList.size() + " events");
                        return events; // once completed pass the list of events to onPostExecute
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
                if (events != null) {
                    removeAllMarkers(); // remove the current outdated markers
                    ArrayList<Event> eventArrayList = new ArrayList<>(events);
                    // update the viewControllers working set of events
                    viewController.updateEventList(new HashSet<>(events));
                    // Add the new events to the map
                    addEventsToMap(eventArrayList);
                    // dismiss the progress notification
                    progress.dismiss();
                    Toast noLocationToast = Toast.makeText(getApplicationContext(), "" + eventArrayList.size() + " Events Found!", Toast.LENGTH_SHORT);
                    noLocationToast.show();

                }
            }

        };

        // Assumes our app is only used in portrait
        task.execute(Math.abs(top - bottom) / 1.2, cLat, cLon);

    }

    protected MaterialShowcaseSequence getTutorialSequence() {
        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(300);
        config.setDismissTextColor(R.color.colorPrimaryDark);

        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(this, TUTORIAL_ID);
        sequence.setConfig(config);

        sequence.addSequenceItem(findViewById(R.id.fab_update_location),
                getString(R.string.tutorial_location_button), "GOT IT");

        sequence.addSequenceItem(findViewById(R.id.fab_map_to_list),
                getString(R.string.tutorial_mtl_button), "GOT IT");

        sequence.addSequenceItem(findViewById(R.id.fab_refresh),
                getString(R.string.tutorial_refresh_button), "GOT IT");

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(this)
                        .setTarget(findViewById(R.id.toolbar))
                        .setContentText(getString(R.string.tutorial_toolbar))
                        .setDismissText("...YEAH SURE")
                        .setDismissTextColor(R.color.colorPrimary)
                        .withRectangleShape()
                        .build()
        );

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(this)
                        .setTarget(findViewById(R.id.map))
                        .setContentText(getString(R.string.tutorial_map))
                        .setDismissText("OK ENOUGH")
                        .setDismissTextColor(R.color.colorPrimary)
                        .withoutShape()
                        .setDismissOnTouch(true)
                        .build()
        );

        return sequence;
    }


    /**
     *  Called when the search button is pressed. Uses Geocoder to search text -> coordinates
     *  So far only good for searching large locations. Ex. Searching ima will not work
     */
    public void onSearch(View view) {
        SearchView searchText = (SearchView) findViewById(R.id.map_search_bar);
        String search = searchText.getQuery().toString();

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
                //createPin(address.getLatitude(), address.getLongitude());
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

        /*if (mapMarkerEvent.containsKey(marker)) {
           displayMarkerInfo(mapMarkerEvent.get(marker));
        }*/
        return false;
    }

    /**
     * Initializes and displays the MapDetailFragment which contains the specifics of the passed
     * event and allows the user to join or unjoin the event
     * @param event the event to display the detailed info for
     */
    private void displayMarkerInfo(Event event) {
        // Setup the info to send to the fragment
        FrameLayout fl = (FrameLayout)findViewById(R.id.fragment_container);
        fl.setVisibility(View.VISIBLE);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        Bundle args = new Bundle();
        args.putString("detailText", event.getTitle());
        args.putParcelable("eventObject", event);
        if (event.getTime() != null) {
            String formattedDate = new SimpleDateFormat("MM/dd/yy hh:mm", Locale.US).format(event.getTime());
            DecimalFormat oneDigit = new DecimalFormat("#,##0.0");
            args.putString("date", formattedDate + " for " + oneDigit.format(event.getDuration()/60.0) + " hours");
        } else {
            Log.d(TAG, "Event has null date " + event.getTitle());
        }
        if (event.getDescription() != null)
            args.putString("description", event.getDescription());
        if (event.getAttending() != null && event.getAttending().size() > 0) {
            Log.d(TAG, "Event has attendees");
            List<Account> attendees = event.getAttending();
            args.putInt("numAttendees", attendees.size());
            Log.d(TAG, attendees.toString());
            Account accnt = ((MOTSApp) getApplication()).getMyAccount();
            if (accnt != null) {
                if (attendees.contains(accnt)) {
                    Log.d(TAG, "I am already attending the event: " + event.getTitle());
                    args.putBoolean("amAttending", true);
                } else {
                    Log.d(TAG, "I am not currently attending the event: " + event.getTitle());
                    args.putBoolean("amAttending", false);
                }
            }

        } else {
            Log.d(TAG, "Event has no attendees");
        }
        // Initialize and display the event
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
        FrameLayout fl = (FrameLayout)findViewById(R.id.fragment_container);
        fl.setVisibility(View.GONE);
    }

    /**
     * Removes all the markers from the map and resets the cluster manager
     */
    private void removeAllMarkers() {
        mMap.clear();
        clusterManager.clearItems();
    }

    /**
     * Adds the list of events to the map
     * @param workingSet the list of events to add to the map
     */
    private void addEventsToMap(ArrayList<Event> workingSet) {
        for (int i = 0; i < workingSet.size(); i++) {
            Event temp = workingSet.get(i);
            addEventToMap(temp);
        }
        clusterManager.cluster();
    }

    /**
     * Adds the passed event to the map. (is public so the mapdetailfragment can use it to update attendance)
     * @param event the event to add to the map
     */
    public void addEventToMap(Event event) {
        clusterManager.addItem(event);
        //clusterManager.cluster();
    }

    /**
     * The callback from tapping and holding on a location of the map. Creates a new event at the held location
     * starts the AddEventActivity
     * @param latLng the location of the tap and hold
     */
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

    /**
     * Callback when an activity was started from the startActivityForResult like in on onMapLongClick
     * @param requestCode the assigned request code when the activity was started to identify what activity is returning
     * @param resultCode the result code from the previous activity (successful return or not)
     * @param data any data passed when the last activity finished
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check that this is getting called by the AddEventActivity
        if (resultCode == RESULT_OK && requestCode == ADD_EVENT_REQUEST_CODE) {
            // Get the list of events (should just be a single event)
            ArrayList<Event> listEvent = data.getParcelableArrayListExtra("eventList");

            if (listEvent != null && listEvent.size() > 0) {
                Log.d(TAG, "event list has: " + listEvent.get(0).getTitle());
                passedEvent = listEvent.get(0);
                addEventsToMap(listEvent);
                Location loc = listEvent.get(0).getLocation();
                // move the camera to the added events location
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(loc.getLatitude(), loc.getLongitude()), ZOOM_IN_MAGNITUDE));
                centerOnLocation = false;
                Log.d(TAG, "end of onActivityResult");

                // we want to display the mapdetailfragment here for the event here, but that is not allowed
                // so we must wait for the onPostResume() to be called, so we save the passed event as a variable (passedEvent)
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
            //TODO: add the event to the cluster manager if the event is not already there
            if (!viewController.getEventSet().contains(passedEvent)) {
                Log.d(TAG, "view controller does not already have the passed event");
                viewController.addEventToSet(passedEvent);
                addEventToMap(passedEvent);
                clusterManager.cluster();
            } else {
                Log.d(TAG, "View controller already has the passed event");
            }
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(passedEvent.getLocation().getLatitude(), passedEvent.getLocation().getLongitude()), ZOOM_IN_MAGNITUDE));
            passedEvent = null;
        }
    }

    /**
     * The custom implementation of the Renderer used for cluster icons
     */
    protected class SportsIconRenderer extends DefaultClusterRenderer<Event> {
        private final IconGenerator iconGenerator = new IconGenerator(getApplicationContext());
        private final ImageView iconImageView;
        private final int iconDimensions;

        public SportsIconRenderer() {
            super(getApplicationContext(), mMap, clusterManager);

            Point screenRes = MOTSApp.getScreenRes();
            iconDimensions = Math.max(Math.min(screenRes.x, screenRes.y) / 17, 24);

            iconImageView = new ImageView(getApplicationContext());
            iconImageView.setLayoutParams(new ViewGroup.LayoutParams(iconDimensions, iconDimensions));

            iconGenerator.setContentView(iconImageView);
        }

        @Override
        protected void onBeforeClusterItemRendered(Event event, MarkerOptions markerOptions) {
            iconImageView.setImageDrawable(getIconDrawable(event));
            Bitmap icon = iconGenerator.makeIcon();
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon)).title(event.getTitle());
        }

        /**
         * Finds a matching icon from the SportsIconFinder
         * @param event the event whose icon to be found
         * @return the matching icon from the library, or a random text icon if none exists
         */
        private Drawable getIconDrawable(Event event) {
            String iconPath = viewController.getEventIconPath(event);
            if (iconPath != null) {
                return SportsIconFinder.getAssetImage(getApplicationContext(), iconPath);
            } else {
                Random rand = new Random();
                String firstLetter = "" + event.getTitle().toUpperCase().charAt(0);
                int randomColor = Color.rgb(20 + rand.nextInt(200), 20 + rand.nextInt(220), 20 + rand.nextInt(220));
                return TextDrawable.builder().buildRound(firstLetter, randomColor);
            }
        }

    }

    /**
     * Called when passed an intent when there is already an existing task that contains the MapsActivity
     * @param intent the intent containing an event to display
     */
    @Override
    public void onNewIntent(Intent intent) {
        Log.d(TAG, "onNewIntent");
        FROM_LIST = intent.getBooleanExtra(ListViewActivity.EXTRA_MESSAGE, false);
        FROM_LIST_ITEM = intent.getBooleanExtra("fromListItem", false);


        if (FROM_LIST || FROM_LIST_ITEM) {
            Log.d(TAG, "From List in onNewIntent");
            centerOnLocation = false;
            passedEvent = intent.getParcelableExtra("selectedEvent");
        }
    }
}

