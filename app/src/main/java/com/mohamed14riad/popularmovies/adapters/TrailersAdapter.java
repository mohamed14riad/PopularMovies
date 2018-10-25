package com.mohamed14riad.popularmovies.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.mohamed14riad.popularmovies.R;
import com.mohamed14riad.popularmovies.models.Trailer;

import java.util.ArrayList;
import java.util.List;

public class TrailersAdapter extends RecyclerView.Adapter<TrailersAdapter.TrailerViewHolder> {
    private Context context;
    private OnTrailerClickListener onTrailerClickListener;

    private List<Trailer> trailerList;

    public interface OnTrailerClickListener {
        void onTrailerClick(int position);
    }

    public TrailersAdapter(Context context, OnTrailerClickListener onTrailerClickListener) {
        this.context = context;
        this.onTrailerClickListener = onTrailerClickListener;
        trailerList = new ArrayList<>();
    }

    public void addAll(List<Trailer> trailerList) {
        this.trailerList.clear();
        this.trailerList.addAll(trailerList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TrailerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TrailerViewHolder(LayoutInflater.from(context).inflate(R.layout.trailer, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TrailerViewHolder holder, int position) {
        Trailer trailer = trailerList.get(position);

        Uri thumbnail = null;
        String thumbnailUrl = Trailer.getThumbnailUrl(trailer);
        if (thumbnailUrl != null && !thumbnailUrl.isEmpty()) {
            thumbnail = Uri.parse(thumbnailUrl);
        }

        Glide.with(context)
                .asBitmap()
                .load(thumbnail)
                .apply(new RequestOptions().placeholder(R.color.colorPrimary).error(R.color.colorPrimary))
                .into(holder.video_thumbnail);
    }

    @Override
    public int getItemCount() {
        if (trailerList.isEmpty()) {
            return 0;
        } else {
            return trailerList.size();
        }
    }

    public class TrailerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView video_thumbnail;

        TrailerViewHolder(View itemView) {
            super(itemView);

            video_thumbnail = (ImageView) itemView.findViewById(R.id.video_thumbnail);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onTrailerClickListener.onTrailerClick(getLayoutPosition());
        }
    }
}
