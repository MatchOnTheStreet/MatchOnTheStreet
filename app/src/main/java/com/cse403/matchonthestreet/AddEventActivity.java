package com.cse403.matchonthestreet;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by larioj on 2/7/16.
 */
public class AddEventActivity extends Activity implements OnClickListener {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

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
                toTimeET.setText(hourOfDay + ":" + minute);
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
                fromTimeET.setText(hourOfDay + ":" + minute);
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
}
