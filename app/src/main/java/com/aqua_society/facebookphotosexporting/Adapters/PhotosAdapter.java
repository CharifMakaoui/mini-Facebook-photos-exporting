package com.aqua_society.facebookphotosexporting.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.aqua_society.facebookphotosexporting.AlbumImages;
import com.aqua_society.facebookphotosexporting.Interfaces.ImageSelected;
import com.aqua_society.facebookphotosexporting.Modules.FacebookPhotos;
import com.aqua_society.facebookphotosexporting.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by MrCharif on 21/03/2017.
 */

public class PhotosAdapter extends RecyclerView.Adapter<PhotosAdapter.ViewHolder> {

    private List<FacebookPhotos> photosList;
    private Context context;
    private ImageSelected imageSelected;

    public PhotosAdapter(Context c, List<FacebookPhotos> photoses){
        this.photosList = photoses;
        this.context = c;
        imageSelected = (AlbumImages) context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PhotosAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_photo, parent, false));

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        FacebookPhotos photos = photosList.get(position);

        Picasso.with(context).load(photos.getImages().getSource()).into(holder.photoView);
        holder.photoChecking.setChecked(photos.selected);
        if(photos.selected){
            holder.overLay.setAlpha(0.3f);
        }
        else{
            holder.overLay.setAlpha(0);
        }
    }

    @Override
    public int getItemCount() {
        return photosList.size();
    }

    public void setList(List<FacebookPhotos> photos){
        this.photosList = photos;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView photoView;
        View overLay;
        CheckBox photoChecking;

        public ViewHolder(View itemView) {
            super(itemView);

            photoView = (ImageView) itemView.findViewById(R.id.photoView);
            overLay = (View) itemView.findViewById(R.id.overLay);
            photoChecking = (CheckBox) itemView.findViewById(R.id.photoChecking);

            itemView.setClickable(true);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FacebookPhotos photos = photosList.get(getAdapterPosition());
                    photos.selected = !photos.selected;
                    photoChecking.setChecked(photos.selected);

                    imageSelected.onSelect(getAdapterPosition(), photos.selected);
                }
            });
        }
    }
}
