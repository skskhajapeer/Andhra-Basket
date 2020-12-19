package com.absket.in;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
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

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Sreejith on 04-05-2017.
 */
public class SearchActivity extends Activity {


    ImageView img_filter;
    ImageView img_back;
    EditText edt_search;
    ListView list_search_result;
    TextView tv_search;
    FloatingActionButton fab_cart;

    String sFilterOption="";
    String sProductName="";
    ProgressDialog progressDialog;
    private static DecimalFormat df2 = new DecimalFormat("##");
    ProductsListAdapter adapterProduct;
    boolean flag_loading;
    private ArrayList<ProductItemBean> itemsList = new ArrayList<>();
    int selectedItemPosition = 0;
    MyTextView badge_text;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        edt_search = (EditText) findViewById(R.id.edt_search);
        tv_search = (TextView) findViewById(R.id.tv_search);
        fab_cart = (FloatingActionButton) findViewById(R.id.fab_cart);
         img_filter=(ImageView)findViewById(R.id.img_filter);
         img_back=(ImageView)findViewById(R.id.img_back);
         edt_search=(EditText)findViewById(R.id.edt_search);
         list_search_result=(ListView)findViewById(R.id.list_search_result);
         tv_search=(TextView)findViewById(R.id.tv_search);
         fab_cart=(FloatingActionButton)findViewById(R.id.fab_cart);
        badge_text = (MyTextView) findViewById(R.id.badge_text);

        img_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(SearchActivity.this);
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
                        CallingSearchFilterRetrofit();
                    }
                });
                card_pricehightolow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sFilterOption = "htol";
                        dialog.dismiss();
                        CallingSearchFilterRetrofit();
                    }
                });

                card_discount.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sFilterOption = "discount";
                        dialog.dismiss();
                        CallingSearchFilterRetrofit();
                    }
                });

                dialog.show();


            }
        });


        fab_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(SearchActivity.this,ReviewOrdersActivity.class);
                startActivity(in);
            }
        });


        tv_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sProductName = edt_search.getText().toString();
                if(!sProductName.equals(""))
                {
                    sFilterOption="normal";
                    CallingSearchFilterRetrofit();
                }
                else
                {
                    Toast.makeText(SearchActivity.this, "Type something to search", Toast.LENGTH_SHORT).show();
                }

            }
        });

        adapterProduct = new ProductsListAdapter(SearchActivity.this,null);
        list_search_result.setAdapter(adapterProduct);


        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
            }
        });
        updateCartValues();
    }


    public void CallingSearchFilterRetrofit()
    {
        progressDialog = new ProgressDialog(SearchActivity.this);
        progressDialog.setMessage("Searching...");
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Please wait");
        progressDialog.show();

        RestAdapter Radapter = new RestAdapter.Builder().setEndpoint(DomainName.sURL+"sorting.php").build();
        RetrofitAPI api = Radapter.create(RetrofitAPI.class);
        api.SearchProducts(sProductName, sFilterOption, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(response.getBody().in()));
                    String output = reader.readLine();
                    flag_loading = false;
                    progressDialog.dismiss();

                    JSONArray jsonArray = new JSONArray(output);

                    itemsList.clear();

                    for(int i=0; i<jsonArray.length();i++){
                        JSONObject object = jsonArray.getJSONObject(i);
                        boolean isExists = false;
                        int selectedIndex = 0;
                        for(int j=0;j<itemsList.size();j++){
                            if(itemsList.get(j).getProductName().equalsIgnoreCase( object.getString("product_name") )){
                                isExists = true;
                                selectedIndex = j;
                                break;
                            }
                        }

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

                        if(isExists){
                            itemsList.get(selectedIndex).itemBeanArrayList.add( itemBean );
                        }else{
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

                    }
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
                    Toast.makeText(SearchActivity.this, "No Products Found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                if(progressDialog.isShowing()) progressDialog.dismiss();
                Toast.makeText(SearchActivity.this, "Connection Error", Toast.LENGTH_SHORT).show();
            }
        });

    }


    public  class ProductsListAdapter extends BaseAdapter {
        LayoutInflater inflater ;
        JSONArray jsonArray;
        Context context;


        public ProductsListAdapter(SearchActivity activity, JSONArray jArray) {
            this.context=activity;
            jsonArray = jArray;
            inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }


        public  class ViewHolder {
            CardView card_row_products,card_addcart;
            ImageView img_product;
            TextView tv_pname,tv_unit_price,tv_mrp,tv_qty,tv_price;
            ImageView img_minus,img_plus;
            LinearLayout linear_size;
            Spinner sp_size;
            TextView tv_size_price,tv_discount,tv_mrpnew;
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
            return itemsList.size();
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


                vi.setTag(holder);
            } else

                holder = (ViewHolder) vi.getTag();
            final ProductItemBean itemBean = itemsList.get(position);
            Glide.with(SearchActivity.this).load("http://andhrabasket.com/main/andhrabasket/andhrabasketadmin/"+itemBean.getProductImage1()).into(holder.img_product);

            holder.tv_pname.setText(itemBean.getProductName());

            String[] items = new String[itemBean.itemBeanArrayList.size()];
            for(int i=0;i<itemBean.itemBeanArrayList.size();i++){
                items[i] = itemBean.itemBeanArrayList.get(i).getProductUnit();
            }

           ArrayAdapter<String> arraySpinnerAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, items);
            holder.sp_size.setAdapter(arraySpinnerAdapter);


            holder.sp_size.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    selectedItemPosition = position;
                    holder.tv_size_price.setText("Rs. " + itemBean.itemBeanArrayList.get(position).getProductUnitprice());
                    if(itemBean.itemBeanArrayList.get(position).getProductMrp().equals("0")){
                        holder.tv_mrpnew.setVisibility(View.INVISIBLE);
                    }else{
                        holder.tv_mrpnew.setVisibility(View.VISIBLE);
                    }
                    holder.tv_mrpnew.setText("Rs."+ itemBean.itemBeanArrayList.get(position).getProductMrp());

                    int diff = Integer.parseInt( itemBean.itemBeanArrayList.get(position).getProductMrp() ) - Integer.parseInt( itemBean.itemBeanArrayList.get(position).getProductUnitprice() );
                    double per = 0;
                    if(Integer.parseInt( itemBean.itemBeanArrayList.get(position).getProductMrp() ) > 0){
                        per = ((double)diff / (double) Integer.parseInt( itemBean.itemBeanArrayList.get(position).getProductMrp() )) * (double) 100;
                    }

                    if(per == 0){
                        holder.tv_discount.setVisibility(View.INVISIBLE);
                    }
                    else{
                        holder.tv_discount.setText(df2.format(per) + "%");
                        holder.tv_discount.setVisibility(View.VISIBLE);
                    }

                    boolean isAvailable = false;
                    ArrayList<SelectedProductBean> selectedItemsList = Singleton.getInstance().selectedItemsList;
                    for(int i=0;i<selectedItemsList.size();i++){
                        if (selectedItemsList.get(i).getId().equalsIgnoreCase(itemBean.itemBeanArrayList.get(selectedItemPosition).getId())) {
                            isAvailable = true;
                            break;
                        }
                    }

                    if(isAvailable){
                        holder.card_addcart.setCardBackgroundColor(Color.parseColor("#76A63F"));
                    }else{
                        holder.card_addcart.setCardBackgroundColor(Color.parseColor("#35373E"));
                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });


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

