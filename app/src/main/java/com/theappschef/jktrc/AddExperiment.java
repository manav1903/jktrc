package com.theappschef.jktrc;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AddExperiment extends AppCompatActivity {

    EditText material,testName,testPerformed,status;
    public static JSONArray jsonArrayTests=null;
    SharedPref sharedPref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_experiment);
        material=findViewById(R.id.material);
        testName=findViewById(R.id.testName);
        testPerformed=findViewById(R.id.testPerformed);
        status=findViewById(R.id.status);
        sharedPref=new SharedPref(this);
        findViewById(R.id.save).setOnClickListener(v -> {
            if(material.getText().toString().equals("")){
                material.setError("Can't be empty");
                return;
            }
            if(testName.getText().toString().equals("")){
                testName.setError("Can't be empty");
                return;
            }
            if(testPerformed.getText().toString().equals("")){
                testPerformed.setError("Can't be empty");
                return;
            }
            if(status.getText().toString().equals("")){
                status.setError("Can't be empty");
                return;
            }


            try {
                upload();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        });
    }

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    OkHttpClient client = new OkHttpClient();

    void upload() throws MalformedURLException {
        try {
            String url;
            if(jsonArrayTests!=null) {
              url = "https://theappschef.in/manav/updateTesting.php";
            }else {
                url = "https://theappschef.in/manav/newTesting.php";
                jsonArrayTests=new JSONArray();
            }
            JSONObject jsonObject = new JSONObject();
            JSONObject jsonObject2 = new JSONObject();
            jsonObject2.put("material",material.getText().toString());
            jsonObject2.put("testName",testName.getText().toString());
            jsonObject2.put("testPerformed",testPerformed.getText().toString());
            jsonObject2.put("status",status.getText().toString());
//            JSONArray jsonArray=new JSONArray();
            jsonArrayTests.put(jsonObject2);

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
                                    Toast.makeText(AddExperiment.this,"Success",Toast.LENGTH_SHORT).show();
                                    finish();
                                        });
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

}