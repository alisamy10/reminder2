package com.example.reminder2;

import android.content.DialogInterface;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.reminder2.Base.BaseActivity;
import com.example.reminder2.database.NoteDataBase;
import com.example.reminder2.locationHelper.MyLocationProvider;
import com.example.reminder2.model.Note;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddReminder extends BaseActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener, View.OnClickListener {


    private int mDay;
    private EditText mTitle;
    private EditText mDecription;
    private EditText mDate;
    private TextView mSetdate;
    private EditText mTime;
    private TextView mSetTime;
    private ImageView mImage;
    private Button mAdd;
    private Button mLocation;
    private Button mSave;
    private int mHour,mYear,mMonth;
    String sDate;
    private int mMinute;
    private String sTime;
    MyLocationProvider myLocationProvider;
    Location location;
    double lat,lang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reminder);
        initView();


        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        String currentDate = sdf.format(new Date());
        mSetdate.setText(currentDate);

        SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm:ss z");
        String currentTime = sdf2.format(new Date());
        mSetTime.setText(currentTime);



    }

    private void initView() {
        mTitle = (EditText) findViewById(R.id.title);
        mDecription = (EditText) findViewById(R.id.decription);
        mDate = (EditText) findViewById(R.id.date);
        mSetdate = (TextView) findViewById(R.id.setdate);
        mSetdate.setOnClickListener(this);
        mTime = (EditText) findViewById(R.id.time);
        mSetTime = (TextView) findViewById(R.id.setTime);
        mSetTime.setOnClickListener(this);
        mImage = (ImageView) findViewById(R.id.image);
        mAdd = (Button) findViewById(R.id.add);
        mAdd.setOnClickListener(this);
        mLocation = (Button) findViewById(R.id.location);
        mLocation.setOnClickListener(this);
        mSave = (Button) findViewById(R.id.save);
        mSave.setOnClickListener(this);
    }


    public void setDate(View v) {

    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        monthOfYear++;
        mDay = dayOfMonth;
        mMonth = monthOfYear;
        mYear = year;
        sDate = dayOfMonth + "/" + monthOfYear + "/" + year;
        Toast.makeText(this, sDate, Toast.LENGTH_SHORT).show();
        mSetdate.setText(sDate);
    }

    public void setTime(View v) {
        Calendar now = Calendar.getInstance();
        TimePickerDialog tpd = TimePickerDialog.newInstance(
                this,
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                false
        );
        tpd.setThemeDark(false);
        tpd.show(getFragmentManager(), "Timepickerdialog");
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
        mHour = hourOfDay;
        mMinute = minute;
        if (minute < 10) {
            sTime = hourOfDay + ":" + "0" + minute;
        } else {
            sTime = hourOfDay + ":" + minute;
        }
        mSetTime.setText(sTime);
    }

   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_reminder, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.save_reminder:


                if (mTitleReminder.getText().toString().length() == 0) {
                    mTitleReminder.setError("Reminder Title cannot be blank!");
                } else {
                    saveReminder();
                    finish();
                }
                return true;


        }

        return super.onOptionsItemSelected(item);
    }

    */

    private void saveReminder() {
        String titleS = mTitle.getText().toString();
        String des=mDecription.getText().toString();
        String date = mSetdate.getText().toString();
        String timeS = mSetTime.getText().toString();

        Note note = new Note(titleS,des ,date ,timeS,lat,lang);
        NoteDataBase.getInstance(this).notesDao()
                .insert(note);
        showMessage(R.string.note_added_successfully, R.string.ok
                , new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                }, false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.setdate:
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getFragmentManager(), "Datepickerdialog");
                break;
            case R.id.setTime:

                break;
            case R.id.add:
                break;
            case R.id.location:
                // TODO 19/10/15
                if (myLocationProvider == null)
                    myLocationProvider = new MyLocationProvider(this);
                // lw msh 3awaz a listen 3la update ab3t null
                location = myLocationProvider.getCurrentLocation(null);
                lat=location.getLatitude();
                lang=location.getLongitude();
                break;
            case R.id.save:
                // TODO 19/10/15
                saveReminder();

                break;
            default:
                break;
        }
    }
}



