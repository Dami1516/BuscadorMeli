package com.example.lenovo.appbuscador;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.mercadolibre.android.sdk.Meli;

import java.util.Date;

public class LoginOAuth extends AppCompatActivity{


    // Request code used to receive callbacks from the SDK
    private static final int REQUEST_CODE = 999;
    private static final String ACCESS_TOKEN_KEY = "ACCESS_TOKEN_KEY";
    private static final String EXPIRES_IN_KEY = "EXPIRES_IN_KEY";

    private Button botonVolver;
    private int num=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen);
        borrarAccessToken(); //Entro a esta clase solo si se vencio el tiempo de expiracion o si nunca me loguie.

        if(savedInstanceState == null) {
            Meli.startLogin(this, REQUEST_CODE);
        }
    }

    private void borrarAccessToken(){
        SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences(getApplicationContext().getPackageName() + ".access_token", Context.MODE_PRIVATE).edit();
        editor.clear();
        editor.apply();
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
        myEditor.putString("AccessToken", Meli.getCurrentIdentity(getApplicationContext()).getAccessToken().getAccessTokenValue());
        Date now = new Date();
        long ut3 = (now.getTime() / 1000L) + Meli.getCurrentIdentity(getApplicationContext()).getAccessToken().getAccessTokenLifetime();
        myEditor.putLong("ValidTo", ut3);
        myEditor.apply();
    }




    private void processLoginProcessWithError() {
        Toast.makeText(LoginOAuth.this, "Oooops, algo ocurrio en el proceso de logueo", Toast.LENGTH_SHORT).show();
    }
}