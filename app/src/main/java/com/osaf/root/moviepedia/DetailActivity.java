package com.osaf.root.moviepedia;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.osaf.root.moviepedia.Adapter.ReviewAdapter;
import com.osaf.root.moviepedia.Adapter.TrailerAdapter;
import com.osaf.root.moviepedia.data.MovieContract;
import com.osaf.root.moviepedia.model.Movies;
import com.osaf.root.moviepedia.utilities.MoviepediaJsonUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class DetailActivity extends AppCompatActivity implements View.OnClickListener,android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor>{

    String mMovieDetailKey;
    String mScrollPositionKey;
    NestedScrollView mScrollView;

    private ReviewAdapter mReviewAdapter;
    private TrailerAdapter mTrailerAdapter;
    private String mMovieTitle;
    private String mMovieId;
    private String mMoviePlot;
    private String mMovieReaseDate;
    private String mMovieAverageVote;
    private String mMoviePosterPath;
    private boolean mIsFavorite;
    private String mBackdrop;
    private int[] mScrollPosition;
    private static Movies movies ;

    TextView title;
    TextView releaseDate;
    TextView vote;
    TextView overView;

    private static final String API_KEY = BuildConfig.API_KEY;
    RecyclerView mReviewsRecyclerView;
    TextView reviews_error;
    ProgressBar mReviewsLoading;
    RecyclerView mTrailersRecyclerView;
    ProgressBar mTrailersLoading;
    TextView trailer_error;
    CollapsingToolbarLayout collapsingToolbar;
    FloatingActionButton fabShare;
    FloatingActionButton fabFavorite;
    private Toast mFavoritesToast;
    ImageView poster;
    ImageView backdrop;
    Toolbar toolbar;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        mScrollPositionKey = getString(R.string.scroll_position_key);
        mMovieDetailKey = getString(R.string.movie_detail_Key);
        mScrollView = findViewById(R.id.scroll_view);
        movies = getIntent().getParcelableExtra(mMovieDetailKey);

        toolbar= findViewById(R.id.toolbar);
        collapsingToolbar = findViewById(R.id.toolbar_layout);

        fabFavorite = findViewById(R.id.fab_favorite);
        fabShare = findViewById(R.id.fab_share);
        if (movies != null) {
            mMovieTitle = movies.getTitle();
            mMovieId = movies.getID();
            mMoviePlot = movies.getOverview();
            mMovieReaseDate = movies.getReleaseDate();
            mMovieAverageVote = movies.getVote();
            mMoviePosterPath = movies.getPosterPath();
            mBackdrop = movies.getmBackdrop();
        }

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        poster = findViewById(R.id.poster_IV);
        title = findViewById(R.id.title_TV);
         releaseDate = findViewById(R.id.relaesedate_TV);
         vote = findViewById(R.id.vote_TV);
         overView = findViewById(R.id.overview_TV);
        backdrop = findViewById(R.id.image_id);
        mReviewsRecyclerView = findViewById(R.id.detail_reviews_recycler);
        reviews_error = findViewById(R.id.textview_error_reviews);
        mReviewsLoading = findViewById(R.id.loading_reviews);
        mTrailersRecyclerView = findViewById(R.id.detail_trailers_recycler);
        mTrailersLoading = findViewById(R.id.loading_trailers);
        trailer_error = findViewById(R.id.textview_error_trailer);

        if (toolbar != null){
            toolbar.setTitle(R.string.app_name);
        }



        collapsingToolbar.setExpandedTitleColor(getResources().getColor(R.color.transparent));

        if (movies != null) {
            collapsingToolbar.setTitle(movies.getTitle());
            collapsingToolbar.setCollapsedTitleTextColor(Color.WHITE);
        }



        fabFavorite.setOnClickListener(this);

        fabShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String videoPath = getResources().getString(R.string.url_base_youtube)
                        + mTrailerAdapter.getTrailerUrl(0);
                ShareCompat.IntentBuilder.from(DetailActivity.this)
                        .setType("text/plain")
                        .setChooserTitle(R.string.share)
                        .setText(getString(R.string.share_text) + " " + movies.getTitle() + "? " + videoPath)
                        .startChooser();
            }
        });

        getSupportLoaderManager().initLoader(1, null, this);
        updateTheUI();

        mTrailersRecyclerView.setHasFixedSize(true);
        mTrailerAdapter = new TrailerAdapter(this);
        mTrailersRecyclerView.setAdapter(mTrailerAdapter);
        new TrailerDownloaderTask().execute();

        mReviewAdapter = new ReviewAdapter();
        mReviewsRecyclerView.setAdapter(mReviewAdapter);
        new ReviewsDownloaderTask().execute();


    }

    private void updateTheUI() {
        title.setText(mMovieTitle);
        releaseDate.setText(mMovieReaseDate);
        vote.setText(mMovieAverageVote);
        overView.setText(mMoviePlot);

        Picasso.with(this).load(mMoviePosterPath)
                .into(poster);

        Picasso.with(this).load(mBackdrop)
                .error(R.drawable.sam).into(backdrop);
    }
    @Override
    public void onClick(View view) {
            if (mIsFavorite) {
                removeFromFavorites();
                fabFavorite.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_favorite_border_white));
            }
            else {
                addToFavorites();
                fabFavorite.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_favorite_white_24px));
            }
        getSupportLoaderManager().initLoader(1, null, this);
    }

    private void removeFromFavorites() {
        getContentResolver().delete(MovieContract.CONTENT_URI,
                MovieContract.MovieEntry.COLUMN_ID + "=?", new String[]{mMovieId});
        if (mFavoritesToast!= null){
            mFavoritesToast.cancel();
        }
        mFavoritesToast= Toast.makeText(this, "Movie removed from favorites", Toast.LENGTH_LONG);
        mFavoritesToast.show();
    }

    private void addToFavorites() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MovieContract.MovieEntry.COLUMN_ID, mMovieId);
        contentValues.put(MovieContract.MovieEntry.COLUMN_NAME, mMovieTitle);
        contentValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, mMoviePlot);
        contentValues.put(MovieContract.MovieEntry.COLUMN_POSTER, mMoviePosterPath);
        contentValues.put(MovieContract.MovieEntry.COLUMN_BACKDROP,mBackdrop);
        contentValues.put(MovieContract.MovieEntry.COLUMN_RATING, mMovieAverageVote);
        contentValues.put(MovieContract.MovieEntry.COLUMN_RELEASE, mMovieReaseDate);
        getContentResolver().insert(MovieContract.CONTENT_URI, contentValues);
        if (mFavoritesToast!= null){
            mFavoritesToast.cancel();
        }
        mFavoritesToast= Toast.makeText(this, "Movie added to favorites", Toast.LENGTH_LONG);
        mFavoritesToast.show();
    }
    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri FAVORITES_URI = MovieContract.CONTENT_URI;
        return new CursorLoader(this, FAVORITES_URI, null,
                null, null, null);
    }
    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader
            , Cursor cursor) {
        ArrayList<String> movieIds = new ArrayList<>();
        if (cursor != null) {
            cursor.moveToPosition(-1);
            try {
                while (cursor.moveToNext()) {
                    String movieId = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_ID));

                    movieIds.add(movieId);
                }
            } finally {
                mIsFavorite = movieIds.contains(mMovieId);
            }
        }
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader loader) {

    }

    class TrailerDownloaderTask extends AsyncTask<Void, Void, Boolean> {

        private List<String> trailersList = new ArrayList<>();

        @Override
        protected void onPreExecute() {
            mTrailersLoading.setVisibility(View.VISIBLE);
            trailer_error.setVisibility(View.INVISIBLE);
        }

        @Override
        protected Boolean doInBackground(Void ... params) {
            URL url;
            try {
                url = new URL(getResources().getString(R.string.url_base_movie)
                        + movies.getID()
                        + getResources().getString(R.string.url_part_trailer)
                        + API_KEY
                );
                String response = MoviepediaJsonUtils.getResponseFromHttpUrl(url);
                //
                JSONObject jsonObject = new JSONObject(response);
                JSONArray results = jsonObject.getJSONArray("results");
                for (int i=0; i < results.length(); i++){
                    JSONObject item = results.getJSONObject(i);

                    //Build item
                    String key = item.getString("key");
                    trailersList.add(key);
                }

            } catch (JSONException | IOException e) {
                e.printStackTrace();
                return false;
            }

            return true;

        }

        @Override
        protected void onPostExecute(Boolean success) {
            mTrailersLoading.setVisibility(View.INVISIBLE);
            if (success) {
                mTrailerAdapter.setData(trailersList);
            } else {
                mTrailerAdapter.setData(new ArrayList<String>());
                trailer_error.setVisibility(View.VISIBLE);
            }
        }
    }


    class ReviewsDownloaderTask extends AsyncTask<Void, Void, Boolean> {

        private List<String> reviewsList = new ArrayList<>();

        @Override
        protected void onPreExecute() {
            mReviewsLoading.setVisibility(View.VISIBLE);
            reviews_error.setVisibility(View.INVISIBLE);
        }

        @Override
        protected Boolean doInBackground(Void ... params) {
            URL url;
            try {
                url = new URL(getResources().getString(R.string.url_base_movie)
                        + movies.getID()
                        + getResources().getString(R.string.url_part_reviews)
                        + API_KEY
                );
                String response = MoviepediaJsonUtils.getResponseFromHttpUrl(url);
                //
                JSONObject jsonObject = new JSONObject(response);
                JSONArray results = jsonObject.getJSONArray("results");
                for (int i=0; i < results.length(); i++){
                    JSONObject item = results.getJSONObject(i);

                    //Build item
                    String key = item.getString("content");
                    reviewsList.add(key);
                }

            } catch (JSONException | IOException e) {
                e.printStackTrace();
                return false;
            }

            return true;

        }

        @Override
        protected void onPostExecute(Boolean success) {
            mReviewsLoading.setVisibility(View.INVISIBLE);
            if (success) {
                mReviewAdapter.setData(reviewsList);
            } else {
                mReviewAdapter.setData(new ArrayList<String>());
                reviews_error.setVisibility(View.VISIBLE);
            }
        }
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putIntArray(mScrollPositionKey,
                new int[]{ mScrollView.getScrollX(), mScrollView.getScrollY()});

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mScrollPosition  = savedInstanceState.getIntArray(mScrollPositionKey);

    }
    // this is only called once the reviews and trailers are both loaded
    void restoreScrollPosition(){
        if(mScrollPosition != null)
            mScrollView.post(new Runnable() {
                public void run() {
                    mScrollView.scrollTo(mScrollPosition[0], mScrollPosition[1]);
                }
            });
    }

}
