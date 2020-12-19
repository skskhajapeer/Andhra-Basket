package com.absket.in;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Sreejith on 28-04-2017.
 */
public class CategoryProductsFragment3 extends Fragment {

    SwipeRefreshLayout swipe_refresh;
    ListView list_categoryproduct;
    ProductsListAdapter adapterProduct;
    boolean flag_loading;

    private static DecimalFormat df2 = new DecimalFormat("##");

    ArrayList<String> arrayId = new ArrayList<>();
    ArrayList<String> arrayPId = new ArrayList<>();
    ArrayList<String> arrayPName = new ArrayList<>();
    ArrayList<String> arrayMainCategId = new ArrayList<>();
    ArrayList<String> arrayMainCateg = new ArrayList<>();
    ArrayList<String> arraySubCategId = new ArrayList<>();
    ArrayList<String> arraySubCateg = new ArrayList<>();
    ArrayList<String> arrayPLongDesc = new ArrayList<>();
    ArrayList<String> arrayPShortDesc = new ArrayList<>();
    ArrayList<String> arrayPImage1 = new ArrayList<>();
    ArrayList<String> arrayPImage2 = new ArrayList<>();
    ArrayList<String> arrayPImage3 = new ArrayList<>();
    ArrayList<String> arrayMRP = new ArrayList<>();
    ArrayList<String> arrayDiscount = new ArrayList<>();
    ArrayList<String> arrayPrice = new ArrayList<>();
    ArrayList<String> arrayUnit = new ArrayList<>();
    ArrayList<String> arrayUnitPrice = new ArrayList<>();
    ArrayList<String> arraySelectedUnit = new ArrayList<>(), arraySelectedUnitPrice = new ArrayList<>();






    String sLastProductId="0";

    ProgressDialog progressDialog;
    SQLiteDatabase BasketSQL;

    static FloatingActionButton fab_filter;
    String sFilterOption;

    static String sSubCategory="";

    ArrayList<String> arrayBrands;

    ArrayList<String> arraySpinnerSize[],arraySpinnerCost[],arraySpinnerMRP[],arraySpinnerDisc[];
    ArrayAdapter arraySpinnerAdapter[];

    ArrayList<String> arrayFilterOption;

