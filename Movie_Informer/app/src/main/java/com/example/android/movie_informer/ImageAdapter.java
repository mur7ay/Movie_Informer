package com.example.android.movie_informer;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ImageAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<String> array;
    private int width;

    public ImageAdapter(Context context, ArrayList<String> paths, int x) {
        mContext = context;
        array = paths;
        width = x;
    }

    @Override
    public int getCount() {
        return array.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;

        if (convertView == null) {
            // No view so creating a new one.
            imageView = new ImageView(mContext);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(0, 0, 0, 0);
        } else {
            // Using recycled view object.
            imageView = (ImageView)convertView;
        }

        Picasso.with(mContext).load("http://image.tmdb.org/t/p/w185/" + array.get(position)).into(imageView);
        return imageView;
    }
}
