package com.example.genesis.wherearethey;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;


public class UserAdapter extends BaseAdapter {

    private final Context mAdapterContext;
    private final ArrayList<UsersInfoHolder> userInfo;

    public UserAdapter(Context context, ArrayList<UsersInfoHolder> userInfo){

        this.mAdapterContext = context;
        this.userInfo = userInfo;


    }


    @Override
    public int getCount() {
        return userInfo.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        final UsersInfoHolder userHolder  = userInfo.get(i);
        if(view ==null){

            final LayoutInflater layoutInflater = LayoutInflater.from(mAdapterContext);
            view = layoutInflater.inflate(R.layout.single_user_card,null);
        }
        final CardView singleUserCard = (CardView)view.findViewById(R.id.singleUserCard);

        final TextView userNameLabel = (TextView)view.findViewById(R.id.username);
        final TextView userPhoneLabel = (TextView)view.findViewById(R.id.userphone);
        final ImageView profileImgView = (ImageView)view.findViewById(R.id.userProfileImg);

        final String user_name  = userHolder.getFirstName() + ' ' + userHolder.getLastName();
        final String phone_number =  userHolder.getPhoneNumber();
        final String image_url = MainActivity.IMAGE_PATH_URL+userHolder.getProfileImgUrl();

        userNameLabel.setText(user_name);
        userPhoneLabel.setText(phone_number);
        Glide.with(view).load(image_url).override(100,100).into(profileImgView);


        if(userHolder.getIsShared().equals("Y")){
            userNameLabel.setTextColor(Color.GREEN);
            singleUserCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent mapIntent = new Intent(mAdapterContext,MapsActivity.class);
                    mapIntent.putExtra("latitude",userHolder.getLatitudeLocation());
                    mapIntent.putExtra("longitude",userHolder.getLongitudeLocation());
                    mapIntent.putExtra("name",user_name);
                    mAdapterContext.startActivity(mapIntent);

                }
            });

        }else{

        }





        return view;


    }
}
