package com.example.genesis.wherearethey;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfile extends AppCompatActivity {

    String user_id;
    String changedStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        Intent profileIntent = getIntent();

        final String username = profileIntent.getStringExtra("username");
        final String phone = profileIntent.getStringExtra("phone");
        user_id = profileIntent.getStringExtra("userid");

        final String image = profileIntent.getStringExtra("image");

        TextView userNameTxt = (TextView)findViewById(R.id.myUserNameTxt);
        TextView phoneNumTxt = (TextView)findViewById(R.id.myPhoneTxt);

        Switch privacySwitch = (Switch)findViewById(R.id.myPrivacyStatusSwitch);

        CircleImageView profileImage = (CircleImageView)findViewById(R.id.myProfilePicture);

        Button deleteAccBtn = (Button)findViewById(R.id.deleteAccBtn);

        deleteAccBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(UserProfile.this);
                dialogBuilder.setTitle("Warning");
                dialogBuilder.setMessage("Do you really want to delete your account?");


                dialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        deleteAcc();
                    }
                });
                dialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                       dialogInterface.dismiss();
                    }
                });
                dialogBuilder.show();




            }
        });







        Glide.with(this).load(MainActivity.IMAGE_PATH_URL+image).into(profileImage);
        userNameTxt.setText(username);
        phoneNumTxt.setText(phone);

        if(DashBoard.mySharingStatus.equals("Y")){
            privacySwitch.setChecked(true);
        }else{
            privacySwitch.setChecked(false);
        }

        privacySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if(b){

                    changedStatus = "Y";
                    changePrivacy();
                }else{

                    changedStatus = "N";
                    changePrivacy();
                }

            }
        });


    }
    public void deleteAcc(){
        StringRequest volleyStringReq = new StringRequest(Request.Method.POST, MainActivity.DELETE_ME, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    JSONObject jsonObject = new JSONObject(response).getJSONObject("response");

                    Toast.makeText(getApplicationContext(),jsonObject.getString("message"),Toast.LENGTH_LONG).show();


                    if(jsonObject.getString("status").equals("SUCCESS")){

                            gotoHomeScreenAfterDelete();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String>  postParams = new HashMap<>();

                postParams.put("user_id",user_id);

                return postParams;
            }
        };

        ReqSingleton.getInstance(this.getApplicationContext()).addToRequestQueue(volleyStringReq);

    }
    public void changePrivacy(){


        StringRequest volleyStringReq = new StringRequest(Request.Method.POST, MainActivity.CHANGE_PRVACY, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {



                    JSONObject jsonObject = new JSONObject(response).getJSONObject("response");

                    Toast.makeText(getApplicationContext(),jsonObject.getString("message"),Toast.LENGTH_SHORT).show();

                    if(jsonObject.getString("status").equals("SUCCESS")){

                        DashBoard.mySharingStatus = changedStatus;
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String>  postParams = new HashMap<>();

                postParams.put("user_id",user_id);
                postParams.put("share",changedStatus);

                return postParams;
            }
        };

        ReqSingleton.getInstance(this.getApplicationContext()).addToRequestQueue(volleyStringReq);


    }
    public void  gotoHomeScreenAfterDelete(){



        Intent homeIntent = new Intent(this, MainActivity.class);
        finishAffinity();
        startActivity(homeIntent);

    }
}
