package com.absket.in;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.absket.in.model.SelectedProductBean;
import com.absket.in.model.Singleton;

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
 * Created by Sreejith on 07-04-2017.
 */
public class ListProductsActivity extends AppCompatActivity {

    /*@InjectView(R.id.list_products) ListView list_products;
    ProductsListAdapter adapterProducts;*/

    FloatingActionButton fab_cart;


    static Activity activity;

    static ArrayList<String> arraySubCategory;
    ProgressDialog progressDialog;
    static String sMainCategory="";
    String sFilterOption="";
    static String sSubCategory="";
    String sBundleSubCateg="";


    SQLiteDatabase BasketSQL;
    CharSequence mTiles[];
   // @InjectView(R.id.img_filter) ImageView img_filter;
    UserSessionManager session;

    CardView card_search;
    FloatingActionButton fab_filter;
    ViewPager pager;
    SlidingTabLayout tabs;
    ViewPagerAdapterCategoryProducts adapterPager;
    ImageView img_back;
    static int iFragPos;
    private TextView badgeText = null;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listproducts);
        activity = this;
        card_search=(CardView)findViewById(R.id.card_search) ;
        fab_filter=(FloatingActionButton)findViewById(R.id.fab_filter);
        pager=(ViewPager)findViewById(R.id.pager) ;
        tabs=(SlidingTabLayout)findViewById(R.id.tabs) ;
        img_back=(ImageView)findViewById(R.id.img_back) ;
        fab_cart=(FloatingActionButton)findViewById(R.id.fab_cart);
        session = new UserSessionManager(ListProductsActivity.this);
        badgeText = (TextView) findViewById(R.id.badge_text);
        badgeText.setText("");

        Bundle b = getIntent().getExtras();
        sMainCategory = b.getString("maincategory");
        sBundleSubCateg = b.getString("subcategory");


        BasketSQL = ListProductsActivity.this.openOrCreateDatabase("ABASKET",MODE_PRIVATE,null);
     //   BasketSQL.execSQL("CREATE TABLE IF NOT EXISTS tbl_cart (id VARCHAR,product_id VARCHAR,product_name VARCHAR,product_image1 VARCHAR,product_mrp VARCHAR,product_discount VARCHAR,product_price VARCHAR,quantity VARCHAR,product_unit VARCHAR,product_cart_price VARCHAR)");
        BasketSQL.execSQL("CREATE TABLE IF NOT EXISTS tbl_last_cart (id VARCHAR, product_id VARCHAR,product_name VARCHAR,product_image1 VARCHAR,product_mrp VARCHAR,product_discount VARCHAR,product_price VARCHAR,quantity VARCHAR,product_unit VARCHAR)");
        BasketSQL.execSQL("CREATE TABLE IF NOT EXISTS tbl_products (id VARCHAR,product_id VARCHAR,product_name VARCHAR,product_image1 VARCHAR,product_image2 VARCHAR,product_image3 VARCHAR,product_mrp VARCHAR,product_discount VARCHAR,product_price VARCHAR,quantity VARCHAR,product_unit VARCHAR,product_unit_price VARCHAR)");
        BasketSQL.execSQL("CREATE TABLE IF NOT EXISTS tbl_cart (product_id VARCHAR,product_name VARCHAR,product_image1 VARCHAR,product_price VARCHAR,quantity VARCHAR,product_unit VARCHAR,product_unitprice VARCHAR,product_discount VARCHAR,product_mrp VARCHAR)");

        /* adapterProducts = new ProductsListAdapter(ListProductsActivity.this,null);
        list_products.setAdapter(adapterProducts);*/


        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        updateCartValues();

        fab_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (iFragPos)
                {
                    case 1: CategoryProductsFragment.fab_filter.performClick();
                        break;
                    case 2: CategoryProductsFragmentNew.fab_filter.performClick();
                        break;

                }

            }
        });


       /* img_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(ListProductsActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.setContentView(R.layout.dialog_filter);
                CardView card_pricehightolow = (CardView) dialog.findViewById(R.id.card_pricehightolow);
                CardView card_pricelowtohigh = (CardView) dialog.findViewById(R.id.card_pricelowtohigh);
                CardView card_discount = (CardView) dialog.findViewById(R.id.card_discount);


                card_pricelowtohigh.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sFilterOption = "ltoh";
                        dialog.dismiss();

                    }
                });
                card_pricehightolow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sFilterOption = "htol";
                        dialog.dismiss();

                    }
                });

                card_discount.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sFilterOption = "discount";
                        dialog.dismiss();

                    }
                });

                dialog.show();
            }
        });*/


        fab_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* if(session.getIsLoggedIn().equals("") || session.getUserMobile().equals("") || session.getUserAddress().equals(""))
                {
                    final Dialog dialog = new Dialog(ListProductsActivity.this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.setContentView(R.layout.dialog_warning);
                    CardView card_ok = (CardView) dialog.findViewById(R.id.card_ok);
                    card_ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                }
                else
                {*/

                    Intent in = new Intent(ListProductsActivity.this,ReviewOrdersActivity.class);
                    startActivityForResult(in, 1);
              //  }


            }
        });


        card_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(ListProductsActivity.this,SearchActivity.class);
                startActivityForResult(in, 1);
            }
        });

        CallingSubCategoryRetrofit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                adapterPager.notifyDataSetChanged();
                updateCartValues();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }

    public void updateCartValues(){
        badgeText.setText("");
        int count = 0;
        for(SelectedProductBean bean: Singleton.getInstance().selectedItemsList){
            count += bean.getQuantity();
        }
        if(count > 0){
            badgeText.setVisibility(View.VISIBLE);
            badgeText.setText(""+count);
        }
        else{
            badgeText.setVisibility(View.GONE);
        }

    }

    public void CallingSubCategoryRetrofit()
    {
        progressDialog = new ProgressDialog(ListProductsActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Please wait");
        progressDialog.show();
        arraySubCategory = new ArrayList<>();
        RestAdapter Radapter = new RestAdapter.Builder().setEndpoint(DomainName.sURL+"getsubcategory.php").build();
        RetrofitAPI api = Radapter.create(RetrofitAPI.class);
        api.GetSubCategory(sMainCategory, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(response.getBody().in()));
                    String output = reader.readLine();
                    progressDialog.dismiss();
                    JSONArray jsonArray = new JSONArray(output);
                    mTiles = new CharSequence[jsonArray.length()];
                    for (int i=0;i<jsonArray.length();i++)
                    {
                        arraySubCategory.add(jsonArray.getJSONObject(i).getString("subcat_name"));
                        mTiles[i]= jsonArray.getJSONObject(i).getString("subcat_name");
                    }
                    adapterPager = new ViewPagerAdapterCategoryProducts(getSupportFragmentManager(),arraySubCategory.size(),mTiles);
                    pager.setAdapter(adapterPager);
                    tabs.setViewPager(pager);


                    if(sBundleSubCateg!=null && !sBundleSubCateg.equals(""))
                    {
                        int index = arraySubCategory.indexOf(sBundleSubCateg);
                        pager.setCurrentItem(index);
                    }


                }
                catch (IOException e)
                {
                    if(progressDialog.isShowing()) progressDialog.dismiss();
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
                Toast.makeText(ListProductsActivity.this, "Connection Error", Toast.LENGTH_SHORT).show();

            }
        });
    }






}
