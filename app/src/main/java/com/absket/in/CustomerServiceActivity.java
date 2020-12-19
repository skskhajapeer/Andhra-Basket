package com.absket.in;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Sreejith on 07-04-2017.
 */
public class CustomerServiceActivity extends Activity {

    CardView card_call;

    CardView card_service1;
    TextView tv_service1;
    TextView tv_service2;
    CardView card_service2;


    ImageView img_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customerservice);

        card_call=(CardView)findViewById(R.id.card_call);
        card_service2=(CardView)findViewById(R.id.card_service2);
        card_service1=(CardView)findViewById(R.id.card_service1);
        tv_service1=(TextView)findViewById(R.id.tv_service1);
        tv_service2=(TextView)findViewById(R.id.tv_service2);
        img_back=(ImageView)findViewById(R.id.img_back);
        card_service1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + tv_service1.getText().toString()));
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        card_service2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + tv_service2.getText().toString()));
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        GetCOntactsTask();
    }

    public void GetCOntactsTask() {
        RestAdapter Radapter = new RestAdapter.Builder().setEndpoint(DomainName.sURL + "getcontacts.php").build();
        RetrofitAPI api = Radapter.create(RetrofitAPI.class);

        api.GetContacts("", new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(response.getBody().in()));
                    String output = reader.readLine();
                    JSONArray jsonArray = new JSONArray(output);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        if (i == 0)
                            tv_service1.setText(jsonArray.getJSONObject(i).getString("phone"));
                        else tv_service2.setText(jsonArray.getJSONObject(i).getString("phone"));
                    }


                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

}
