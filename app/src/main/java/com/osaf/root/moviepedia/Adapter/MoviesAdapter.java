package com.osaf.root.moviepedia.Adapter;

import android.content.Context;
import android.graphics.Movie;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.osaf.root.moviepedia.R;
import com.osaf.root.moviepedia.model.Movies;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MoviesViewHolder> {

    private final Context mContext;
    private ArrayList<Movies> mDataSource;
    private final ListItemClickListener mListItemClickListener;


    public MoviesAdapter(Context context, ArrayList<Movies> moviesPostersList,ListItemClickListener listItemClickListener) {
        mContext = context;
        mDataSource = moviesPostersList;
        this.mListItemClickListener = listItemClickListener;
    }

    public void setDataSource(ArrayList<Movies> dataSource) {
        mDataSource = dataSource;
        notifyDataSetChanged();
    }
    class MoviesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView mImageView;

        public MoviesViewHolder(View view) {
            super(view);
            mImageView = view.findViewById(R.id.poster_image);
            view.setOnClickListener(this);
        }
        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            mListItemClickListener.onMovieItemClick(mDataSource.get(position));
        }
    }
    public interface ListItemClickListener {
        void onMovieItemClick(Movies movie);
    }



    @Override
    public MoviesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mview = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.poster_item, parent, false);
        return new MoviesViewHolder(mview);
    }


    @Override
    public void onBindViewHolder(final MoviesViewHolder holder, final int position) {
        Movies details = mDataSource.get(position);
        String posterpath = details.getPosterPath();
        Picasso.with(mContext).load(posterpath)
                .into(holder.mImageView);

    }


    @Override
    public int getItemCount() {
        return mDataSource.size();
    }
}
