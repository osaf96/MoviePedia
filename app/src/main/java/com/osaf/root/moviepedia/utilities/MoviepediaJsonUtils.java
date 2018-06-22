package com.osaf.root.moviepedia.utilities;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import com.osaf.root.moviepedia.BuildConfig;
import com.osaf.root.moviepedia.model.Movies;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

public class MoviepediaJsonUtils {
    private static final String SCHEME = "https";
    private static final String AUTHORITY = "api.themoviedb.org";
    private static final String PATH_1 = "3";
    private static final String PATH_2 = "movie";
    private static final String MY_API_KEY = BuildConfig.API_KEY;
    private static final String KEY_QUERY = "api_key";

    public static ArrayList<Movies> getParseMovieJson(String jsonMovies, Context context) {

        final String BACKDROP_URL = "https://image.tmdb.org/t/p/w1280/";
        final String IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w500/";
       if(jsonMovies != null) {
           try {
               JSONObject movieJson = new JSONObject(jsonMovies);
               JSONArray movieArray = movieJson.getJSONArray("results");

               ArrayList<Movies> movieArrayList = new ArrayList<>();

               for (int i = 0; i < movieArray.length(); i++) {

                   JSONObject movieObject = movieArray.getJSONObject(i);

                   String id = movieObject.getString("id");
                   String title = movieObject.getString("title");
                   String release_date = movieObject.getString("release_date");
                   String poster_path = movieObject.getString("poster_path");
                   String backdrop = movieObject.getString("backdrop_path");
                   String vote_average = movieObject.getString("vote_average");
                   String overview = movieObject.getString("overview");


                   Movies movies = new Movies(id, title, release_date,
                           IMAGE_BASE_URL + poster_path, BACKDROP_URL + backdrop, vote_average, overview);

                   movieArrayList.add(movies);
               }

               return movieArrayList;
           } catch (final JSONException e) {
               e.printStackTrace();
           }
       }
       return null;
    }
    public static URL buildUrlForMovies(String id, String searchType) {
        Uri.Builder urlBuilder = new Uri.Builder();
        urlBuilder.scheme(SCHEME);
        urlBuilder.authority(AUTHORITY);
        urlBuilder.appendPath(PATH_1);
        urlBuilder.appendPath(PATH_2);
        if (id != null) {
            urlBuilder.appendPath(id);
        }
        urlBuilder.appendPath(searchType);
        urlBuilder.appendQueryParameter(KEY_QUERY, MY_API_KEY);
        URL url = null;
        try {
            url = new URL(urlBuilder.build().toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();
            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");
            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

}
