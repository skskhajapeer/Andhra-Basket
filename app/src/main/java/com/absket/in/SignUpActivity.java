package com.absket.in;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.HashMap;
import java.util.Map;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Sreejith on 07-04-2017.
 */
public class SignUpActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener
{
    TextView tv_gotologin;
    TextView tv_skip;
    CardView card_bg_signup;
    CardView card_fbsignup;
    CardView card_googlesignup;
    EditText edt_username;
    EditText edt_email;
    EditText edt_password;
    CardView card_signup;
    EditText edt_refcode;
    LoginButton fb_login_button;

    String sUserName="",sEmailId="",sPassword="",sRefCode="",sCode="";
    UserSessionManager session;
    ProgressDialog progressDialog;
    CallbackManager callbackManager;

    private FirebaseAuth mAuth;
    int RC_SIGN_IN=100;
    private FirebaseAuth.AuthStateListener mAuthListener;
    GoogleApiClient mGoogleApiClient;
    public static  int REQUEST_APP_SETTINGS=5000;

    String testValue = "andhrabasket";

    String convertedstring;
    byte[] encodeValue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        setContentView(R.layout.activity_signup);


        tv_gotologin=(TextView)findViewById(R.id.tv_gotologin);
        tv_skip=(TextView)findViewById(R.id.tv_skip);
        card_bg_signup=(CardView)findViewById(R.id.card_bg_signup);
        card_fbsignup=(CardView)findViewById(R.id.card_fbsignup);
        card_googlesignup=(CardView)findViewById(R.id.card_googlesignup);
        edt_username=(EditText)findViewById(R.id.edt_username);
        edt_email=(EditText)findViewById(R.id.edt_email);
        edt_password=(EditText)findViewById(R.id.edt_password);
        card_signup=(CardView)findViewById(R.id.card_signup);
        edt_refcode=(EditText)findViewById(R.id.edt_refcode);
        fb_login_button=(LoginButton)findViewById(R.id.login_button);



        card_bg_signup.setVisibility(View.INVISIBLE);
        session = new UserSessionManager(SignUpActivity.this);
        fb_login_button.setReadPermissions("email");



        final Animation an = AnimationUtils.loadAnimation(this,R.anim.swing_up_left);


        Handler hr1 = new Handler();
        hr1.postDelayed(new Runnable() {
            @Override
            public void run() {
                card_bg_signup.setVisibility(View.VISIBLE);
                card_bg_signup.startAnimation(an);
            }
        },500);



        mAuth = FirebaseAuth.getInstance();

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleApiClient with access to the Google Sign-In API and the
// options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d("TAG", "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d("TAG", "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };

