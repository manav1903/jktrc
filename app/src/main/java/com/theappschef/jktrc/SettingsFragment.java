package com.theappschef.jktrc;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class SettingsFragment extends Fragment {


    public SettingsFragment() {
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
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.cv_Logout).setOnClickListener(v -> {
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            if (firebaseAuth.getCurrentUser() != null) {
                firebaseAuth.signOut();
                startActivity(new Intent(getActivity(), MainActivity.class));
            }
        });
        view.findViewById(R.id.cv_new_Client).setOnClickListener(v->{
            startActivity(new Intent(getActivity(), AddClient.class));
        });
        view.findViewById(R.id.cv_all_Clients).setOnClickListener(v->{
            startActivity(new Intent(getActivity(), AllClients.class));
        });
        view.findViewById(R.id.cv_add_Client).setOnClickListener(v->{
            ((DashboardActivity) requireActivity()).showGetClient();
        });

    }
}