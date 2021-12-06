package com.theappschef.jktrc;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.sucho.placepicker.AddressData;
import com.sucho.placepicker.Constants;
import com.sucho.placepicker.MapType;
import com.sucho.placepicker.PlacePicker;

public class MainActivity extends AppCompatActivity implements LocationListener {

    private static final int TIME_BEFORE_MANUAL_BUTTON_SHOWN = 15 * 1000; //15 Secs
    GoogleMap gmap = null;
    String address = null;
    LatLng locationLatLong = null;
    String city;
    Double rangeLat1=28.714785,rangeLat2=28.716017;
    Double rangeLon1=77.126440,rangeLon2=77.127872;


    int     PERMISSION_REQUEST_CODE = 555,
            ENABLE_GPS_REQUEST_CODE = 1000,
            PLACEPICKER_REQUEST_CODE = 1111;
    boolean firstTime = false;

    //so that it is not called multiple times one over other
    Boolean isPlacePickerShowing = false;

    //to keep track of how many times to ask for location permission
    //when user clicks deny on permission
    int permissionRequestedCount = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPref sharedPref=new SharedPref(this);
        sharedPref.setChoice("");
//        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            //get user location and show enable location if it is disabled
            Log.d("debugg", "Location : running checks");
            getLocation();
        }else{
            //request permissions
//            runChecks();
            requestLocationPermission();
        }
        findViewById(R.id.grant).setOnClickListener(v -> {
            requestLocationPermission();
        });

    }


    @SuppressLint("MissingPermission")
    private void getLocation(){
        Log.d("debugg", "Location : getting location");
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if(locationManager==null)
            return;

        Location lastGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        Location lastNetwork = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        if(lastGPS!=null){
            Log.d("debugg", "Location : last Known location from gps");
            locationLatLong = new LatLng(lastGPS.getLatitude(),lastGPS.getLongitude());
            handleRecievedLocation();
        }else if(lastNetwork!=null){
            Log.d("debugg", "Location : last known location from network");
            locationLatLong = new LatLng(lastNetwork.getLatitude(),lastNetwork.getLongitude());
            handleRecievedLocation();
        } else {
            Log.d("debugg", "requesting new location");
            boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                permissionRequestedCount++;
                showEnableGPSDialog();
            } else {
                if (isGPSEnabled) {
                    //minimum time of update 30sec and minimum change in location for update 10meters
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,10 * 1000,0,this, null);
                }
                if(isNetworkEnabled){
                    //minimum time of update 30sec and minimum change in location for update 10meters
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,10 * 1000,0,  this, null);
                }
            }
        }
    }
    @Override
    public void onLocationChanged(Location location) {
        Log.d("debugg", String.valueOf(location.getLongitude()+"    "+location.getLatitude()));
        locationLatLong = new LatLng(location.getLatitude(),location.getLongitude());

        //as we got the location hence removing the listener to not get further updates
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        if(lm!=null)
            lm.removeUpdates(this);

        handleRecievedLocation();

//        txtLat = (TextView) findViewById(R.id.textview1);
//        txtLat.setText("Latitude:" + location.getLatitude() + ", Longitude:" + location.getLongitude());
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d("Latitude","disable");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d("Latitude","enable");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("Latitude","status");
    }
    private void handleRecievedLocation(){
        Log.d("debugg", locationLatLong.latitude+" "+locationLatLong.longitude);

        if(locationLatLong.latitude>=rangeLat1&&locationLatLong.latitude<=rangeLat2&&locationLatLong.longitude>=rangeLon1&&locationLatLong.longitude<=rangeLon2){
            Toast.makeText(this,"In Area",Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this,LoginActivity.class));
        }




//        if(!isPlacePickerShowing) {
//            Intent intent = new PlacePicker.IntentBuilder()
//                    .setLatLong(locationLatLong.latitude, locationLatLong.longitude)  // Initial Latitude and Longitude the Map will load into
//                    .showLatLong(true)  // Show Coordinates in the Activity
//                    .setMapZoom(12.0f)  // Map Zoom Level. Default: 14.0
//                    .setAddressRequired(true) // Set If return only Coordinates if cannot fetch Address for the coordinates. Default: True
//                    .hideMarkerShadow(true) // Hides the shadow under the map marker. Default: False
////                    .setMarkerDrawable(R.drawable.ic_location_pin) // Change the default Marker Image
//                    .setMarkerImageImageColor(R.color.colorPrimary)
//                    .setFabColor(R.color.colorPrimary)
//                    .setPrimaryTextColor(R.color.colorBackground) // Change text color of Shortened Address
//                    .setSecondaryTextColor(R.color.colorBackground) // Change text color of full Address
//                    .setBottomViewColor(R.color.colorPrimary) // Change Address View Background Color (Default: White)
//                    .setMapRawResourceStyle(R.raw.map_style)  //Set Map Style (https://mapstyle.withgoogle.com/)
//                    .setMapType(MapType.NORMAL)
//                    .setPlaceSearchBar(true, "AIzaSyCr_GCjBT4lZ7ct-o8xdql_2d9T7H8Cj-w") //Activate GooglePlace Search Bar. Default is false/not activated. SearchBar is a chargeable feature by Google
//                    .build(this);
//
//            isPlacePickerShowing = true;
//            startActivityForResult(intent, PLACEPICKER_REQUEST_CODE);
//        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if(isPlacePickerShowing){
            isPlacePickerShowing = false;
        }else{
            runChecks();
        }
    }
    private void runChecks(){
        if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            //get user location and show enable location if it is disabled
            Log.d("debugg", "Location : running checks");
            getLocation();
        }else{
            findViewById(R.id.grant).setVisibility(View.VISIBLE);

            //request permissions
//            requestLocationPermission();
        }
    }
    private void showEnableGPSDialog() {
        LocationRequest mReqHigh = LocationRequest.create().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationRequest mReqBal = LocationRequest.create().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().
                addLocationRequest(mReqHigh).
                addLocationRequest(mReqBal);

        Task<LocationSettingsResponse> result =
                LocationServices.getSettingsClient(this).checkLocationSettings(builder.build());

        result.addOnCompleteListener(task -> {
            try {
                LocationSettingsResponse response = task.getResult(ApiException.class);
                runChecks();
            }catch (ApiException exception) {
                switch (exception.getStatusCode()) {
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the
                        // user a dialog.
                        try {
                            // Cast to a resolvable exception.
                            ResolvableApiException resolvable = (ResolvableApiException) exception;
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            resolvable.startResolutionForResult(
                                    MainActivity.this,
                                    ENABLE_GPS_REQUEST_CODE);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        } catch (ClassCastException e) {
                            // Ignore, should be an impossible error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        break;
                }
            }
        });
    }

    private  void requestLocationPermission(){
        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            runChecks();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==ENABLE_GPS_REQUEST_CODE ){
            //resultCode = 0 means denied and resultCode = -1 means granted
            if(resultCode == -1) {
                runChecks();
            }else{
                if(permissionRequestedCount<1){
                    runChecks();
                }else{
                    Toast.makeText(getApplicationContext(),"Please turn on gps",Toast.LENGTH_LONG ).show();
//                    if(!firstTime) {
//                        finish();
//                    }else runChecks();
                }
            }
        }else if(requestCode==PLACEPICKER_REQUEST_CODE){
            if(resultCode == RESULT_OK && data!=null) {
                AddressData addressData = data.getParcelableExtra(Constants.ADDRESS_INTENT);
//                handleRecievedData(addressData);
                Log.d("debugg", addressData.toString());
            }else {
//                errorGettingDataFromPlacePicker();
            }
        }
    }
}