        card_fbsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sCode = "facebook";
                FacebookSdk.sdkInitialize(getApplicationContext());
                AppEventsLogger.activateApp(SignUpActivity.this);
                fb_login_button.performClick();
            }
        });


        callbackManager = CallbackManager.Factory.create();

        //FACEBOOK LOGIN
        // Callback registration
        fb_login_button.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {
                // App code
                String email = loginResult.toString();
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {

                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.i("LoginActivity", response.toString());
                        getFacebookData(object);
                        SignUpFBGoogle();

                    }
                });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id, first_name, last_name, email,gender, birthday, location"); // Parámetros que pedimos a facebook
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }

        });


        card_googlesignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sCode = "google";
                signIn();
            }
        });





        card_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sUserName = edt_username.getText().toString();
                sPassword = edt_password.getText().toString();
                sEmailId = edt_email.getText().toString();
                sRefCode = edt_refcode.getText().toString();


                if(!isValidEmail(sEmailId))
                {
                    Toast.makeText(SignUpActivity.this, "Enter Valid Email-Id", Toast.LENGTH_SHORT).show();

                }

               else if(sUserName.equals("") || sPassword.equals("") || sEmailId.equals("") || !sEmailId.contains("@") || !sEmailId.contains("."))
                {
                    Toast.makeText(SignUpActivity.this, "Check Your Fields", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    CallingSignUpRetrofit();
                }

            }
        });


        tv_gotologin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(SignUpActivity.this,LoginActivity.class);
                finish();
                startActivity(in);
            }
        });

        tv_skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(SignUpActivity.this,MainActivity_New.class);
                finish();
                startActivity(in);
            }
        });
    }


    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately
                // ...
            }
        }
        if(requestCode == REQUEST_APP_SETTINGS)
        {
            // if (hasPermissions(requiredPermissions)) {
            // Toast.makeText(this, "All permissions granted!", Toast.LENGTH_SHORT).show();
            //} else {
            //    Toast.makeText(this, "Permissions not granted.", Toast.LENGTH_LONG).show();
            // }
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }




    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("TAG", "firebaseAuthWithGoogle:" + acct.getId());

        sEmailId = acct.getEmail();
        sUserName = acct.getGivenName();
        if(sUserName.equals(""))
        {
            sUserName = acct.getDisplayName();
            if(sUserName.equals(""))
            {
                sUserName = "RentRaaja User";
            }
        }
        //    uri = acct.getPhotoUrl();
        //  BitmapDrawable bitmapDrawable = ((BitmapDrawable) img_user.getDrawable());
        //  Bitmap bitmap = bitmapDrawable .getBitmap();
        // session.setUserBitmap(""+uri);

        // Bitmap bg = getBitmapfromUrl(""+uri);
        // session.setUserBitmap(bitmapToBase64(bitmap));

        //https://plus.google.com/s2/photos/profile/" + user.getGooglePlusId() + "?sz=100

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("TAG", "signInWithCredential:onComplete:" + task.isSuccessful());
                        SignUpFBGoogle();
                    }
                });
    }



    private Bundle getFacebookData(JSONObject object) {
        Bundle bundle = new Bundle();
        try {

            String id = object.getString("id");

            try {
                URL profile_pic = new URL("https://graph.facebook.com/" + id + "/picture?width=200&height=150");
                Log.i("profile_pic", profile_pic + "");
                bundle.putString("profile_pic", profile_pic.toString());
                //Bitmap b =getBitmapfromUrl(profile_pic.toString());
                //session.setUserBitmap(""+profile_pic);

            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            }

            bundle.putString("idFacebook", id);
            if (object.has("first_name"))
                bundle.putString("first_name", object.getString("first_name"));
            sUserName =  object.getString("first_name");
            if (object.has("last_name"))
                bundle.putString("last_name", object.getString("last_name"));
            sUserName = sUserName +" "+ object.getString("last_name");
            if (object.has("email"))
                bundle.putString("email", object.getString("email"));
            sEmailId = object.getString("email");
          /*  if (object.has("gender"))
                bundle.putString("gender", object.getString("gender"));
            if (object.has("birthday"))
                bundle.putString("birthday", object.getString("birthday"));
            if (object.has("location"))
                bundle.putString("location", object.getJSONObject("location").getString("name"));*/

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return bundle;
    }


    public void CallingSignUpRetrofit()
    {
        progressDialog = new ProgressDialog(SignUpActivity.this);
        progressDialog.setMessage("Signing Up...");
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Please wait");
        progressDialog.show();

        RestAdapter Radapter = new RestAdapter.Builder().setEndpoint(DomainName.sURL+"signup.php").build();
        RetrofitAPI api = Radapter.create(RetrofitAPI.class);
        api.NormalSignUp(sUserName, sEmailId, sPassword, sRefCode,session.getUserFCM(), new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {

                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(response.getBody().in()));
                    String output = reader.readLine();
                    Log.d("outputis",output);

                    progressDialog.dismiss();

                    if(output.contains("email exists"))
                    {
                        Toast.makeText(getApplicationContext(),"Email Already Exists",Toast.LENGTH_LONG).show();
                    }
                    else {
                        JSONArray jArray = new JSONArray(output);

                        for (int i = 0; i < jArray.length(); i++) {
                            JSONObject jsonObject = jArray.getJSONObject(i);
                            session.setUserName(jsonObject.getString("customer_name"));
                            session.setUserEmail(jsonObject.getString("customer_email"));
                            session.setUserId(jsonObject.getString("id"));
                            session.setCustomerId(jsonObject.getString("customer_id"));
                            session.setUserAddress(jsonObject.getString("customer_address"));
                            session.setRefCode(jsonObject.getString("ref_code"));
                            session.setUserMobile(jsonObject.getString("customer_mobile"));
                        }

                        session.setRefCodeSignUp(sRefCode);
                        session.setIsLoggedIn("true");
                        Intent in = new Intent(SignUpActivity.this, MainActivity_New.class);
                        finish();
                        startActivity(in);
                    }


                }
                catch (IOException e)
                {
                    if(progressDialog.isShowing()) progressDialog.dismiss();
                    e.printStackTrace();
                }
                catch (JSONException e)
                {
                    if(progressDialog.isShowing()) progressDialog.dismiss();
                    e.printStackTrace();
                }

            }

            @Override
            public void failure(RetrofitError error) {
                if(progressDialog.isShowing()) progressDialog.dismiss();
                Toast.makeText(SignUpActivity.this, "Connection Error", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }



   /* public void SignUpFBGoogle()
    {

        progressDialog = new ProgressDialog(SignUpActivity.this);
        progressDialog.setMessage("Creating an Account...");
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Please wait");
        progressDialog.show();
       *//* String testValue=sEmailId+" "+"andhrabasket";
        byte[] encodeValue = Base64.encode(testValue.getBytes(), Base64.DEFAULT);*//*


        RestAdapter Radapter = new RestAdapter.Builder().setEndpoint(DomainName.sURL+ "signupfbgoogle.php").build();
        RetrofitAPI api = Radapter.create(RetrofitAPI.class);

        String testValue=sEmailId+" "+"andhrabasket";


        encodeValue = Base64.encode(testValue.getBytes(), Base64.DEFAULT);

        try {

            encodeValue = testValue.getBytes("UTF-8");

            convertedstring = Base64.encodeToString(encodeValue, Base64.DEFAULT);

            Log.d("Base64 ", convertedstring);

        } catch (UnsupportedEncodingException e) {

            e.printStackTrace();

        }


        Map<String,String> params = new HashMap<String, String>();
        params.put("email", sEmailId);
        params.put("username", sUserName);
        params.put("fcm", convertedstring);
        params.put("code", sCode);

        api.FbGoogleSignUp(params, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                try {

                    BufferedReader reader = new BufferedReader(new InputStreamReader(response.getBody().in()));
                    String output = reader.readLine();
                    if(progressDialog.isShowing()) progressDialog.dismiss();
                    Log.d("bodyresis",response.getBody().toString());
                    Log.d("outputvalis",output);
                    Log.d("responseis",response2.toString());
                    if(output.contains("exists"))
                    {
                        LoginManager.getInstance().logOut();
                        FacebookSdk.sdkInitialize(getApplicationContext());
                        Toast.makeText(SignUpActivity.this, "Email Already Exists.. Please Select on SignIn Option", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        JSONArray jsonArray = new JSONArray(output);

                        for(int i=0;i<jsonArray.length();i++)
                        {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            session.setUserName(jsonObject.getString("customer_name"));
                            session.setUserEmail(jsonObject.getString("customer_email"));
                            session.setUserId(jsonObject.getString("id"));
                            session.setCustomerId(jsonObject.getString("customer_id"));
                            session.setUserAddress(jsonObject.getString("customer_address"));
                            session.setRefCode(jsonObject.getString("ref_code"));
                            session.setUserMobile(jsonObject.getString("customer_mobile"));

                            session.setIsLoggedIn("true");
                            Intent in = new Intent(SignUpActivity.this, MainActivity.class);
                            finish();
                            startActivity(in);
                        *//*    LoginManager.getInstance().logOut();
                            FacebookSdk.sdkInitialize(getApplicationContext());
*//*
                        }



                    }
                    progressDialog.dismiss();
                }
                catch (IOException e)
                {
                    if(progressDialog.isShowing()) progressDialog.dismiss();
                    LoginManager.getInstance().logOut();
                    FacebookSdk.sdkInitialize(getApplicationContext());
                    progressDialog.dismiss();
                    e.printStackTrace();
                }
                catch (JSONException e)
                {
                    if(progressDialog.isShowing()) progressDialog.dismiss();
                    LoginManager.getInstance().logOut();
                    FacebookSdk.sdkInitialize(getApplicationContext());
                    e.printStackTrace();
                }


            }

            @Override
            public void failure(RetrofitError error) {
                progressDialog.dismiss();
                LoginManager.getInstance().logOut();
                FacebookSdk.sdkInitialize(getApplicationContext());
                Toast.makeText(SignUpActivity.this, "Connection Error", Toast.LENGTH_SHORT).show();

            }
        });
    }*/



    public void SignUpFBGoogle()
    {

        progressDialog = new ProgressDialog(SignUpActivity.this);
        progressDialog.setMessage("Creating an Account...");
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Please wait");
        progressDialog.show();
        RestAdapter Radapter = new RestAdapter.Builder().setEndpoint(DomainName.sURL + "signupfbgoogle.php").build();
        RetrofitAPI api = Radapter.create(RetrofitAPI.class);


        String testValue=sEmailId+" "+"andhrabasket";


        encodeValue = Base64.encode(testValue.getBytes(), Base64.DEFAULT);

        try {

            encodeValue = testValue.getBytes("UTF-8");

            convertedstring = Base64.encodeToString(encodeValue, Base64.DEFAULT);

            Log.d("Base64 ", convertedstring);

        } catch (UnsupportedEncodingException e) {

            e.printStackTrace();

        }
        Map<String,String> params = new HashMap<String, String>();
        params.put("email", sEmailId);
        params.put("username", sUserName);
        params.put("fcm", convertedstring);
        params.put("code", sCode);

        api.FbGoogleSignUp(params, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(response.getBody().in()));
                    String output = reader.readLine();
                    if(progressDialog.isShowing()) progressDialog.dismiss();
                    if(output.contains("exists"))
                    {
                        LoginManager.getInstance().logOut();
                        FacebookSdk.sdkInitialize(getApplicationContext());
                        Toast.makeText(SignUpActivity.this, "Email Already Exists.. Please Select on SignIn Option", Toast.LENGTH_SHORT).show();

                    }
                    else
                    {
                        JSONArray jsonArray = new JSONArray(output);
                        for(int i=0;i<jsonArray.length();i++)
                        {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            session.setUserName(jsonObject.getString("customer_name"));
                            session.setUserEmail(jsonObject.getString("customer_email"));
                            session.setUserId(jsonObject.getString("id"));
                            session.setCustomerId(jsonObject.getString("customer_id"));
                            session.setUserAddress(jsonObject.getString("customer_address"));
                            session.setRefCode(jsonObject.getString("ref_code"));
                            session.setUserMobile(jsonObject.getString("customer_mobile"));

                            session.setIsLoggedIn("true");
                            Intent in = new Intent(SignUpActivity.this, MainActivity_New.class);
                            finish();
                            startActivity(in);
                            LoginManager.getInstance().logOut();
                            FacebookSdk.sdkInitialize(getApplicationContext());
                        }



                    }
                    progressDialog.dismiss();
                }
                catch (IOException e)
                {
                    if(progressDialog.isShowing()) progressDialog.dismiss();
                    LoginManager.getInstance().logOut();
                    FacebookSdk.sdkInitialize(getApplicationContext());
                    progressDialog.dismiss();
                    e.printStackTrace();
                }
                catch (JSONException e)
                {
                    if(progressDialog.isShowing()) progressDialog.dismiss();
                    LoginManager.getInstance().logOut();
                    FacebookSdk.sdkInitialize(getApplicationContext());
                    e.printStackTrace();
                }


            }

            @Override
            public void failure(RetrofitError error) {
                progressDialog.dismiss();
                LoginManager.getInstance().logOut();
                FacebookSdk.sdkInitialize(getApplicationContext());
                Toast.makeText(SignUpActivity.this, "Connection Error", Toast.LENGTH_SHORT).show();

            }
        });
    }



    private  boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

}
