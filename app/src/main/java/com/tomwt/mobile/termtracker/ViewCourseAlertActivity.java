package com.tomwt.mobile.termtracker;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import java.util.Date;
import java.util.Locale;

public class ViewCourseAlertActivity extends AppCompatActivity {

    private Switch startDateAlertSwitch;
    private Switch endDateAlertSwitch;

    private int parentType;
    private int PID;

    private String courseStartDate;
    private String courseEndDate;
    private int courseStartAlertStatusOld;
    private int courseEndAlertStatusOld;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_course_alert);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Manage Course Alerts");

        startDateAlertSwitch = (Switch) findViewById(R.id.switch2);
        endDateAlertSwitch = (Switch) findViewById(R.id.switch1);

//        Toast.makeText(this, "The Switch is " + (startDateAlertSwitch.isChecked() ? "on" : "off"),
//                Toast.LENGTH_LONG).show();

        Intent intent = getIntent();

//        Uri uri = intent.getParcelableExtra(TermTrackerProvider.CONTENT_ITEM_TYPE);
//        PID = uri.getLastPathSegment();
//        String parentType = intent.getParcelableExtra(TermTrackerProvider.CONTENT_PARENT_TYPE);
        parentType = intent.getIntExtra(TermTrackerProvider.CONTENT_PARENT_TYPE, 0);
        PID = intent.getIntExtra(TermTrackerProvider.CONTENT_PARENT_ID, 0);
        courseStartDate = intent.getStringExtra(TermTrackerProvider.CONTENT_COURSE_START);
        courseEndDate = intent.getStringExtra(TermTrackerProvider.CONTENT_COURSE_END);


        // query SharedPreferences to see if there is a startDate alert and if so set a flag
        // query SharedPreferences to see if there is a endDate alert and if so set a flag
        // set the switches based on the flags queried from the SharedPreferences
        sharedPreferences = getSharedPreferences(TermTrackerProvider.CONTENT_SHAREDPREFERENCES, Context.MODE_PRIVATE);


        courseStartAlertStatusOld = sharedPreferences.getInt("classStart_" + PID, -1);
        courseEndAlertStatusOld = sharedPreferences.getInt("classEnd_" + PID, -1);

        if (courseStartAlertStatusOld > 0) {
            startDateAlertSwitch.setChecked(true);
        }
        if (courseEndAlertStatusOld > 0) {
            endDateAlertSwitch.setChecked(true);
        }



//        Uri alertURI = Uri.withAppendedPath(TermTrackerProvider.CONTENT_URI_PATHLESS, DBOpenHelper.TABLE_ALERTS);
//        currentAlertID = uri.getLastPathSegment();
//        alertsFilter = DBOpenHelper.ALERTS_ID + "=" + currentAlertID;
//
//        Cursor alertCursor = getContentResolver().query(alertURI, DBOpenHelper.ALERTS_ALL_COLUMNS, alertsFilter, null, null);
//        alertCursor.moveToFirst();
//        alertMsgTextOld = alertCursor.getString(alertCursor.getColumnIndex(DBOpenHelper.ALERTS_TEXT));
//        alertDateTextOld = alertCursor.getString(alertCursor.getColumnIndex(DBOpenHelper.ALERTS_DATE));
//        alertMsgEditor.setText(alertMsgTextOld);
//        alertDateEditor.setText(alertDateTextOld);



    }


    private void finishEditing() {

        // as part of finishEditing we would check our original state of the switches and compare to current state
        // if the switches match then do nothing
        // if the switches do not match then add or delete the alert based on the difference respectively
            // update the SharedPreferences accordingly per alert change

        SharedPreferences.Editor spEditor = sharedPreferences.edit();

        if (startDateAlertSwitch.isChecked()) {
            spEditor.putInt("classStart_" + PID, 1);
            // set the notification for start date
            updateAlert("You have a class starting soon!  Check TermTracker for more details.", courseStartDate, TermTrackerProvider.COURSE_STARTALERT_OFFSET + PID);
        }
        else {
            spEditor.putInt("classStart_" + PID, 0);
            // remove the notification for start date
            removeAlert("You have a class starting soon!  Check TermTracker for more details.", TermTrackerProvider.COURSE_STARTALERT_OFFSET + PID);
        }

        if (endDateAlertSwitch.isChecked()) {
            spEditor.putInt("classEnd_" + PID, 1);
            // set the notification for end date
            updateAlert("You have a class ending soon!  Check TermTracker for more details.", courseEndDate, TermTrackerProvider.COURSE_ENDALERT_OFFSET + PID);
        }
        else {
            spEditor.putInt("classEnd_" + PID, 0);
            // remove the notification for end date
            removeAlert("You have a class ending soon!  Check TermTracker for more details.", TermTrackerProvider.COURSE_ENDALERT_OFFSET + PID);
        }

        spEditor.commit();

        Intent intent = new Intent();
        intent.putExtra("returnValue", "9999");
        setResult(RESULT_OK, intent);
        finish();
    }

    private void updateAlert(String alertMsgText, String alertDateText, int alertID) {
        scheduleNotification(buildNotification(alertMsgText), buildAlertTarget(alertDateText), alertID);
        Toast.makeText(this, "ALERTS UPDATED...", Toast.LENGTH_LONG).show();
        setResult(RESULT_OK);
    }

    private void removeAlert(String alertMsgTextOld, int alertID) {
        Toast.makeText(this, "REMOVE ALERT CALLED...", Toast.LENGTH_LONG).show();
        removeNotification(alertMsgTextOld,  alertID);
    }

    private void scheduleNotification(Notification notification, long delay, int notificationID) {

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

    private void removeNotification(String msgText, int notificationID) {
        Notification notification = buildNotification(msgText);
        Intent notificationIntent = new Intent(this, NotificationPublisher.class);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, notificationID);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification);
        PendingIntent.getBroadcast(this, notificationID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT).cancel();
    }

    private long buildAlertTarget(String targetAlertDate) {
        String myFormat = "MM/dd/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        targetAlertDate.trim();
        Date date = new Date();
        try {
            date = sdf.parse(targetAlertDate);
        }
        catch (Exception e) { }
        long targetDateInMillis = date.getTime();
        long nowInMillis = SystemClock.elapsedRealtime();
        long delay = targetDateInMillis - nowInMillis;

        if(delay < 45000)
            delay = 45000;

        return 45000; //delay;  //TODO:  remove this hardcoding to enable real notification dates to function - here for testing purposes
    }



    // all three of the below methods are required to ensure full capture of the user leaving this activity
    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                // NavUtils.navigateUpFromSameTask(this);
                Intent intent = new Intent();
                intent.putExtra("returnValue", "9999");
                setResult(RESULT_OK, intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent objEvent) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBackPressed();
            return true;
        }
        return super.onKeyUp(keyCode, objEvent);
    }

    @Override
    public void onBackPressed() {
        finishEditing();
    }

}
