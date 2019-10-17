package com.example.reminder2;

import android.Manifest;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.example.reminder2.Base.BaseActivity;
import com.example.reminder2.database.NoteDataBase;
import com.example.reminder2.locationHelper.MyLocationProvider;
import com.example.reminder2.model.Note;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends BaseActivity implements LocationListener, OnMapReadyCallback {

    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 600;
    private MyLocationProvider myLocationProvider;
    private Location location;
    private MapView mapView;
    private MarkerOptions pickupMarker;
    private Toolbar toolbar;
    private GoogleMap googleMap;
    private FloatingActionButton fab, fab2 , fab3;
    private List<Note> allNotes;
    private Marker userMarker;
    boolean checkForOneTimeCallDarwarMarker = false ;
     private  String tagForNotification = "";
     boolean getMarkersOneTime  ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        getMarkersOneTime=true;
        initView();
        mapView.onCreate(savedInstanceState);
        setSupportActionBar(toolbar);
        onClick();

        allNotes = new ArrayList<>();




        if (isLocationPermissionAllowed()) {//permission granted
            //call your function
            getUserLocation();
        } else { //request runtime permission
            requestLocationPermission();
        }



    }
    public void drawCurrentUserLocation(){
        if(location==null||googleMap==null){
            return;
        }
        Log.e("user_location",location.getLatitude()+ "  "+location.getLongitude());
        if(userMarker==null)
            userMarker = googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(location.getLatitude(),location.getLongitude()))
                    .title("i'm here"+location.getLatitude()+" "+location.getLongitude()).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_car)));


        else userMarker.setPosition(new LatLng(location.getLatitude(),location.getLongitude()));
        googleMap.animateCamera(CameraUpdateFactory
                .newLatLngZoom(new LatLng(location.getLatitude(),location.getLongitude()), 15));

    }


    private void onClick(){

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MapsActivity.this, AddReminder.class));
                checkForOneTimeCallDarwarMarker= true;
                getMarkersOneTime =true;
            }
        });



        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getUserLocation();
                drawCurrentUserLocation();
            }
        });

        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(MapsActivity.this, ShowMemory.class));


            }
        });

    }

    private void initView() {
        mapView =  findViewById(R.id.map);
        toolbar = findViewById(R.id.toolbar);
        fab = findViewById(R.id.fab);
        fab3=findViewById(R.id.fab3);
        fab2=findViewById(R.id.fab2);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();

        mapView.getMapAsync(this);

        allNotes = NoteDataBase.getInstance(getApplicationContext()).notesDao().getAllNotes();


    }
    @Override
    public void onLocationChanged(Location location) {
        this.location = location;


    if (googleMap!=null) {

       drawUserMarker(tagForNotification);
       Log.e("k","loc");

    }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    private boolean isLocationPermissionAllowed() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            return true;
        return false;
    }

    private void getUserLocation() {
        if (myLocationProvider == null)
            myLocationProvider = new MyLocationProvider(this);
        // lw msh 3awaz a listen 3la update ab3t null
        location = myLocationProvider.getCurrentLocation(this);
    }



    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


    private void requestLocationPermission() {
        // Permission is not granted
        // Should we show an explanation?
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Show an explanation to the user *asynchronously* -- don't block
            // this thread waiting for the user's response! After the user
            // sees the explanation, try again to request the permission.
            showMessage("this app wants your location ", "OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    ActivityCompat.requestPermissions(MapsActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION
                    );

                }
            }, true);
        } else {
            // No explanation needed; request the permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);

            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant. The callback method gets the
            // result of the request.
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    getUserLocation();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "can not access location", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }


    @Override
    public void onMapReady(final GoogleMap googleMap) {
        this.googleMap = googleMap;

        getMarkars(getMarkersOneTime);

        Log.e("k","ready");

        drawUserMarker(tagForNotification);

    }

    public void drawUserMarker(String tag){


         drawCurrentUserLocation();

         if (tag.equals("")&&location!=null&&allNotes.size()!=0) {
             tagForNotification="stop";
         for (int i = 0; i < allNotes.size(); i++) {


                 if (distance(allNotes.get(i).getLat(), allNotes.get(i).getLng(), location.getLatitude(), location.getLongitude()) * 1000 <= 400) {

                     createNotification(allNotes.get(i).getTitle());

                 }

             }
         freezNotificationOneMin();
             //else pickupMarker.position(new LatLng (allNotes.get(i).getLat(),allNotes.get(i).getLng()));


         }


         googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
             @Override
             public boolean onMarkerClick(Marker marker) {

                 Toast.makeText(MapsActivity.this, marker.getPosition().latitude + "", Toast.LENGTH_SHORT).show();


                 return false;
             }
         });



}

 private void getMarkars(boolean check) {


        if (check) {
            Log.e("k","getMark");
            for (int i = 0; i < allNotes.size(); i++) {


                LatLng l = new LatLng(allNotes.get(i).getLat(), allNotes.get(i).getLng());
                pickupMarker = new MarkerOptions();
                pickupMarker.position(l);
                googleMap.addMarker(pickupMarker);

            }
            getMarkersOneTime=false;
        }
}


    private void freezNotificationOneMin(){



        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

           tagForNotification ="";
           Log.e("k","openHandler");



            }
        },60000);


    }


}