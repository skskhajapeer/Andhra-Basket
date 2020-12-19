package com.absket.in.utils;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.google.android.gms.analytics.Tracker;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.webengage.sdk.android.WebEngage;
import com.webengage.sdk.android.WebEngageActivityLifeCycleCallbacks;
import com.webengage.sdk.android.WebEngageConfig;

import io.fabric.sdk.android.Fabric;

public class TestApplication  extends Application {

    private static TestApplication mInstance;

    String senderId = "9gj4Lm35hwGsFDzsx4bMLC";

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

        FirebaseApp.initializeApp(getApplicationContext());
        registerActivityLifecycleCallbacks(new WebEngageActivityLifeCycleCallbacks(this));
        WebEngage.registerPushNotificationCallback(new PushNotificationCallbacksImpl());
        WebEngage.get().setRegistrationID(FirebaseInstanceId.getInstance().getToken());
        Fabric.with(this, new CrashlyticsCore.Builder().build(), new Crashlytics());

        WebEngageConfig webEngageConfig = new WebEngageConfig.Builder()
                .setDebugMode(true)
                .build();
        WebEngage.engage(this.getApplicationContext(), webEngageConfig);

        Fabric.with(this, new Crashlytics());

        AnalyticsTrackers.initialize(this);
        AnalyticsTrackers.getInstance().get(AnalyticsTrackers.Target.APP);

    }
    public synchronized Tracker getGoogleAnalyticsTracker() {
        AnalyticsTrackers analyticsTrackers = AnalyticsTrackers.getInstance();
        return analyticsTrackers.get(AnalyticsTrackers.Target.APP);
    }
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(TestApplication.this);
    }

    public static synchronized TestApplication getInstance() {
        return mInstance;
    }
}
