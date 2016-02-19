package com.cse403.matchonthestreet;

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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Random;

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

    private ViewController viewController;

    RecyclerView recyclerView;
    protected RecyclerViewAdapter recyclerViewAdapter;

    protected SearchView searchView;
    protected EditText dateFromEntry;
    protected DatePickerDialog datePicker;
    protected EditText dateToEntry;
    protected SetTextDatePickerDialog datePickerFactory;
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
        applyButton = (Button) findViewById(R.id.filter_apply_button);
        datePickerFactory = new SetTextDatePickerDialog(this);

        dateFromEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerFactory.getPicker(dateFromEntry).show();
            }
        });

        dateToEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                recyclerViewAdapter.getFilter().filter(newText);
                viewController.updateEventList(new HashSet<>(recyclerViewAdapter.getFilteredItems()));
                return true;
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

        }
    }

    protected class SetTextDatePickerDialog {
        private Context context;

        private DatePickerDialog.OnDateSetListener listener;
        private EditText dateEntry;
        private Calendar calendar;

        public SetTextDatePickerDialog(Context context) {
            this.context = context;
            this.calendar = Calendar.getInstance();
        }

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
