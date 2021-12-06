package com.theappschef.jktrc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {

    EditText phone,otp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        phone=findViewById(R.id.phone);

        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser()!=null){
            startActivity(new Intent(LoginActivity.this,DashboardActivity.class));
            finish();
        }
        findViewById(R.id.send_otp).setOnClickListener(v -> {
            if (phone.getText().toString().length()==10&&!op) {
                verifyNumber(phone.getText().toString(), otp_being_retrived);
            }
            else if (op){
                Toast.makeText(this, "Please wait a few moments before sending requesting otp again", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(this, "Please Enter a valid number", Toast.LENGTH_SHORT).show();
            }
        });

        otp=findViewById(R.id.otp);
        findViewById(R.id.check_otp).setOnClickListener(v -> {
            if(otp.getText().length()==6) {
                PhoneAuthCredential cred = PhoneAuthProvider.getCredential(authVerificationID, otp.getText().toString());
                signInUsingCredentials(cred);
            }
            else {
                Toast.makeText(this, "Please Enter a valid number", Toast.LENGTH_SHORT).show();
            }
        });
    }

    Boolean otp_being_retrived = false,op=false;

    private PhoneAuthProvider.ForceResendingToken mResendToken;

    private String authVerificationID;

    private void verifyNumber(String phone, final boolean isResend) {
        findViewById(R.id.progress_bar).setVisibility(View.VISIBLE);
        otp_being_retrived = true;
        Button b=
                findViewById(R.id.send_otp);
        op=true;
        new Handler().postDelayed(()->{
            b.setText("Resend Otp");
            op=false;

                },60000
        );
        PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                otp_being_retrived = false;
                signInUsingCredentials(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                otp_being_retrived = false;
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                if (!isResend) {
                } else {
                    Toast.makeText(getApplicationContext(), "Code Resent", Toast.LENGTH_SHORT).show();
                }
                findViewById(R.id.uihelper).setVisibility(View.VISIBLE);
                findViewById(R.id.progress_bar).setVisibility(View.GONE);
                mResendToken = forceResendingToken;
                authVerificationID = s;
            }
            @Override
            public void onCodeAutoRetrievalTimeOut(@NonNull String s) {
                otp_being_retrived = false;
            }
        };

        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber("+91"+phone)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(mCallBack)
                .build();

        PhoneAuthOptions options2 = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber("+91"+phone)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(mCallBack)
                .setForceResendingToken(mResendToken)
                .build();
        if (!isResend) {
            PhoneAuthProvider.verifyPhoneNumber(options);
        } else {
            PhoneAuthProvider.verifyPhoneNumber(options2);
        }
    }
    private FirebaseAuth mAuth;

    private void signInUsingCredentials(AuthCredential credential) {
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
//                    Toast.makeText(LoginActivity.this,"Success",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this,DashboardActivity.class));

                    finish();
                }
            } else {
                task.getException().printStackTrace();
                Toast.makeText(getApplicationContext(), "Error Signing in", Toast.LENGTH_LONG).show();
            }
        });
    }
}