    ArrayList<String> arrayOutofStock = new ArrayList<>();
    int outostockstr;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_categoryproduct2,null);
        fab_filter = (FloatingActionButton) view.findViewById(R.id.fab_filter);
        list_categoryproduct = (ListView) view.findViewById(R.id.list_categoryproduct);
        swipe_refresh = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        //ButterKnife.inject(getActivity());

        adapterProduct = new ProductsListAdapter(ListProductsActivity.activity,null);
        list_categoryproduct.setAdapter(adapterProduct);

        fab_filter.setVisibility(View.INVISIBLE);

        //call the asycntask to get the products based on main category
       // CallingProductsRetrofit(sLastProductId);*/

        BasketSQL =getActivity().openOrCreateDatabase("ABASKET",Context.MODE_PRIVATE,null);
       // BasketSQL.execSQL("CREATE TABLE IF NOT EXISTS tbl_cart (id VARCHAR,product_id VARCHAR,product_name VARCHAR,product_image1 VARCHAR,product_mrp VARCHAR,product_discount VARCHAR,product_price VARCHAR,quantity VARCHAR,product_unit VARCHAR)");
        BasketSQL.execSQL("CREATE TABLE IF NOT EXISTS tbl_last_cart (id VARCHAR,product_id VARCHAR,product_name VARCHAR,product_image1 VARCHAR,product_mrp VARCHAR,product_discount VARCHAR,product_price VARCHAR,quantity VARCHAR,product_unit VARCHAR)");
        BasketSQL.execSQL("CREATE TABLE IF NOT EXISTS tbl_cart (product_id VARCHAR,product_name VARCHAR,product_image1 VARCHAR,product_price VARCHAR,quantity VARCHAR,product_unit VARCHAR,product_unitprice VARCHAR,product_discount VARCHAR,product_mrp VARCHAR)");


        fab_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(getActivity());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.setContentView(R.layout.dialog_filter);
                CardView card_pricehightolow = (CardView) dialog.findViewById(R.id.card_pricehightolow);
                CardView card_pricelowtohigh = (CardView) dialog.findViewById(R.id.card_pricelowtohigh);
                CardView card_discount = (CardView) dialog.findViewById(R.id.card_discount);
                CardView card_brand = (CardView) dialog.findViewById(R.id.card_brand);


                card_pricelowtohigh.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sFilterOption = "ltoh";
                        dialog.dismiss();
                        CallingSortMainretrofit();

                    }
                });
                card_pricehightolow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sFilterOption = "htol";
                        dialog.dismiss();
                        CallingSortMainretrofit();

                    }
                });

                card_brand.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                         dialog.dismiss();
                         CallingBrandListRetrofit();
                    }
                });

                card_discount.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sFilterOption = "discount";
                        dialog.dismiss();
                        CallingSortMainretrofit();

                    }
                });

                dialog.show();
            }
        });


        swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                CallingProductsRetrofit(sLastProductId);
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
/*
                if(firstVisibleItem+visibleItemCount == totalItemCount && totalItemCount!=0)
                {
                    if(flag_loading == false)
                    {
                        flag_loading = true;
                        String sLastId=arrayId.get(totalItemCount-1);
                        CallingProductsRetrofit(sLastId);
                    }
                }*/


            }
        });
        return view;
    }

    public static void iPosition(int iPos)
    {
        ListProductsActivity.sSubCategory  = ListProductsActivity.arraySubCategory.get(iPos);
        sSubCategory = ListProductsActivity.arraySubCategory.get(iPos);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser)
        {
            //adapterProduct = new ProductsListAdapter(ListProductsActivity.activity,null);
            //list_categoryproduct.setAdapter(adapterProduct);
            ListProductsActivity.iFragPos = 3;
            //call the asycntask to get the products based on main category
            CallingProductsRetrofit(sLastProductId);
        }
    }



    public void CallingProductsRetrofit(String sLastId)
    {
        progressDialog = new ProgressDialog(ListProductsActivity.activity);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Please wait");
        progressDialog.show();

        arrayId = new ArrayList<>();
        arrayPId = new ArrayList<>();
        arrayPName = new ArrayList<>();
        arrayMainCategId = new ArrayList<>();
        arrayMainCateg = new ArrayList<>();
        arraySubCategId = new ArrayList<>();
        arraySubCateg = new ArrayList<>();
        arrayPLongDesc = new ArrayList<>();
        arrayPShortDesc = new ArrayList<>();
        arrayPImage1 = new ArrayList<>();
        arrayPImage2 = new ArrayList<>();
        arrayPImage3 = new ArrayList<>();
        arrayMRP = new ArrayList<>();
        arrayDiscount = new ArrayList<>();
        arrayPrice = new ArrayList<>();
        arrayUnit = new ArrayList<>();
        arrayUnitPrice = new ArrayList<>();
        arraySelectedUnit = new ArrayList<>();
        arraySelectedUnitPrice = new ArrayList<>();
        arrayOutofStock = new ArrayList<>();
        RestAdapter Radapter = new RestAdapter.Builder().setEndpoint(DomainName.sURL+"getproducts.php").build();
        RetrofitAPI api = Radapter.create(RetrofitAPI.class);
        api.GetProducts(ListProductsActivity.sMainCategory, sSubCategory , sLastId, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(response.getBody().in()));
                    String output = reader.readLine();
                    flag_loading = false;
                    progressDialog.dismiss();
                    JSONArray jsonArray = new JSONArray(output);
                    BasketSQL.execSQL("DELETE FROM tbl_products");
                    for(int i=0;i<jsonArray.length();i++)
                    {

                        BasketSQL.execSQL("INSERT INTO tbl_products (id,product_id,product_name,product_image1,product_image2,product_image3,product_mrp,product_discount,product_price,quantity,product_unit,product_unit_price) VALUES ('"+jsonArray.getJSONObject(i).getString("id")+"'," +
                                "'"+jsonArray.getJSONObject(i).getString("product_id")+"','"+jsonArray.getJSONObject(i).getString("product_name")+"','"+jsonArray.getJSONObject(i).getString("product_image1")+"','"+jsonArray.getJSONObject(i).getString("product_image2")+"','"+jsonArray.getJSONObject(i).getString("product_image3")+"','"+jsonArray.getJSONObject(i).getString("product_mrp")+"'" +
                                ",'"+jsonArray.getJSONObject(i).getString("product_discount")+"','"+jsonArray.getJSONObject(i).getString("product_price")+"','0','"+jsonArray.getJSONObject(i).getString("product_unit")+"','"+jsonArray.getJSONObject(i).getString("product_unitprice")+"')");

                        if(!arrayPName.contains(jsonArray.getJSONObject(i).getString("product_name"))) {
                            arrayId.add(jsonArray.getJSONObject(i).getString("id"));
                            arrayPId.add(jsonArray.getJSONObject(i).getString("product_id"));
                            arrayPName.add(jsonArray.getJSONObject(i).getString("product_name"));
                            arrayMainCategId.add(jsonArray.getJSONObject(i).getString("product_maincat_id"));
                            arrayMainCateg.add(jsonArray.getJSONObject(i).getString("product_maincat"));
                            arraySubCategId.add(jsonArray.getJSONObject(i).getString("product_subcat_id"));
                            arraySubCateg.add(jsonArray.getJSONObject(i).getString("product_subcat"));
                            arrayPLongDesc.add(jsonArray.getJSONObject(i).getString("product_longdesc"));
                            arrayPShortDesc.add(jsonArray.getJSONObject(i).getString("product_shortdesc"));
                            arrayPImage1.add(jsonArray.getJSONObject(i).getString("product_image1"));
                            arrayPImage2.add(jsonArray.getJSONObject(i).getString("product_image2"));
                            arrayPImage3.add(jsonArray.getJSONObject(i).getString("product_image3"));
                            arrayMRP.add(jsonArray.getJSONObject(i).getString("product_mrp"));
                            arrayDiscount.add(jsonArray.getJSONObject(i).getString("product_discount"));
                            arrayPrice.add(jsonArray.getJSONObject(i).getString("product_price")); ////main cost
                            arrayUnit.add(jsonArray.getJSONObject(i).getString("product_unit"));  //100g,200g,kg
                            arrayUnitPrice.add(jsonArray.getJSONObject(i).getString("product_unitprice")); //main cost
                            outostockstr = jsonArray.getJSONObject(i).getInt("product_stock");
                            String outofs = String.valueOf(outostockstr);
                            arrayOutofStock.add(outofs);
                            arraySelectedUnit.add("");
                            arraySelectedUnitPrice.add("");
                        }

                    }
                    if(swipe_refresh.isRefreshing()) swipe_refresh.setRefreshing(false);

                    arraySpinnerSize = new ArrayList[jsonArray.length()];
                    arraySpinnerCost = new ArrayList[jsonArray.length()];
                    arraySpinnerMRP = new ArrayList[jsonArray.length()];
                    arraySpinnerDisc = new ArrayList[jsonArray.length()];
                    arraySpinnerAdapter = new ArrayAdapter[jsonArray.length()];
                    adapterProduct.notifyDataSetChanged();
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
                    if(arrayId==null || arrayId.size()==0)
                    Toast.makeText(getActivity(), "No Products Found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                if(progressDialog.isShowing()) progressDialog.dismiss();
                if(swipe_refresh.isRefreshing()) swipe_refresh.setRefreshing(false);
                Toast.makeText(getActivity(), "Connection Error", Toast.LENGTH_SHORT).show();
            }
        });

    }


    public  class ProductsListAdapter extends BaseAdapter {
        LayoutInflater inflater = (LayoutInflater) ListProductsActivity.activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        JSONArray jsonArray;


        public ProductsListAdapter(Activity activity, JSONArray jArray) {
            jsonArray = jArray;
            inflater = (LayoutInflater) ListProductsActivity.activity.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }


        public  class ViewHolder {
            CardView card_row_products,card_addcart,card_outofstock;
            ImageView img_product;
            TextView tv_pname,tv_unit_price,tv_mrp,tv_qty,tv_price,tv_mrpnew;
            LinearLayout linear_size;
            Spinner sp_size;
            TextView tv_size_price,tv_discount;
        }

        @Override
        public int getCount() {
            return arrayId.size();
        } //jArray.length()

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return arrayId.size();
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View vi = convertView;
            final ViewHolder holder;

            if (convertView == null) {
                vi = inflater.inflate(R.layout.rows_products, null);
                holder = new ViewHolder();
                holder.card_row_products = (CardView) vi.findViewById(R.id.card_row_products);
                holder.img_product = (ImageView) vi.findViewById(R.id.img_product);
                holder.tv_pname = (TextView) vi.findViewById(R.id.tv_pname);
                holder.tv_unit_price = (TextView) vi.findViewById(R.id.tv_unit_price);
                holder.tv_mrp = (TextView) vi.findViewById(R.id.tv_mrp);
                holder.tv_mrp.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                holder.tv_price = (TextView) vi.findViewById(R.id.tv_price);
                holder.linear_size = (LinearLayout) vi.findViewById(R.id.linear_size);
                holder.tv_size_price = (TextView) vi.findViewById(R.id.tv_size_price);
                holder.sp_size = (Spinner) vi.findViewById(R.id.sp_size);
                holder.card_addcart  = (CardView) vi.findViewById(R.id.card_addcart);
                holder.tv_discount = (TextView) vi.findViewById(R.id.tv_discount);
                holder.tv_mrpnew = (TextView) vi.findViewById(R.id.tv_mrpnew);
                holder.tv_mrpnew.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                holder.card_outofstock = (CardView) vi.findViewById(R.id.card_outofstock);

                vi.setTag(holder);
            } else

                holder = (ViewHolder) vi.getTag();

            Glide.with(getActivity()).load("http://andhrabasket.com/main/andhrabasket/andhrabasketadmin/"+arrayPImage1.get(position)).into(holder.img_product);

            holder.tv_pname.setText(arrayPName.get(position));
            holder.tv_unit_price.setText(arrayUnit.get(position)+" "+arrayUnitPrice.get(position));

            if(arrayMRP.get(position).equals("0"))
            {
                holder.tv_mrpnew.setVisibility(View.INVISIBLE);
            }
            else {
                holder.tv_mrpnew.setVisibility(View.VISIBLE);
                holder.tv_mrpnew.setText("Rs." + arrayMRP.get(position)); //arrayUniPrice MRP
            }
            holder.tv_price.setText("Rs. "+arrayUnitPrice.get(position));
          //  holder.tv_discount.setText(arrayDiscount.get(position)+" %");

            int diff = Integer.parseInt( arrayMRP.get(position) ) - Integer.parseInt( arrayUnitPrice.get(position) );
            double per = 0;
            if(Integer.parseInt( arrayMRP.get(position) ) > 0){
                per = ((double)diff / (double) Integer.parseInt( arrayMRP.get(position) )) * (double) 100;
            }

            Log.e("RAM", "PER::"+ df2.format(per));


            String outofstockin = arrayOutofStock.get(position);
            Log.d("outofstockvis", outofstockin);
            int outofstockint = Integer.parseInt(arrayOutofStock.get(position));
            if (outofstockint == 0) {
                holder.card_outofstock.setVisibility(View.VISIBLE);
                holder.card_addcart.setVisibility(View.GONE);

            }


            if(arrayDiscount.get(position).equals("0"))
            {
                holder.tv_discount.setVisibility(View.INVISIBLE);
            }
            else
            {
                holder.tv_discount.setVisibility(View.VISIBLE);
            }

            holder.tv_discount.setText(per + " %");

            Cursor cursor_size = BasketSQL.rawQuery("SELECT * FROM tbl_products WHERE product_name='"+arrayPName.get(position)+"'",null);
            if(cursor_size.getCount()>0)
            {
                arraySpinnerSize[position] = new ArrayList<>();
                arraySpinnerCost[position] = new ArrayList<>();
                arraySpinnerMRP[position] = new ArrayList<>();
                arraySpinnerDisc[position] = new ArrayList<>();

                while(cursor_size.moveToNext())
                {
                    arraySpinnerSize[position].add(cursor_size.getString(10));
                    arraySpinnerCost[position].add(cursor_size.getString(11));
                    arraySpinnerMRP[position].add(cursor_size.getString(6));
                    arraySpinnerDisc[position].add(cursor_size.getString(7));
                }
                arraySpinnerAdapter[position] = new ArrayAdapter(getActivity(),android.R.layout.simple_dropdown_item_1line,arraySpinnerSize[position]);
                holder.sp_size.setAdapter(arraySpinnerAdapter[position]);
                final int pos = position;
                final double finalPer = per;

                holder.sp_size.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        holder.tv_size_price.setText("Rs. "+arraySpinnerCost[pos].get(position));
                        holder.tv_mrpnew.setText("Rs."+arraySpinnerMRP[pos].get(position));

                       /* if(arraySpinnerDisc[pos].get(position).equals("0"))
                        {
                            holder.tv_discount.setVisibility(View.INVISIBLE);
                        }
                        else
                        {
                            holder.tv_discount.setText(arraySpinnerMRP[pos].get(position)+"%");
                            holder.tv_discount.setVisibility(View.VISIBLE);
                        }
*/
                        if(finalPer == 0)
                        {
                            holder.tv_discount.setVisibility(View.INVISIBLE);
                        }
                        else
                        {
                            holder.tv_discount.setText(df2.format(finalPer) + "%");
                            holder.tv_discount.setVisibility(View.VISIBLE);
                        }

                        arraySelectedUnit.remove(pos);
                        arraySelectedUnit.add(pos,arraySpinnerSize[pos].get(position));
                        arraySelectedUnitPrice.remove(pos);
                        arraySelectedUnitPrice.add(pos,arraySpinnerCost[pos].get(position));
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }

            Cursor cursor_product_verify = BasketSQL.rawQuery("SELECT * FROM tbl_cart WHERE product_id='"+arrayPId.get(position)+"'",null);
            if(cursor_product_verify.getCount()>0)
            {
                holder.card_addcart.setCardBackgroundColor(Color.parseColor("#76A63F"));
            }
            else
            {
                holder.card_addcart.setCardBackgroundColor(Color.parseColor("#35373E"));
            }

            holder.card_addcart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   /* Cursor cursor_product = BasketSQL.rawQuery("SELECT * FROM tbl_cart WHERE product_id='"+arrayPId.get(position)+"'",null);
                    if(cursor_product.getCount()>0)
                    {
                        BasketSQL.execSQL("UPDATE tbl_cart SET product_price='"+arraySelectedUnitPrice.get(position)+"',product_unit='"+arraySelectedUnit.get(position)+"',product_cart_price='"+arraySelectedUnitPrice.get(position)+"' WHERE product_id='"+arrayPId.get(position)+"'");
                        Toast.makeText(getActivity(), "Cart Updated. Change the Quantity in Check Out Screen", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        BasketSQL.execSQL("INSERT INTO tbl_cart (id,product_id,product_name,product_image1,product_price,quantity,product_unit,product_cart_price) VALUES ('"+arrayId.get(position)+"','"+arrayPId.get(position)+"','"+arrayPName.get(position)+"','"+arrayPImage1.get(position)+"','"+arraySelectedUnitPrice.get(position)+"','1','"+arraySelectedUnit.get(position)+"','"+arrayPrice.get(position)+"')");
                        Toast.makeText(getActivity(), "Added to cart", Toast.LENGTH_SHORT).show();
                        holder.card_addcart.setCardBackgroundColor(Color.parseColor("#76A63F"));
                    }*/
                    Cursor cursor_product = BasketSQL.rawQuery("SELECT * FROM tbl_cart WHERE product_id='" + arrayPId.get(position) + "'", null);
                    if (cursor_product.getCount() > 0) {
                        BasketSQL.execSQL("UPDATE tbl_cart SET product_price='"+arraySelectedUnitPrice.get(position)+"',product_unitprice='"+arraySelectedUnitPrice.get(position)+"',product_unit='"+arraySelectedUnit.get(position)+"' WHERE product_id='"+arrayPId.get(position)+"'");
                        Toast.makeText(getActivity(), "Cart Updated", Toast.LENGTH_SHORT).show();
                    } else {
                        BasketSQL.execSQL("INSERT INTO tbl_cart (product_id,product_name,product_image1,product_price,quantity,product_unit,product_unitprice,product_discount,product_mrp) VALUES ('"+arrayPId.get(position)+"','"+arrayPName.get(position)+"','"+arrayPImage1.get(position)+"','"+arraySelectedUnitPrice.get(position)+"','1','"+arraySelectedUnit.get(position)+"','"+arraySelectedUnitPrice.get(position)+"','"+arrayDiscount.get(position)+"','"+arrayMRP.get(position)+"')");
                        Toast.makeText(getActivity(), "Added to cart", Toast.LENGTH_SHORT).show();
                        holder.card_addcart.setCardBackgroundColor(Color.parseColor("#76A63F"));
                    }

                    ((ListProductsActivity) getActivity()).updateCartValues();


                }
            });




            holder.card_row_products.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent in = new Intent(getActivity(),DescriptionActivity.class);
                    in.putExtra("id",arrayPId.get(position));
                    in.putStringArrayListExtra("spinnersize",arraySpinnerSize[position]);
                    in.putStringArrayListExtra("spinnercost",arraySpinnerCost[position]);
                    in.putExtra("productid",arrayPId.get(position));
                    in.putExtra("productname",arrayPName.get(position));
                    in.putExtra("desc",arrayPLongDesc.get(position));
                    in.putExtra("image1",arrayPImage1.get(position));
                    in.putExtra("image2",arrayPImage2.get(position));
                    in.putExtra("image3",arrayPImage3.get(position));
                    in.putExtra("discount",arrayDiscount.get(position));
                    in.putExtra("mrp",arrayMRP.get(position));

                    startActivity(in);

                }
            });

            return vi;
        }
    }



    public void CallingSortMainretrofit()
    {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Filtering...");
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Please wait");
        progressDialog.show();
        arrayId = new ArrayList<>();
        arrayPId = new ArrayList<>();
        arrayPName = new ArrayList<>();
        arrayMainCategId = new ArrayList<>();
        arrayMainCateg = new ArrayList<>();
        arraySubCategId = new ArrayList<>();
        arraySubCateg = new ArrayList<>();
        arrayPLongDesc = new ArrayList<>();
        arrayPShortDesc = new ArrayList<>();
        arrayPImage1 = new ArrayList<>();
        arrayPImage2 = new ArrayList<>();
        arrayPImage3 = new ArrayList<>();
        //arrayMRP = new ArrayList<>();
        arrayDiscount = new ArrayList<>();
        arrayPrice = new ArrayList<>();
        arrayUnit = new ArrayList<>();
        arrayUnitPrice = new ArrayList<>();

        arraySelectedUnit = new ArrayList<>();
        arraySelectedUnitPrice = new ArrayList<>();
        RestAdapter Radapter = new RestAdapter.Builder().setEndpoint(DomainName.sURL+"sorting_main.php").build();
        RetrofitAPI api = Radapter.create(RetrofitAPI.class);
        api.MainSort(ListProductsActivity.sMainCategory, sSubCategory, sFilterOption,arrayFilterOption, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(response.getBody().in()));
                    String output = reader.readLine();
                    flag_loading = false;
                    progressDialog.dismiss();
                    if (swipe_refresh.isRefreshing()) swipe_refresh.setRefreshing(false);
                    JSONArray jsonArray = new JSONArray(output);
                    BasketSQL.execSQL("DELETE FROM tbl_products");
                    for (int i = 0; i < jsonArray.length(); i++) {

                        BasketSQL.execSQL("INSERT INTO tbl_products (id,product_id,product_name,product_image1,product_image2,product_image3,product_mrp,product_discount,product_price,quantity,product_unit,product_unit_price) VALUES ('"+jsonArray.getJSONObject(i).getString("id")+"'," +
                                "'"+jsonArray.getJSONObject(i).getString("product_id")+"','"+jsonArray.getJSONObject(i).getString("product_name")+"','"+jsonArray.getJSONObject(i).getString("product_image1")+"','"+jsonArray.getJSONObject(i).getString("product_image2")+"','"+jsonArray.getJSONObject(i).getString("product_image3")+"','"+jsonArray.getJSONObject(i).getString("product_mrp")+"'" +
                                ",'"+jsonArray.getJSONObject(i).getString("product_discount")+"','"+jsonArray.getJSONObject(i).getString("product_price")+"','0','"+jsonArray.getJSONObject(i).getString("product_unit")+"','"+jsonArray.getJSONObject(i).getString("product_unitprice")+"')");


                        if(!arrayPName.contains(jsonArray.getJSONObject(i).getString("product_name"))) {
                            arrayId.add(jsonArray.getJSONObject(i).getString("id"));
                            arrayPId.add(jsonArray.getJSONObject(i).getString("product_id"));
                            arrayPName.add(jsonArray.getJSONObject(i).getString("product_name"));
                            arrayMainCategId.add(jsonArray.getJSONObject(i).getString("product_maincat_id"));
                            arrayMainCateg.add(jsonArray.getJSONObject(i).getString("product_maincat"));
                            arraySubCategId.add(jsonArray.getJSONObject(i).getString("product_subcat_id"));
                            arraySubCateg.add(jsonArray.getJSONObject(i).getString("product_subcat"));
                            arrayPLongDesc.add(jsonArray.getJSONObject(i).getString("product_longdesc"));
                            arrayPShortDesc.add(jsonArray.getJSONObject(i).getString("product_shortdesc"));
                            arrayPImage1.add(jsonArray.getJSONObject(i).getString("product_image1"));
                            arrayPImage2.add(jsonArray.getJSONObject(i).getString("product_image2"));
                            arrayPImage3.add(jsonArray.getJSONObject(i).getString("product_image3"));
                            //arrayMRP.add(jsonArray.getJSONObject(i).getString("product_mrp"));
                            arrayDiscount.add(jsonArray.getJSONObject(i).getString("product_discount"));
                            arrayPrice.add(jsonArray.getJSONObject(i).getString("product_price"));
                            arrayUnit.add(jsonArray.getJSONObject(i).getString("product_unit"));
                            arrayUnitPrice.add(jsonArray.getJSONObject(i).getString("product_unitprice"));
                            arraySelectedUnit.add("");
                            arraySelectedUnitPrice.add("");
                        }

                    }

                    arraySpinnerSize = new ArrayList[jsonArray.length()];
                    arraySpinnerCost = new ArrayList[jsonArray.length()];
                    arraySpinnerMRP = new ArrayList[jsonArray.length()];
                    arraySpinnerDisc = new ArrayList[jsonArray.length()];
                    arraySpinnerAdapter = new ArrayAdapter[jsonArray.length()];

                    adapterProduct.notifyDataSetChanged();
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
                    Toast.makeText(getActivity(), "No Products Found", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void failure(RetrofitError error) {
                if(progressDialog.isShowing()) progressDialog.dismiss();
                Toast.makeText(getActivity(), "Connection Error", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void CallingBrandListRetrofit()
    {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading Brand List...");
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Please wait");
        progressDialog.show();
        arrayBrands = new ArrayList<>();
        RestAdapter Radapter = new RestAdapter.Builder().setEndpoint(DomainName.sURL+"getbrandlist.php").build();
        RetrofitAPI api = Radapter.create(RetrofitAPI.class);
        api.GetBrandList(ListProductsActivity.sMainCategory,sSubCategory, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {

                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(response.getBody().in()));
                    String output = reader.readLine();
                    progressDialog.dismiss();
                    JSONArray jsonArray = new JSONArray(output);
                    for(int i=0;i<jsonArray.length();i++)
                    {
                        arrayBrands.add(jsonArray.getJSONObject(i).getString("brand_name"));
                    }

                    if(arrayBrands.size()>0)
                    {
                        arrayFilterOption = new ArrayList<String>();
                        final Dialog dialog_brand = new Dialog(getActivity());
                        dialog_brand.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog_brand.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog_brand.setContentView(R.layout.dialog_brand_list);
                        ListView list_brand = (ListView) dialog_brand.findViewById(R.id.list_brands);
                        CardView card_search = (CardView) dialog_brand.findViewById(R.id.card_search);
                        /*ArrayAdapter adapter = new ArrayAdapter(getActivity(),android.R.layout.simple_dropdown_item_1line,arrayBrands);
                        list_brand.setAdapter(adapter);
                        list_brand.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                dialog_brand.dismiss();
                                sFilterOption = arrayBrands.get(position);
                                if(arrayFilterOption.contains(sFilterOption))
                                {
                                    arrayFilterOption.remove(sFilterOption);
                                }
                                else
                                {
                                    arrayFilterOption.add(sFilterOption);
                                }

                                CallingSortMainretrofit();
                            }
                        });*/
                        MultiBrandSelectorAdapter adapter = new MultiBrandSelectorAdapter(getActivity(),null);
                        list_brand.setAdapter(adapter);

                        card_search.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog_brand.dismiss();
                                if(arrayFilterOption.size()>0) {
                                    sFilterOption = arrayFilterOption.get(0);
                                    CallingSortMainretrofit();
                                }
                                else
                                    Toast.makeText(getActivity(), "Please select a brand", Toast.LENGTH_SHORT).show();
                            }
                        });

                        dialog_brand.show();
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
                Toast.makeText(getActivity(), "Connection Error", Toast.LENGTH_SHORT).show();

            }
        });

    }

    public  class MultiBrandSelectorAdapter extends BaseAdapter {
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        JSONArray jsonArray;


        public MultiBrandSelectorAdapter(Activity activity, JSONArray jArray) {
            jsonArray = jArray;
            inflater = (LayoutInflater) activity.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }


        public  class ViewHolder {
            TextView tv_brand;
            CheckBox cb_brand;
        }

        @Override
        public int getCount() {
            return arrayBrands.size();
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

                vi = inflater.inflate(R.layout.row_multibrand_select, null);

                holder = new ViewHolder();

                holder.tv_brand = (TextView) vi.findViewById(R.id.tv_brand);
                holder.cb_brand = (CheckBox) vi.findViewById(R.id.cb_brand);



                vi.setTag(holder);
            } else

                holder = (ViewHolder) vi.getTag();

               if(arrayFilterOption.contains(holder.tv_brand.getText().toString()))
               {
                   holder.cb_brand.setChecked(true);
               }
               else
               {
                   holder.cb_brand.setChecked(false);
               }

            holder.tv_brand.setText(arrayBrands.get(position));

            holder.cb_brand.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(holder.cb_brand.isChecked())
                    {
                        arrayFilterOption.add(holder.tv_brand.getText().toString());
                    }
                    else {
                        arrayFilterOption.remove(holder.tv_brand.getText().toString());
                    }
                }
            });



            return vi;
        }
    }



}

