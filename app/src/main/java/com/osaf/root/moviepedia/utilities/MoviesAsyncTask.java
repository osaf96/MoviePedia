package com.osaf.root.moviepedia.utilities;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.SharedPreferences;

import android.preference.PreferenceManager;

import com.osaf.root.moviepedia.R;
import com.osaf.root.moviepedia.model.Movies;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class MoviesAsyncTask extends AsyncTaskLoader<ArrayList<Movies>>  {


    private final String LOG_TAG = MoviesAsyncTask.class.getSimpleName();

    public MoviesAsyncTask(Context context){
        super(context);
    }

    @Override
    public ArrayList<Movies> loadInBackground() {
        Context context = this.getContext();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String fetchMoviesString = sharedPreferences.getString(context.getResources().getString(R.string.fetch_movie_key), context.getResources().getString(R.string.popularity_item));



        URL url = MoviepediaJsonUtils.buildUrlForMovies(null, fetchMoviesString);

        String response = null;
        try {
            response = MoviepediaJsonUtils.getResponseFromHttpUrl(url);
        } catch (IOException e) {
            e.printStackTrace();
        }


            return MoviepediaJsonUtils.getParseMovieJson(response,context);

    }
    @Override
    protected void onStartLoading() {
        forceLoad();
    }



}


