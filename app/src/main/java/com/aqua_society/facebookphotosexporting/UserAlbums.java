package com.aqua_society.facebookphotosexporting;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.aqua_society.facebookphotosexporting.Adapters.AlbumAdapter;
import com.aqua_society.facebookphotosexporting.Modules.FacebookAlbums;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UserAlbums extends AppCompatActivity {

    private List<FacebookAlbums> alFBAlbum ;
    RecyclerView recyclerView;
    AlbumAdapter albumAdapter;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_albums);

        alFBAlbum = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.listAlbums);
        albumAdapter = new AlbumAdapter(this, alFBAlbum);

        Toolbar topToolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(topToolBar);
        getSupportActionBar().setTitle("List Albums");
        topToolBar.setTitleTextColor(Color.WHITE);

        GridLayoutManager gridLayoutManager
                = new GridLayoutManager(getBaseContext(), 2, GridLayoutManager.VERTICAL, false);

        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(albumAdapter);

        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading Albums");
        dialog.setCancelable(false);


        dialog.show();
        GetUserAlbums();
    }

    private void GetUserAlbums(){
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/" + AccessToken.getCurrentAccessToken().getUserId() + "/"
                        +"albums",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        Log.d("FacebookAlbums", "Facebook Albums: " + response.toString());
                        try {
                            if (response.getError() == null) {
                                JSONObject joMain = response.getJSONObject();
                                if (joMain.has("data")) {

                                    JSONArray jaData = joMain.optJSONArray("data");
                                    alFBAlbum = new ArrayList<>();

                                    for (int i = 0; i < jaData.length(); i++) {

                                        JSONObject joAlbum = jaData.getJSONObject(i);

                                        FacebookAlbums facebookAlbums = new FacebookAlbums()
                                                .setId(joAlbum.optString("id"))
                                                .setName(joAlbum.optString("name"));
                                        alFBAlbum.add(facebookAlbums);

                                    }

                                    Log.d("FacebookAlbums", "Nombre des albums : " + alFBAlbum.size());

                                    Collections.sort(alFBAlbum);
                                    albumAdapter.setList(alFBAlbum);
                                    albumAdapter.notifyDataSetChanged();
                                    dialog.dismiss();
                                }
                            } else {
                                Log.d("Test", response.getError().toString());
                                dialog.setMessage("Response Error");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            dialog.setMessage("Exception");
                        }
                    }
                }
        ).executeAsync();
    }
}
