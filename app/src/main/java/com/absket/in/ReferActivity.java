package com.absket.in;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
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
 * Created by Sreejith on 08-05-2017.
 */
public class ReferActivity extends Activity {

    CardView card_refer;

    UserSessionManager session;

    ProgressDialog progressDialog;
    TextView tv_bal_amount;
    TextView tv_refer_code;
    TextView tv_total_amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refer);
        tv_refer_code = (TextView) findViewById(R.id.tv_refer_code);
        session = new UserSessionManager(ReferActivity.this);
        tv_total_amount = (TextView) findViewById(R.id.tv_total_amount);
        tv_bal_amount= (TextView) findViewById(R.id.tv_bal_amount);
        tv_refer_code.setText(session.getRefCode());
        card_refer=(CardView)findViewById(R.id.card_refer);
        card_refer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = "Use Referal Code "+session.getRefCode()+" while registering, to get 50 rs cashback for your first purchase. Download AndhraBasket Android App Now: https://play.google.com/store/apps/details?id=com.absket.in";
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Download AndhraBasket");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
            }
        });

        GetCashRetrofit();

    }

    public void GetCashRetrofit()
    {
        progressDialog = new ProgressDialog(ReferActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setTitle("Please Wait...");
        progressDialog.setCancelable(false);
        progressDialog.dismiss();
        RestAdapter Radapter = new RestAdapter.Builder().setEndpoint(DomainName.sURL+"getcash.php").build();
        RetrofitAPI api = Radapter.create(RetrofitAPI.class);
        api.GetCash(session.getRefCode(), new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {

                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(response.getBody().in()));
                    String output = reader.readLine();
                    output = output.replaceAll("\"","");
                    output = output.replaceAll(" ","");
                    if(progressDialog.isShowing()) progressDialog.dismiss();
                    if(output.equals("0@0"))
                    {
                        Toast.makeText(ReferActivity.this, "No referal amount detected", Toast.LENGTH_SHORT).show();
                        tv_total_amount.setText("No Referal Amount");
                        tv_bal_amount.setText("");
                    }
                    else {
                        output = output.replaceAll("\"","");
                        output = output.replaceAll(" ","");
                        String sm[] = output.split("@");
                        tv_total_amount.setText("Total Referal Amount : Rs. " + sm[0]);
                        tv_bal_amount.setText("Balance Referal Amount : Rs. "+sm[1]);
                    }
                }
                catch (IOException e)
                {
                    if(progressDialog.isShowing()) progressDialog.dismiss();
                    e.printStackTrace();
                    Toast.makeText(ReferActivity.this, "Check Your Internet Connection", Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void failure(RetrofitError error) {
                progressDialog.dismiss();
                Toast.makeText(ReferActivity.this, "Check Your Internet Connection", Toast.LENGTH_SHORT).show();

            }
        });
    }

}
