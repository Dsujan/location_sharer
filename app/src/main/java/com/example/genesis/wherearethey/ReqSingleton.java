package com.example.genesis.wherearethey;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class ReqSingleton {

        private static ReqSingleton singletonInstance;
        private RequestQueue volleyReqQueue;
        private static Context mContext;

        private ReqSingleton(Context context) {
            mContext = context;
            volleyReqQueue = getRequestQueue();
        }

        public static synchronized ReqSingleton getInstance(Context context) {
            if (singletonInstance == null) {
                singletonInstance = new ReqSingleton(context);
            }
            return singletonInstance;
        }

        public RequestQueue getRequestQueue() {
            if (volleyReqQueue == null) {
                // getApplicationContext() is key, it keeps you from leaking the
                // Activity or BroadcastReceiver if someone passes one in.
                volleyReqQueue = Volley.newRequestQueue(mContext.getApplicationContext());
            }
            return volleyReqQueue;
        }

        public <T> void addToRequestQueue(Request<T> req) {
            getRequestQueue().add(req);
        }

    }
