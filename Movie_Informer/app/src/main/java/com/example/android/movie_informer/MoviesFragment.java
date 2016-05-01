package com.example.android.movie_informer;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by shawnmurray on 4/27/16.
 * This Fragment will hold the gridview
 */
public class MoviesFragment extends Fragment {

    static GridView gridview;
    static int width;
    static ArrayList<String> posters;
    static boolean sortByPop;
    static String API_KEY = "292045ea04fcdc083fb7908532fbd563";

    static PreferenceChangeListener listener;
    static SharedPreferences sharedPreferences;
    static boolean sortByFavorites;
    static ArrayList<String> postersFav = new ArrayList<>();

    public MoviesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        setHasOptionsMenu(true);
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

//        WindowManager wm =(WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
//        Display display = wm.getDefaultDisplay();
//        Point size = new Point();
//        display.getSize(size);
//        if(MainActivity.TABLET)
//        {
//            width = size.x/6;
//        }
//        else width=size.x/3;
        if(getActivity()!=null)
        {
            ArrayList<String> array = new ArrayList<>();
            ImageAdapter adapter = new ImageAdapter(getActivity(),array,width);
            gridview = (GridView)rootView.findViewById(R.id.gridview);

            gridview.setColumnWidth(width);
            gridview.setAdapter(adapter);
        }
        //listen for presses on gridview items
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                System.out.println(position);
            }
        });


        return rootView;
    }

    private class PreferenceChangeListener implements SharedPreferences.OnSharedPreferenceChangeListener {


        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            gridview.setAdapter(null);
            onStart();
        }
    }

    @Override
    public void onStart()
    {
        super.onStart();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        listener = new PreferenceChangeListener();
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener);

        if (sharedPreferences.getString("sortby", "popularity").equals("popylarity")) {
            getActivity().setTitle("Most Popular Movies");

            sortByPop = true;
            sortByFavorites = false;

        } else if (sharedPreferences.getString("sortby", "popularity").equals("rating")) {
            getActivity().setTitle("Highest Rated");

            sortByPop = false;
            sortByFavorites = false;
        } else if (sharedPreferences.getString("sortby", "popularity").equals("favorites")) {
            getActivity().setTitle("Favorite Movies");

            sortByPop = false;
            sortByFavorites = true;
        }

        TextView textView = new TextView(getActivity());
        RelativeLayout layout = (RelativeLayout)getActivity().findViewById(R.id.relativelayout);

        if (sortByFavorites) {
            if (postersFav.size() == 0) {
                textView.setText("You have no favorited movies.");

                if (layout.getChildCount() == 1)
                    layout.addView(textView);
                gridview.setVisibility(GridView.GONE);

            } else {
                gridview.setVisibility(GridView.VISIBLE);
                layout.removeView(textView);
            }

            if (postersFav != null && getActivity() !=  null) {
                ImageAdapter adapter = new ImageAdapter(getActivity(), postersFav, width);
                gridview.setAdapter(adapter);
            }
        }

        else {
            gridview.setVisibility(GridView.VISIBLE);
            layout.removeView(textView);


            if (isNetworkAvailable()) {
                //gridview.setVisibility(GridView.VISIBLE);
                new ImageLoadTask().execute();
            } else {
                TextView textview1 = new TextView(getActivity());
                RelativeLayout layout1 = (RelativeLayout) getActivity().findViewById(R.id.relativelayout);
                textview1.setText("You are not connected to the Internet");
                if (layout1.getChildCount() == 1) {
                    layout1.addView(textview1);
                }
                gridview.setVisibility(GridView.GONE);
            }
        }
    }

    public boolean isNetworkAvailable()
    {
        ConnectivityManager connectivityManager = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo !=null &&activeNetworkInfo.isConnected();
    }
    public class ImageLoadTask extends AsyncTask<Void, Void, ArrayList<String>> {

        @Override
        protected ArrayList<String> doInBackground(Void... params) {
            while(true){
                try{
                    posters = new ArrayList(Arrays.asList(getPathsFromAPI(sortByPop)));
                    return posters;
                }
                catch(Exception e)
                {
                    continue;
                }
            }

        }
        @Override
        protected void onPostExecute(ArrayList<String>result)
        {
            if(result!=null && getActivity()!=null)
            {
                ImageAdapter adapter = new ImageAdapter(getActivity(),result, width);
                gridview.setAdapter(adapter);

            }
        }
        public String[] getPathsFromAPI(boolean sortbypop)
        {
            while(true)
            {
                HttpURLConnection urlConnection = null;
                BufferedReader reader = null;
                String JSONResult;

                try {
                    String urlString = null;
                    if (sortbypop) {
                        urlString = "http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=" + API_KEY;
                    } else {
                        urlString = "http://api.themoviedb.org/3/discover/movie?sort_by=vote_average.desc&vote_count.gte=500&api_key=" + API_KEY;
                    }
                    URL url = new URL(urlString);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();

                    //Read the input stream into a String
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
                    JSONResult = buffer.toString();

                    try {
                        return getPathsFromJSON(JSONResult);
                    } catch (JSONException e) {
                        return null;
                    }
                }catch(Exception e)
                {
                    continue;
                }finally {
                    if(urlConnection!=null)
                    {
                        urlConnection.disconnect();
                    }
                    if(reader!=null)
                    {
                        try{
                            reader.close();
                        }catch(final IOException e)
                        {
                        }
                    }
                }


            }
        }
        public String[] getPathsFromJSON(String JSONStringParam) throws JSONException {

            JSONObject JSONString = new JSONObject(JSONStringParam);

            JSONArray moviesArray = JSONString.getJSONArray("results");
            String[] result = new String[moviesArray.length()];

            for(int i = 0; i<moviesArray.length();i++)
            {
                JSONObject movie = moviesArray.getJSONObject(i);
                String moviePath = movie.getString("poster_path");
                result[i] = moviePath;
            }
            return result;
        }
    }
}
