package com.theappschef.jktrc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.GnssStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DashboardActivity extends AppCompatActivity implements LocationListener {
    Double rangeLat1 = 28.714785, rangeLat2 = 28.716017;
    Double rangeLon1 = 77.126440, rangeLon2 = 77.127872;
    GnssStatus.Callback mGnssStatusCallback;
    BottomNavigationView bottomNavigationView;
    SharedPref sharedPref;
    Fragment fragmentTop;
    ArrayList<String> arrayList;
    public JSONArray jsonArrayTests;
    public ArrayList<ArrayList<String>> testList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
//        if (getSupportActionBar() != null) {
//            getSupportActionBar().hide();
//        }

       func();
        testList = new ArrayList<>();
        sharedPref = new SharedPref(this);
        loadFragment(new HomeFragment());
        locationCheck();
        bottomNavigationView = findViewById(R.id.navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment fragment = null;
            if (item.getItemId() == R.id.navigation_home_purchased) {
                fragment = new HomeFragment();
            } else if (item.getItemId() == R.id.navigation_report) {
                fragment = new SettingsFragment();

            }
            return loadFragment(fragment);
        });
        bottomNavigationView.setBackground(null);
        bottomNavigationView.getMenu().getItem(1).setEnabled(false);
        findViewById(R.id.fab_add_transaction).setOnClickListener(v -> {
            if(!sharedPref.getChoice().equals("")) {
                startActivity(new Intent(this, AddExperiment.class));
            }
            else {
                showGetClient();
            }
            });
        showGetClient();

    }

    public void func(){
        arrayList=new ArrayList<>();
        DatabaseReference mDatabase;
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Idlist").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot s:snapshot.getChildren()) {
                    arrayList.add(s.getKey());
                    Log.d("tag",s.getKey());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    @Override
    public void onLocationChanged(@NonNull Location location) {
        LatLng locationLatLong;
        locationLatLong = new LatLng(location.getLatitude(), location.getLongitude());

        if (locationLatLong.latitude >= rangeLat1 && locationLatLong.latitude <= rangeLat2 && locationLatLong.longitude >= rangeLon1 && locationLatLong.longitude <= rangeLon2) {
            Toast.makeText(this, "In Area", Toast.LENGTH_SHORT).show();
        } else {
            startActivity(new Intent(this, MainActivity.class));
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

        boolean gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!gps_enabled) {
            startActivity(new Intent(this, MainActivity.class));
        }

    }

    LocationManager locationManager;

    public void locationCheck() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (locationManager == null)
            return;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);

        mGnssStatusCallback = new GnssStatus.Callback() {
            @Override
            public void onStopped() {
                startActivity(new Intent(DashboardActivity.this, MainActivity.class));
                finish();
                super.onStopped();
            }
        };
    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            fragmentTop = fragment;
            //switching fragment
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            if (fragmentTop instanceof HomeFragment) {
                if(sharedPref.getChoice()!=""){
                    try {
                        upload();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
//                ((HomeFragment) fragmentTop).inflate(testList);
            }
            return true;
        }
        return false;
    }

    public void showGetClient() {
        final String[] s = {""};
        final Dialog dialog = new Dialog(DashboardActivity.this);
        dialog.setContentView(R.layout.dialog_get_client);
        dialog.setCancelable(true);
        dialog.show();
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        dialog.getWindow().setLayout((6 * width) / 7, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        EditText editText = dialog.findViewById(R.id.exitC);
        dialog.findViewById(R.id.select).setOnClickListener(v -> {
            if (editText.getText().toString().equals("")) {
                editText.setError("Enter a client id to proceed");
            }
            else if(!arrayList.contains(editText.getText().toString())){
                Toast.makeText(this,"Client doesn't exist",Toast.LENGTH_SHORT).show();
            }
            else {
                sharedPref.setChoice(editText.getText().toString());
                try {
                    upload();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
            }
        });
    }

    public void showUpdateStatus(int position) {
        final String[] s = {""};
        final Dialog dialog = new Dialog(DashboardActivity.this);
        dialog.setContentView(R.layout.dialog_update_client);
        dialog.setCancelable(false);
        dialog.show();
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        dialog.getWindow().setLayout((6 * width) / 7, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        EditText editText = dialog.findViewById(R.id.exitC);
        dialog.findViewById(R.id.select).setOnClickListener(v -> {
            s[0]=editText.getText().toString();
            JSONObject jsonObject2 = new JSONObject();
            try {


            jsonObject2.put("material",testList.get(position).get(0));
            jsonObject2.put("testName",testList.get(position).get(1));
            jsonObject2.put("testPerformed",testList.get(position).get(2));
            jsonObject2.put("status",s[0]);
            jsonArrayTests.put(position,jsonObject2);
            upload2();
            dialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    @Override
    protected void onResume() {
        func();
        bottomNavigationView.setSelectedItemId(0);
        loadFragment(new HomeFragment());
        super.onResume();
    }

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    OkHttpClient client = new OkHttpClient();
    void upload2() throws MalformedURLException {
        try {
            String url;
            if(jsonArrayTests!=null) {
                url = "https://theappschef.in/manav/updateTesting.php";
            }else {
                url = "https://theappschef.in/manav/newTesting.php";
                jsonArrayTests=new JSONArray();
            }
            JSONObject jsonObject = new JSONObject();
//            JSONArray jsonArray=new JSONArray();
//            jsonArrayTests.put(jsonObject2);

//            jsonArray.put(jsonObject2);
            jsonObject.put("client_id",sharedPref.getChoice());
            jsonObject.put("tests_done",jsonArrayTests.toString());

            Log.d("tag",jsonArrayTests.toString());
            Handler mHandler = new Handler(Looper.getMainLooper());

            postguy(url, jsonObject.toString(), new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            mHandler.post(() ->{
                                        e.printStackTrace();

                                    }
                            );
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            if (response.isSuccessful()) {
                                Log.d("tag",response.body().string());
                                mHandler.post(() ->{
                                    Toast.makeText(DashboardActivity.this,"Success",Toast.LENGTH_SHORT).show();
//                                    finish();
                                    try {
                                        upload();
                                    } catch (MalformedURLException e) {
                                        e.printStackTrace();
                                    }
                                });
                            }
                        }
                    }
            );
        } catch (JSONException ex) {
            Log.d("Exception", "JSON exception", ex);
        }
    }

    void upload() throws MalformedURLException {
        testList.clear();

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("client_id", sharedPref.getChoice());

            Handler mHandler = new Handler(Looper.getMainLooper());
            postguy("https://theappschef.in/manav/getDetails.php", jsonObject.toString(), new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            mHandler.post(() -> {
                                        e.printStackTrace();

                                    }
                            );
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            if (response.isSuccessful()) {
                                try {
                                    JSONObject jsonObject1 = new JSONObject(response.body().string());

                                    if (!jsonObject1.getString("client_id").equals(sharedPref.getChoice())) {
                                        jsonArrayTests = null;

                                        mHandler.post(() -> {
                                            Toast.makeText(DashboardActivity.this, "No Existing Tests", Toast.LENGTH_SHORT).show();
                                            if (fragmentTop instanceof HomeFragment) {
                                                ((HomeFragment) fragmentTop).noData();
                                            }
//                                            finish();
                                        });
                                    } else {
                                        JSONArray jsonArray = new JSONArray(jsonObject1.getString("tests_done"));
                                        AddExperiment.jsonArrayTests = new JSONArray(jsonObject1.getString("tests_done"));
                                        jsonArrayTests = new JSONArray(jsonObject1.getString("tests_done"));

                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            JSONObject j = jsonArray.getJSONObject(i);
                                            ArrayList<String> arrayList = new ArrayList<>();
                                            arrayList.add(j.getString("material"));
                                            arrayList.add(j.getString("testName"));
                                            arrayList.add(j.getString("testPerformed"));
                                            arrayList.add(j.getString("status"));
                                            System.out.println(j.toString());
                                            testList.add(arrayList);
                                        }
                                        mHandler.post(() -> {
                                            if (fragmentTop instanceof HomeFragment) {
                                                ((HomeFragment) fragmentTop).inflate(testList);
                                            }
                                        });
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                            }
                        }
                    }
            );
        } catch (JSONException ex) {
            Log.d("Exception", "JSON exception", ex);
        }
    }

    void postguy(String url, String json, okhttp3.Callback callback) {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        okhttp3.Call call = client.newCall(request);
        call.enqueue(callback);
    }

    @Override
    public void onBackPressed() {
    }
}