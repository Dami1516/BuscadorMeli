package com.example.lenovo.appbuscador;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.mercadolibre.android.sdk.Identity;
import com.mercadolibre.android.sdk.Meli;

import java.util.Date;

public class MainScreen extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_screen);

        // prepare the UI.
        setupUi();

        // Set SDK to log events
        Meli.setLoggingEnabled(true);

        // Initialize the MercadoLibre SDK
        Meli.initializeSDK(this.getApplicationContext());
    }

    @Override
    protected void onResume(){
        super.onResume();
        setupUi();
    }


    private void setupUi() {

        Button btnSearch = (Button) findViewById(R.id.btn_search);
        btnSearch.setOnClickListener(this);

        if (!esValidoAC())
            btnSearch.setText("Primero debe loguearse");
        else
            btnSearch.setText("Buscar");
    }

    private boolean esValidoAC(){
        SharedPreferences myPreferences = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        long validTo = myPreferences.getLong("ValidTo",0);
        Date now = new Date();
        long ut3 = (now.getTime() / 1000L);
        return (ut3<validTo);
    }

    @Override
    public void onClick(View v) {
        if (!esValidoAC()){
            Intent intentLogin = new Intent(this, LoginOAuth.class);
            startActivity(intentLogin);
        }
        else {
            Intent intent = new Intent(this, ResultScreen.class);
            EditText toSearch = findViewById(R.id.textBusqueda);
            intent.putExtra("Search", toSearch.getText().toString());
            startActivity(intent);
        }
    }


    private String getUserID() {
        Identity identity = Meli.getCurrentIdentity(getApplicationContext());
        if (identity == null) {
            return null;
        } else {
            return identity.getUserId();
        }
    }
}
