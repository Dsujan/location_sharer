package com.example.genesis.wherearethey;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    final int CAMERA_SELCTED = 1;
    final int GALLERY_SELECTED = 0;

    ImageView profilePicImage;
    String image_string;
    Boolean checkGpsSetting;

    private ProgressDialog networkProgress;

    String latitude;
    String longitude;

    LocationManager locationManager;
    LocationListener locationListener;

    ManageGpsSettings manageGpsSettings;

    Boolean isImageSelected = false;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        locationManager=(LocationManager) getSystemService(Context.LOCATION_SERVICE);

        profilePicImage = (ImageView) findViewById(R.id.profilePictureImg);

        networkProgress = new ProgressDialog(this);
        networkProgress.setCanceledOnTouchOutside(false);
        //getting user's current location
        getUserLocation();

    }

    //dialog box for selecting image selection options
    public void openImageOptions(View v) {


        final CharSequence[] dialogItems = {"Gallery", "Camera", "Cancel"};

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle("Choose one option.");
        dialogBuilder.setItems(dialogItems, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if (dialogItems[i].equals("Gallery")) {

                    Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    galleryIntent.setType("image/*");
                    startActivityForResult(galleryIntent, GALLERY_SELECTED);
                }
                if (dialogItems[i].equals("Camera")) {

                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_SELCTED);

                }
                if (dialogItems[i].equals("Cancel")) {
                    dialogInterface.dismiss();
                }


            }
        });
        dialogBuilder.show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);

        if(data!=null){
            isImageSelected = true;
            if (requestCode == CAMERA_SELCTED) {


                final Bitmap selectedPictureBitmap = (Bitmap) data.getExtras().get("data");

                profilePicImage.setBackground(null);
                profilePicImage.setImageBitmap(selectedPictureBitmap);
                //base64 encoded image string
                image_string = prepareImageForUpload(selectedPictureBitmap);

            } else if (requestCode == GALLERY_SELECTED) {

                final Uri selectedGaleryImgUri = data.getData();
                //setting profile picture in layout
                profilePicImage.setBackground(null);
                profilePicImage.setImageURI(selectedGaleryImgUri);


                //conerting URI to Image Bitmap
                try {

                    final InputStream selctedImageStream = getContentResolver().openInputStream(selectedGaleryImgUri);
                    final Bitmap selctedPictureBitmap = BitmapFactory.decodeStream(selctedImageStream);

                    //base64 encoded image string
                    image_string = prepareImageForUpload(selctedPictureBitmap);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }


            }
        }else{

            isImageSelected = false;
        }

    }


    public String prepareImageForUpload(Bitmap profileImg) {

        ByteArrayOutputStream byteArrayStream = new ByteArrayOutputStream();
        profileImg.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayStream);

        byte[] bufferByte = byteArrayStream.toByteArray();
        String encoded_img = Base64.encodeToString(bufferByte, Base64.DEFAULT);
        return encoded_img;

    }

    //registers new user
    public void registerNewUser(View v) {

        EditText firstNameTxt = (EditText) findViewById(R.id.firstnametxt);
        EditText lastNameTxt = (EditText) findViewById(R.id.lastnametxt);
        EditText phoneNumTxt = (EditText) findViewById(R.id.phonetxt);
        EditText groupNumTxt = (EditText) findViewById(R.id.groupnum);
        EditText passwordTxt = (EditText) findViewById(R.id.passwordRegTxt);
        EditText confirmPassTxt = (EditText) findViewById(R.id.confirmpassword) ;


        String firstName = firstNameTxt.getText().toString();
        String lastName = lastNameTxt.getText().toString();
        String phoneNumber = phoneNumTxt.getText().toString();
        String groupNumber = groupNumTxt.getText().toString();
        String password = passwordTxt.getText().toString();
        String confirmPass = confirmPassTxt.getText().toString();

        //validations
        if (firstName.isEmpty()) {
            firstNameTxt.setError("Cannot be empty!");
        } else if (lastName.isEmpty()) {
            lastNameTxt.setError("Cannot be empty!");
        } else if (phoneNumber.isEmpty()) {
            phoneNumTxt.setError("Cannot be empty!");
        } else if (groupNumber.isEmpty()) {
            groupNumTxt.setError("Cannot be empty!");
        } else if (password.isEmpty()) {
            passwordTxt.setError("Cannot be empty!");
        } else if(!isImageSelected){
            Toast.makeText(this,"Please Select a Profile Picture",Toast.LENGTH_LONG).show();
        }else if(!password.equals(confirmPass)){
            confirmPassTxt.setError("Password didn't matched!");
        }else {
            //adds to db
            addToDb(firstName, lastName, phoneNumber, groupNumber, password, image_string,latitude,longitude);
        }


    }

    public void addToDb(final String firstname, final String lastname, final String phonenum, final String groupnum, final String password, final String img_str, final String latitude, final String longitude) {
        networkProgress.setMessage("Registering..");
        networkProgress.show();
        StringRequest volleyStringReq = new StringRequest(Request.Method.POST, MainActivity.REGISTER_API, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                networkProgress.dismiss();


                try {
                    //parsing JSON response
                    JSONObject jsonResponse;

                    jsonResponse = new JSONObject(response).getJSONObject("response");
                    String status = jsonResponse.getString("status");
                    String message = jsonResponse.getString("message");

                    if (status.equals("ERROR")) {

                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

                    } else {

                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                        Intent mainIntent = new Intent(getApplicationContext(),MainActivity.class);
                        finishAffinity();
                        startActivity(mainIntent);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> postParams = new HashMap<>();

                postParams.put("first_name", firstname);
                postParams.put("last_name", lastname);
                postParams.put("phone_no", phonenum);
                postParams.put("group_no", groupnum);
                postParams.put("password", password);
                postParams.put("loc_lat", latitude);
                postParams.put("loc_long", longitude);
                postParams.put("image", img_str);


                return postParams;
            }
        };


        ReqSingleton.getInstance(this.getApplicationContext()).addToRequestQueue(volleyStringReq);


    }

    //sets uers co-ordinate if required settings are provided else prompts to allow manage location settings
    public void getUserLocation() {



        manageGpsSettings = new ManageGpsSettings(this);
        //if settings are pre configured
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Log.d("LOCATION--DEBUG","Setting is true");

            fetch_location();


        }
        //if settings aren't configured.
        else {
            Log.d("LOCATION--DEBUG","Setting is False");
            manageGpsSettings.changeCurrentLocationSetting();
            //fetching location after settings are changed
            fetch_location();

        }


    }

    //fetches current location co-ordinates
    public void fetch_location() {

        networkProgress.setMessage("Fetching location..");
        networkProgress.show();

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                Log.d("LOCATION--DEBUG","Location Successfully fetched!");

                longitude = String.valueOf(location.getLongitude());
                latitude = String.valueOf(location.getLatitude());

                //removing update listener after update response
                locationManager.removeUpdates(locationListener);
                networkProgress.dismiss();


            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListener);





    }
    //changes progress bar msg
    private  Runnable changeProgressMessage = new Runnable() {
        @Override
        public void run() {
            networkProgress.setMessage("Registering...");
        }
    };

}
