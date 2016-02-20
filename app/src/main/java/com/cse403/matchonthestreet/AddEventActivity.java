package com.cse403.matchonthestreet;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import com.google.android.gms.maps.model.LatLng;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

/**
 * Created by larioj on 2/7/16.
 */
public class AddEventActivity extends NavActivity implements OnClickListener {

    private EditText fromDateET;
    private DatePickerDialog fromDatePD;
    private EditText fromTimeET;
    private TimePickerDialog fromTimePD;
    
    private EditText toDateET;
    private DatePickerDialog toDatePD;
    private EditText toTimeET;
    private TimePickerDialog toTimePD;

    private SimpleDateFormat dateFormatter;
    private SimpleDateFormat timeFormatter;
    private Calendar calendar;


    private Location location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        Intent intent = getIntent();
        double lat = intent.getDoubleExtra("latitude", 0.0);
        double lon = intent.getDoubleExtra("longitude", 0.0);

        location = new Location("userCreatedLocation");
        location.setLatitude(lat);
        location.setLongitude(lon);


        calendar  = Calendar.getInstance();
        dateFormatter = new SimpleDateFormat("dd-MM-yy", Locale.US);

        fromDateET = (EditText) findViewById(R.id.event_from_date);
        fromDateET.setInputType(InputType.TYPE_NULL);
        fromDateET.setOnClickListener(this);
        fromDatePD = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar date = Calendar.getInstance();
                date.set(year, monthOfYear, dayOfMonth);
                fromDateET.setText(dateFormatter.format(date.getTime()));
            }
        },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));

        toDateET = (EditText) findViewById(R.id.event_to_date);
        toDateET.setInputType(InputType.TYPE_NULL);
        toDateET.setOnClickListener(this);
        toDatePD = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar date = Calendar.getInstance();
                date.set(year, monthOfYear, dayOfMonth);
                toDateET.setText(dateFormatter.format(date.getTime()));
            }
        },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));


        toTimeET = (EditText) findViewById(R.id.event_to_time);
        toTimeET.setInputType(InputType.TYPE_NULL);
        toTimeET.setOnClickListener(this);
        toTimePD = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                //Calendar time = Calendar.getInstance();
                //time.set(Calendar.HOUR_OF_DAY, hourOfDay);
                //time.set(Calendar.MINUTE, minute);
                //toTimeET.setText(timeFormatter.format(time.getTime()));
                String pad = "";
                if (minute < 10) pad = "0";
                String time = hourOfDay + ":" + pad + minute;
                toTimeET.setText(time);
            }
        },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                false);
                
        fromTimeET = (EditText) findViewById(R.id.event_from_time);
        fromTimeET.setInputType(InputType.TYPE_NULL);
        fromTimeET.setOnClickListener(this);
        fromTimePD = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                //Calendar time = Calendar.getInstance();
                //time.set(Calendar.HOUR_OF_DAY, hourOfDay);
                //time.set(Calendar.MINUTE, minute);
                //fromTimeET.setText(timeFormatter.format(time.getTime()));
                String pad = "";
                if (minute < 10) pad = "0";
                String time = hourOfDay + ":" + pad + minute;
                fromTimeET.setText(time);
            }
        },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                false);
    }

    @Override
    public void onClick(View v) {
        if (v == fromDateET) {
            fromDatePD.show();
        } else if (v == toDateET) {
            toDatePD.show();
        } else if (v == toTimeET) {
            toTimePD.show();
        } else if (v == fromTimeET) {
            fromTimePD.show();
        }
    }

    public void createEvent(View view) {

        //TODO: Check that all the fields were filled out

        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);


        String timerange = fromDateET.getText().toString() + " " + fromTimeET.getText().toString();

        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yy HH:mm", Locale.US);
        Date date;
        try {
            date = format.parse(timerange);

        } catch (ParseException e) {
            date = new Date();
            e.printStackTrace();
            Log.d("AddEventActivity", "Date parse failed");
        }
        Log.d("AddEventActivity", "Date toString is: " + date.toString());

        EditText titleET = (EditText)findViewById(R.id.event_title);

        String title = titleET.getText().toString();

        EditText eventDescET = (EditText)findViewById(R.id.event_description);
        String description = eventDescET.getText().toString();

        // TODO: Add duration attribute
        Event event = new Event(title, location, date, 60, calendar.getTime(), description);
        Intent resultIntent = new Intent(); 

        Log.d("AddEventActivity", "Date toString is: " + event.time.toString());

        ArrayList<Event> list = new ArrayList<>();
        list.add(event);

        resultIntent.putParcelableArrayListExtra("eventList", list);

        setResult(RESULT_OK, resultIntent);

        finish();

    }
}