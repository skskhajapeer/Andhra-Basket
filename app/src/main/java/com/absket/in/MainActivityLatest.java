package com.absket.in;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.absket.in.CustomFonts.MyTextView;
import com.absket.in.CustomFonts.MyTextViewBold;
import com.absket.in.adapter.ExpandableListAdapter;
import com.absket.in.adapter.MyAdapter;
import com.absket.in.model.SelectedProductBean;
import com.absket.in.model.Singleton;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.relex.circleindicator.CircleIndicator;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivityLatest extends AppCompatActivity {

    private FirebaseAuth mAuth;
    int RC_SIGN_IN=100;
    private FirebaseAuth.AuthStateListener mAuthListener;
    GoogleApiClient mGoogleApiClient;
    public static  int REQUEST_APP_SETTINGS=5000;
    String sUserName="",sEmailId="",sPassword="",sRefCode="",sCode="";
    UserSessionManager session;
    ProgressDialog progressDialog;
    CallbackManager callbackManager;
    LoginButton fb_login_button;
    CardView card_fbsignup;
    CardView card_googlesignup;
    String convertedstring;
    byte[] encodeValue;
    Bundle bundle;
    JSONObject jsonObject;
    String facebook = "facebook";
    String sEmail = "";
    Button login,signup;
    MyTextView tv_skip;
    RelativeLayout mailinear;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainactivity_latest);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        login = (Button) findViewById(R.id.login);
        signup = (Button) findViewById(R.id.signup);
        tv_skip=(MyTextView) findViewById(R.id.tv_skip);
        mailinear=(RelativeLayout) findViewById(R.id.mailinear);
         final Animation an = AnimationUtils.loadAnimation(this, R.anim.swing_up_left);
        mailinear.setVisibility(View.INVISIBLE);


        Handler hr1 = new Handler();
        hr1.postDelayed(new Runnable() {
            @Override
            public void run() {
                mailinear.setVisibility(View.VISIBLE);
                mailinear.startAnimation(an);
            }
        }, 500);



        tv_skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(MainActivityLatest.this, MainActivity_New.class);
                startActivity(in);
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(MainActivityLatest.this, LoginActivity.class);
                startActivity(in);
            }
        });
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(MainActivityLatest.this, SignUpActivity.class);
                startActivity(in);
            }
        });

    }

}
