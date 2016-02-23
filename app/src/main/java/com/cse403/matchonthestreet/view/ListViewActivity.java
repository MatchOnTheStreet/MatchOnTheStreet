package com.cse403.matchonthestreet.view;

import android.app.DatePickerDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.SearchView;

import com.cse403.matchonthestreet.R;
import com.cse403.matchonthestreet.controller.MOTSApp;
import com.cse403.matchonthestreet.controller.RecyclerViewAdapter;
import com.cse403.matchonthestreet.controller.ViewController;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Locale;

//import android.support.v7.app.AppCompatActivity;

/**
 * Created by Hao on 2/6/16.
 *
 * This is the list activity of the application.
 * A representation of all available events in a list format.
 *
 */
public class ListViewActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = ".ListViewActivity.MESSAGE";

    /** The current instance of ViewController, for loading data */
    private ViewController viewController;

    /** The list of events and their adapter */
    RecyclerView recyclerView;
    protected RecyclerViewAdapter recyclerViewAdapter;

    /** Search bar for keyword filtering */
    protected SearchView searchView;

    /** Text area displaying selected starting date */
    protected EditText dateFromEntry;

    /** Text area displaying selected ending date */
    protected EditText dateToEntry;

    protected EditText searchRadius;

    /** Stored instance of factory class of date picker dialogs for date entries */
    protected SetTextDatePickerDialog datePickerFactory;

    /** The button to apply filters */
    protected Button applyButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Obtain the current instance of ViewController
        viewController = ((MOTSApp)getApplicationContext()).getViewController();

        // Initial interface setups
        initActivityTransitions();
        setContentView(R.layout.activity_list_view);

        // Set up the tool bar
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("List & Filter");
        }
        
        // Set up the list of events
        recyclerView = (RecyclerView) findViewById(R.id.list_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewAdapter = new RecyclerViewAdapter(this, new ArrayList<>(viewController.getEventSet()));
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, null));

        // Text entries & button
        searchView = (SearchView) findViewById(R.id.filter_search_bar);
        dateFromEntry = (EditText) findViewById(R.id.filter_date_from);
        dateToEntry = (EditText) findViewById(R.id.filter_date_to);
        searchRadius = (EditText) findViewById(R.id.filter_radius);
        applyButton = (Button) findViewById(R.id.filter_apply_button);
        datePickerFactory = new SetTextDatePickerDialog(this);

        applyButton.setOnClickListener(new ApplyButtonOnClickListener());

        // Set the date entries to be uneditable
        dateFromEntry.setInputType(0);
        dateToEntry.setInputType(0);

        // OnClick listeners for popping up the date pickers
        dateFromEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateFromEntry.setText("");
                datePickerFactory.getPicker(dateFromEntry).show();
            }
        });

        dateToEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateToEntry.setText("");
                datePickerFactory.getPicker(dateToEntry).show();
            }
        });

        // Floating action button to the map view
        FloatingActionButton fabToMap =
                (FloatingActionButton) this.findViewById(R.id.fab_list_to_map);

        fabToMap.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(ListViewActivity.this, MapsActivity.class);
                // TODO: send extra msg to map view, e.g. user location
                intent.putExtra(EXTRA_MESSAGE, true);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.list_view_search, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        SearchView searchView =
                (SearchView) findViewById(R.id.filter_search_bar);

        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName())
        );

        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterByInput();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        try {
            return super.dispatchTouchEvent(motionEvent);
        } catch (NullPointerException e) {
            return false;
        }
    }

    private void initActivityTransitions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Slide transition = new Slide();
            transition.excludeTarget(android.R.id.statusBarBackground, true);
            getWindow().setEnterTransition(transition);
            getWindow().setReturnTransition(transition);
        }
    }


    protected class ApplyButtonOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            filterByInput();
        }
    }

    private void filterByInput() {
        // TODO: Hardcoded "From" and "to"
        String keywords = searchView.getQuery().toString();

        String startDate = dateFromEntry.getText().toString();
        if (startDate.toLowerCase().equals("from")) startDate = "";

        String endDate = dateToEntry.getText().toString();
        if (endDate.toLowerCase().equals("to")) endDate = "";

        String radius = searchRadius.getText().toString();

        Location userLocation = viewController.getUserLocation();
        String latLong;
        if (userLocation != null) {
            double lat = userLocation.getLatitude();
            double lon = userLocation.getLongitude();
            latLong = lat + ">$<" + lon;
        } else {
            latLong = "";
        }

        recyclerViewAdapter.getFilter().filter(keywords + "::" + startDate + "::" + endDate + "::" +
                radius + "::" + latLong);

        viewController.updateEventList(new HashSet<>(recyclerViewAdapter.getFilteredItems()));
    }

    /**
     * Custom inner factory class for generating date picker dialogs for different
     * date entries.
     */
    protected class SetTextDatePickerDialog {
        private Context context;

        private Calendar calendar;

        public SetTextDatePickerDialog(Context context) {
            this.context = context;
            this.calendar = Calendar.getInstance();
        }

        /**
         * Returns a specific DatePickerDialog for this text entry field
         * @param et The text date entry field whose value to be chosen
         * @return the DatePickDialog used to provide value for the field
         */
        public DatePickerDialog getPicker(final EditText et) {
            return new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    Calendar date = Calendar.getInstance();
                    date.set(year, monthOfYear, dayOfMonth);
                    et.setText(new SimpleDateFormat("dd-MM-yy", Locale.US).format(date.getTime()));
                }
            }, calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        }

    }
}
