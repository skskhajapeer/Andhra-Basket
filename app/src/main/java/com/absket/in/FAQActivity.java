package com.absket.in;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.Toast;

import com.absket.in.adapter.ExpandableListAdapter1;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Sreejith on 13-07-2017.
 */
public class FAQActivity extends Activity {

    ExpandableListAdapter1 listAdapter;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    ExpandableListView lvExp;
    UserSessionManager session;
    int lastExpandedPosition=-1;
    ImageView img_back;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);
        lvExp = (ExpandableListView) findViewById(R.id.lvExp);
        session = new UserSessionManager(FAQActivity.this);
        img_back = (ImageView) findViewById(R.id.img_back);
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
        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();

        RestAdapter Radapter = new RestAdapter.Builder().setEndpoint(DomainName.sURL+"getfaq.php").build();
        RetrofitAPI api = Radapter.create(RetrofitAPI.class);

        api.GetFAQ(session.getUserMobile(), new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(response.getBody().in()));
                    String output = reader.readLine();
                    JSONArray jsonArray = new JSONArray(output);
                    int count;
                    for(int i=0;i<jsonArray.length();i++)
                    {
                        List<String> header = new ArrayList<String>();
                        count = i+1;
                        listDataHeader.add(""+count+". "+jsonArray.getJSONObject(i).getString("question"));
                        header.add(jsonArray.getJSONObject(i).getString("answer"));
                        listDataChild.put(listDataHeader.get(i),header);
                    }

                    listAdapter = new ExpandableListAdapter1(FAQActivity.this, listDataHeader, listDataChild);
                    lvExp.setAdapter(listAdapter);


                    lvExp.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

                        @Override
                        public void onGroupExpand(int groupPosition) {
                            if (lastExpandedPosition != -1
                                    && groupPosition != lastExpandedPosition) {
                                lvExp.collapseGroup(lastExpandedPosition);
                            }
                            lastExpandedPosition = groupPosition;
                        }
                    });

                }
                catch (IOException e)
                {
                    e.printStackTrace();
                    Toast.makeText(FAQActivity.this, "Check Your Internet Connection", Toast.LENGTH_SHORT).show();

                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                    Toast.makeText(FAQActivity.this, "Check Your Internet Connection", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(FAQActivity.this, "Check Your Internet Connection", Toast.LENGTH_SHORT).show();

            }
        });
    }

}
