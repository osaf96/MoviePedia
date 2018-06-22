package com.osaf.root.moviepedia;

import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.osaf.root.moviepedia.Adapter.MoviesAdapter;
import com.osaf.root.moviepedia.data.MovieContract;
import com.osaf.root.moviepedia.model.Movies;
import com.osaf.root.moviepedia.utilities.MoviesAsyncTask;

import java.util.ArrayList;
public class MainActivity extends AppCompatActivity implements MoviesAdapter.ListItemClickListener,SharedPreferences.OnSharedPreferenceChangeListener, LoaderManager.LoaderCallbacks<Cursor> {


    private static final int MOVIE_LOADER = 20;
    private ArrayList<Movies> mMovies;
    private MoviesAdapter mMovieAdapter;

    ProgressBar mProgressBar;
    TextView mNoNetworkTextView;
    RecyclerView moviesRecyclerView;
    String mMovieDetailKey;
    String mRecyclerPositionKey;
    GridLayoutManager mGridLayoutManager;
    Parcelable listState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        mMovies = new ArrayList<>();

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            mGridLayoutManager = new GridLayoutManager(this,2, LinearLayoutManager.VERTICAL,false);
        }else{
            mGridLayoutManager = new GridLayoutManager(this, 4,LinearLayoutManager.VERTICAL,false);
        }
        mNoNetworkTextView = findViewById(R.id.error_textview);
        mProgressBar = findViewById(R.id.progressBar);
        moviesRecyclerView = findViewById(R.id.rv_movies);
        mMovieDetailKey = getString(R.string.movie_detail_Key);
        mRecyclerPositionKey = getString((R.string.recycler_position_key));
        moviesRecyclerView.setHasFixedSize(true);
        moviesRecyclerView.setLayoutManager(mGridLayoutManager);
        mMovieAdapter = new MoviesAdapter(this, mMovies, this);
        moviesRecyclerView.setAdapter(mMovieAdapter);
        String fetchMoviesString = sharedPreferences.getString(this.getResources().getString(R.string.fetch_movie_key), this.getResources().getString(R.string.popularity_item));
        String favorites = (this.getResources().getString(R.string.fetch_favorites));
       // {
            if (fetchMoviesString.equals(favorites)) {
                mMovies = new ArrayList<>();
                getSupportLoaderManager().initLoader(1, null, this);
            } else if(isNetworkAvailable(this)) {
                fetchJsonForMovies();
            } else{
           mNoNetworkTextView.setVisibility(View.VISIBLE);
           Toast.makeText(this,"No Network Connection",Toast.LENGTH_LONG).show();
        }

    }

    //Inflate Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.poster_fragment, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_setting) {
            Intent startSettingsActivity = new Intent(this, SettingActivity.class);
            startActivity(startSettingsActivity);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    //Based on a stackoverflow snippet
    private static boolean isNetworkAvailable(Context ctx) {

        ConnectivityManager connMgr = (ConnectivityManager)
                ctx.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        return networkInfo != null && networkInfo.isConnected();
    }


    private void fetchJsonForMovies() {
        android.app.LoaderManager loaderManager = getLoaderManager();
        Loader<String> loader = loaderManager.getLoader(MOVIE_LOADER);
        if (loader == null) {

            loaderManager.initLoader(MOVIE_LOADER, null, movieLoaderListener);
        } else {

            loaderManager.restartLoader(MOVIE_LOADER, null, movieLoaderListener);
        }
    }

    public android.app.LoaderManager.LoaderCallbacks<ArrayList<Movies>> movieLoaderListener
            = new android.app.LoaderManager.LoaderCallbacks<ArrayList<Movies>>() {
        @Override
        public Loader<ArrayList<Movies>> onCreateLoader(int i, Bundle bundle) {
            mProgressBar.setVisibility(View.VISIBLE);
            mNoNetworkTextView.setVisibility(View.GONE);
            return new MoviesAsyncTask(MainActivity.this);
        }

        @Override
        public void onLoadFinished(Loader<ArrayList<Movies>> loader, ArrayList<Movies> movies) {
            if (movies != null) {
                mMovies = movies;
                mMovieAdapter.setDataSource(mMovies);
                if (listState != null) {
                    mGridLayoutManager.onRestoreInstanceState(listState);
                }
            } else {
                mNoNetworkTextView.setVisibility(View.VISIBLE);
            }
            mProgressBar.setVisibility(View.GONE);

        }

        @Override
        public void onLoaderReset(Loader<ArrayList<Movies>> loader) {
            mMovies = null;
        }
    };



    @Override
    public void onMovieItemClick(Movies movie) {
        Intent detailIntent = new Intent(this, DetailActivity.class);
        detailIntent.putExtra(mMovieDetailKey, movie);
        startActivity(detailIntent);


    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if (s.equals(getResources().getString(R.string.fetch_movie_key))) {
            if (s.equals(this.getResources().getString(R.string.fetch_favorites))) {
                mMovies = new ArrayList<>();
                mMovieAdapter.setDataSource(mMovies);
            } else {
                fetchJsonForMovies();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //clean up unregister pref listener.
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }


    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri NAME_URI = MovieContract.CONTENT_URI;
        return new CursorLoader(this, NAME_URI, null,
                null, null, null);
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader
            , Cursor cursor) {
        if (cursor != null) {
            cursor.moveToPosition(-1);
            try {
                while (cursor.moveToNext()) {
                    String title = cursor.getString(cursor.getColumnIndex
                            (MovieContract.MovieEntry.COLUMN_NAME));
                    String movieId = cursor.getString(cursor.getColumnIndex
                            (MovieContract.MovieEntry.COLUMN_ID));
                    String overview = cursor.getString(cursor.getColumnIndex
                            (MovieContract.MovieEntry.COLUMN_OVERVIEW));
                    String date = cursor.getString(cursor.getColumnIndex
                            (MovieContract.MovieEntry.COLUMN_RELEASE));
                    String rating = cursor.getString(cursor.getColumnIndex
                            (MovieContract.MovieEntry.COLUMN_RATING));
                    String posterPath = cursor.getString(cursor.getColumnIndex
                            (MovieContract.MovieEntry.COLUMN_POSTER));
                    String backdrop = cursor.getString(cursor.getColumnIndex
                            (MovieContract.MovieEntry.COLUMN_BACKDROP));
                    Movies movie = new Movies(movieId, title, date, posterPath,backdrop, rating, overview);
                    mMovies.add(movie);
                }
            } finally {
                mMovieAdapter.setDataSource(mMovies);
                mGridLayoutManager.onRestoreInstanceState(listState);
            }
        }
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader loader) {

    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(mRecyclerPositionKey,
                moviesRecyclerView.getLayoutManager().onSaveInstanceState());
    }

    @Override
    protected void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);
        listState = state.getParcelable(mRecyclerPositionKey);
        System.out.println(listState);
        moviesRecyclerView.getLayoutManager().onRestoreInstanceState(listState);
    }

}

