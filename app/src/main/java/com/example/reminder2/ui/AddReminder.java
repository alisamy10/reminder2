package com.example.reminder2.ui;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.reminder2.Base.BaseActivity;
import com.example.reminder2.R;
import com.example.reminder2.database.NoteDataBase;
import com.example.reminder2.locationHelper.MyLocationProvider;
import com.example.reminder2.database.model.Note;
import com.theartofdev.edmodo.cropper.CropImage;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddReminder extends BaseActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener, View.OnClickListener {


    private int mHour, mYear, mMonth, mMinute, mDay;
    private String sTime, sDate;
    private MyLocationProvider myLocationProvider;
    private Location location;
    private double lat, lang;
    private int Request_Camera = 100, Select_Image = 101;
    public byte[] byteImage;
    private EditText tittleEdit, descEdit;
    private TextView dateTxt, timeTxt;
    private Button saveBtn;
    private CircleImageView memoryImage;
    private Bitmap bitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reminder);
        initView();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        sDate = sdf.format(new Date());
        dateTxt.setText(sDate);

        SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm:ss ");
        sTime = sdf2.format(new Date());
        timeTxt.setText(sTime);


    }

    private void initView() {
        descEdit = findViewById(R.id.edit_desc);
        tittleEdit = findViewById(R.id.edit_tittle);
        dateTxt = findViewById(R.id.txt_date1);
        dateTxt.setOnClickListener(this);
        timeTxt = findViewById(R.id.txt_time1);
        timeTxt.setOnClickListener(this);
        saveBtn = findViewById(R.id.sign_up_btn);
        saveBtn.setOnClickListener(this);
        memoryImage = (CircleImageView) findViewById(R.id.profile_image);
        memoryImage.setOnClickListener(this);
    }


    private void saveReminder() {

        String titleS = tittleEdit.getText().toString();
        String des = descEdit.getText().toString();
        String date = dateTxt.getText().toString();
        String timeS = timeTxt.getText().toString();
        if (myLocationProvider == null)
            myLocationProvider = new MyLocationProvider(this);
        // lw msh 3awaz a listen 3la update ab3t null
        location = myLocationProvider.getCurrentLocation(null);

        lat = location.getLatitude();
        lang = location.getLongitude();

        if (memoryImage.getDrawable() != null) {

            bitmap = ((BitmapDrawable) memoryImage.getDrawable()).getBitmap();
            if (bitmap != null) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
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
        dateTxt.setText(sDate);
    }


    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
        mHour = hourOfDay;
        mMinute = minute;
        if (minute < 10)
            sTime = hourOfDay + ":" + "0" + minute;
        else
            sTime = hourOfDay + ":" + minute;

        timeTxt.setText(sTime);
    }
    MaterialDialog materialDialog ;

    private void selectImage() {
          materialDialog = new MaterialDialog.Builder(this)
                .title(R.string.uploadImages)
                .items(R.array.uploadImages)
                .itemsIds(R.array.itemIds)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        switch (which){
                            case 0:

                                Intent intentgallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                intentgallery.setType("image/*");
                                if(intentgallery.resolveActivity(getPackageManager())!=null)
                                startActivityForResult(intentgallery.createChooser(intentgallery, "Select File"), Select_Image);
                                break;
                            case 1:
                                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                if(intent.resolveActivity(getPackageManager())!=null)
                                    startActivityForResult(intent, Request_Camera);
                                break;
                            case 2:
                                materialDialog.dismiss();
                                break;
                        }
                    }
                })
                .show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {


        super.onActivityResult(requestCode, resultCode, data);


        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == Request_Camera) {

                bitmap = (Bitmap) data.getExtras().get("data");
                if (bitmap != null)
                    memoryImage.setImageBitmap(bitmap);

            } else if (requestCode == Select_Image) {
                if (data != null) {
                    try {
                        final Uri SelectedImageUri = data.getData();
                        final InputStream stream = getContentResolver().openInputStream(SelectedImageUri);
                        bitmap = BitmapFactory.decodeStream(stream);
                        CropImage.activity(SelectedImageUri)
                                .setAspectRatio(1, 1)
                                .setMinCropWindowSize(500, 500)
                                .start(this);
                        memoryImage.setImageBitmap(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_date1:
                setDate();
                // TODO 19/10/17
                break;
            case R.id.txt_time1:
                setTime();
                // TODO 19/10/17
                break;
            case R.id.sign_up_btn:
                if (tittleEdit.getText().toString().trim().isEmpty()) {
                    tittleEdit.setError("input required");
                    return;
                }
                if (descEdit.getText().toString().trim().isEmpty()) {
                    descEdit.setError("input required");
                    return;
                }

                else {
                    saveReminder();
                    startActivity(new Intent(this, MapsActivity.class));
                    finish();
                }
                // TODO 19/10/17
                break;
            case R.id.profile_image:// TODO 19/10/18
                selectImage();
                break;
            default:
                break;
        }
    }
}

