package com.example.reminder2;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddReminder extends BaseActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener, View.OnClickListener {


    private EditText mTitle, mDecription, mDate, mTime;
    private TextView mSetdate, mSetTime;
    private ImageView mImage;
    private Button mAdd, mLocation, mSave;

    private int mHour, mYear, mMonth, mMinute, mDay;
    private String sTime, sDate;
    private MyLocationProvider myLocationProvider;
    private Location location;
    private double lat, lang;
    int Request_Camera = 100, Select_Image = 101;
    public byte[] byteImage;

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
        mTitle = findViewById(R.id.title);
        mDecription = findViewById(R.id.decription);
        mDate = findViewById(R.id.date);
        mSetdate = findViewById(R.id.setdate);
        mTime = findViewById(R.id.time);
        mSetdate.setOnClickListener(this);
        mSetTime = findViewById(R.id.setTime);
        mSetTime.setOnClickListener(this);

        mImage = findViewById(R.id.image);
        mAdd = findViewById(R.id.add);
        mAdd.setOnClickListener(this);
        mLocation = findViewById(R.id.location);
        mLocation.setOnClickListener(this);
        mSave = findViewById(R.id.save);
        mSave.setOnClickListener(this);
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
        String des = mDecription.getText().toString();
        String date = mSetdate.getText().toString();
        String timeS = mSetTime.getText().toString();

        if (mImage.getDrawable() != null) {

            Bitmap bitmap = ((BitmapDrawable) mImage.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            if (bitmap != null) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byteImage = baos.toByteArray();

            }
        }

        Note note = new Note(titleS, des, date, timeS, lat, lang, byteImage);
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
            case R.id.add:
                SelectImage();
                break;
            case R.id.location:
                // TODO 19/10/15
                if (myLocationProvider == null)
                    myLocationProvider = new MyLocationProvider(this);
                // lw msh 3awaz a listen 3la update ab3t null
                location = myLocationProvider.getCurrentLocation(null);
                lat = location.getLatitude();
                lang = location.getLongitude();
                break;
            case R.id.save:
                // TODO 19/10/15
                saveReminder();
                break;
            case R.id.setdate:// TODO 19/10/16
                setDate();
                break;
            case R.id.setTime:// TODO 19/10/16
                setTime();
                break;
            default:
                break;
        }
    }

    public void setTime() {
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

    public void setDate() {
        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dpd.show(getFragmentManager(), "Datepickerdialog");
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


    private void SelectImage() {

        final CharSequence[] items = {"Camera", "Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(AddReminder.this);
        builder.setTitle("Add Image");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {

                if (items[i].equals("Camera")) {

                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, Request_Camera);


                } else if (items[i].equals("Gallery")) {

                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                    startActivityForResult(intent.createChooser(intent, "Select File"), Select_Image);

                } else if (items[i].equals("Cancel")) {

                    dialog.dismiss();

                }
            }
        });

        builder.show();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {


        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == Request_Camera) {


                Bundle bundle = data.getExtras();
                final Bitmap bitmap = (Bitmap) bundle.get("data");
                mImage.setImageBitmap(bitmap);

            } else if (requestCode == Select_Image) {

                Uri SelectedImageUri = data.getData();
                mImage.setImageURI(SelectedImageUri);
            }


        }
    }

}



