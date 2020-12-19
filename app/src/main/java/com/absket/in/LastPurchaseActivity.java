package com.absket.in;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.absket.in.CustomFonts.MyTextView;
import com.absket.in.model.ItemBean;
import com.absket.in.model.ProductItemBean;
import com.absket.in.model.SelectedProductBean;
import com.absket.in.model.Singleton;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Sreejith on 08-05-2017.
 */
public class LastPurchaseActivity extends Activity {


    SQLiteDatabase BasketSQL;
    ArrayList<String> arrayPIdold;
    ArrayList<String> arrayPNameOld;
    LastPurchaseActivity context;
    int selectedItemPosition = 0;

    ProductsListAdapter adapterProduct;
    private static DecimalFormat df2 = new DecimalFormat("##");

    ArrayList<String> arrayPId;
    ArrayList<String> arrayPName;
    ArrayList<String> arrayPImage1;
    ArrayList<String> arrayMRP;
    ArrayList<String> arrayDiscount;
    ArrayList<String> arrayPrice;
    ArrayList<String> arrayQty;

    ProgressDialog progressDialog;

    ArrayList<String> arrayId;
    ArrayList<String> arrayMainCategId;
    ArrayList<String> arrayMainCateg;
    ArrayList<String> arraySubCategId;
    ArrayList<String> arraySubCateg;
    ArrayList<String> arrayPLongDesc;
    ArrayList<String> arrayPShortDesc;
    ArrayList<String> arrayPImage2;
    ArrayList<String> arrayPImage3;
    ArrayList<String> arrayUnit;
    ArrayList<String> arrayUnitPrice;


    ArrayList<String> arraySpinnerSize[], arraySpinnerCost[];
    ArrayAdapter arraySpinnerAdapter[];

