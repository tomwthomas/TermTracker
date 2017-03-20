package com.tomwt.mobile.termtracker;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.SystemClock;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;

import java.util.Locale;

public class ViewAssessmentAlertActivity extends AppCompatActivity {

    final Calendar alertCal = Calendar.getInstance();

    @TargetApi(Build.VERSION_CODES.N)
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
    }

    private void updateLabel() {
        String myFormat = "MM/dd/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        EditText alertDate = (EditText) findViewById(R.id.data_alertDate);
        alertDate.setText(sdf.format(alertCal.getTime()));
        alertDate.setCursorVisible(false);
        View view = this.getCurrentFocus();
        view.clearFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

        scheduleNotification(buildNotification("message 1"), 1000, 1);
        scheduleNotification(buildNotification("message 2"), 5000, 2);
        removeNotification(2);
        scheduleNotification(buildNotification("message 3"), 10000, 3);
    }

    private void scheduleNotification(Notification notification, int delay, int notificationID) {

        Intent notificationIntent = new Intent(this, NotificationPublisher.class);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, notificationID);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, notificationID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        long futureInMillis = SystemClock.elapsedRealtime() + delay;
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
    }

    private Notification buildNotification(String content) {
        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle("Scheduled Notification");
        builder.setContentText(content);
        builder.setSmallIcon(R.drawable.ic_alert_bang);
        builder.setAutoCancel(true);
        return builder.build();
    }

    private void removeNotification(int notificationID) {
        Notification notification = buildNotification("message 2");
        Intent notificationIntent = new Intent(this, NotificationPublisher.class);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, notificationID);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification);
        PendingIntent.getBroadcast(this, notificationID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT).cancel();
    }
}
