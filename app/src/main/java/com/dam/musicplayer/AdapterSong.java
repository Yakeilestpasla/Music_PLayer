package com.dam.musicplayer;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

public class AdapterSong extends RecyclerView.Adapter<AdapterSong.viewHolder> {

    Context context;
    ArrayList<ModelSong> songArrayList;

    public AdapterSong(Context context, ArrayList<ModelSong> songArrayList) {
        this.context = context;
        this.songArrayList = songArrayList;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_song,parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {

holder.title.setText(songArrayList.get(position).getSongTitle());
holder.artist.setText(songArrayList.get(position).getSongArtiste());
Uri imgUri = songArrayList.get(position).getSongUri();

RequestOptions options = new RequestOptions()
        .centerCrop()
        .error(R.drawable.ic_baseline_play_circle_filled_24)
        .placeholder(R.drawable.ic_baseline_play_circle_filled_24);

        Context context = holder.cover.getContext();

        Glide.with(context)
                .load(imgUri)
                .apply(options)
                .fitCenter()
                .override(150,150)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.cover);




    }

    @Override
    public int getItemCount() {
        return songArrayList.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
TextView title, artist;
ImageView cover;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tvTitre);
            artist = itemView.findViewById(R.id.tvArtiste);
            cover = itemView.findViewById(R.id.ivCover);
        }
    }
}
