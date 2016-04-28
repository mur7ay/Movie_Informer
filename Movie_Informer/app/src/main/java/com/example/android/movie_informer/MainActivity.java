package com.example.android.movie_informer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Replacing current fragment with new fragment not continually adding new fragments.
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.gridview, new MoviesFragment()).commit();
        }

    }


}
