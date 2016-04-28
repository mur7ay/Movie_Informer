package com.example.android.movie_informer;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by shawnmurray on 4/27/16.
 * This Fragment will hold the gridview
 */
public class MoviesFragment extends Fragment {

    static GridView gridview;
    static int width;
    static ArrayList<String> posters;
    static boolean sortByPopularity;
    static final String API_KEY = "292045ea04fcdc083fb7908532fbd563";

    public MoviesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        // Inflate the layout for this fragment

        if (getActivity() != null) {
            ArrayList<String> array = new ArrayList<>();
            ImageAdapter adapter = new ImageAdapter(getActivity(), array, width);
            gridview = (GridView) gridview.findViewById(R.id.gridview);

            gridview.setColumnWidth(2);
            gridview.setAdapter(adapter);

        }

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("Position", "" + position);
                //Intent intent = new Intent(MoviesFragment.this, MovieDetails.class);
                //intent.putExtra("Position", mThumbIds[position]);
                //startActivity(intent);
            }
        });


        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity().setTitle("Most Popular Movies");

        if (isNetworkAvailable()) {
            gridview.setVisibility(gridview.VISIBLE);
            new ImageLoadTask().execute();
        } else {
            Toast.makeText(getContext(), "Sorry, network isn't available", Toast.LENGTH_SHORT).show();
            gridview.setVisibility(GridView.GONE);
        }

    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    // The last parameter is what is returned so an arraylist of strings is return.
    public class ImageLoadTask extends AsyncTask<Void, Void, ArrayList<String>> {

        // String of poster paths
        @Override
        protected ArrayList<String> doInBackground(Void... params) {
            while (true) {
                try {
                    posters = new ArrayList(Arrays.asList(getPathsFromAPI(sortByPopularity)));
                    return posters;
                } catch (Exception e) {
                    continue;
                }

            }
        }

        @Override
        protected void onPostExecute(ArrayList<String> result) {

            if (result != null && getActivity() != null) {
                ImageAdapter imageAdapter = new ImageAdapter(getActivity(), result, width);
                gridview.setAdapter(imageAdapter);
            }
            super.onPostExecute(result);
        }

        // returning a string array.
        public String[] getPathsFromAPI(boolean sortByPopularity) {

            while(true) {
                HttpsURLConnection urlConnection = null;
                BufferedReader reader = null;
                String JSONResult;

                try {
                    String urlString = null;
                    if (sortByPopularity) {
                        urlString = "https://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=" + API_KEY;
                    } else {
                        urlString = "https://api.themoviedb.org/3/discover/movie?sort_by=vote_average.desc&vote_count." +
                                "gte=500&api_key=" + API_KEY;
                    }
                    URL url = new URL(urlString);
                    urlConnection = (HttpsURLConnection)url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();

                    // Reading input stream into a String
                    InputStream inputStream = urlConnection.getInputStream();
                    StringBuffer buffer = new StringBuffer();

                    if (inputStream == null) {
                        return null;
                    }

                    reader = new BufferedReader(new InputStreamReader(inputStream));
                    String line;

                    while ((line = reader.readLine()) != null) {
                        buffer.append(line + "\n");
                    }
                    if (buffer.length() == 0) {
                        return null;
                    }
                    // All of the results will be in this result.
                    JSONResult = buffer.toString();

                    try {
                        return getPathsFromJSON(JSONResult);
                    } catch (JSONException e) {
                        return null;
                    }
                } catch (Exception e) {
                    continue;
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (final IOException e) {

                        }
                    }
                }
            }
        }
        public String[] getPathsFromJSON(String JSONStringParam) throws JSONException {

            JSONObject JSONString = new JSONObject(JSONStringParam);

            JSONArray moviesArray = JSONString.getJSONArray("result");
            String[] result = new String[moviesArray.length()];

            for (int i = 0; i < moviesArray.length(); i++) {
                JSONObject movie = moviesArray.getJSONObject(i);
                String moviePath = movie.getString("poster_path");
                result[i] = moviePath;

            }
            return result;
        }
    }


}
