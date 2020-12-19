package com.absket.in;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidadvance.topsnackbar.TSnackbar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by KhajaPeer on 09-04-2018.
 */

public class ChangePassword extends Activity {

    EditText current_password;
    EditText new_password;
    EditText confirm_password,email_id;
    Button btn_submit;
    ProgressDialog progressDialog;
    String sNewPassword = "", sConfirmPassword = "", sEmail = "";
    ImageView img_close;
    LinearLayout mainlinear;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.change_password);
        current_password = (EditText) findViewById(R.id.current_password);
        new_password = (EditText) findViewById(R.id.new_password);
        confirm_password = (EditText) findViewById(R.id.new_password);
        email_id=(EditText)findViewById(R.id.email_id);
        btn_submit = (Button) findViewById(R.id.btn_submit);
        sNewPassword = new_password.getText().toString();
        sEmail = current_password.getText().toString();
        mainlinear=(LinearLayout)findViewById(R.id.mainlinear);
        img_close = (ImageView) findViewById(R.id.img_close);
        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        sConfirmPassword = confirm_password.getText().toString();
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               /* if(!sNewPassword.equals("") && sNewPassword.equals(sConfirmPassword))
                {
                    ChangePasswordRetrofit();
                }
                else
                {
                    Toast.makeText(ChangePassword.this, "New Password & Confirm Password is not same!", Toast.LENGTH_SHORT).show();
                }*/



                if (email_id.getText().toString().isEmpty()) {
                    TSnackbar snackbar = TSnackbar
                            .make(mainlinear,("Enter Email Id"), TSnackbar.LENGTH_LONG);
                    snackbar.setActionTextColor(Color.WHITE);
                    View snackbarView = snackbar.getView();
                    snackbarView.setBackgroundColor(Color.parseColor("#C04848"));
                    TextView textView = (TextView) snackbarView.findViewById(com.androidadvance.topsnackbar.R.id.snackbar_text);
                    textView.setTextColor(Color.WHITE);
                    textView.setGravity(Gravity.CENTER);
                    snackbar.show();
                    //finish();
                } else if (current_password.getText().toString().isEmpty()) {
                    TSnackbar snackbar = TSnackbar
                            .make(mainlinear, ("Enter Current Password"), TSnackbar.LENGTH_LONG);
                    snackbar.setActionTextColor(Color.WHITE);
                    View snackbarView = snackbar.getView();
                    snackbarView.setBackgroundColor(Color.parseColor("#C04848"));
                    TextView textView = (TextView) snackbarView.findViewById(com.androidadvance.topsnackbar.R.id.snackbar_text);
                    textView.setTextColor(Color.WHITE);
                    textView.setGravity(Gravity.CENTER);
                    snackbar.show();
                    //finish();
                } else if (new_password.getText().toString().isEmpty()) {
                    TSnackbar snackbar = TSnackbar
                            .make(mainlinear, ("Enter New Password"), TSnackbar.LENGTH_LONG);
                    snackbar.setActionTextColor(Color.WHITE);
                    View snackbarView = snackbar.getView();
                    snackbarView.setBackgroundColor(Color.parseColor("#C04848"));
                    TextView textView = (TextView) snackbarView.findViewById(com.androidadvance.topsnackbar.R.id.snackbar_text);
                    textView.setTextColor(Color.WHITE);
                    textView.setGravity(Gravity.CENTER);
                    snackbar.show();
                    //finish();
                }

                else if (confirm_password.getText().toString().isEmpty()) {
                    TSnackbar snackbar = TSnackbar
                            .make(mainlinear, ("Enter Confirm Password"), TSnackbar.LENGTH_LONG);
                    snackbar.setActionTextColor(Color.WHITE);
                    View snackbarView = snackbar.getView();
                    snackbarView.setBackgroundColor(Color.parseColor("#C04848"));
                    TextView textView = (TextView) snackbarView.findViewById(com.androidadvance.topsnackbar.R.id.snackbar_text);
                    textView.setTextColor(Color.WHITE);
                    textView.setGravity(Gravity.CENTER);
                    snackbar.show();
                    //finish();
                }

                else if (!new_password.getText().toString().equalsIgnoreCase(confirm_password.getText().toString())) {
                    TSnackbar snackbar = TSnackbar
                            .make(mainlinear, ("Both passwords should same"), TSnackbar.LENGTH_LONG);
                    snackbar.setActionTextColor(Color.WHITE);
                    View snackbarView = snackbar.getView();
                    snackbarView.setBackgroundColor(Color.parseColor("#C04848"));
                    TextView textView = (TextView) snackbarView.findViewById(com.androidadvance.topsnackbar.R.id.snackbar_text);
                    textView.setTextColor(Color.WHITE);
                    textView.setGravity(Gravity.CENTER);
                    snackbar.show();
                    //finish();
                } else {
                    ChangePasswordRetrofit();
                }
            }
        });
    }

    public void ChangePasswordRetrofit() {
        progressDialog = new ProgressDialog(ChangePassword.this);
        progressDialog.setMessage("Changing Password...");
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Please wait...");
        progressDialog.show();


        RestAdapter Radapter = new RestAdapter.Builder().setEndpoint("http://www.andhrabasket.com/main/andhrabasket/andhrabasketphp/changepassword.php").build();
        RetrofitAPI api = Radapter.create(RetrofitAPI.class);
        api.ChangePassword(email_id.getText().toString(),current_password.getText().toString(), new_password.getText().toString(), new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {

                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(response.getBody().in()));
                    String output = reader.readLine();
                    if (progressDialog.isShowing()) progressDialog.dismiss();
                    if (output.contains("true")) {
                        Toast.makeText(ChangePassword.this, "Password Changed Successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(ChangePassword.this, "Something went wrong! Try Again", Toast.LENGTH_SHORT).show();
                    }

                } catch (IOException e) {
                    if (progressDialog.isShowing()) progressDialog.dismiss();
                    e.printStackTrace();
                    Toast.makeText(ChangePassword.this, e+"", Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void failure(RetrofitError error) {
                if (progressDialog.isShowing()) progressDialog.dismiss();
                Toast.makeText(ChangePassword.this, "Check Your Internet Connection", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
