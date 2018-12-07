package com.example.lenovo.appbuscador;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mercadolibre.android.sdk.ApiResponse;
import com.mercadolibre.android.sdk.Meli;
import com.mercadolibre.android.sdk.MeliException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

import extras.Objeto;

public class ItemScreen extends AppCompatActivity {

    Objeto objeto;
    JSONObject detalle;
    JSONObject descripcion;
    ViewPager viewPagerGallery;
    Bitmap [] imagenes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_screen);
        obtenerDatos(savedInstanceState);
        setupUi();
        getDatostoML();
    }

    private void obtenerDatos(Bundle bundle){
        try {
            objeto = (Objeto) getIntent().getExtras().getSerializable("Item");
        }
        catch (NullPointerException e){
            Log.e("ERROR PARAM BUNDLE", e.toString());
        }
    }

    private void setupUi(){
        TextView tituloItem = findViewById(R.id.tituloItem);
        tituloItem.setText(objeto.getTitle());
    }

    private void getDatostoML(){

        try {
            new ItemScreen.GetAsycTask(0).execute(new ItemScreen.Command() {
                @Override
                ApiResponse executeCommand() {
                    return Meli.get("/items/" + objeto.getId() + "?access_token="+Meli.getCurrentIdentity(getApplicationContext()).getAccessToken().getAccessTokenValue());
                }
            });
        } catch (MeliException e) {
            Log.e("ERROR MELI", e.toString());
        }
        try {
            new ItemScreen.GetAsycTask(1).execute(new ItemScreen.Command() {
                @Override
                ApiResponse executeCommand() {
                    return Meli.get("/items/" + objeto.getId() + "/description?access_token="+Meli.getCurrentIdentity(getApplicationContext()).getAccessToken().getAccessTokenValue());
                }
            });
        } catch (MeliException e) {
            Log.e("ERROR MELI", e.toString());
        }
    }

    public boolean chequearJSONValido(JSONObject jo){
        boolean tr=true;
        try {
            if (jo.getString("message").equals("invalid_token")){
                tr=false;
            }
        } catch (JSONException e) {
            //El access token es válido
        } catch (NullPointerException e){
            e.printStackTrace();
            Log.e("ERROR JSON", e.toString());
        }
        return tr;
    }

    public void setDescripcion() {
        TextView descripcionItem = findViewById(R.id.descripcion);
        if (chequearJSONValido(descripcion)){
            try {
                descripcionItem.setText(descripcion.getString("plain_text"));
            } catch (JSONException e) {//El JSON siempre es válido
                e.printStackTrace();
                Log.e("ERROR JSON", e.toString());
            }
        }
        else
        {
            Toast.makeText(getApplicationContext(),"Caducó la sesión, debe loguearse nuevamente",Toast.LENGTH_SHORT).show();
            finish();
            Intent intent = new Intent(this, LoginOAuth.class);
            startActivity(intent);
        }
    }

    public void setDetalle(){
        JSONArray pictures;
        if (chequearJSONValido(detalle)) {
            try {
                pictures = detalle.getJSONArray("pictures");
                imagenes = new Bitmap[pictures.length()];

                TextView campoCantidad=findViewById(R.id.cantImagenes);
                campoCantidad.setText(pictures.length()+ " fotos");

                TextView campoGarantia=findViewById(R.id.garantia);
                if (detalle.getString("warranty").equals("null"))
                    campoGarantia.setText("No especificada");
                else
                    campoGarantia.setText(detalle.getString("warranty"));

                TextView campoPrecio=findViewById(R.id.precio);
                campoPrecio.setText("$ "+detalle.getString("price"));

                Button botonComprar=findViewById(R.id.btn_comprar);
                botonComprar.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Uri uri = null;
                        try {
                            uri = Uri.parse(detalle.getString("permalink"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("ERROR JSON", e.toString());
                        }
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    }
                });

                for (int i = 0; i < pictures.length(); i++) {
                    ImageView img = findViewById(R.id.imageView);
                    DownloadImageTask task = new DownloadImageTask(img, i);
                    task.execute(((JSONObject) pictures.get(i)).getString("url"));
                }
            } catch (JSONException e) {//El JSON siempre es válido
                e.printStackTrace();
                Log.e("ERROR JSON", e.toString());
            }
        }
        else
        {
            Toast.makeText(getApplicationContext(),"Caducó la sesión, debe loguearse nuevamente",Toast.LENGTH_SHORT).show();
            finish();
            Intent intent = new Intent(this, LoginOAuth.class);
            startActivity(intent);
        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;
        int index;
        public DownloadImageTask(ImageView bmImage,int index) {
            this.bmImage = bmImage;
            this.index=index;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap bmp = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                bmp = BitmapFactory.decodeStream(in);
            } catch (MalformedURLException e) {
                Log.e("ERROR URL", e.getMessage());
                e.printStackTrace();
            } catch (IOException e) {
                Log.e("ERROR IO", e.getMessage());
                e.printStackTrace();
            }
            return bmp;
        }

        @Override
        protected void onPreExecute() {
            findViewById(R.id.pg_loading_gallery).setVisibility(View.VISIBLE);
        }

        protected void onPostExecute(Bitmap result) {
            imagenes[index]=result;
            try {
                if (index == detalle.getJSONArray("pictures").length()-1) {//Si es el último elemento
                    viewPagerGallery = findViewById(R.id.viewPagerGallery);
                    ViewPagerGalleryAdapter viewPagerGalleryAdapter = new ViewPagerGalleryAdapter(getApplicationContext());
                    viewPagerGalleryAdapter.setImages(imagenes);
                    viewPagerGallery.setAdapter(viewPagerGalleryAdapter);
                    findViewById(R.id.pg_loading_gallery).setVisibility(View.GONE);

                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("ERROR JSON", e.toString());
            }
            catch (NullPointerException e){//apiResponse.getContent() falla por no poder obtener el contenido
                Toast.makeText(ItemScreen.this,"Verifique la conexión a internet",Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private class GetAsycTask extends AsyncTask<ItemScreen.Command, Void, ApiResponse> {

        int tipo;

        public GetAsycTask(int tipo) {//Tipo 0: detalle, Tipo 1: descripcion;
            this.tipo = tipo;
        }
        @Override
        protected void onPreExecute() {
            findViewById(R.id.pg_loading).setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(ApiResponse apiResponse) {
            findViewById(R.id.pg_loading).setVisibility(View.GONE);
            try {
                if (tipo==0) {
                    detalle = new JSONObject(apiResponse.getContent());
                    setDetalle();
                }
                else {
                    descripcion=new JSONObject(apiResponse.getContent());
                    setDescripcion();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("ERROR JSON", e.toString());
            } catch (NullPointerException e){
                Log.e("ERROR INTERNET", e.toString());
                Toast.makeText(ItemScreen.this,"Verifique la conexión a internet",Toast.LENGTH_SHORT).show();
                finish();
            }
        }

        @Override
        protected ApiResponse doInBackground(ItemScreen.Command... params) {
            return params[0].executeCommand();
        }
    }

    private abstract class Command {
        abstract ApiResponse executeCommand();
    }
}

