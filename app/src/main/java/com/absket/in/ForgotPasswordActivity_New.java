package com.absket.in;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
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
 * Created by Sreejith on 19-07-2017.
 */
public class ForgotPasswordActivity_New extends Activity {

    EditText mailid;

    Button btn_submit;
    LinearLayout dragView;
    String sOTP = "", sEmail = "", sIsOTPVerified = "", sGeneratedOTP = "", sNewPassword = "", sConfirmPassword = "";
    ProgressDialog progressDialog;
    ImageView img_close;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_password);
        btn_submit = (Button) findViewById(R.id.btn_submit);
        mailid = (EditText) findViewById(R.id.mailid);
        dragView = (LinearLayout) findViewById(R.id.drag_view);

        sEmail = mailid.getText().toString();

        img_close = (ImageView) findViewById(R.id.img_close);
        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mailid.getText().toString().isEmpty()) {

                    TSnackbar snackbar = TSnackbar
                            .make(dragView, getString(R.string.label_error_email_message), TSnackbar.LENGTH_LONG);
                    snackbar.setActionTextColor(Color.WHITE);
                    View snackbarView = snackbar.getView();
                    snackbarView.setBackgroundColor(Color.parseColor("#C04848"));
                    TextView textView = (TextView) snackbarView.findViewById(com.androidadvance.topsnackbar.R.id.snackbar_text);
                    textView.setTextColor(Color.WHITE);
                    textView.setGravity(Gravity.CENTER);
                    snackbar.show();

                    return;
                }

                if (!isValidEmail(mailid.getText().toString().trim())) {

                    TSnackbar snackbar = TSnackbar
                            .make(dragView, getString(R.string.label_error_email_valid_message), TSnackbar.LENGTH_LONG);
                    snackbar.setActionTextColor(Color.WHITE);
                    View snackbarView = snackbar.getView();
                    snackbarView.setBackgroundColor(Color.parseColor("#C04848"));
                    TextView textView = (TextView) snackbarView.findViewById(com.androidadvance.topsnackbar.R.id.snackbar_text);
                    textView.setTextColor(Color.WHITE);
                    textView.setGravity(Gravity.CENTER);
                    snackbar.show();
                    return;


                }

                ForgotPasswordRetrofit();

            }
        });

    }

    public final static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    public void ForgotPasswordRetrofit() {
        progressDialog = new ProgressDialog(ForgotPasswordActivity_New.this);
        progressDialog.setMessage("Forgot Password...");
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Please wait...");
        progressDialog.show();
        RestAdapter Radapter = new RestAdapter.Builder().setEndpoint("http://www.andhrabasket.com/main/andhrabasket/andhrabasketphp/forgotpassword.php").build();
        RetrofitAPI api = Radapter.create(RetrofitAPI.class);
        api.ChangePassword_New(mailid.getText().toString(), new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {

                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(response.getBody().in()));
                    String output = reader.readLine();
                    if (progressDialog.isShowing()) progressDialog.dismiss();

                    if (output.contains("Message has been sent")) {
                        Toast.makeText(ForgotPasswordActivity_New.this, "Email sent to registered emailID", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(ForgotPasswordActivity_New.this, "Sorry your email not found in our database", Toast.LENGTH_SHORT).show();
                    }
                   /* if(output.contains("true"))
                    {
                        Toast.makeText(ForgotPasswordActivity_New.this, "Password Changed Successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    else {
                        Toast.makeText(ForgotPasswordActivity_New.this, "Something went wrong! Try Again", Toast.LENGTH_SHORT).show();
                    }
*/
                } catch (IOException e) {
                    if (progressDialog.isShowing()) progressDialog.dismiss();
                    e.printStackTrace();
                    Toast.makeText(ForgotPasswordActivity_New.this, "Check Your Internet Connection", Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void failure(RetrofitError error) {
                if (progressDialog.isShowing()) progressDialog.dismiss();
                Toast.makeText(ForgotPasswordActivity_New.this, "Check Your Internet Connection", Toast.LENGTH_SHORT).show();
            }
        });

    }

}
