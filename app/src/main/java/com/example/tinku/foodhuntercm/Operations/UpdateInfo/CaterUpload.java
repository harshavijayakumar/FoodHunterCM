package com.example.tinku.foodhuntercm.Operations.UpdateInfo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.app.AlertDialog;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.tinku.foodhuntercm.Operations.Search.SearchActivity;
import com.example.tinku.foodhuntercm.R;
import com.example.tinku.foodhuntercm.Requests.CaterUploadRequest;


import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

/**
 * Created by sandeep on 4/12/2016.
 */
public class CaterUpload extends AppCompatActivity {
    ImageView iv;
    Button btnCaterUpdate;
     Button btnload;
    boolean imageset = false;
      private static final int RESULT_LOAD_IMAGE = 1;
      private static final String SERVER_ADDRESS = "http://www.nativebites.comxa.com/";
      private class UploadImage extends AsyncTask<Void, Void, Void> {
        Bitmap image;
        String name;
        String uname;

        public UploadImage(Bitmap image, String name, String uname){
            this.name = name;
            this.image = image;
            this.uname = uname;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(getApplicationContext(), "Image Uploaded", Toast.LENGTH_SHORT).show();

            Intent intent = getIntent();
            final String username = intent.getStringExtra("username");
            Intent intent2 = new Intent(CaterUpload.this, SearchActivity.class);

            intent.putExtra("username", username);
            CaterUpload.this.startActivity(intent2);
        }

        @Override
        protected Void doInBackground(Void... params) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            String encodedImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);

            ArrayList<NameValuePair> dataTosend = new ArrayList<>();
            dataTosend.add(new BasicNameValuePair("image", encodedImage));
            dataTosend.add(new BasicNameValuePair("name", name));
            dataTosend.add(new BasicNameValuePair("uname", uname));

            HttpParams httpRequestParams = getHttpParamms();

            HttpClient client = new DefaultHttpClient(httpRequestParams);
            HttpPost post = new HttpPost(SERVER_ADDRESS +"image.php");
            try{
                post.setEntity(new UrlEncodedFormEntity(dataTosend));
                client.execute(post);
            }
            catch(Exception e){

            }
            return null;
        }

    }

    private HttpParams getHttpParamms(){
        HttpParams httpRequestParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpRequestParams, 1000 * 30);
        HttpConnectionParams.setSoTimeout(httpRequestParams, 1000 * 30);
        return httpRequestParams;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload);

        iv = (ImageView) findViewById(R.id.ivFood);


        final EditText foodtype,location, foodName, price, contactNumber;
        foodtype = (EditText) findViewById(R.id.etFoodType);
        location = (EditText) findViewById(R.id.etLocation);
        foodName = (EditText) findViewById(R.id.etFood);
        price = (EditText)findViewById(R.id.etPrice);
        contactNumber = (EditText)findViewById(R.id.etContactNumber);
        Intent intent = getIntent();
        final String username = intent.getStringExtra("username");

        btnCaterUpdate = (Button)findViewById(R.id.updatebutton);
        btnCaterUpdate.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // Switching to Register screen
                final String caterName = foodtype.getText().toString();
                final String caterLocation = location.getText().toString();
                final String caterFood = foodName.getText().toString();
                final float foodPrice = Float.parseFloat(price.getText().toString());
                final int caterContact = Integer.parseInt(contactNumber.getText().toString());
                if(imageset) {
                    Bitmap image = ((BitmapDrawable) iv.getDrawable()).getBitmap();
                    new UploadImage(image, foodName.getText().toString(), caterName).execute();
                }


                final Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            if (success) {
                                //   String name = jsonResponse.getString("name");
                                if(imageset == false) {
                                    Intent intent = getIntent();
                                    final String username = intent.getStringExtra("username");
                                    Intent intent2 = new Intent(CaterUpload.this, SearchActivity.class);

                                    intent.putExtra("username", username);
                                    CaterUpload.this.startActivity(intent2);
                                }
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(CaterUpload.this);
                                builder.setMessage("Cater Upload Failed")
                                        .setNegativeButton("Retry", null)
                                        .create()
                                        .show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                };
                CaterUploadRequest caterUploadRequest = new CaterUploadRequest(username, caterName, caterLocation, caterFood, foodPrice, caterContact, responseListener);
                RequestQueue queue = Volley.newRequestQueue(CaterUpload.this);
                queue.add(caterUploadRequest);


            }
        });

        iv.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
              if(v.getId() == R.id.ivFood){
                  Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                  startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);
              }
            }
        });
    }


   protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null){
            Uri selectedImage = data.getData();
            iv.setImageURI(selectedImage);
            imageset = true;
        }
    }
}

