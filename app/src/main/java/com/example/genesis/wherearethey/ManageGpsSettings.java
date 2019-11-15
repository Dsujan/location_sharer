package com.example.genesis.wherearethey;

import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;



public class ManageGpsSettings {

    private final Context mContext;
    public boolean isSettingOk = false;
    public final int CHECK_LOCATION_SETTINGS = 1;


    public ManageGpsSettings(Context context) {

        this.mContext = context;
    }

    //defining location request for getting GPS value i.e. High Accuracy
    public LocationRequest setUpLocationRequest() {

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;

    }

    public void changeCurrentLocationSetting() {

        LocationSettingsRequest.Builder locationSettingBuilder = new LocationSettingsRequest.Builder().addLocationRequest(setUpLocationRequest());
        SettingsClient settingsClient = LocationServices.getSettingsClient(mContext);
        Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(locationSettingBuilder.build());

        task.addOnSuccessListener((Activity) mContext, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                Log.d("LOCATION--DEBUG", "Location Settings configured!");
                isSettingOk = true;
            }
        });
        task.addOnFailureListener((Activity) mContext, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("LOCATION--DEBUG", "Location Settings not configured!");
                isSettingOk = false;

                if (e instanceof ResolvableApiException) {
                    ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                    try {
                        resolvableApiException.startResolutionForResult((Activity) mContext, CHECK_LOCATION_SETTINGS);
                    } catch (IntentSender.SendIntentException e1) {
                        e1.printStackTrace();

                    }


                }

            }
        });

    }
    //returns location settings status
    public boolean getLocationSettingStatus(){
        if (isSettingOk) {
            return true;
        }else{
            return  false;
        }
    }



}
