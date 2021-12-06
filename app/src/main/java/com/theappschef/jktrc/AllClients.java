package com.theappschef.jktrc;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AllClients extends AppCompatActivity {

    AllClientAdapter mAdapter;
    public ArrayList<ClientModel> testList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_clients);

        testList = new ArrayList<>();
        findViewById(R.id.submit).setOnClickListener(v -> {
            EditText editText = findViewById(R.id.password);
            if (editText.getText().toString().equals("123456")) {
                try {
                    findViewById(R.id.passScreen).setVisibility(View.GONE);
                    findViewById(R.id.uihelper).setVisibility(View.VISIBLE);
                    upload();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            } else {
                editText.setError("Wrong Password");
            }
        });
    }

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    OkHttpClient client = new OkHttpClient();

    void upload() throws MalformedURLException {
        testList.clear();

        Handler mHandler = new Handler(Looper.getMainLooper());
        postguy("https://theappschef.in/manav/getClientDetails.php", "", new Callback() {
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
                                JSONArray jsonArray = new JSONArray(response.body().string());

                                System.out.println(jsonArray.length());
                                for (int i = 0; i < jsonArray.length() - 1; i++) {
                                    ClientModel clientModel = new ClientModel();
                                    JSONObject j = jsonArray.getJSONObject(i);
                                    clientModel.setClient_id(j.getString("client_id"));
                                    clientModel.setName(j.getString("name"));
                                    clientModel.setEmail(j.getString("email"));
                                    clientModel.setPhone(j.getString("phone"));
                                    clientModel.setCompany(j.getString("company"));
                                    clientModel.setWork_area(j.getString("work_area"));
                                    clientModel.setMaterial(j.getString("material"));
                                    clientModel.setSpecified(j.getString("specified"));
                                    clientModel.setSample(j.getString("sample"));
                                    System.out.println(j.toString());

                                    testList.add(clientModel);
                                }
                                mHandler.post(() -> {
                                    System.out.println(testList);
                                    inflate(testList);
                                });
//                                    }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }
                    }
                }
        );
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

    public void inflate(ArrayList<ClientModel> a) {
        try {
            RecyclerView mRvBankName = findViewById(R.id.rv_bank_name);
            mRvBankName.setHasFixedSize(true);
            mRvBankName.setLayoutManager(new LinearLayoutManager(this));
            mRvBankName.addItemDecoration(new DividerItemDecoration(mRvBankName.getContext(), DividerItemDecoration.VERTICAL));
            mAdapter = new AllClientAdapter(this, a);
            mRvBankName.setAdapter(mAdapter);
            findViewById(R.id.ll_no_data_found).setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}