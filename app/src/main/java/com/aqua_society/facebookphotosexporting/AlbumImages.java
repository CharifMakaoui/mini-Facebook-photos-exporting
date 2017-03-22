package com.aqua_society.facebookphotosexporting;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.aqua_society.facebookphotosexporting.Adapters.PhotosAdapter;
import com.aqua_society.facebookphotosexporting.Interfaces.ImageSelected;
import com.aqua_society.facebookphotosexporting.Interfaces.UploadProgress;
import com.aqua_society.facebookphotosexporting.Modules.FacebookImages;
import com.aqua_society.facebookphotosexporting.Modules.FacebookPhotos;
import com.aqua_society.facebookphotosexporting.Utils.FileUploader;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class AlbumImages extends AppCompatActivity implements ImageSelected, UploadProgress {

    List<FacebookPhotos> lstFBImages;
    String AlbumID;

    RecyclerView recyclerView;
    PhotosAdapter photosAdapter;
    ProgressDialog dialog;

    MenuItem itemUpload;

    List<String> imageToUploadPath = new ArrayList<>();

    ProgressDialog progressDialog;
    AlertDialog.Builder alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_images);

        AlbumID = getIntent().getStringExtra("ALBUM_ID");
        String AlbumName = getIntent().getStringExtra("ALBUM_NAME");

        Toolbar topToolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(topToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(AlbumName);
        topToolBar.setTitleTextColor(Color.WHITE);

        lstFBImages = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.listPhotos);
        photosAdapter = new PhotosAdapter(this, lstFBImages);

        GridLayoutManager gridLayoutManager
                = new GridLayoutManager(getBaseContext(), 2, GridLayoutManager.VERTICAL, false);

        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(photosAdapter);

        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading Album Images");
        dialog.setCancelable(false);

        dialog.show();
        GetFacebookImages(AlbumID);

        progressDialog = new ProgressDialog(this);
        alertDialog = new AlertDialog.Builder(this);
    }

    public void GetFacebookImages(final String albumId) {

        Bundle parameters = new Bundle();
        parameters.putString("fields", "images");
        /* make the API call */
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/" + albumId + "/photos",
                parameters,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {

                        Log.v("AlbumImages", "Facebook Photos response: " + response);
                        try {
                            if (response.getError() == null) {

                                JSONObject joMain = response.getJSONObject();
                                if (joMain.has("data")) {
                                    JSONArray jaData = joMain.optJSONArray("data");

                                    for (int i = 0; i < jaData.length(); i++) {
                                        JSONObject joAlbum = jaData.getJSONObject(i);
                                        JSONArray jaImages = joAlbum.getJSONArray("images");

                                        FacebookPhotos photos = new FacebookPhotos()
                                                .setId(joAlbum.getString("id"));

                                        if (jaImages.length() > 0) {
                                            FacebookImages images = new FacebookImages()
                                                    .setHeight(jaImages.getJSONObject(0).getInt("height"))
                                                    .setWidth(jaImages.getJSONObject(0).getInt("width"))
                                                    .setSource(jaImages.getJSONObject(0).getString("source"));

                                            photos.setImages(images);

                                            lstFBImages.add(photos);//lstFBImages is Images object array
                                        }
                                    }

                                    photosAdapter.setList(lstFBImages);
                                    photosAdapter.notifyDataSetChanged();
                                    dialog.dismiss();
                                }

                            } else {
                                Log.v("AlbumImages", response.getError().toString());
                                dialog.setMessage("Response Error");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            dialog.setMessage("Exception");
                        }

                    }
                }
        ).executeAsync();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.image_controller, menu);
        itemUpload = menu.findItem(R.id.action_upload);
        itemUpload.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }

        if (id == R.id.action_upload) {
            imageToUploadPath.clear();
            progressDialog.setMax(100);
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.show();

            for (int i = 0; i < lstFBImages.size(); i++) {
                if (lstFBImages.get(i).selected) {
                    String path = DownloadImageToSD(lstFBImages.get(i).getImages().getSource());
                    imageToUploadPath.add(path);
                }
            }

            if (imageToUploadPath.size() > 0) {
                StartUpload();
            }

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSelect(int pos, boolean is) {
        isStoragePermissionGranted();

        lstFBImages.get(pos).selected = is;

        boolean somSelect = false;
        for (int i = 0; i < lstFBImages.size(); i++) {
            if (lstFBImages.get(i).selected) {
                somSelect = true;
            }
        }

        itemUpload.setVisible(somSelect);
    }

    @Override
    public void onBackPressed() {

        if (itemUpload.isVisible()) {
            for (int i = 0; i < lstFBImages.size(); i++) {
                lstFBImages.get(i).selected = false;
            }
            photosAdapter.setList(lstFBImages);
            photosAdapter.notifyDataSetChanged();
            itemUpload.setVisible(false);
        } else {
            super.onBackPressed();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.v("Nice", "Permission: " + permissions[0] + "was " + grantResults[0]);
        }
    }

    public void StartUpload() {
        // Extract Bitmap from ImageView drawable
        final Activity context = this;

        progressDialog.setTitle("Start Upload ...");
        progressDialog.show();

        Thread thread = new Thread() {
            @Override
            public void run() {
                Log.d("UPLOAD_IMAGE", "Start Uploading");
                try {
                    String charset = "UTF-8";
                    String backEnd = context.getResources().getString(R.string.server_back_end);

                    FileUploader multipart = new FileUploader(context, backEnd, charset);

                    File sourceFile[] = new File[imageToUploadPath.size()];
                    for (int i = 0; i < imageToUploadPath.size(); i++) {
                        sourceFile[i] = new File(imageToUploadPath.get(i));
                    }

                    for (int i = 0; i < imageToUploadPath.size(); i++) {
                        multipart.addFilePart("uploaded_file[]", sourceFile[i]);
                        final int pos = i;

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.setProgress(0);
                                progressDialog.setMessage("Uploading File " + (pos + 1) + " / " + imageToUploadPath.size() + " ...");
                                progressDialog.show();
                            }
                        });
                    }

                    List<String> response = multipart.finish();

                    Log.d("UPLOAD_IMAGE", "SERVER REPLIED: ");

                    String DataResponse = "";
                    for (String line : response) {
                        Log.d("UPLOAD_IMAGE", line);
                        DataResponse += line;
                    }

                    JSONObject jsonObject = new JSONObject(DataResponse);


                    final String stat = jsonObject.getString("status");

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();

                            if (stat.equals("success")) {
                                alertDialog.setTitle("Upload Success");
                                alertDialog.setTitle("All Your selected Image was Uploaded");
                                alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        onBackPressed();
                                    }
                                });
                            } else {
                                alertDialog.setTitle("Upload Error");
                                alertDialog.setTitle("An error occurred in the time we upload images to the server");
                                alertDialog.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        onBackPressed();
                                    }
                                });
                            }

                            alertDialog.show();
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                            alertDialog.setTitle("Upload Error");
                            alertDialog.setTitle("Error Connect to server");
                            alertDialog.setNegativeButton("ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    onBackPressed();
                                }
                            });
                            alertDialog.show();
                        }
                    });
                }
            }
        };

        thread.start();
    }

    private String DownloadImageToSD(String Furl) {
        String filepath = "";
        try {
            Log.d("UPLOAD_IMAGE", Furl);

            progressDialog.setTitle("Download Images To SD ...");
            progressDialog.show();

            // Init URL and Data
            URL url = new URL(Furl);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoInput(true);
            urlConnection.connect();
            InputStream is = urlConnection.getInputStream();

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            Bitmap bmImg = BitmapFactory.decodeStream(is, null, options);

            // Create App Images Folder
            String DirectoryName = "AppsFacebookBAckUP";
            File fileSD_Root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES + "/" + DirectoryName).getAbsoluteFile();

            boolean file00E = true;
            if (!fileSD_Root.exists())
                file00E = fileSD_Root.mkdir();
            if (!file00E) {
                Log.d("UPLOAD_IMAGE", "AppsFacebookBAckUP Folder Not Exist");
                return null;
            }


            // Setup File Name
            String path = url.getPath();
            String filename = path.substring(path.lastIndexOf('/') + 1);
            Log.i("UPLOAD_IMAGE", "" + filename);
            File file = new File(fileSD_Root, filename);


            if (file.createNewFile()) {
                Log.i("UPLOAD_IMAGE", "Create New File");


                FileOutputStream fos = new FileOutputStream(file);
                bmImg.compress(Bitmap.CompressFormat.JPEG, 75, fos);
                fos.flush();
                fos.close();

                Log.i("UPLOAD_IMAGE", "File Path : " + file.getAbsolutePath());
            } else {
                Log.i("UPLOAD_IMAGE", "File Exist");

            }

            filepath = file.getAbsolutePath();
        } catch (Exception e) {
            Log.i("UPLOAD_IMAGE:", "Error : ", e);
            filepath = null;
            e.printStackTrace();
        }

        Log.i("UPLOAD_IMAGE", " " + filepath);
        return filepath;
    }

    @Override
    public void uploadProgress(int progress) {
        Log.d("UPLOAD_IMAGE", "Upload Progress : " + progress);
        progressDialog.setProgress(progress);
        progressDialog.show();
    }
}
