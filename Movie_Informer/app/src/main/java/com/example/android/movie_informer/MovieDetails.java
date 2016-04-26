package com.example.android.movie_informer;

import android.os.Bundle;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class MovieDetails extends MainActivity {

    ImageView imageView1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        imageView1 = (ImageView)findViewById(R.id.imageView_details);
        int position = getIntent().getIntExtra("Position", 1);

        Picasso.with(this).load(position).into(imageView1);

    }

}