//                    ((ListProductsActivity) getActivity()).updateCartValues();
                    updateCartValues();

                }
            });

            holder.card_row_products.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Singleton.getInstance().descBean = itemBean;
                    Intent in = new Intent(SearchActivity.this, DescriptionActivity.class);
                    startActivityForResult(in, 1);
                }
            });


            /*holder.tv_unit_price.setText(item.g+" "+arrayUnitPrice.get(position));
            if(arrayMRP.get(position).equals("0"))
            {
                holder.tv_mrpnew.setVisibility(View.INVISIBLE);
            }
            else {
                holder.tv_mrpnew.setVisibility(View.VISIBLE);
                holder.tv_mrpnew.setText("Rs." + arrayMRP.get(position));
            }



            holder.tv_price.setText("Rs. "+arrayPrice.get(position));



            holder.tv_discount.setText(arrayDiscount.get(position) + " %");


            int diff = Integer.parseInt( arrayMRP.get(position) ) - Integer.parseInt( arrayUnitPrice.get(position) );
            double per = 0;
            if(Integer.parseInt( arrayMRP.get(position) ) > 0){
                per = ((double)diff / (double) Integer.parseInt( arrayMRP.get(position) )) * (double) 100;
            }

            holder.tv_discount.setText(per + " %");

            if(arrayDiscount.get(position).equals("0"))
            {
                holder.tv_discount.setVisibility(View.INVISIBLE);
            }
            else
            {
                holder.tv_discount.setVisibility(View.VISIBLE);
            }

            Cursor cursor_size = BasketSQL.rawQuery("SELECT * FROM tbl_products WHERE product_name='"+arrayPName.get(position)+"'",null);
            if(cursor_size.getCount()>0)
            {
                arraySpinnerSize[position] = new ArrayList<>();
                arraySpinnerCost[position] = new ArrayList<>();

                while(cursor_size.moveToNext())
                {
                    arraySpinnerSize[position].add(cursor_size.getString(10));
                    arraySpinnerCost[position].add(cursor_size.getString(11));
                }

                arraySpinnerAdapter[position] = new ArrayAdapter(SearchActivity.this,android.R.layout.simple_dropdown_item_1line,arraySpinnerSize[position]);
                holder.sp_size.setAdapter(arraySpinnerAdapter[position]);
                final int pos = position;
                final double finalPer = per;
                holder.sp_size.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        holder.tv_size_price.setText("Rs. "+arraySpinnerCost[pos].get(position));
                        arraySelectedUnit.remove(pos);
                        arraySelectedUnit.add(pos,arraySpinnerSize[pos].get(position));
                        arraySelectedUnitPrice.remove(pos);
                        arraySelectedUnitPrice.add(pos,arraySpinnerCost[pos].get(position));

                        if(finalPer == 0)
                        {
                            holder.tv_discount.setVisibility(View.INVISIBLE);
                        }
                        else
                        {
                            holder.tv_discount.setText(df2.format(finalPer) + "%");
                            holder.tv_discount.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }


            Cursor cursor_product_verify = BasketSQL.rawQuery("SELECT * FROM tbl_cart WHERE product_id='" + arrayPId.get(position) + "'", null);
            if (cursor_product_verify.getCount() > 0) {
                holder.card_addcart.setCardBackgroundColor(Color.parseColor("#76A63F"));
            } else {
                holder.card_addcart.setCardBackgroundColor(Color.parseColor("#35373E"));
            }


            holder.card_addcart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Cursor cursor_product = BasketSQL.rawQuery("SELECT * FROM tbl_cart WHERE product_id='"+arrayPId.get(position)+"'",null);
                    if(cursor_product.getCount()>0)
                    {
                        BasketSQL.execSQL("UPDATE tbl_cart SET product_price='"+arraySelectedUnitPrice.get(position)+"',product_unit='"+arraySelectedUnit.get(position)+"'");
                        BasketSQL.execSQL("UPDATE tbl_last_cart SET product_price='"+arraySelectedUnitPrice.get(position)+"',product_unit='"+arraySelectedUnit.get(position)+"'");
                        Toast.makeText(SearchActivity.this, "Cart Updated", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        BasketSQL.execSQL("INSERT INTO tbl_cart (product_id,product_name,product_image1,product_price,quantity,product_unit,product_unitprice,product_discount,product_mrp) VALUES ('"+arrayPId.get(position)+"','"+arrayPName.get(position)+"','"+arrayPImage1.get(position)+"','"+arraySelectedUnitPrice.get(position)+"','1','"+arraySelectedUnit.get(position)+"','"+arraySelectedUnitPrice.get(position)+"','"+arrayDiscount.get(position)+"','"+arrayMRP.get(position)+"')");

                       // BasketSQL.execSQL("INSERT INTO tbl_cart (id,product_id,product_name,product_image1,product_price,quantity,product_unit) VALUES ('"+arrayId.get(position)+"','"+arrayPId.get(position)+"','"+arrayPName.get(position)+"','"+arrayPImage1.get(position)+"','"+arraySelectedUnitPrice.get(position)+"','1','"+arraySelectedUnit.get(position)+"')");
                        Toast.makeText(SearchActivity.this, "Added to cart", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            holder.card_row_products.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent in = new Intent(SearchActivity.this,DescriptionActivity.class);
                    in.putExtra("id",arrayPId.get(position));
                    in.putStringArrayListExtra("spinnersize",arraySpinnerSize[position]);
                    in.putStringArrayListExtra("spinnercost",arraySpinnerCost[position]);
                    in.putExtra("productid",arrayPId.get(position));
                    in.putExtra("productname",arrayPName.get(position));
                    in.putExtra("desc",arrayPLongDesc.get(position));
                    startActivity(in);
                }
            });*/


            return vi;
        }
    }

    public void updateCartValues(){
        badge_text.setText("");
        int count = 0;
        for(SelectedProductBean bean: Singleton.getInstance().selectedItemsList){
            count += bean.getQuantity();
        }
        if(count > 0){
            badge_text.setVisibility(View.VISIBLE);
            badge_text.setText(""+count);
        }
        else{
            badge_text.setVisibility(View.GONE);
        }

    }
}
