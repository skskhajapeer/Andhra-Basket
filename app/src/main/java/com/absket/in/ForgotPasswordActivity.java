package com.absket.in;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Sreejith on 19-07-2017.
 */
public class ForgotPasswordActivity extends Activity {

    EditText edt_email;
    CardView card_sendotp;
    EditText edt_otp;
    CardView card_verify;
    EditText edt_newpswd;
    EditText edt_confirmpswd;
    CardView card_change;
    TextView tv_verify;
    TextView tv_sendotp;


    String sOTP="",sEmail="",sIsOTPVerified="",sGeneratedOTP="",sNewPassword="",sConfirmPassword="";
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpswd);
        edt_email=(EditText)findViewById(R.id.edt_email);
        edt_otp=(EditText)findViewById(R.id.edt_otp);
        edt_newpswd=(EditText)findViewById(R.id.edt_newpswd);
        edt_confirmpswd=(EditText)findViewById(R.id.edt_confirmpswd);
        card_verify=(CardView)findViewById(R.id.card_verify);
        card_sendotp=(CardView)findViewById(R.id.card_sendotp);
        tv_sendotp=(TextView)findViewById(R.id.tv_sendotp);
        tv_verify=(TextView)findViewById(R.id.tv_verify);
        card_change=(CardView)findViewById(R.id.card_change);
        card_sendotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sEmail = edt_email.getText().toString();

                SendOTPRetrfoti();

                /*if(!sEmail.contains("@") || !sEmail.contains(".") || sEmail.equals(""))
                {
                    Toast.makeText(ForgotPasswordActivity.this, "Check Your Email Id", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    SendOTPRetrfoti();
                }*/
            }
        });


        card_verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sOTP = edt_otp.getText().toString();
                if(sOTP.equals(sGeneratedOTP))
                {
                    Toast.makeText(ForgotPasswordActivity.this, "OTP Verified", Toast.LENGTH_SHORT).show();
                    card_verify.setBackgroundColor(Color.parseColor("#F6892A"));
                    sIsOTPVerified="yes";
                }
                else
                {
                    Toast.makeText(ForgotPasswordActivity.this, "Check entered OTP", Toast.LENGTH_SHORT).show();
                    sIsOTPVerified="";
                }

            }
        });


        card_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sNewPassword = edt_newpswd.getText().toString();
                sConfirmPassword = edt_confirmpswd.getText().toString();
                if(!sNewPassword.equals("") && sNewPassword.equals(sConfirmPassword))
                {
                   // ChangePasswordRetrofit();
                }
                else
                {
                    Toast.makeText(ForgotPasswordActivity.this, "New Password & Confirm Password is not same!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void SendOTPRetrfoti()
    {
        progressDialog = new ProgressDialog(ForgotPasswordActivity.this);
        progressDialog.setMessage("Sending OTP...");
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Please wait...");
        progressDialog.show();
        RestAdapter Radapter = new RestAdapter.Builder().setEndpoint(DomainName.sURL+"sendotp.php").build();
        RetrofitAPI api = Radapter.create(RetrofitAPI.class);
        api.SendOTP(sEmail, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {

                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(response.getBody().in()));
                    String output = reader.readLine();
                    output = output.replaceAll("\"","");
                    output = output.replaceAll(" ","");
                    sGeneratedOTP = output;
                    if(progressDialog.isShowing()) progressDialog.dismiss();
                    Toast.makeText(ForgotPasswordActivity.this, "OTP Sent Successfully! Check Your Mail", Toast.LENGTH_SHORT).show();
                    card_sendotp.setCardBackgroundColor(Color.parseColor("#F6892A"));
                    tv_sendotp.setText("OTP SENT");

                }
                catch (IOException e)
                {
                    if(progressDialog.isShowing()) progressDialog.dismiss();
                    e.printStackTrace();
                    Toast.makeText(ForgotPasswordActivity.this, "Check Your Internet Connection", Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void failure(RetrofitError error) {
                if(progressDialog.isShowing()) progressDialog.dismiss();
                Toast.makeText(ForgotPasswordActivity.this, "Check Your Internet Connection", Toast.LENGTH_SHORT).show();
            }
        });
    }



   /* public void ChangePasswordRetrofit()
    {
        progressDialog = new ProgressDialog(ForgotPasswordActivity.this);
        progressDialog.setMessage("Changing Password...");
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Please wait...");
        progressDialog.show();
        RestAdapter Radapter = new RestAdapter.Builder().setEndpoint(DomainName.sURL+"changepassword.php").build();
        RetrofitAPI api = Radapter.create(RetrofitAPI.class);
        api.ChangePassword(sEmail,sNewPassword, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {

                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(response.getBody().in()));
                    String output = reader.readLine();
                    if(progressDialog.isShowing()) progressDialog.dismiss();
                    if(output.contains("true"))
                    {
                        Toast.makeText(ForgotPasswordActivity.this, "Password Changed Successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    else {
                        Toast.makeText(ForgotPasswordActivity.this, "Something went wrong! Try Again", Toast.LENGTH_SHORT).show();
                    }

                }
                catch (IOException e)
                {
                    if(progressDialog.isShowing()) progressDialog.dismiss();
                    e.printStackTrace();
                    Toast.makeText(ForgotPasswordActivity.this, "Check Your Internet Connection", Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void failure(RetrofitError error) {
                if(progressDialog.isShowing()) progressDialog.dismiss();
                Toast.makeText(ForgotPasswordActivity.this, "Check Your Internet Connection", Toast.LENGTH_SHORT).show();
            }
        });

    }*/

}