    ArrayList<String> arraySelectedUnit = new ArrayList<>(), arraySelectedUnitPrice = new ArrayList<>();
    UserSessionManager session;
    ListView list_lastpurchase;
    FloatingActionButton fab_cart;
    ImageView img_back;
    // ProductsListAdapter adapterProducts;
    TextView tv_total;
    private ArrayList<ProductItemBean> itemsList = new ArrayList<>();
    private TextView badgeText = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_last_purchase);
        list_lastpurchase = (ListView) findViewById(R.id.list_lastpurchase);


        session = new UserSessionManager(LastPurchaseActivity.this);
        fab_cart = (FloatingActionButton) findViewById(R.id.fab_cart);
        tv_total = (TextView) findViewById(R.id.tv_total);

        tv_total.setText("Total Rs. " + session.getLastPurchasePrice());
        img_back = (ImageView) findViewById(R.id.img_back);

        badgeText = (TextView) findViewById(R.id.badge_text);
        badgeText.setText("");

        //CallingLastPurchaseRetrofit();

        lastPurchase();


        fab_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(LastPurchaseActivity.this, ReviewOrdersActivity.class);
                startActivity(in);
            }
        });

        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        updatePrice();

    }

    public void updatePrice(){
        double price = 0;
        for (int i = 0; i < Singleton.getInstance().selectedItemsList.size(); i++) {
            price += Double.parseDouble(Singleton.getInstance().selectedItemsList.get(i).getProductUnitprice()) * Singleton.getInstance().selectedItemsList.get(i).getQuantity();
        }

        tv_total.setText("Total Rs. " + price);

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


    public void lastPurchase() {
        progressDialog = new ProgressDialog(LastPurchaseActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Please wait");
        progressDialog.show();

        adapterProduct = new ProductsListAdapter();
        Log.d("useridis", session.getCustomerId());

        RestAdapter Radapter = new RestAdapter.Builder().setEndpoint(DomainName.sURL + "lastpurchase.php").build();
        RetrofitAPI api = Radapter.create(RetrofitAPI.class);
        Map<String,String> params = new HashMap<String, String>();
        params.put("customer_id", session.getCustomerId());
        api.LastPurhchase(params, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {

                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(response.getBody().in()));
                    String output = reader.readLine();
                    progressDialog.dismiss();
                    JSONArray jsonArray = new JSONArray(output);


                    itemsList.clear();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);

                        ItemBean itemBean = new ItemBean();
                        itemBean.setId(object.getString("id"));
                        itemBean.setProductId(  object.getString("product_id") );
                        itemBean.setProductMainCatId( object.getString("product_maincat_id") );
                        itemBean.setProductSubCatId( object.getString("product_subcat_id") );
                        itemBean.setProductMrp( object.getString("product_mrp") );
                        itemBean.setProductDiscount( object.getString("product_discount") );
                        itemBean.setProductPrice( object.getString("product_price") );
                        itemBean.setProductUnit( object.getString("product_unit") );
                        itemBean.setProductUnitprice( object.getString("product_unitprice"));




                        itemBean.setProductStock( object.getInt("product_stock"));

                        itemBean.setProductQuanity(object.getString("product_quanity"));
                        itemBean.setSupplierId(  object.getString("supplier_id") );
                        itemBean.setSupplierName( object.getString("supplier_name") );
                        itemBean.setProductFeatured( object.getString("product_featured") );
                        itemBean.setProductStatus( object.getString("product_status") );
                        itemBean.setCreatedOn( object.getString("created_on") );
                        itemBean.setUpdatedOn( object.getString("updated_on") );
                        itemBean.setHasDiscount( object.getString("has_discount") );

                        ProductItemBean bean = new ProductItemBean();
                        bean.setProductName( object.getString("product_name"));
                        bean.setProductMainCat( object.getString("product_maincat") );
                        bean.setProductSubcat( object.getString("product_subcat") );
                        bean.setProductLongdesc( object.getString("product_longdesc") );
                        bean.setProductShortdesc( object.getString("product_shortdesc") );
                        bean.setProductImage1( object.getString("product_image1") );
                        bean.setProductImage2( object.getString("product_image2") );
                        bean.setProductImage3( object.getString("product_image3") );
                        bean.setTags( object.getString("tags"));

                        bean.itemBeanArrayList.add( itemBean );

                        itemsList.add(bean);


                    }


                    adapterProduct.setValues(LastPurchaseActivity.this, itemsList);
                    list_lastpurchase.setAdapter(adapterProduct);
                    adapterProduct.notifyDataSetChanged();
                } catch (IOException e) {
                    if (progressDialog.isShowing()) progressDialog.dismiss();
                    e.printStackTrace();
                } catch (JSONException e) {
                    if (progressDialog.isShowing()) progressDialog.dismiss();
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "No Products Found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                if (progressDialog.isShowing()) progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Connection Error", Toast.LENGTH_SHORT).show();

            }
        });

    }


    public class ProductsListAdapter extends BaseAdapter {
        LayoutInflater inflater;
        ArrayList<ProductItemBean> itemsList = new ArrayList<>();

        public ProductsListAdapter() {
        }

        public ProductsListAdapter(LastPurchaseActivity activity, ArrayList<ProductItemBean> list_items) {
            context = activity;
            this.itemsList = list_items;
            inflater = (LayoutInflater) LastPurchaseActivity.this.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        //Custom method - 22-10-2017
        public void setValues(LastPurchaseActivity timelineActivity, ArrayList<ProductItemBean> list_items) {
            context = timelineActivity;
            this.itemsList = list_items;
            inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        }

        public class ViewHolder {
            CardView card_row_products, card_addcart, card_outofstock;
            ImageView img_product;
            TextView tv_pname, tv_unit_price, tv_mrp, tv_qty, tv_price, tv_mrpnew;
            ImageView img_minus, img_plus;
            LinearLayout linear_size;
            TextView tv_size_price, tv_discount;
            MyTextView txt_qty;
        }

        @Override
        public int getCount() {
            return itemsList.size();
        } //jArray.length()

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View vi = convertView;
            final ProductsListAdapter.ViewHolder holder;

            if (convertView == null) {
                vi = inflater.inflate(R.layout.rows_products_lst_purchase, null);
                holder = new ProductsListAdapter.ViewHolder();
                holder.card_row_products = (CardView) vi.findViewById(R.id.card_row_products);
                holder.img_product = (ImageView) vi.findViewById(R.id.img_product);
                holder.tv_pname = (TextView) vi.findViewById(R.id.tv_pname);
                holder.tv_unit_price = (TextView) vi.findViewById(R.id.tv_unit_price);
                holder.tv_mrp = (TextView) vi.findViewById(R.id.tv_mrp);
                holder.tv_mrp.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                holder.tv_price = (TextView) vi.findViewById(R.id.tv_price);
                holder.linear_size = (LinearLayout) vi.findViewById(R.id.linear_size);
                holder.tv_size_price = (TextView) vi.findViewById(R.id.tv_size_price);
                holder.card_addcart = (CardView) vi.findViewById(R.id.card_addcart);
                holder.tv_discount = (TextView) vi.findViewById(R.id.tv_discount);
                holder.tv_mrpnew = (TextView) vi.findViewById(R.id.tv_mrpnew);
                holder.tv_mrpnew.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                holder.txt_qty=(MyTextView)vi.findViewById(R.id.txt_qty);
                holder.card_outofstock = (CardView) vi.findViewById(R.id.card_outofstock);


                vi.setTag(holder);
            } else {

                holder = (ProductsListAdapter.ViewHolder) vi.getTag();
            }
            holder.tv_pname.setText(itemsList.get(position).getProductName());

            final ProductItemBean itemBean = itemsList.get(position);

            Glide.with(getApplicationContext()).load("http://andhrabasket.com/main/andhrabasket/andhrabasketadmin/" + itemBean.getProductImage1()).into(holder.img_product);

            holder.tv_size_price.setText("Rs. " +itemBean.itemBeanArrayList.get(0).getProductUnitprice());


            int totalPrice = 0;
            for(int i = 0 ; i < itemsList.size(); i++) {
                totalPrice = Integer.parseInt(itemBean.itemBeanArrayList.get(0).getProductUnitprice());
                Log.d("totalamountis",String.valueOf(totalPrice));
            }

            holder.txt_qty.setText(itemBean.itemBeanArrayList.get(0).getProductUnit());


          /*  if (itemBean.itemBeanArrayList.get(0).getHasDiscount().equals("yes")) {
                holder.tv_mrpnew.setVisibility(View.VISIBLE);
                holder.tv_mrpnew.setText(itemBean.itemBeanArrayList.get(0).getProductDiscount());

            } else {
                holder.tv_mrpnew.setVisibility(View.INVISIBLE);
            }*/

            if(itemBean.itemBeanArrayList.get(0).getProductMrp().equals("0")){
                holder.tv_mrpnew.setVisibility(View.INVISIBLE);
            }else{
                holder.tv_mrpnew.setVisibility(View.VISIBLE);
            }
            holder.tv_mrpnew.setText("Rs."+ itemBean.itemBeanArrayList.get(0).getProductMrp());


            int diff = Integer.parseInt(itemBean.itemBeanArrayList.get(0).getProductMrp()) - Integer.parseInt(itemBean.itemBeanArrayList.get(0).getProductUnitprice());
            double per = 0;
            if (Integer.parseInt(itemBean.itemBeanArrayList.get(0).getProductMrp()) > 0) {
                per = ((double) diff / (double) Integer.parseInt(itemBean.itemBeanArrayList.get(0).getProductMrp())) * (double) 100;
            }

            if (per == 0) {
                holder.tv_discount.setVisibility(View.INVISIBLE);
            } else {
                holder.tv_discount.setText(df2.format(per) + "%");
                holder.tv_discount.setVisibility(View.VISIBLE);
            }


            holder.card_outofstock.setVisibility(View.GONE);
            holder.card_addcart.setVisibility(View.VISIBLE);
            int outofstockint = itemBean.itemBeanArrayList.get(0).getProductStock();
            if (outofstockint == 0) {
                holder.card_outofstock.setVisibility(View.VISIBLE);
                holder.card_addcart.setVisibility(View.GONE);
            }


            holder.card_addcart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    ArrayList<SelectedProductBean> selectedItemsList = Singleton.getInstance().selectedItemsList;
                    if(itemBean.itemBeanArrayList.size() < selectedItemPosition){
                        selectedItemPosition = 0;
                    }

                    ItemBean itemBean1 = itemBean.itemBeanArrayList.get(selectedItemPosition);

                    boolean isAvailable = false;
                    for(int i=0;i<selectedItemsList.size();i++){
                        if (selectedItemsList.get(i).getId().equalsIgnoreCase(itemBean.itemBeanArrayList.get(selectedItemPosition).getId())) {
                            isAvailable = true;
                            break;
                        }
                    }
                    if(!isAvailable){ //Insert item
                        SelectedProductBean bean = new SelectedProductBean();

                        bean.setId(itemBean1.getId());
                        bean.setProductId(  itemBean1.getProductId() );
                        bean.setProductMainCatId( itemBean1.getProductMainCatId() );
                        bean.setProductSubCatId( itemBean1.getProductSubCatId() );
                        bean.setProductMrp( itemBean1.getProductMrp() );
                        bean.setProductDiscount( itemBean1.getProductDiscount() );
                        bean.setProductPrice( itemBean1.getProductPrice() );
                        bean.setProductUnit( itemBean1.getProductUnit() );
                        bean.setProductUnitprice( itemBean1.getProductUnitprice() );
                        bean.setProductStock( itemBean1.getProductStock() );
                        bean.setProductQuanity(itemBean1.getProductQuanity());
                        bean.setSupplierId(  itemBean1.getSupplierId() );
                        bean.setSupplierName( itemBean1.getSupplierName() );
                        bean.setProductFeatured( itemBean1.getProductFeatured() );
                        bean.setProductStatus( itemBean1.getProductStatus() );
                        bean.setCreatedOn( itemBean1.getCreatedOn() );
                        bean.setUpdatedOn( itemBean1.getUpdatedOn() );
                        bean.setHasDiscount( itemBean1.getHasDiscount() );

                        bean.setProductName( itemBean.getProductName());
                        bean.setProductMainCat( itemBean.getProductMainCat() );
                        bean.setProductSubcat( itemBean.getProductSubcat() );
                        bean.setProductLongdesc( itemBean.getProductLongdesc() );
                        bean.setProductShortdesc( itemBean.getProductShortdesc() );
                        bean.setProductImage1( itemBean.getProductImage1() );
                        bean.setProductImage2( itemBean.getProductImage2() );
                        bean.setProductImage3( itemBean.getProductImage3() );
                        bean.setTags( itemBean.getTags());
                        bean.setQuantity(1);

                        selectedItemsList.add( bean );
                    }
                    holder.card_addcart.setCardBackgroundColor(Color.parseColor("#76A63F"));

                    updatePrice();

                }
            });
            return vi;

        }


    }

}
