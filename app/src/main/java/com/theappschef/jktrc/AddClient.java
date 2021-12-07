package com.theappschef.jktrc;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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

public class AddClient extends AppCompatActivity {
EditText client_id,name,email,phone,company,work,material,specified,sample;
    private DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_client);
        client_id=findViewById(R.id.client_id);
        name=findViewById(R.id.name);
        email=findViewById(R.id.email);
        phone=findViewById(R.id.phone);
        company=findViewById(R.id.company);
        work=findViewById(R.id.work_area);
        material=findViewById(R.id.material);
        specified=findViewById(R.id.specified);
        sample=findViewById(R.id.sample);
        findViewById(R.id.save).setOnClickListener(v -> {
            if(client_id.getText().toString().equals("")){
                client_id.setError("Can't be empty");
                return;
            }
            if(name.getText().toString().equals("")){
                name.setError("Can't be empty");
                return;
            }
            if(email.getText().toString().equals("")){
                email.setError("Can't be empty");
                return;
            }
            if(phone.getText().toString().equals("")){
                phone.setError("Can't be empty");
                return;
            }
            if(company.getText().toString().equals("")){
                company.setError("Can't be empty");
                return;
            }
            if(work.getText().toString().equals("")){
                work.setError("Can't be empty");
                return;
            }
            if(material.getText().toString().equals("")){
                material.setError("Can't be empty");
                return;
            }
            if(specified.getText().toString().equals("")){
                specified.setError("Can't be empty");
                return;
            }
            if(sample.getText().toString().equals("")){
                company.setError("Can't be empty");
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
                url = "https://theappschef.in/manav/newClient.php";

            JSONObject jsonObject = new JSONObject();

            jsonObject.put("client_id",client_id.getText().toString());
            jsonObject.put("name",name.getText().toString());
            jsonObject.put("email",email.getText().toString());
            jsonObject.put("phone",phone.getText().toString());
            jsonObject.put("company",company.getText().toString());
            jsonObject.put("work_area",work.getText().toString());
            jsonObject.put("material",material.getText().toString());
            jsonObject.put("specified",specified.getText().toString());
            jsonObject.put("sample",sample.getText().toString());

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
                                    mDatabase = FirebaseDatabase.getInstance().getReference();
                                    mDatabase.child("Idlist").child(client_id.getText().toString()).setValue("true");
                                    Toast.makeText(AddClient.this,"Success",Toast.LENGTH_SHORT).show();
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