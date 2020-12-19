package com.absket.in;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
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
import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Sreejith on 03-05-2017.
 */
public class MyOrdersActivity extends Activity {



    UserSessionManager session;
    ProgressDialog progressDialog;

    ArrayList<String> arrayBno = new ArrayList<>(),arrayOdate = new ArrayList<>(),arrayStatus = new ArrayList<>(),arrayTotprice=new ArrayList<>();
    ProductsListAdapter adapterMyOrder;
    ImageView img_back;
    ListView list_categoryproduct;
    SwipeRefreshLayout swipe_refresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myorders);
        session = new UserSessionManager(MyOrdersActivity.this);
        img_back=(ImageView)findViewById(R.id.img_back);
        list_categoryproduct=(ListView)findViewById(R.id.list_categoryproduct) ;
        swipe_refresh=(SwipeRefreshLayout)findViewById(R.id.swipe_refresh);
        swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
              CallingMyOrdersRetrofit();
            }
        });
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        list_categoryproduct.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (list_categoryproduct.getChildAt(0) != null) {
                    swipe_refresh.setEnabled(list_categoryproduct.getFirstVisiblePosition() == 0 && list_categoryproduct.getChildAt(0).getTop() == 0);
                }


            }
        });

        adapterMyOrder = new ProductsListAdapter(MyOrdersActivity.this,null);
        list_categoryproduct.setAdapter(adapterMyOrder);

        CallingMyOrdersRetrofit();



    }

    public void CallingMyOrdersRetrofit()
    {
        progressDialog = new ProgressDialog(MyOrdersActivity.this);
        progressDialog.setMessage("Getting Orders...");
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Please wait");
        progressDialog.show();
        arrayBno = new ArrayList<>();
        arrayOdate = new ArrayList<>();
        arrayStatus = new ArrayList<>();
        arrayStatus = new ArrayList<>();
        RestAdapter Radapter = new RestAdapter.Builder().setEndpoint(DomainName.sURL+"getmyorders.php").build();
        RetrofitAPI api = Radapter.create(RetrofitAPI.class);
        api.GetMyOrders(session.getCustomerId(), new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(response.getBody().in()));
                    String output = reader.readLine();
                    if(swipe_refresh.isRefreshing()) swipe_refresh.setRefreshing(false);
                    progressDialog.dismiss();
                    JSONArray jsonArray = new JSONArray(output);
                    for (int i = 0; i < jsonArray.length(); i++) {

                        arrayBno.add(jsonArray.getJSONObject(i).getString("order_id"));
                        arrayTotprice.add(jsonArray.getJSONObject(i).getString("total_price"));
                        arrayStatus.add(jsonArray.getJSONObject(i).getString("order_status"));
                        arrayOdate.add(jsonArray.getJSONObject(i).getString("order_date"));
                    }
                    adapterMyOrder.notifyDataSetChanged();
                }
                catch (IOException e)
                {
                    if(progressDialog.isShowing()) progressDialog.dismiss();
                    if(swipe_refresh.isRefreshing()) swipe_refresh.setRefreshing(false);
                    e.printStackTrace();
                }
                catch (JSONException e)
                {
                    if(progressDialog.isShowing()) progressDialog.dismiss();
                    if(swipe_refresh.isRefreshing()) swipe_refresh.setRefreshing(false);
                    e.printStackTrace();
                    Toast.makeText(MyOrdersActivity.this, "No Products Found", Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(MyOrdersActivity.this, "Check Your Internet Connection", Toast.LENGTH_SHORT).show();

            }
        });
    }


    public  class ProductsListAdapter extends BaseAdapter {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        JSONArray jsonArray;


        public ProductsListAdapter(Activity activity, JSONArray jArray) {
            jsonArray = jArray;
            inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }


        public  class ViewHolder {
            CardView card_myorders;
            TextView tv_bno,tv_total_price,tv_order_date,tv_delivery_status;
        }

        @Override
        public int getCount() {
            return arrayOdate.size();
        } //jArray.length()

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return arrayOdate.size();
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View vi = convertView;
            final ViewHolder holder;

            if (convertView == null) {
                vi = inflater.inflate(R.layout.row_myorders, null);
                holder = new ViewHolder();
                holder.tv_bno = (TextView) vi.findViewById(R.id.tv_bno);
                holder.tv_total_price = (TextView) vi.findViewById(R.id.tv_total_price);
                holder.tv_order_date = (TextView) vi.findViewById(R.id.tv_order_date);
                holder.tv_delivery_status = (TextView) vi.findViewById(R.id.tv_delivery_status);
                holder.card_myorders = (CardView) vi.findViewById(R.id.card_myorders);

                vi.setTag(holder);
            } else

                holder = (ViewHolder) vi.getTag();

            holder.tv_bno.setText(arrayBno.get(position));
            holder.tv_total_price.setText(arrayTotprice.get(position));
            holder.tv_order_date.setText(arrayOdate.get(position));
            holder.tv_delivery_status.setText(arrayStatus.get(position));

            holder.card_myorders.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent in = new Intent(MyOrdersActivity.this,MyOrdersFullActivity.class);
                    in.putExtra("total_price",arrayTotprice.get(position));
                    in.putExtra("status",arrayStatus.get(position));
                    in.putExtra("order_date",arrayOdate.get(position));
                    in.putExtra("order_id",arrayBno.get(position));
                    startActivity(in);
                }
            });

            return vi;
        }
    }

}
