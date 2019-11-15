package com.example.genesis.wherearethey;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    public final static String  serverIP = "192.168.43.88";
    public final static String IMAGE_PATH_URL = "http://"+serverIP+":8080/wherearethey/images/";
    public final static String API_URL = "http://"+serverIP+":8080/wherearethey/api.php";
    public final static String AUTH_API = API_URL +"?auth";
    public final static String REGISTER_API = API_URL + "?register";
    public final static String FETCH_MEMBERS = API_URL + "?showMembers";
    public final static String DELETE_ME = API_URL + "?deleteMe";
    public final static String CHANGE_PRVACY = API_URL + "?updatePrivacy";





    private ProgressDialog networkProgress;


    static String sessionId;
    static String  groupId;
    Boolean isLoginSucess;

    EditText usernameTxt,passwordTxt;
    String username,password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestLocationPermission(this);


        usernameTxt = (EditText) findViewById(R.id.usernametxt);
        passwordTxt = (EditText) findViewById(R.id.passwordtxt);

        networkProgress = new ProgressDialog(this);
        networkProgress.setCanceledOnTouchOutside(false);





    }


    public void showSignUpForm(View v){
        Intent intent = new Intent(this,SignUpActivity.class);
        startActivity(intent);
    }
    //onclick method of login button .
    public void loginUser(View view){


        username = usernameTxt.getText().toString().trim();
        password = passwordTxt.getText().toString().trim();

        if(username.isEmpty()){
            usernameTxt.setError("Cannot be empty!");
        }
        else if (password.isEmpty()){
            passwordTxt.setError("Cannot be empty!");
        }else{

            networkProgress.setMessage("Logging in..");
            networkProgress.show();
            authenticateUser(username,password);



        }


    }

    public void authenticateUser(final String username, final String password){
        StringRequest volleyStringReq = new StringRequest(Request.Method.POST, AUTH_API, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                networkProgress.dismiss();
                try {
                    //parsing JSON response
                    JSONObject jsonResponse = new JSONObject(response).getJSONObject("response");
                    String status = jsonResponse.getString("status");

                    //checking json response for authentication
                    if(!status.equals("ERROR")){
                        //login successful
                        //getting sessionId
                        sessionId = jsonResponse.getString("sessionId");
                        groupId = jsonResponse.getString("group_no");
                        isLoginSucess = true;

                        Toast.makeText(getApplicationContext(),"Sucessful",Toast.LENGTH_SHORT).show();
                        //showing user's dashboard
                        startActivity(new Intent(getApplicationContext(),DashBoard.class));

                    }else{
                        isLoginSucess = false;
                        erase_login_fields();
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
            protected Map<String, String> getParams() {

                Map<String, String>  postParams = new HashMap<>();
                postParams.put("phone_no",username);
                postParams.put("password",password);

                return postParams;
            }
        };
        

        ReqSingleton.getInstance(this.getApplicationContext()).addToRequestQueue(volleyStringReq);


    }
    public  void erase_login_fields(){

       usernameTxt.setText("");
       passwordTxt.setText("");
       Toast.makeText(getApplicationContext(),"Login Faled!",Toast.LENGTH_SHORT).show();



    }
    //requesting location permission at frist launch
    public static  void requestLocationPermission(Context context){
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity)context,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                    //showing info about the permission req
                    Toast.makeText(context,"Needed for Location",Toast.LENGTH_LONG).show();
            }else{
                //requesting location permission
                ActivityCompat.requestPermissions((Activity)context,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},10);




            }
        }else{
            // Permission already given
        }


    }



}
