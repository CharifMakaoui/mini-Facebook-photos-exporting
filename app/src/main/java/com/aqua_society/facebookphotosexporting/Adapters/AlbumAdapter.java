package com.aqua_society.facebookphotosexporting.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aqua_society.facebookphotosexporting.AlbumImages;
import com.aqua_society.facebookphotosexporting.Modules.FacebookAlbums;
import com.aqua_society.facebookphotosexporting.R;

import java.util.List;

/**
 * Created by MrCharif on 20/03/2017.
 */

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.ViewHolder> {

    private Context context;
    private List<FacebookAlbums> albumsList;

    public AlbumAdapter(Context context, List<FacebookAlbums> facebookAlbumses){
        this.albumsList = facebookAlbumses;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new AlbumAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_albume, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
         FacebookAlbums albums = albumsList.get(position);
         holder.albumeName.setText(albums.getName());
    }

    @Override
    public int getItemCount() {
        return albumsList.size();
    }

    public void setList(List<FacebookAlbums> list){
        this.albumsList = list;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView albumeName;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            albumeName = (TextView) itemView.findViewById(R.id.albumeName);
        }

        @Override
        public void onClick(View v) {
            FacebookAlbums albums = albumsList.get(getAdapterPosition());
            Intent intent = new Intent(context, AlbumImages.class);
            intent.putExtra("ALBUM_ID", albums.getId());
            intent.putExtra("ALBUM_NAME", albums.getName());
            context.startActivity(intent);


        }
    }
}
