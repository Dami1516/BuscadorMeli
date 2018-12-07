package com.example.lenovo.appbuscador;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.util.ArrayList;

import extras.Objeto;

public class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.ViewHolderResult> {

    private ArrayList<Objeto> listResult;
    private final ClickListener listener;

    public ResultAdapter(ArrayList<Objeto> listResult, ClickListener listener) {
        this.listResult = listResult;
        this.listener=listener;
    }

    @NonNull
    @Override
    public ViewHolderResult onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view=LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_result_list,null,false);
        return new ViewHolderResult(view,listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderResult viewHolderResult, int i) {
        viewHolderResult.asignarDatos(listResult.get(i));
    }

    @Override
    public int getItemCount() {
        return listResult.size();
    }

    protected class ViewHolderResult extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView resultId;
        TextView price;
        ImageView thumbnail;
        Button verMasButton;
        private WeakReference<ClickListener> listenerRef;

        protected ViewHolderResult(@NonNull View itemView, ClickListener listener) {
            super(itemView);
            listenerRef = new WeakReference<>(listener);
            resultId=itemView.findViewById(R.id.idResult);
            price=itemView.findViewById(R.id.price);
            thumbnail=itemView.findViewById(R.id.thumbnail);
            itemView.setOnClickListener(this);
            verMasButton=itemView.findViewById(R.id.buttonVer);
            verMasButton.setOnClickListener(this);
        }

        protected void asignarDatos(Objeto o) {
            resultId.setText(o.getTitle());
            price.setText("$ "+o.getPrice());
            DownloadImageTask task = new DownloadImageTask(thumbnail);
            task.execute(o.getThumbnail());
        }

        @Override
        public void onClick(View v) {
            listenerRef.get().onPositionClicked(getAdapterPosition());
        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;
        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
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
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
