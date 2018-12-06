package com.example.lenovo.appbuscador;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.mercadolibre.android.sdk.Meli;

import java.util.Date;

public class LoginOAuth extends AppCompatActivity {


    // Request code used to receive callbacks from the SDK
    private static final int REQUEST_CODE = 999;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState == null) {
            Meli.startLogin(this, REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                processLoginProcessCompleted();
            } else {
                processLoginProcessWithError();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
        finish();
    }


    private void processLoginProcessCompleted() {
        SharedPreferences myPreferences = PreferenceManager.getDefaultSharedPreferences(this.getBaseContext());
        SharedPreferences.Editor myEditor = myPreferences.edit();
        myEditor.putString("AccessToken", Meli.getCurrentIdentity(getBaseContext()).getAccessToken().getAccessTokenValue());
        Date now = new Date();
        long ut3 = (now.getTime() / 1000L) + Meli.getCurrentIdentity(getBaseContext()).getAccessToken().getAccessTokenLifetime();
        myEditor.putLong("ValidTo", ut3);
        myEditor.commit();
    }




    private void processLoginProcessWithError() {
        Toast.makeText(LoginOAuth.this, "Oooops, algo ocurrio en el proceso de logueo", Toast.LENGTH_SHORT).show();
    }
}