package com.absket.in;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
 * Created by Sreejith on 01-06-2017.
 */
public class OtpActivity extends Activity {

    TextView tv_about;
    UserSessionManager session;
    ImageView img_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aboutus);
        tv_about = (TextView) findViewById(R.id.tv_about);
        img_back = (ImageView) findViewById(R.id.img_back);
        session = new UserSessionManager(this);
        GetAboutUsTask();

        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    public void GetAboutUsTask()
    {
        RestAdapter Radapter = new RestAdapter.Builder().setEndpoint(DomainName.sURL+"getaboutus.php").build();
        RetrofitAPI api = Radapter.create(RetrofitAPI.class);

        api.GetAboutUs(session.getUserMobile(), new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(response.getBody().in()));
                    String output = reader.readLine();
                    JSONArray jsonArray = new JSONArray(output);
                    tv_about.setText(jsonArray.getJSONObject(0).getString("terms"));
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                    Toast.makeText(OtpActivity.this, "Check Your Internet Connection", Toast.LENGTH_SHORT).show();

                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                    Toast.makeText(OtpActivity.this, "Check Your Internet Connection", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(OtpActivity.this, "Check Your Internet Connection", Toast.LENGTH_SHORT).show();

            }
        });
    }


}
