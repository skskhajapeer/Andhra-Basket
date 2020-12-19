package com.absket.in;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Sreejith on 11-05-2017.
 */
public class SpecialShopsActivity extends Activity {




    ArrayList<String> arraySubCategory,arraySubCategoryImg;

    String sMainCatgeory="";
    ProgressDialog progressDialog;
    MainCategoryDrawerAdapter adapter;
    ImageView img_back;
    GridView grid_special;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specialshops);
        img_back=(ImageView)findViewById(R.id.img_back);
        grid_special=(GridView)findViewById(R.id.grid_special);
        Bundle b = getIntent().getExtras();
        sMainCatgeory = b.getString("maincategory");
        CallingSpecialShopRetrofit();

        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    public void CallingSpecialShopRetrofit()
    {
        progressDialog = new ProgressDialog(SpecialShopsActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Please wait");
        progressDialog.show();
        arraySubCategoryImg = new ArrayList<>();
        arraySubCategory = new ArrayList<>();
        RestAdapter Radapter = new RestAdapter.Builder().setEndpoint(DomainName.sURL+"getspecialshops.php").build();
        RetrofitAPI api = Radapter.create(RetrofitAPI.class);
        api.GetSpecialShops(sMainCatgeory, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {

                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(response.getBody().in()));
                    String output = reader.readLine();
                    progressDialog.dismiss();
                    JSONArray jsonArray = new JSONArray(output);
                    for(int i=0;i<jsonArray.length();i++)
                    {
                        arraySubCategory.add(jsonArray.getJSONObject(i).getString("subcat_name"));
                        arraySubCategoryImg.add(jsonArray.getJSONObject(i).getString("subcat_image"));
                    }

                    adapter = new MainCategoryDrawerAdapter(SpecialShopsActivity.this,null);
                    grid_special.setAdapter(adapter);



                }
                catch (IOException e)
                {
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
                Toast.makeText(SpecialShopsActivity.this, "Connection Error", Toast.LENGTH_SHORT).show();

            }
        });

    }



    public class MainCategoryDrawerAdapter extends BaseAdapter
    {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        JSONArray jsonArray;


        public MainCategoryDrawerAdapter(Activity activity, JSONArray jArray) {
            jsonArray = jArray;
            inflater = (LayoutInflater) activity.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }


        public  class ViewHolder {
            ImageView img_category;
            TextView tv_categoryname;
            CardView card_ss;
        }

        @Override
        public int getCount() {
            return arraySubCategory.size();
        }

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

                vi = inflater.inflate(R.layout.rows_special_sopd, null);

                holder = new ViewHolder();

                holder.img_category = (ImageView) vi.findViewById(R.id.img_category);
                holder.tv_categoryname = (TextView) vi.findViewById(R.id.tv_categoryname);
                holder.card_ss = (CardView) vi.findViewById(R.id.card_ss);
                vi.setTag(holder);

            } else

                holder = (ViewHolder) vi.getTag();


            holder.tv_categoryname.setText(arraySubCategory.get(position));
            Glide.with(SpecialShopsActivity.this).load("http://andhrabasket.com/main/andhrabasket/andhrabasketadmin/"+arraySubCategoryImg.get(position)).into(holder.img_category);
            holder.card_ss.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent in = new Intent(SpecialShopsActivity.this,ListProductsActivity.class);
                    in.putExtra("maincategory",sMainCatgeory);
                    in.putExtra("subcategory",arraySubCategory.get(position));
                    startActivity(in);
                }
            });


            return vi;
        }
    }
}
