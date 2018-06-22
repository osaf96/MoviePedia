package com.osaf.root.moviepedia.Adapter;



import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.osaf.root.moviepedia.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import static android.view.View.GONE;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerViewHolder> {
private List<String> mTrailersList = null;
    private Context mContext;

    public TrailerAdapter(Context context) {
        mContext = context;
    }

    @Override
    public TrailerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trailer_item, parent, false);
        return new TrailerViewHolder(mContext, view);
    }

    @Override
    public void onBindViewHolder(TrailerViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return mTrailersList == null ? 0 : mTrailersList.size();
    }

    public void setData(List<String> trailers) {
        mTrailersList = trailers;
        notifyDataSetChanged();
    }

    public String getTrailerUrl(int position) {
        if (mTrailersList != null && mTrailersList.size() > 0 && position < mTrailersList.size()) {
            return mTrailersList.get(position);
        }
        return "";
    }

    class TrailerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        Context context;


        ImageView playIcon;
        ProgressBar loadingIcon;
        ImageView trailerThmb;

        public TrailerViewHolder(Context context, View itemView) {
            super(itemView);

            this.context = context;
            playIcon = itemView.findViewById(R.id.play_icon);
            loadingIcon = itemView.findViewById(R.id.loading_icon);
            trailerThmb = itemView.findViewById(R.id.trailer_thmb);

            itemView.setOnClickListener(this);
        }

        void bind(int position) {
            loadingIcon.setVisibility(View.VISIBLE);
            playIcon.setVisibility(View.INVISIBLE);
            Picasso.with(itemView.getContext())
                    .load(context.getResources().getString(R.string.url_base_youtube_video_tmhb) + mTrailersList.get(position) + "/0.jpg")
                    .error(R.drawable.sad_icon)
                    .into(trailerThmb, new Callback() {
                        @Override
                        public void onSuccess() {
                            loadingIcon.setVisibility(GONE);
                            playIcon.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onError() {
                            playIcon.setVisibility(GONE);
                            loadingIcon.setVisibility(GONE);
                        }
                    })
            ;

        }

        @Override
        public void onClick(View view) {
            String videoPath = mContext.getResources().getString(R.string.url_base_youtube)
                    + mTrailersList.get(this.getAdapterPosition());
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(videoPath));
            mContext.startActivity(intent);
        }
    }
}