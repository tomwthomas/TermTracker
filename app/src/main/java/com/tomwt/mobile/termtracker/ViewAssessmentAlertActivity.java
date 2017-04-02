package com.tomwt.mobile.termtracker;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Build;
import android.os.SystemClock;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Date;
import java.util.Locale;

public class ViewAssessmentAlertActivity extends AppCompatActivity {

    final Calendar alertCal = Calendar.getInstance();

    private static final int EDITOR_REQUEST_CODE = 1001;

    private String action;
//    private EditText editor;
//    private String notesFilter;
//    private String oldText;

    // REFACTORED::  ADDED
    private String alertsFilter;
    private EditText alertMsgEditor;
    private EditText alertDateEditor;
    private String alertMsgTextOld;
    private String alertDateTextOld;
    private String currentAlertID;

    private int parentType;
    private int PID;


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
        getSupportActionBar().setTitle("Assessment Alert Info");

        alertMsgEditor = (EditText) findViewById(R.id.data_alertText);
        alertDateEditor = (EditText) findViewById(R.id.data_alertDate);

        Intent intent = getIntent();

        Uri uri = intent.getParcelableExtra(TermTrackerProvider.CONTENT_ITEM_TYPE);
//        PID = uri.getLastPathSegment();
//        String parentType = intent.getParcelableExtra(TermTrackerProvider.CONTENT_PARENT_TYPE);
        parentType = intent.getIntExtra(TermTrackerProvider.CONTENT_PARENT_TYPE, 0);
        PID = intent.getIntExtra(TermTrackerProvider.CONTENT_PARENT_ID, 0);

        if (uri == null) {
            action = Intent.ACTION_INSERT;
//            getSupportActionBar().setTitle("INTENT.INSERT (uri==null)...");
        } else {
            action = Intent.ACTION_EDIT;
//            notesFilter = DBOpenHelper.NOTES_ID + "=" + uri.getLastPathSegment();
//
//            Cursor cursor = getContentResolver().query(uri, DBOpenHelper.NOTES_ALL_COLUMNS, notesFilter, null, null);
//            cursor.moveToFirst();
//            oldText = cursor.getString(cursor.getColumnIndex(DBOpenHelper.NOTES_DETAILS));
//            editor.setText(oldText);
//            editor.requestFocus();

            // REFACTORED:: ADDED
            Uri alertURI = Uri.withAppendedPath(TermTrackerProvider.CONTENT_URI_PATHLESS, DBOpenHelper.TABLE_ALERTS);
            currentAlertID = uri.getLastPathSegment();
            alertsFilter = DBOpenHelper.ALERTS_ID + "=" + currentAlertID;

            Cursor alertCursor = getContentResolver().query(alertURI, DBOpenHelper.ALERTS_ALL_COLUMNS, alertsFilter, null, null);
            alertCursor.moveToFirst();
            alertMsgTextOld = alertCursor.getString(alertCursor.getColumnIndex(DBOpenHelper.ALERTS_TEXT));
            alertDateTextOld = alertCursor.getString(alertCursor.getColumnIndex(DBOpenHelper.ALERTS_DATE));
            alertMsgEditor.setText(alertMsgTextOld);
            alertDateEditor.setText(alertDateTextOld);

//            // build out the list of courses for this term and display them in the GUI
//            final Uri courseURI = Uri.withAppendedPath(TermTrackerProvider.CONTENT_URI_PATHLESS, DBOpenHelper.TABLE_COURSES);
//            Cursor courseCursor = getContentResolver().query(courseURI, DBOpenHelper.COURSES_ALL_COLUMNS, null, null, null);
//            String[] from = {DBOpenHelper.COURSES_TITLE};
//            int[] to = {android.R.id.text1};
//            CursorAdapter cursorAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, courseCursor, from, to, 0);
//
//            ListView list = (ListView) findViewById(android.R.id.list);
//            list.setAdapter(cursorAdapter);
//
//            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                    Intent intent = new Intent(ViewTermActivity.this, ViewCourseActivity.class);
//                    Uri uri = Uri.parse(courseURI + "/" + id);
//                    Log.d("ViewTermActivity", "courseURI: " + uri.toString());
//                    intent.putExtra(TermTrackerProvider.CONTENT_ITEM_TYPE, uri);
//                    startActivityForResult(intent, EDITOR_REQUEST_CODE);
//                }
//            });
        }



