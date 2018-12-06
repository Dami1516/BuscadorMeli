package com.example.lenovo.appbuscador;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class ViewPagerGalleryAdapter extends PagerAdapter {

    private Context context;
    private LayoutInflater layoutInflater;
    private Bitmap[] images=new Bitmap[5];

    public ViewPagerGalleryAdapter(Context context) {
        this.context = context;
    }

    public void setImages(Bitmap[] images){
        this.images=images;
    }

    @Override
    public int getCount() {
        return images.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view==o;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.item_gallery,null);
        ImageView imageView=(ImageView) view.findViewById(R.id.imageView);
        imageView.setImageBitmap(images[position]);

        ViewPager vp = (ViewPager) container;
        vp.addView(view,0);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        ViewPager vp= (ViewPager)container;
        View view = (View) object;
        vp.removeView(view);
    }
}
