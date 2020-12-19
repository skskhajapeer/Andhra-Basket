package com.absket.in;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Sreejith on 06-04-2017.
 */
public class SplashScreenActivity extends Activity {
    CardView card_splash;
    UserSessionManager session;
    ProgressDialog progressDialog;
    String versionName = "";
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_splash);
        card_splash = (CardView) findViewById(R.id.card_splash);
        card_splash.setVisibility(View.INVISIBLE);
        session = new UserSessionManager(SplashScreenActivity.this);
        getVersionInfo();
        checkversionUpdate();
        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.absket.in", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
                String sDebug = "Here";
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }



        /*ObjectAnimator anim = (ObjectAnimator) AnimatorInflater.loadAnimator(SplashScreenActivity.this, R.animator.flipping);
        anim.setTarget(card_splash);
        anim.setDuration(1000);
        anim.start();

        Handler hr1 = new Handler();
        hr1.postDelayed(new Runnable() {
            @Override
            public void run() {
                card_splash.setVisibility(View.VISIBLE);
            }
        },500);*/


     /*   Handler hr2 = new Handler();
        hr2.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (session.getIsLoggedIn().equals("true")) {
                    Intent in = new Intent(SplashScreenActivity.this, MainActivity_New.class);
                    finish();
                    startActivity(in);
                } else {
                    Intent in = new Intent(SplashScreenActivity.this, LoginActivity.class);
                    finish();
                    startActivity(in);
                }
            }
        }, 1000);
        */

    }

    public void checkversionUpdate() {
        progressDialog = new ProgressDialog(SplashScreenActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Please wait");
        progressDialog.show();

        RestAdapter Radapter = new RestAdapter.Builder().setEndpoint(DomainName.sURL + "getversion.php").build();
        RetrofitAPI api = Radapter.create(RetrofitAPI.class);
        api.GetVersionUpdate(new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(response.getBody().in()));
                    String output = reader.readLine();
                    Log.d("outputis",output);
                    progressDialog.dismiss();

                    int serverVersionNumber = 1;
                    try{
                        serverVersionNumber = Integer.parseInt(output);
                    }catch (Exception e){

                    }

                    int versionCode = BuildConfig.VERSION_CODE;
                    if(versionCode >= serverVersionNumber) {
                        launchPage();
                    }else{

                        showUpdateDialog();
                    }


                } catch (IOException e) {
                    if (progressDialog.isShowing()) progressDialog.dismiss();
                    e.printStackTrace();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                if (progressDialog.isShowing()) progressDialog.dismiss();
                Toast.makeText(SplashScreenActivity.this, "Connection Error", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void launchPage(){
        if (session.getIsLoggedIn().equals("true")) {
            Intent in = new Intent(SplashScreenActivity.this, MainActivity_New.class);
            finish();
            startActivity(in);
        } else {
            Intent in = new Intent(SplashScreenActivity.this, GetStartedActivity.class);
            finish();
            startActivity(in);
        }
    }

    private void getVersionInfo() {

        int versionCode = -1;
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            versionName = packageInfo.versionName;
            versionCode = packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


    }

    private void showUpdateDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(SplashScreenActivity.this);
        builder.setTitle("A New Update is Available");
        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse
                        ("market://details?id=com.absket.in")));
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                launchPage();
            }
        });

        builder.setCancelable(false);
        dialog = builder.show();
    }
}
