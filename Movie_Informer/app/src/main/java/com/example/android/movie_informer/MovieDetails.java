package com.example.android.movie_informer;

import android.os.Bundle;

import com.squareup.picasso.Picasso;

public class MovieDetails extends MainActivity {

    //ImageView imageView1 = (ImageView)findViewById(R.id.imageView1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        int position = getIntent().getIntExtra("position", -1);

        if (position != -1) {
            Picasso.with(MovieDetails.this).load(MainActivity.mThumbIds[position]).into(imageView);
        } else {
            Picasso.with(MovieDetails.this).load(MainActivity.mThumbIds[position]).into(imageView);
        }
    }

}
