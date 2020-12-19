package com.absket.in;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.absket.in.CustomFonts.MyTextView;
import com.absket.in.bottomactivity.InterceptableFrameLayout;
import com.absket.in.bottomactivity.PrettyAnimator;
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
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.common.base.Optional;
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

public class NewSignupActivity extends FragmentActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    InterceptableFrameLayout interceptableFrameLayout;
    LinearLayout dragView;
    NestedScrollView scrollView;
    PrettyAnimator prettyAnimator;
    boolean signupStatus;
    Toolbar toolbar;
    String normallogin = "normallogin";
    public static int REQUEST_APP_SETTINGS = 5000;

    MyTextView btnSkip,forgotpswd,alreadyMember,login,haveLogin,signupNow;
    TextView dum;
    TextInputLayout fname_input;
    TabLayout tabLayout;
    byte[] encodeValue;

    TextView tv_skip;
    TextView login_tab,sign_tab;
    String sEmail = "", sRefCode="",sPassword = "", sCode = "",sUserName="";
EditText et_email,et_pswd;
    UserSessionManager session;
    ProgressDialog progressDialog;
    EditText et_uname;
    EditText edt_refcode;
    ImageView facebook,google;
    String strfacebook = "facebook";

    LoginButton fb_login_button;
    CallbackManager callbackManager;
    Bundle bundle;
    JSONObject jsonObject;
    GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mAuth;
    int RC_SIGN_IN = 100;
    private FirebaseAuth.AuthStateListener mAuthListener;

    String convertedstring;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_signup_page);
        interceptableFrameLayout = (InterceptableFrameLayout) findViewById(R.id.interceptable_frame_layout);
        dragView = (LinearLayout) findViewById(R.id.drag_view);
        scrollView = (NestedScrollView) findViewById(R.id.scroll_view);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.white_circle_close);
        et_uname=(EditText)findViewById(R.id.et_uname);
        tv_skip = (TextView) findViewById(R.id.tv_skip);
        forgotpswd = (MyTextView) findViewById(R.id.tv_forgotpswd);
        alreadyMember = (MyTextView) findViewById(R.id.tv_already_member);
        login = (MyTextView) findViewById(R.id.login);
        haveLogin = (MyTextView) findViewById(R.id.have_login);
        signupNow = (MyTextView) findViewById(R.id.signup_now);
        sign_tab=(TextView)findViewById(R.id.sign_tab);
        et_email=(EditText)findViewById(R.id.et_email);
        et_pswd=(EditText)findViewById(R.id.et_pswd);
        fname_input = (TextInputLayout) findViewById(R.id.fname_input);
        login_tab=(TextView)findViewById(R.id.login);
        edt_refcode=(EditText)findViewById(R.id.edt_refcode);
        facebook = (ImageView) findViewById(R.id.facebook);
        google = (ImageView) findViewById(R.id.google);
        fb_login_button = (LoginButton) findViewById(R.id.login_button);
        tabLayout = (TabLayout) findViewById(R.id.tabs1);
        tabLayout.addTab(tabLayout.newTab().setText("Login"));
        tabLayout.addTab(tabLayout.newTab().setText("Sign Up"));
        setTabBG(R.drawable.tab_left_select, R.drawable.tab_right_unselect);
        session = new UserSessionManager(NewSignupActivity.this);
        FacebookSdk.sdkInitialize(getApplicationContext());
        fb_login_button.setReadPermissions("email");

        callbackManager = CallbackManager.Factory.create();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(NewSignupActivity.this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mAuth = FirebaseAuth.getInstance();

        final SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setScopes(gso.getScopeArray());

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tabLayout.getSelectedTabPosition() == 0) {

                    setTabBG(R.drawable.tab_left_select, R.drawable.tab_right_unselect);
                    loadLogin();
                } else {
                    setTabBG(R.drawable.tab_left_unselect, R.drawable.tab_right_select);
                    loadsignup();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }

        });
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
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadLogin();
            }
        });

        login_tab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (signupStatus) {

                    sEmail = et_email.getText().toString();
                    sPassword = et_pswd.getText().toString();

                            sUserName = et_uname.getText().toString();
                    sRefCode = edt_refcode.getText().toString();

                    if(!isValidEmail(sEmail))
                    {
                        Toast.makeText(NewSignupActivity.this, "Enter Valid Email-Id", Toast.LENGTH_SHORT).show();

                    }

                    else if(sUserName.equals("") || sPassword.equals("") || sEmail.equals("") || !sEmail.contains("@") || !sEmail.contains("."))
                    {
                        Toast.makeText(NewSignupActivity.this, "Check Your Fields", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        CallingSignUpRetrofit();
                    }



                } else {


                    edt_refcode.setVisibility(View.GONE);
                    sEmail = et_email.getText().toString();
                    sPassword = et_pswd.getText().toString();

                    if (!isValidEmail(sEmail)) {
                        Toast.makeText(NewSignupActivity.this, "Enter Valid Email-Id", Toast.LENGTH_SHORT).show();

                    } else if (sEmail.equals("") || sPassword.equals("") || !sEmail.contains("@") || !sEmail.contains(".")) {
                        Toast.makeText(NewSignupActivity.this, "Check Your Fields", Toast.LENGTH_SHORT).show();
                    } else {
                        CallingNormalLogin();
                    }
                }
            }
        });

        tv_skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(NewSignupActivity.this, MainActivity_New.class);
                finish();
                startActivity(in);
            }
        });

        forgotpswd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(NewSignupActivity.this, ForgotPasswordActivity_New.class);
                startActivity(in);
            }
        });


        final Intent intent = getIntent();
     String activeTab=   intent.getStringExtra("active_tab");
     if(activeTab.equalsIgnoreCase("signup")){
      loadsignup();

     }else{

         loadLogin();


     }

     if(signupStatus==true){


     }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prettyAnimator.doClose();
            }
        });
    prettyAnimator = new PrettyAnimator(this);

    interceptableFrameLayout = (InterceptableFrameLayout) findViewById(R.id.interceptable_frame_layout);
    dragView = (LinearLayout) findViewById(R.id.drag_view);
        prettyAnimator.onViewCreated(savedInstanceState,
                interceptableFrameLayout,
                dragView,
                Optional.<View>absent(),
                new PrettyAnimator.OnPrettyAnimatorListener() {
                    @Override
                    public boolean childAtTheTop() {
                        return isViewVisible(dragView);
                    }

                    private boolean isViewVisible(View view) {
                        Rect scrollBounds = new Rect();
                        scrollView.getDrawingRect(scrollBounds);
                        float top = view.getY();
                        float bottom = top + view.getHeight();
                        return scrollBounds.top <= top && scrollBounds.bottom > bottom;
                    }

                    @Override
                    public void closeView(View view) {
                        finish();
                    }
                });
        prettyAnimator.showFull();
    }


    private void setTabBG(int tab1, int tab2) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
            ViewGroup tabStrip = (ViewGroup) tabLayout.getChildAt(0);
            View tabView1 = tabStrip.getChildAt(0);
            View tabView2 = tabStrip.getChildAt(1);
            if (tabView1 != null) {
                int paddingStart = tabView1.getPaddingStart();
                int paddingTop = tabView1.getPaddingTop();
                int paddingEnd = tabView1.getPaddingEnd();
                int paddingBottom = tabView1.getPaddingBottom();
                ViewCompat.setBackground(tabView1, AppCompatResources.getDrawable(tabView1.getContext(), tab1));
                ViewCompat.setPaddingRelative(tabView1, paddingStart, paddingTop, paddingEnd, paddingBottom);
            }

            if (tabView2 != null) {
                int paddingStart = tabView2.getPaddingStart();
                int paddingTop = tabView2.getPaddingTop();
                int paddingEnd = tabView2.getPaddingEnd();
                int paddingBottom = tabView2.getPaddingBottom();
                ViewCompat.setBackground(tabView2, AppCompatResources.getDrawable(tabView2.getContext(), tab2));
                ViewCompat.setPaddingRelative(tabView2, paddingStart, paddingTop, paddingEnd, paddingBottom);
            }
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        prettyAnimator.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        prettyAnimator.onDestroyView();
        super.onDestroy();
    }

