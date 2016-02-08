package com.cse403.matchonthestreet;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.EditText;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by larioj on 2/7/16.
 */
public class AddEventActivity extends Activity implements OnClickListener {

    private EditText fromDateET;
    private EditText fromTimeET;

    private DatePickerDialog fromDatePD;
    private TimePickerDialog fromTimePD;

    private SimpleDateFormat dateFormatter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        Calendar calendar  = Calendar.getInstance();
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
    }

    @Override
    public void onClick(View v) {
        if (v == fromDateET) {
            fromDatePD.show();
        }
    }
}
