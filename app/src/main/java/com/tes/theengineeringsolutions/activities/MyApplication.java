package com.tes.theengineeringsolutions.activities;

import com.orm.SugarApp;
import com.tes.theengineeringsolutions.Models.ConnectivityReceiver;

public class MyApplication extends SugarApp {

    private static MyApplication mInstance;

    public static synchronized MyApplication getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }
}

