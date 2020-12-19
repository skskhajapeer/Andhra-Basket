package com.absket.in;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
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
 * Created by Sreejith on 07-04-2017.
 */
public class NotificationActivity extends Activity {

    NotifyListAdapter adapterNotify;
    ProgressDialog progressDialog;
    UserSessionManager session;
    ImageView img_back;
    ListView list_notify;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notify);
        session = new UserSessionManager(NotificationActivity.this);
        img_back=(ImageView)findViewById(R.id.img_back) ;
        list_notify=(ListView)findViewById(R.id.list_notify);
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        GetNotify();
    }


    public void GetNotify()
    {
        progressDialog = new ProgressDialog(NotificationActivity.this);
        progressDialog.setMessage("Getting Notification...");
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Please wait");
        progressDialog.show();
        RestAdapter Radapter = new RestAdapter.Builder().setEndpoint(DomainName.sURL+"getnotify.php").build();
        RetrofitAPI api = Radapter.create(RetrofitAPI.class);
        api.GetNotify(session.getUserMobile(), new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(response.getBody().in()));
                    String output = reader.readLine();
                    JSONArray jsonArray = new JSONArray(output);
                    progressDialog.dismiss();
                    adapterNotify = new NotifyListAdapter(NotificationActivity.this,jsonArray);
                    list_notify.setAdapter(adapterNotify);

                }
                catch (IOException e)
                {
                    if(progressDialog.isShowing()) progressDialog.dismiss();
                    e.printStackTrace();
                    Toast.makeText(NotificationActivity.this, "No Notifications Recevied Check After Some Time", Toast.LENGTH_SHORT).show();
                }
                catch (JSONException e)
                {
                    if(progressDialog.isShowing()) progressDialog.dismiss();
                    e.printStackTrace();
                    Toast.makeText(NotificationActivity.this, "Try Again", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                if(progressDialog.isShowing()) progressDialog.dismiss();
                Toast.makeText(NotificationActivity.this, "Check Your Internet Connection", Toast.LENGTH_SHORT).show();

            }
        });
    }

    public  class NotifyListAdapter extends BaseAdapter {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        JSONArray jsonArray;


        public NotifyListAdapter(Activity activity, JSONArray jArray) {
            jsonArray = jArray;
            inflater = (LayoutInflater) activity.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }


        public  class ViewHolder {

            CardView card_row_notify;
            TextView tv_tandc,tv_title,tv_shortdesc;
        }

        @Override
        public int getCount() {
            return jsonArray.length();
        } //jArray.length()

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View vi = convertView;
            final ViewHolder holder;

            if (convertView == null) {

                vi = inflater.inflate(R.layout.rows_notify, null);

                holder = new ViewHolder();


                holder.card_row_notify = (CardView) vi.findViewById(R.id.card_row_notify);
                holder.tv_tandc = (TextView) vi.findViewById(R.id.tv_tandc);
                holder.tv_title = (TextView) vi.findViewById(R.id.tv_title);
                holder.tv_tandc.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
                holder.tv_shortdesc = (TextView) vi.findViewById(R.id.tv_shortdesc);


                vi.setTag(holder);
            } else

                holder = (ViewHolder) vi.getTag();

            //holder.card_main_category.startAnimation(AnimationUtils.loadAnimation(MainActivity.activity,R.anim.zoom_in));
            /*ObjectAnimator scaleDown = ObjectAnimator.ofPropertyValuesHolder(holder.card_row_notify,
                    PropertyValuesHolder.ofFloat("scaleX", 1.2f),
                    PropertyValuesHolder.ofFloat("scaleY", 1.2f));
            scaleDown.setDuration(200);
            scaleDown.setRepeatCount(1);
            scaleDown.setRepeatMode(2);
            scaleDown.start();*/

            try {
                holder.tv_title.setText(jsonArray.getJSONObject(position).getString("product_desc"));
               // holder.tv_shortdesc.setText(jsonArray.getJSONObject(position).getString("offer_short_desc"));
            }catch (JSONException e)
            {
                e.printStackTrace();
            }

            return vi;
        }
    }
}