        final DatePickerDialog.OnDateSetListener datePicker = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                alertCal.set(Calendar.YEAR, year);
                alertCal.set(Calendar.MONTH, monthOfYear);
                alertCal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }
        };

        // EditText alertDate = (EditText) findViewById(R.id.data_alertDate);
        alertDateEditor.setOnFocusChangeListener(new View.OnFocusChangeListener() {
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
//        String myFormat = "MM/dd/yy";
        String myFormat = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        EditText alertDate = (EditText) findViewById(R.id.data_alertDate);
        alertDate.setText(sdf.format(alertCal.getTime()));
        alertDate.setCursorVisible(false);
        View view = this.getCurrentFocus();
        view.clearFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

//        scheduleNotification(buildNotification("message 1"), 1000, 1);
//        scheduleNotification(buildNotification("message 2"), 5000, 2);
//        removeNotification(2);
//        scheduleNotification(buildNotification("message 3"), 10000, 3);
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






    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(action != Intent.ACTION_INSERT) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_alerts, menu);
        }

        return true;
    }

    private void removeAlert() {
        Toast.makeText(this, "REMOVE ALERT CALLED...", Toast.LENGTH_LONG).show();
        // this should build up the call to removeNotification based on the URI we currently have OR just move this call to there
        removeNotification(alertMsgTextOld, Integer.parseInt(currentAlertID));
        deleteAlert();
//        Intent intent = new Intent(ViewAssessmentActivity.this, ViewAssessmentAlertActivity.class);
//        startActivityForResult(intent, EDITOR_REQUEST_CODE);


    }


    private void finishEditing() {
//        String newText = editor.getText().toString().trim();
//
        // REFACTORED:: ADDED
        String alertMsgTextNew = alertMsgEditor.getText().toString().trim();
        String alertDateTextNew = alertDateEditor.getText().toString().trim();

        switch (action) {
            case Intent.ACTION_INSERT:
                if (alertMsgTextNew.length() == 0 || alertDateTextNew.length() == 0) {
                    setResult(RESULT_CANCELED);
                } else {
                    insertAlert(alertMsgTextNew, alertDateTextNew);
                }
                break;
            case Intent.ACTION_EDIT:
                if (alertMsgTextNew.length() == 0 || alertDateTextNew.length() == 0) { // REFACTORED
//                    deleteNote();
                } else if (alertMsgTextOld.equals(alertMsgTextNew) && alertDateTextOld.equals(alertDateTextNew)) { // REFACTORED
                    setResult(RESULT_CANCELED);
                } else {
//                    updateNote(newText);
                    // REFACTORED:: ADDED
                    updateAlert(alertMsgTextNew, alertDateTextNew);
                }

        }

        Intent intent = new Intent();
        intent.putExtra("returnValue", "9999");
        setResult(RESULT_OK, intent); // TODO:  not really returning actual result, likely a global problem.  REFACTOR!
        finish();
    }

    // REFACTORED:: ADDED
    private void updateAlert(String alertMsgText, String alertDateText) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.ALERTS_TEXT, alertMsgText);
        values.put(DBOpenHelper.ALERTS_DATE, alertDateText);
        values.put(DBOpenHelper.ALERTS_TYPE, parentType);
        values.put(DBOpenHelper.ALERTS_PID, PID);
        Uri alertURI = Uri.withAppendedPath(TermTrackerProvider.CONTENT_URI_PATHLESS, DBOpenHelper.TABLE_ALERTS);
        getContentResolver().update(alertURI, values, alertsFilter, null);
        scheduleNotification(buildNotification(alertMsgText), buildAlertTarget(), Integer.parseInt(currentAlertID));
        Toast.makeText(this, "ALERTS UPDATED...", Toast.LENGTH_LONG).show();
        setResult(RESULT_OK);
    }

    private void insertAlert(String alertMsg, String alertDate) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.ALERTS_TEXT, alertMsg);
        values.put(DBOpenHelper.ALERTS_DATE, alertDate);
        values.put(DBOpenHelper.ALERTS_TYPE, parentType);
        values.put(DBOpenHelper.ALERTS_PID, PID);
        Uri alertURI = getContentResolver().insert(Uri.withAppendedPath(TermTrackerProvider.CONTENT_URI_PATHLESS, DBOpenHelper.TABLE_ALERTS), values);
        Log.d("ViewAssessAlertActivity", "alertURI: " + alertURI.toString());
        Log.d("ViewAssessAlertActivity", "Inserted an alert " + alertURI.getLastPathSegment());

//        String myFormat = "MM/dd/yy";
//        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
//        String targetAlertDate = alertDateEditor.getText().toString().trim();
//        Date date = new Date();
//        try {
//            date = sdf.parse(targetAlertDate);
//        }
//        catch (Exception e) { }
//        long targetDateInMillis = date.getTime();
//        long nowInMillis = SystemClock.elapsedRealtime();
//        long delay = targetDateInMillis - nowInMillis;  // TODO:  would pass in below where is currently set to 60000 in order to alert on a target date
        scheduleNotification(buildNotification(alertMsgEditor.getText().toString().trim()), buildAlertTarget(), Integer.parseInt(alertURI.getLastPathSegment()));
    }

    private long buildAlertTarget() {
        String myFormat = "MM/dd/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        String targetAlertDate = alertDateEditor.getText().toString().trim();
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

    private void deleteAlert() {
        Uri alertURI = Uri.withAppendedPath(TermTrackerProvider.CONTENT_URI_PATHLESS, DBOpenHelper.TABLE_ALERTS);
        getContentResolver().delete(alertURI, alertsFilter, null);
        setResult(RESULT_OK);
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
            case R.id.menu_removeAlert:
                removeAlert();
                onBackPressed();
                setResult(RESULT_OK);
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
