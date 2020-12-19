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
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatSpinner;
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
import android.widget.TextView;
import android.widget.Toast;

import com.absket.in.model.ItemBean;
import com.absket.in.model.ProductItemBean;
import com.absket.in.model.SelectedProductBean;
import com.absket.in.model.Singleton;
import com.absket.in.utils.TestApplication;
import com.bumptech.glide.Glide;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

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


public class CategoryProductsFragmentNew extends Fragment {

    SwipeRefreshLayout swipe_refresh;
    ListView list_categoryproduct;
    ProductsListAdapter adapterProduct;
    boolean flag_loading;
    ArrayList<String> arrayFilterOption;
    private static DecimalFormat df2 = new DecimalFormat("##");

    static FloatingActionButton fab_filter;
    private ArrayList<ProductItemBean> itemsList = new ArrayList<>();
    String sFilterOption="";
    ProgressDialog progressDialog;
    String sLastProductId = "0";
    static String sSubCategory = "";
    int selectedItemPosition = 0;

    ArrayList<String> arrayBrands;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_categoryproduct1,null);

        fab_filter = (FloatingActionButton) view.findViewById(R.id.fab_filter);
        list_categoryproduct = (ListView) view.findViewById(R.id.list_categoryproduct);
        swipe_refresh = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);

        adapterProduct = new ProductsListAdapter(ListProductsActivity.activity,null);
        list_categoryproduct.setAdapter(adapterProduct);
        adapterProduct.notifyDataSetChanged();

        fab_filter.setVisibility(View.INVISIBLE);


        TestApplication application = (TestApplication) getActivity().getApplication();
        Tracker mTracker = application.getGoogleAnalyticsTracker();
        mTracker.setScreenName("CategoryList Page" + "");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());


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
            }
        });
        return view;
    }

    public void CallingSortMainretrofit()
    {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Filtering...");
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Please wait");
        progressDialog.show();
        Log.e("RAM", ListProductsActivity.sMainCategory);
        Log.e("RAM", "CallingProductsRetrofit == " + sSubCategory);

        RestAdapter Radapter = new RestAdapter.Builder().setEndpoint(DomainName.sURL+"sorting_main.php").build();
        RetrofitAPI api = Radapter.create(RetrofitAPI.class);
        api.MainSort(ListProductsActivity.sMainCategory, sSubCategory, sFilterOption, arrayFilterOption, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(response.getBody().in()));
                    String output = reader.readLine();
                    flag_loading = false;
                    progressDialog.dismiss();
                    if(swipe_refresh.isRefreshing()) swipe_refresh.setRefreshing(false);
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
                    if(swipe_refresh.isRefreshing()) swipe_refresh.setRefreshing(false);
                    e.printStackTrace();
                }
                catch (JSONException e)
                {
                    if(progressDialog.isShowing()) progressDialog.dismiss();
                    if(swipe_refresh.isRefreshing()) swipe_refresh.setRefreshing(false);
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

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
//        final Bundle args = getArguments();
//        final int position = args.getInt("position");

//        ListProductsActivity.sSubCategory = ListProductsActivity.arraySubCategory.get(position);
//        sSubCategory = ListProductsActivity.arraySubCategory.get(position);

//        Log.e("RAM", "CallingProductsRetrofit :: " + sSubCategory);

//        sFilterOption="";
//        ListProductsActivity.iFragPos = 1;
//        CallingProductsRetrofit(sLastProductId);
    }

    static CategoryProductsFragmentNew init(int position) {
        CategoryProductsFragmentNew truitonFrag = new CategoryProductsFragmentNew();
        Bundle args = new Bundle();
        args.putInt("position", position);
        truitonFrag.setArguments(args);
        return truitonFrag;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser)
        {
            // adapterProduct = new ProductsListAdapter(ListProductsActivity.activity,null);
            // list_categoryproduct.setAdapter(adapterProduct);
            sFilterOption="";
            ListProductsActivity.iFragPos = 2;
            CallingProductsRetrofit(sLastProductId);
        }
    }

    public static void iPosition(int iPos)
    {
        ListProductsActivity.sSubCategory = ListProductsActivity.arraySubCategory.get(iPos);
        sSubCategory = ListProductsActivity.arraySubCategory.get(iPos);

    }

    public void CallingProductsRetrofit(String sLastId)
    {
        progressDialog = new ProgressDialog(ListProductsActivity.activity);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Please wait");
        progressDialog.show();

        Log.e("RAM", ListProductsActivity.sMainCategory);
        Log.e("RAM", "CallingProductsRetrofit" + sSubCategory);



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
                    if(swipe_refresh.isRefreshing()) swipe_refresh.setRefreshing(false);
                    JSONArray jsonArray = new JSONArray(output);

                   // itemsList.clear();



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
                    if(swipe_refresh.isRefreshing()) swipe_refresh.setRefreshing(false);
                    e.printStackTrace();
                }
                catch (JSONException e)
                {
                    if(progressDialog.isShowing()) progressDialog.dismiss();
                    if(swipe_refresh.isRefreshing()) swipe_refresh.setRefreshing(false);
                    e.printStackTrace();
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
        LayoutInflater inflater;
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
            ImageView img_minus,img_plus;
            LinearLayout linear_size;
            AppCompatSpinner sp_size;
            TextView tv_size_price,tv_discount;
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
            final ProductsListAdapter.ViewHolder holder;

            if (convertView == null) {
                vi = inflater.inflate(R.layout.rows_products, null);
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
                holder.sp_size = (AppCompatSpinner) vi.findViewById(R.id.sp_size);
                holder.card_addcart  = (CardView) vi.findViewById(R.id.card_addcart);
                holder.tv_discount = (TextView) vi.findViewById(R.id.tv_discount);
                holder.tv_mrpnew = (TextView) vi.findViewById(R.id.tv_mrpnew);
                holder.tv_mrpnew.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                holder.card_outofstock = (CardView) vi.findViewById(R.id.card_outofstock);


                vi.setTag(holder);
            } else

                holder = (ProductsListAdapter.ViewHolder) vi.getTag();

            try {

                final ProductItemBean itemBean = itemsList.get(position);

                Glide.with(getActivity()).load("http://andhrabasket.com/main/andhrabasket/andhrabasketadmin/" + itemBean.getProductImage1()).into(holder.img_product);

                holder.tv_pname.setText(itemBean.getProductName());

                String[] items = new String[itemBean.itemBeanArrayList.size()];
                for(int i=0;i<itemBean.itemBeanArrayList.size();i++){
                    items[i] = itemBean.itemBeanArrayList.get(i).getProductUnit();
                }

                ArrayAdapter<String> arraySpinnerAdapter = new ArrayAdapter(ListProductsActivity.activity, android.R.layout.simple_dropdown_item_1line, items);
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

                        holder.card_outofstock.setVisibility(View.GONE);
                        holder.card_addcart.setVisibility(View.VISIBLE);
                        int outofstockint =  itemBean.itemBeanArrayList.get(position).getProductStock();
                        if (outofstockint == 0) {
                            holder.card_outofstock.setVisibility(View.VISIBLE);
                            holder.card_addcart.setVisibility(View.GONE);
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

                   ((ListProductsActivity) getActivity()).updateCartValues();

                    }
                });

                holder.img_product.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Singleton.getInstance().descBean = itemBean;
                        Intent in = new Intent(getActivity(), DescriptionActivity.class);
                        getActivity().startActivityForResult(in, 1);
                    }
                });



            }catch (Exception e)
            {
                e.printStackTrace();
            }

            return vi;
        }
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
                                CallingSortMainretrofit();
                            }
                        });
*/

                        MultiBrandSelectorAdapter adapter = new MultiBrandSelectorAdapter(getActivity(),null);
                        list_brand.setAdapter(adapter);

                        card_search.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog_brand.dismiss();
                                if(arrayFilterOption.size()>0) {
                                    //sFilterOption = arrayFilterOption.get(0);
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
            final MultiBrandSelectorAdapter.ViewHolder holder;

            if (convertView == null) {

                vi = inflater.inflate(R.layout.row_multibrand_select, null);

                holder = new MultiBrandSelectorAdapter.ViewHolder();

                holder.tv_brand = (TextView) vi.findViewById(R.id.tv_brand);
                holder.cb_brand = (CheckBox) vi.findViewById(R.id.cb_brand);



                vi.setTag(holder);
            } else

                holder = (MultiBrandSelectorAdapter.ViewHolder) vi.getTag();

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