private void loadLogin(){
    TabLayout.Tab tab =  tabLayout.getTabAt(0);
    tab.select();
    signupStatus=false;
    forgotpswd.setVisibility(View.VISIBLE);
    alreadyMember.setVisibility(View.GONE);
    edt_refcode.setVisibility(View.GONE);
    login.setText("Login");
    haveLogin.setText("Don't have an account ? ");
    signupNow.setVisibility(View.VISIBLE);
    fname_input.setVisibility(View.GONE);

    facebook.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            sCode = "facebook";
            FacebookSdk.sdkInitialize(getApplicationContext());
            AppEventsLogger.activateApp(NewSignupActivity.this);
            fb_login_button.performClick();
        }
    });

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
                    LoginFBGoogle();

                    //  SignUpFBGoogle();

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
            Toast.makeText(NewSignupActivity.this, "" + exception.toString(), Toast.LENGTH_SHORT).show();
            // tv_forgotpswd.setText("" + exception.toString());
        }

    });


}
    public void CallingSignUpRetrofit()
    {
        progressDialog = new ProgressDialog(NewSignupActivity.this);
        progressDialog.setMessage("Signing Up...");
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Please wait");
        progressDialog.show();

        RestAdapter Radapter = new RestAdapter.Builder().setEndpoint(DomainName.sURL+"signup.php").build();
        RetrofitAPI api = Radapter.create(RetrofitAPI.class);
        api.NormalSignUp(sUserName, sEmail, sPassword, sRefCode,session.getUserFCM(), new Callback<Response>() {
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
                        Intent in = new Intent(NewSignupActivity.this, MainActivity_New.class);
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
                Toast.makeText(NewSignupActivity.this, "Connection Error", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void loadsignup(){

        TabLayout.Tab tab =  tabLayout.getTabAt(1);
        tab.select();
        signupStatus=true;
        et_uname.setFocusable(true);
        edt_refcode.setVisibility(View.VISIBLE);
        alreadyMember.setVisibility(View.VISIBLE);
        et_email.setText("");
        et_pswd.setText("");
        //   login.setVisibility(View.VISIBLE);
        forgotpswd.setVisibility(View.GONE);
        // haveLogin.setVisibility(View.GONE);
        signupNow.setVisibility(View.GONE);
        haveLogin.setText("By signing up you agree to the andhra basekt"+"\n"+ "Terms and Conditions");
        login.setText("SignUp");
        String text = "<font color=#ff000000>Already a member ? </font> <font color=#F6892A>Login</font>";
        alreadyMember.setText(Html.fromHtml(text));
        fname_input.setVisibility(View.VISIBLE);

        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sCode = "google";
                signIn();
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


            Log.e("RAM", "bool" + result.isSuccess());

            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately
                // ...
            }
        }
        if (requestCode == REQUEST_APP_SETTINGS) {
            // if (hasPermissions(requiredPermissions)) {
            // Toast.makeText(this, "All permissions granted!", Toast.LENGTH_SHORT).show();
            //} else {
            //    Toast.makeText(this, "Permissions not granted.", Toast.LENGTH_LONG).show();
            // }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("TAG", "firebaseAuthWithGoogle:" + acct.getId());
        bundle = new Bundle();
        sEmail = acct.getEmail();
        bundle.putString("first_name", acct.getDisplayName());
        bundle.putString("last_name", "");
        bundle.putString("email", sEmail);

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("TAG", "signInWithCredential:onComplete:" + task.isSuccessful());
                        LoginFBGoogle();

                        // SignUpFBGoogle();
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
    public void CallingNormalLogin() {
        progressDialog = new ProgressDialog(NewSignupActivity.this);
        progressDialog.setMessage("Loging in...");
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Please wait...");
        progressDialog.show();
        RestAdapter Radapter = new RestAdapter.Builder().setEndpoint(DomainName.sURL + "login.php").build();
        RetrofitAPI api = Radapter.create(RetrofitAPI.class);
        api.NormalLogin(sEmail, sPassword, session.getUserFCM(), new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {

                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(response.getBody().in()));
                    String output = reader.readLine();
                    progressDialog.dismiss();

                    if(output.contains("invalid credentials")){
                        Toast.makeText(getApplicationContext(),"Invalid User Details",Toast.LENGTH_LONG).show();
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
                            session.setRefCodeSignUp(jsonObject.getString("ref_code_reg"));
                        }
                        session.setIsLoggedIn("true");
                        Intent in = new Intent(NewSignupActivity.this, MainActivity_New.class);
                        in.putExtra(MainActivity_New.KEY_EXTRA, normallogin);

                        finish();
                        startActivity(in);
                    }

                } catch (IOException e) {
                    if (progressDialog.isShowing()) progressDialog.dismiss();
                    e.printStackTrace();
                } catch (JSONException e) {
                    if (progressDialog.isShowing()) progressDialog.dismiss();
                    e.printStackTrace();
                }

            }

            @Override
            public void failure(RetrofitError error) {
                if (progressDialog.isShowing()) progressDialog.dismiss();
                Toast.makeText(NewSignupActivity.this, "Connection Error", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private Bundle getFacebookData(JSONObject object) {
        bundle = new Bundle();
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
            //sUserName =  object.getString("first_name");
            if (object.has("last_name"))
                bundle.putString("last_name", object.getString("last_name"));
            // sUserName = sUserName +" "+ object.getString("last_name");
            if (object.has("email"))
                bundle.putString("email", object.getString("email"));
            sEmail = object.getString("email");

            sUserName=object.getString("first_name")+" "+object.getString("last_name");
           /* if (object.has("gender"))
                bundle.putString("gender", object.getString("gender"));
            if (object.has("birthday"))
                bundle.putString("birthday", object.getString("birthday"));
            if (object.has("location"))
                bundle.putString("location", object.getJSONObject("location").getString("name"));
*/
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return bundle;
    }
    public void LoginFBGoogle() {

        String username = bundle.getString("first_name") + " " + bundle.getString("last_name");
        String useremail = bundle.getString("email");
        progressDialog = new ProgressDialog(NewSignupActivity.this);
        progressDialog.setMessage("Logging in...");
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Please wait");
        progressDialog.show();
        RestAdapter Radapter = new RestAdapter.Builder().setEndpoint(DomainName.sURL + "loginfbgoogle.php").build();
        RetrofitAPI api = Radapter.create(RetrofitAPI.class);

        String testValue=sEmail+" "+"andhrabasket";

        encodeValue = Base64.encode(testValue.getBytes(), Base64.DEFAULT);

        try {
            encodeValue = testValue.getBytes("UTF-8");
            convertedstring = Base64.encodeToString(encodeValue, Base64.DEFAULT);
        } catch (UnsupportedEncodingException e) {

            e.printStackTrace();

        }

        Map<String,String> params = new HashMap<String, String>();
        params.put("email", sEmail);
        params.put("code", sCode);
        params.put("fcm", convertedstring);

        api.FbGoogleLogin(params, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(response.getBody().in()));
                    String output = reader.readLine();

                    progressDialog.dismiss();
                    if (output.contains("no records")) {
                        LoginManager.getInstance().logOut();
                        FacebookSdk.sdkInitialize(getApplicationContext());
                        // Toast.makeText(LoginActivity.this, "Invalid account", Toast.LENGTH_SHORT).show();
                        // SignUpFBGoogle();
                    } else {
                        JSONArray jsonArray = new JSONArray(output);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            jsonObject = jsonArray.getJSONObject(i);
                            session.setUserName(jsonObject.getString("customer_name"));
                            session.setUserEmail(jsonObject.getString("customer_email"));
                            session.setUserId(jsonObject.getString("id"));
                            session.setCustomerId(jsonObject.getString("customer_id"));
                            session.setUserAddress(jsonObject.getString("customer_address"));
                            session.setRefCode(jsonObject.getString("ref_code"));
                            session.setUserMobile(jsonObject.getString("customer_mobile"));
                            session.setRefCodeSignUp(jsonObject.getString("ref_code_reg"));
                            //session.setIsLoggedIn("true");

                        }

                        session.setIsLoggedIn("true");
                        Intent in = new Intent(NewSignupActivity.this, MainActivity_New.class);
                        in.putExtra(MainActivity_New.KEY_EXTRA, strfacebook);
                        in.putExtras(bundle);
                        finish();
                        startActivity(in);

                    }
                } catch (IOException e) {
                    if (progressDialog.isShowing()) progressDialog.dismiss();
                    LoginManager.getInstance().logOut();
                    FacebookSdk.sdkInitialize(getApplicationContext());
                    Toast.makeText(NewSignupActivity.this, "Account Invalid", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                } catch (JSONException e) {
                    if (progressDialog.isShowing()) progressDialog.dismiss();
                    LoginManager.getInstance().logOut();
                    FacebookSdk.sdkInitialize(getApplicationContext());
                    Toast.makeText(NewSignupActivity.this, "Account Invalid", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

            }

            @Override
            public void failure(RetrofitError error) {
                LoginManager.getInstance().logOut();
                FacebookSdk.sdkInitialize(getApplicationContext());
                if (progressDialog.isShowing()) progressDialog.dismiss();
                Toast.makeText(NewSignupActivity.this, "Connection Error", Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

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
}
