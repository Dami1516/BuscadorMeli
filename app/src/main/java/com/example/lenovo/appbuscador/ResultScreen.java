package com.example.lenovo.appbuscador;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.mercadolibre.android.sdk.ApiResponse;
import com.mercadolibre.android.sdk.Meli;
import com.mercadolibre.android.sdk.MeliException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import extras.Objeto;

public class ResultScreen extends AppCompatActivity{

    ArrayList<Objeto> listResult;
    RecyclerView recycler;
    LinearLayoutManager linearLayoutManager;
    String toSearch;
    int offsetMeLi=0;
    int limitMeLi=10;
    private int previousTotal = 0;
    private boolean loading = true;
    private int visibleThreshold = 3;
    int firstVisibleItem, visibleItemCount, totalItemCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result_screen);

        toSearch=getIntent().getExtras().getString("Search");
        listResult=new ArrayList<>();
        try {
            new GetAsycTask().execute(new Command() {
                @Override
                ApiResponse executeCommand() {
                    return Meli.get("/sites/MLA/search?q="+toSearch+"&limit="+limitMeLi);
                }
            });
        } catch (MeliException e) {
            Log.e("ERROR MELI",e.toString());
        }
    }

    public void agregarResultados(JSONArray resultados){
        try {
            for (int i=0;i<resultados.length();i++) {
                JSONObject objeto = resultados.getJSONObject(i);
                listResult.add(new Objeto(objeto.optString("title"),objeto.getString("price"),objeto.getString("thumbnail"),objeto.getString("id"),objeto));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("ERROR JSON",e.toString());
        }
    }

    public void setearResultados(JSONArray resultados){
        String title="Resultados para <b>"+toSearch+"</b>";
        TextView tvTitle=findViewById(R.id.tituloResult);
        tvTitle.setText(Html.fromHtml(title));
        try {
            for (int i=0;i<resultados.length();i++) {
                JSONObject objeto = resultados.getJSONObject(i);
                listResult.add(new Objeto(objeto.optString("title"),objeto.getString("price"),objeto.getString("thumbnail"),objeto.getString("id"),objeto));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("ERROR JSON",e.toString());
        } catch (NullPointerException e){
            Log.e("ERROR INTERNET",e.toString());
            Toast.makeText(ResultScreen.this,"Verifique la conexión a internet",Toast.LENGTH_SHORT).show();
            finish();
        }
        recycler=findViewById(R.id.recyclerId);
        recycler.setHasFixedSize(true);
        linearLayoutManager=new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recycler.setLayoutManager(linearLayoutManager);
        ResultAdapter adapter=new ResultAdapter(listResult, new ClickListener() {
            @Override public void onPositionClicked(int position) {
                Intent intent = new Intent(getApplicationContext(), ItemScreen.class);
                intent.putExtra("Item",listResult.get(position));
                startActivity(intent);
            }
        });
        recycler.setAdapter(adapter);
        recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                visibleItemCount = recycler.getChildCount();
                totalItemCount = linearLayoutManager.getItemCount();
                firstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();

                if (loading) {
                    if (totalItemCount > previousTotal) {
                        loading = false;
                        previousTotal = totalItemCount;
                    }
                }
                if (!loading && (totalItemCount - visibleItemCount)
                        <= (firstVisibleItem + visibleThreshold)) {
                    try {
                        new GetAsycTask().execute(new Command() {
                            @Override
                            ApiResponse executeCommand() {
                                return Meli.get("/sites/MLA/search?q="+toSearch+"&offset="+offsetMeLi+"&limit="+limitMeLi);
                            }
                        });
                    } catch (MeliException e) {
                        Log.e("ERROR MELI",e.toString());
                        Toast.makeText(ResultScreen.this,"Hubo un error, intente nuevamente por favor",Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    loading = true;
                }
            }
        });
    }

    private class GetAsycTask extends AsyncTask<Command, Void, ApiResponse> {
        @Override
        protected void onPreExecute() {
            findViewById(R.id.pg_loading).setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(ApiResponse apiResponse) {
            findViewById(R.id.pg_loading).setVisibility(View.GONE);
                JSONObject reader;
                try {
                    reader = new JSONObject(apiResponse.getContent());
                    JSONArray resultado = reader.getJSONArray("results");
                    if (offsetMeLi == 0){
                        setearResultados(resultado);
                    }
                    else
                        agregarResultados(resultado);
                    offsetMeLi += limitMeLi;
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("ERROR JSON",e.toString());
                } catch (NullPointerException e){//apiResponse.getContent() falla por no poder obtener el contenido
                    Log.e("ERROR INTERNET",e.toString());
                    Toast.makeText(ResultScreen.this,"Verifique la conexión a internet",Toast.LENGTH_SHORT).show();
                    finish();
                }
        }

        @Override
        protected ApiResponse doInBackground(Command... params) {
            return params[0].executeCommand();
        }
    }

    private abstract class Command {
        abstract ApiResponse executeCommand();
    }
}
