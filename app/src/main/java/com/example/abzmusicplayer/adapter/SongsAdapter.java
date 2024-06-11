package com.example.abzmusicplayer.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.abzmusicplayer.R;
import com.example.abzmusicplayer.model.MusicModel;
import com.example.abzmusicplayer.model.OnClick;
import com.example.abzmusicplayer.model.PlaylistModel;

import java.util.List;

public class SongsAdapter extends RecyclerView.Adapter<SongsAdapter.Vh> {
    Context context;
    List<MusicModel> list;
    String check;
    OnClick onClick;
    public SongsAdapter(Context context, List<MusicModel> list, String check, OnClick onClick) {
        this.context = context;
        this.list = list;
        this.check = check;
        this.onClick = onClick;
    }

    public void setList(List<MusicModel> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Vh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_songs, parent, false);
        return new Vh(view);
    }

    @SuppressLint({"SetTextI18n", "SimpleDateFormat"})
    @Override
    public void onBindViewHolder(@NonNull Vh holder, @SuppressLint("RecyclerView") int position) {
        MusicModel model = list.get(position);
        holder.tvName.setText(model.getMusicName());
        holder.tvAuthor.setText(model.getAuthorName());
        holder.ivSong.setImageResource(model.getImage());

        if (check.equals("search")){
            holder.tvAddToPlayList.setVisibility(View.VISIBLE);
        }else{
            holder.tvAddToPlayList.setVisibility(View.GONE);
        }

        holder.tvAddToPlayList.setOnClickListener(view -> onClick.clicked(position));

        holder.itemView.setOnClickListener(v -> {
            if (check.equals("search")){
                Bundle bundle = new Bundle();
                bundle.putSerializable("data", model);
                Navigation.findNavController(v).navigate(R.id.action_searchFragment_to_playSongFragment, bundle);
            }else{
                Bundle bundle = new Bundle();
                bundle.putSerializable("data", model);
                Navigation.findNavController(v).navigate(R.id.action_songsFragment_to_playSongFragment, bundle);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class Vh extends RecyclerView.ViewHolder {
        TextView tvName, tvAuthor, tvAddToPlayList;
        ImageView ivSong;

        public Vh(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvAddToPlayList = itemView.findViewById(R.id.tvAddToPlayList);
            tvAuthor = itemView.findViewById(R.id.tvAuthor);
            ivSong = itemView.findViewById(R.id.ivSong);
        }
    }
}
