package com.theappschef.jktrc;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    AddTestAdapter mAdapter;
    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);




    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }
    public void noData(){
        getView().findViewById(R.id.ll_no_data_found).setVisibility(View.VISIBLE);
    }
    public void inflate(ArrayList<ArrayList<String>> a){
        try {
            RecyclerView mRvBankName = getView().findViewById(R.id.rv_bank_name);
            mRvBankName.setHasFixedSize(true);
            mRvBankName.setLayoutManager(new LinearLayoutManager(getContext()));
            mRvBankName.addItemDecoration(new DividerItemDecoration(mRvBankName.getContext(), DividerItemDecoration.VERTICAL));
            mAdapter = new AddTestAdapter(getActivity(),a);
            mRvBankName.setAdapter(mAdapter);
            getView().findViewById(R.id.ll_no_data_found).setVisibility(View.GONE);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}