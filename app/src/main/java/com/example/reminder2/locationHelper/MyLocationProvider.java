package com.example.reminder2.locationHelper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import java.util.List;


public class MyLocationProvider {

    LocationManager locationManager;
    Location location;
    public static final long MINIMUM_TIME_BETWEEN_UPDATES = 5*1000;  // 5 second
    public static final float MINIMUM_DISTANCE_BETWEEN_UPDATES = 10; // 10 meter


    public MyLocationProvider(Context context){
        locationManager = ((LocationManager)
                context.getSystemService(Context.LOCATION_SERVICE));
        location=null;
    }
    public boolean canGetLocation(){
        boolean GPSEnabled =
                locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean networkEnabled = locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER);
        return GPSEnabled || networkEnabled;
    }

    @SuppressLint("MissingPermission")
    public Location getCurrentLocation(LocationListener locationListener){
        if(!canGetLocation()){
            return null;
        }
        String provider = LocationManager.GPS_PROVIDER;
        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            provider = LocationManager.NETWORK_PROVIDER;

        location = locationManager.getLastKnownLocation(provider);

        if(location==null){
            location = getBestLastKnowLocation();
        }
        if(locationListener!=null){   // to track user location
            locationManager.requestLocationUpdates(provider,
                    MINIMUM_TIME_BETWEEN_UPDATES,MINIMUM_DISTANCE_BETWEEN_UPDATES,
                    locationListener);
        }

        return location;
    }

    @SuppressLint("MissingPermission")
    private Location getBestLastKnowLocation() {

        List<String> providers = locationManager.getAllProviders();
        Location bestLocation = null;

/*
        for (int i =0 ;i<providers.size();i++){
            String provider = providers.get(i);
        }
*/
        for(String provider : providers){
            Location temp = locationManager.getLastKnownLocation(provider);
            if(temp==null)continue;
            if(bestLocation ==null)
                bestLocation=temp;
            else{
                if(temp.getAccuracy()> bestLocation.getAccuracy())
                    bestLocation=temp;
            }
        }

        return bestLocation;
    }

}
