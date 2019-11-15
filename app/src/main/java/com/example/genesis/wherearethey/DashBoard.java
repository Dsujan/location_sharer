package com.example.genesis.wherearethey;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DashBoard extends AppCompatActivity {
    double longitude;
    double latitude;
    String groupId;
    String  sessionId;

    ProgressDialog networkProgress;

    ArrayList<UsersInfoHolder> usersInfo = new ArrayList<UsersInfoHolder>();



    String myFirstName;
    String myLastName;
    String myProfilePicture;
    public static String mySharingStatus;
    String myGroupName;
    String myLatitude;
    String myLongitude;
    String myPhoneNum;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);

         groupId = MainActivity.groupId;
         sessionId = MainActivity.sessionId;

        networkProgress = new ProgressDialog(this);
        networkProgress.setCanceledOnTouchOutside(false);
        networkProgress.setMessage("Loading Data..");
        networkProgress.show();

        fetch_data();







//        GridView gridView = (GridView) findViewById(R.id.userHolderGrid);
//        UserAdapter userAdapter = new UserAdapter(this, fill_userinfos());
//        gridView.setAdapter(userAdapter);

    }



    public void enumerateUsersInfo(String first_name,String last_name,String phone_num,String latitude,String longitude, String image_name,String isShared){

        usersInfo.add(new UsersInfoHolder(first_name,last_name,phone_num,latitude,longitude,image_name,isShared));
    }

    public void fetch_data(){

        Log.d("FETCH--DEBUG","Fetch method called");

        StringRequest volleyStringReq = new StringRequest(Request.Method.POST, MainActivity.FETCH_MEMBERS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("FETCH--DEBUG","Got Response from server ");

                try {

                    JSONObject jsonObjectResoponse = new JSONObject(response).getJSONObject("response");
                    JSONObject myInfoJsonObject = jsonObjectResoponse.getJSONObject("my_info");


                    String status = jsonObjectResoponse.getString("status");
                    String groupName = jsonObjectResoponse.getString("group_name");

                    myFirstName =myInfoJsonObject.getString("first_name");
                    myLastName = myInfoJsonObject.getString("last_name");
                    myPhoneNum = myInfoJsonObject.getString("phone_no");
                    myProfilePicture = myInfoJsonObject.getString("profile_photo");
                    mySharingStatus = myInfoJsonObject.getString("is_shared");
                    myGroupName = myInfoJsonObject.getString("group_name");
                    myLatitude = myInfoJsonObject.getString("loc_lat");
                    myLongitude = myInfoJsonObject.getString("loc_lat");




                    JSONArray allMembersInfo = jsonObjectResoponse.getJSONArray("members_info");

                    for(int i = 0; i < allMembersInfo.length(); i++) {

                        JSONObject singleMemberObject = (JSONObject) allMembersInfo.get(i);

                        final String first_name = singleMemberObject.getString("first_name");
                        final String last_name =  singleMemberObject.getString("last_name");
                        final String phone_no = singleMemberObject.getString("phone_no");
                        final String loc_lat = singleMemberObject.getString("loc_lat");
                        final String loc_long = singleMemberObject.getString("loc_long");
                        final String image = singleMemberObject.getString("image");
                        final String is_shared = singleMemberObject.getString("is_shared");

                        //excluding current session  user
                        if(!phone_no.equals(myPhoneNum)){
                            enumerateUsersInfo(first_name,last_name,phone_no,loc_lat,loc_long,image,is_shared);

                        }


                    }
                    Log.d("RESPONSE--DEBUG",String.valueOf(usersInfo.size()));
                    setMembersCard(usersInfo);
                    setLoggedInUserInfo(myFirstName+' '+myLastName,myGroupName);
                    networkProgress.dismiss();


                } catch (JSONException e) {
                    e.printStackTrace();


                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.d("FETCH--DEBUG", error.toString());

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String>  postParams = new HashMap<>();

                postParams.put("group_no",groupId);
                postParams.put("user_id",sessionId);


                return postParams;

            }
        };

        ReqSingleton.getInstance(this.getApplicationContext()).addToRequestQueue(volleyStringReq);


    }
    public void setMembersCard(ArrayList<UsersInfoHolder> info){

        GridView gridView = (GridView) findViewById(R.id.userHolderGrid);
        UserAdapter userAdapter = new UserAdapter(this, info);
        gridView.setAdapter(userAdapter);


    }
    public void setLoggedInUserInfo(String userName,String groupName){

        TextView currentUser = (TextView)findViewById(R.id.currentUser);
        TextView currentGroup = (TextView)findViewById(R.id.currentGroup);

        currentUser.setText(userName);
        currentGroup.setText(groupName);

        currentUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showUserProfile();
            }
        });

    }
    public void showUserProfile(){

        Intent profileIntent = new Intent(this,UserProfile.class);
        profileIntent.putExtra("image",myProfilePicture);
        profileIntent.putExtra("username",myFirstName+' '+myLastName);
        profileIntent.putExtra("phone",myPhoneNum);
        profileIntent.putExtra("userid",sessionId);

        startActivity(profileIntent);

    }



}
