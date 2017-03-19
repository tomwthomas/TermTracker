package com.tomwt.mobile.termtracker;

import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.util.Locale;

public class ViewAssessmentAlertActivity extends AppCompatActivity {

    final Calendar alertCal = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_assessment_alert);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Assessment Alert Information");



        final DatePickerDialog.OnDateSetListener datePicker = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                alertCal.set(Calendar.YEAR, year);
                alertCal.set(Calendar.MONTH, monthOfYear);
                alertCal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }
        };

        EditText alertDate = (EditText) findViewById(R.id.data_alertDate);
        alertDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    new DatePickerDialog(ViewAssessmentAlertActivity.this, datePicker, alertCal.get(Calendar.YEAR), alertCal.get(Calendar.MONTH), alertCal.get(Calendar.DAY_OF_MONTH)).show();
                }
                else {
                    // add lost focus here if appropriate
                }
            }
        });



//        alertDate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                new DatePickerDialog(ViewAssessmentAlertActivity.this, datePicker, alertCal.get(Calendar.YEAR), alertCal.get(Calendar.MONTH), alertCal.get(Calendar.DAY_OF_MONTH)).show();
//            }
//        });
    }

    private void updateLabel() {
        String myFormat = "MM/dd/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        EditText alertDate = (EditText) findViewById(R.id.data_alertDate);
        alertDate.setText(sdf.format(alertCal.getTime()));
        LinearLayout hiddenField = (LinearLayout) findViewById(R.id.hiddenField);
        hiddenField.setFocusable();
    }





//        EditText dataAlertDate = (EditText) findViewById(R.id.data_alertDate);
//        dataAlertDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if(hasFocus) {
//                    // show calendar
//                }
//                else {
//                    // hide calendar
//                }
//            }
//
//        });
//    }
//
//    private void showCalendar(View v) {
//        DialogFragment calendar = new DatePickerFragment();
//        calendar.show(getSupportFragmentManager(), "datePicker");
//
//    }

}
