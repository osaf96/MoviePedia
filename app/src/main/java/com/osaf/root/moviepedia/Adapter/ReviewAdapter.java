package com.osaf.root.moviepedia.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.osaf.root.moviepedia.R;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {
    private List<String> mReviewsList = null;

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_item, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return mReviewsList == null ? 0 : mReviewsList.size();
    }

    public void setData(List<String> reviews){
        mReviewsList = reviews;
        notifyDataSetChanged();
    }

    class ReviewViewHolder extends RecyclerView.ViewHolder {

        TextView reviewText;

        public ReviewViewHolder(View itemView) {
            super(itemView);
            reviewText = itemView.findViewById(R.id.review_text);
        }

        void bind(int position) {
            reviewText.setText(mReviewsList.get(position));
        }
    }
}
