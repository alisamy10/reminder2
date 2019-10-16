package com.example.reminder2;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.example.reminder2.Base.BaseActivity;
import com.example.reminder2.database.NoteDataBase;
import com.example.reminder2.locationHelper.MyLocationProvider;
import com.example.reminder2.model.Note;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends BaseActivity implements LocationListener, OnMapReadyCallback {

    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 600;
    MyLocationProvider myLocationProvider;
    Location location;
    MapView mapView;
    MarkerOptions pickupMarker;
    Toolbar toolbar;
    GoogleMap googleMap;
    LocationManager locationManager;
    private FloatingActionButton fab, fab2;
    List<Note> allNotes;
    Marker userMarker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        initView();
        allNotes = new ArrayList<>();

        mapView.onCreate(savedInstanceState);
        setSupportActionBar(toolbar);

        if (isLocationPermissionAllowed()) {//permission granted
            //call your function
            getUserLocation();
        } else { //request runtime permission
            requestLocationPermission();
        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MapsActivity.this, AddReminder.class));
            }
        });
        fab2=findViewById(R.id.fab2);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
        });
     onMapReady(googleMap);
        mapView.getMapAsync(this);

    }

    private void initView() {
        mapView = (MapView) findViewById(R.id.map);
        toolbar = findViewById(R.id.toolbar);
        fab = findViewById(R.id.fab);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
        allNotes = NoteDataBase.getInstance(getApplicationContext()).notesDao().getAllNotes();
        for (int i = 0; i < allNotes.size(); i++) {
            Toast.makeText(this, "bvbvn" + allNotes.get(i).getLat(), Toast.LENGTH_SHORT).show();
        }
        drawUserMarker();
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
    public void onLocationChanged(Location location) {
        this.location = location;
        drawUserMarker();

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
            showMessage("this app wants your location to find nearby drivers", "OK", new DialogInterface.OnClickListener() {
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

        drawUserMarker();
    }






    public void drawUserMarker(){
        if(location==null||googleMap==null){


            return;
        }

        if(userMarker==null)
            userMarker = googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(location.getLatitude(),location.getLongitude()))
                    .title("i'm here"+location.getLatitude()+" "+location.getLongitude()).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_car)));


        else userMarker.setPosition(new LatLng(location.getLatitude(),location.getLongitude()));
        googleMap.animateCamera(CameraUpdateFactory
                .newLatLngZoom(new LatLng(location.getLatitude(),location.getLongitude()), 15));



        for(int i =0;i<allNotes.size();i++)
        {
            Toast.makeText(this, "hellooo", Toast.LENGTH_SHORT).show();

            LatLng l = new LatLng(allNotes.get(i).getLat(),allNotes.get(i).getLng());
            pickupMarker = new MarkerOptions();
            pickupMarker.position(l);
            googleMap.addMarker(pickupMarker);


        //else pickupMarker.position(new LatLng (allNotes.get(i).getLat(),allNotes.get(i).getLng()));


        }





        //if(distance(30.125742,31.281684,location.getLatitude(),location.getLongitude())*1000>=400){
           // Toast.makeText(this, "vkjvvkjvjkvkv", Toast.LENGTH_SHORT).show();
            //createNotification("hlloasdhsad");

        //}

        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                Toast.makeText(MapsActivity.this,marker.getPosition().latitude+ "", Toast.LENGTH_SHORT).show();


                return false;
            }
        });




    }

    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    private String CHANNEL_ID;

    private void createNotificationChannel() {
        CharSequence channelName = CHANNEL_ID;
        String channelDesc = "channelDesc";
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, channelName, importance);
            channel.setDescription(channelDesc);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            assert notificationManager != null;
            NotificationChannel currChannel = notificationManager.getNotificationChannel(CHANNEL_ID);
            if (currChannel == null)
                notificationManager.createNotificationChannel(channel);
        }
    }




    public void createNotification(String message) {

        CHANNEL_ID ="ahli";
        if (message != null ) {
            createNotificationChannel();

            Intent intent = new Intent(this, MapsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setContentTitle("ali")
                    .setContentText(message)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);
            Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            mBuilder.setSound(uri);


            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            int notificationId = (int) (System.currentTimeMillis()/4);
            notificationManager.notify(notificationId, mBuilder.build());
        }
    }

}