package com.absket.in;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.absket.in.CustomFonts.MyTextView;
import com.absket.in.model.ItemBean;
import com.absket.in.model.ProductItemBean;
import com.absket.in.model.SelectedProductBean;
import com.absket.in.model.Singleton;
import com.bumptech.glide.Glide;
import com.mostafaaryan.transitionalimageview.TransitionalImageView;

import java.util.ArrayList;

import me.relex.circleindicator.CircleIndicator;

/**
 * Created by Sreejith on 10-05-2017.
 */
public class DescriptionActivity extends Activity {

    ArrayList<String> arrayPImages;
    ViewPager pager;
    CircleIndicator indicator;
    MyTextView tv_title;
    MyTextView tv_description,badge_text;
    AppCompatSpinner sp_size;
    MyTextView tv_unit_price;
    CardView card_addcart;

    ViewPagerAdapterImages_Description adapterImages;

    ArrayList<String> arraySpinnerSize, arraySpinnerCost;
    ArrayAdapter adapterSpinner;

    String sUnitPrice = "", sUnit = "", sId = "", sProductId = "", sProductName = "", sImage1 = "", sMRP = "", sDesc = "";

    FloatingActionButton fab_cart;
    ImageView img_plus;
    ImageView img_minus;
    MyTextView tv_qty;
    CardView card_addqty;
    String sDiscount;
    ImageView img_back;
    Handler handler;
    CardView card_outofstock;
    int iCount = 0;
    int selectedItemPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);
        fab_cart = (FloatingActionButton) findViewById(R.id.fab_cart);
        img_plus = (ImageView) findViewById(R.id.img_plus);
        img_minus = (ImageView) findViewById(R.id.img_minus);
        tv_title = (MyTextView) findViewById(R.id.tv_title);
        tv_qty = (MyTextView) findViewById(R.id.tv_qty);
        badge_text = (MyTextView) findViewById(R.id.badge_text);
        tv_description = (MyTextView) findViewById(R.id.tv_description);
        card_addqty = (CardView) findViewById(R.id.card_addqty);
        img_back = (ImageView) findViewById(R.id.img_back);
        card_addcart = (CardView) findViewById(R.id.card_addcart);
        card_outofstock = (CardView) findViewById(R.id.card_outofstock);
        sp_size = (AppCompatSpinner) findViewById(R.id.sp_size);
        tv_unit_price = (MyTextView) findViewById(R.id.tv_unit_price);
        pager = (ViewPager) findViewById(R.id.pager);
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
            }
        });

        Intent intent = getIntent();
        final ProductItemBean bean = Singleton.getInstance().descBean;

        Log.e("RAM", "bean::"+bean.getProductName());

        indicator = (CircleIndicator) findViewById(R.id.indicator);
        arraySpinnerSize = new ArrayList<>();
        arraySpinnerCost = new ArrayList<>();
        VariableHolder.arrayProductImages = new ArrayList<>();

        sProductName = bean.getProductName();
        sDesc = bean.getProductLongdesc();

       VariableHolder.arrayProductImages.add( bean.getProductImage1() );
//        VariableHolder.arrayProductImages.add( bean.getProductImage2()  );
//        VariableHolder.arrayProductImages.add( bean.getProductImage3() );

        tv_title.setText(sProductName);
        tv_description.setText(sDesc);


        pager.setCurrentItem(iCount);

        VariableHolder.sViewPagerImages = "";
        adapterImages = new ViewPagerAdapterImages_Description(DescriptionActivity.this, VariableHolder.arrayProductImages.size());
        pager.setAdapter(adapterImages);
        indicator.setViewPager(pager);
        indicator.setVisibility(View.GONE);


        boolean isAvailable = false;
        ArrayList<SelectedProductBean> selectedItemsList = Singleton.getInstance().selectedItemsList;
        for(int i=0;i<selectedItemsList.size();i++){
            if (selectedItemsList.get(i).getId().equalsIgnoreCase(bean.itemBeanArrayList.get(0).getId())) {
                isAvailable = true;
                break;
            }
        }

        if(isAvailable){
            card_addqty.setVisibility(View.VISIBLE);
            card_addcart.setEnabled(false);
            card_addcart.setCardBackgroundColor(Color.parseColor("#76A63F"));
            tv_qty.setText(""+Singleton.getInstance().selectedItemsList.get(selectedItemPosition).getQuantity());
        }else{
            card_addqty.setVisibility(View.VISIBLE);
            card_addcart.setEnabled(true);
            tv_qty.setText("0");
            card_addcart.setCardBackgroundColor(Color.parseColor("#35373E"));
        }




        img_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (tv_qty.getText().toString().equals("0")) {
                    Toast.makeText(DescriptionActivity.this, "Add to cart before increasing the quantity", Toast.LENGTH_SHORT).show();
                    return;
                }
                int position = -1;
                ArrayList<SelectedProductBean> selectedItemsList = Singleton.getInstance().selectedItemsList;
                for(int i=0;i<selectedItemsList.size();i++){
                    if (selectedItemsList.get(i).getId().equalsIgnoreCase(bean.itemBeanArrayList.get(selectedItemPosition).getId())) {
                        position = i;
                        break;
                    }
                }

                if(position == -1 ){
                    return;
                }

                int quantity = Singleton.getInstance().selectedItemsList.get(position).getQuantity();
                Singleton.getInstance().selectedItemsList.get(position).setQuantity(quantity + 1);

                double price = 0;
                price += Double.parseDouble(Singleton.getInstance().selectedItemsList.get(position).getProductUnitprice()) * (quantity + 1);
                /*for (int i = 0; i < Singleton.getInstance().selectedItemsList.size(); i++) {
                    price += Double.parseDouble(Singleton.getInstance().selectedItemsList.get(i).getProductUnitprice()) * Singleton.getInstance().selectedItemsList.get(i).getQuantity();
                }*/

//                tv_unit_price.setText("Rs. " + price);
                tv_qty.setText("" + Singleton.getInstance().selectedItemsList.get(position).getQuantity());
                updateCartValues();

            }
        });

        img_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(tv_qty.getText().toString().equalsIgnoreCase("0")){
                    return;
                }

                int position = -1;
                ArrayList<SelectedProductBean> selectedItemsList = Singleton.getInstance().selectedItemsList;
                for(int i=0;i<selectedItemsList.size();i++){
                    if (selectedItemsList.get(i).getId().equalsIgnoreCase(bean.itemBeanArrayList.get(selectedItemPosition).getId())) {
                        position = i;
                        break;
                    }
                }

                int quantity = Singleton.getInstance().selectedItemsList.get(position).getQuantity() - 1;

                tv_qty.setText("" + quantity);

                if (quantity == 0) {
                    Singleton.getInstance().selectedItemsList.remove(position);
//                    tv_unit_price.setText("Rs. " + bean.itemBeanArrayList.get(selectedItemPosition).getProductUnitprice());
                }else{
                    Singleton.getInstance().selectedItemsList.get(position).setQuantity(quantity);

                    double price = 0;
                    price += Double.parseDouble(Singleton.getInstance().selectedItemsList.get(position).getProductUnitprice()) * quantity;
                    /*for (int i = 0; i < Singleton.getInstance().selectedItemsList.size(); i++) {
                        if(i == selectedItemPosition){
                            price += Double.parseDouble(Singleton.getInstance().selectedItemsList.get(i).getProductUnitprice()) * Singleton.getInstance().selectedItemsList.get(i).getQuantity();
                        }
                    }*/

//                    tv_unit_price.setText("Rs. " + price);
                }

                updateCartValues();

                if(quantity > 0){
                    card_addqty.setVisibility(View.VISIBLE);
                    card_addcart.setEnabled(false);
                    card_addcart.setCardBackgroundColor(Color.parseColor("#76A63F"));
                }else{
                    card_addqty.setVisibility(View.VISIBLE);
                    card_addcart.setEnabled(true);
                    card_addcart.setCardBackgroundColor(Color.parseColor("#35373E"));
                }
            }
        });

        String[] items = new String[bean.itemBeanArrayList.size()];
        for(int i=0;i<bean.itemBeanArrayList.size();i++){
            items[i] = bean.itemBeanArrayList.get(i).getProductUnit();
        }

        ArrayAdapter<String> arraySpinnerAdapter = new ArrayAdapter(DescriptionActivity.this, android.R.layout.simple_dropdown_item_1line, items);
        sp_size.setAdapter(arraySpinnerAdapter);


        adapterSpinner = new ArrayAdapter(DescriptionActivity.this, android.R.layout.simple_dropdown_item_1line, items);
        sp_size.setAdapter(adapterSpinner);

        tv_qty.setText("0");


        sp_size.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedItemPosition = position;
                tv_qty.setText("0");
                tv_unit_price.setText("Rs. " + bean.itemBeanArrayList.get(position).getProductUnitprice());


                boolean isAvailable = false;
                int quantity = 0;
                ArrayList<SelectedProductBean> selectedItemsList = Singleton.getInstance().selectedItemsList;
                for(int i=0;i<selectedItemsList.size();i++){
                    if (selectedItemsList.get(i).getId().equalsIgnoreCase(bean.itemBeanArrayList.get(selectedItemPosition).getId())) {
                        isAvailable = true;
                        quantity = selectedItemsList.get(i).getQuantity();
                        break;
                    }
                }

                if(isAvailable){
                    tv_qty.setText("" + quantity);
                    card_addcart.setCardBackgroundColor(Color.parseColor("#76A63F"));
                }else{
                    card_addcart.setCardBackgroundColor(Color.parseColor("#35373E"));
                }

                card_outofstock.setVisibility(View.GONE);
                card_addcart.setVisibility(View.VISIBLE);
                int outofstockint =  bean.itemBeanArrayList.get(position).getProductStock();
                if (outofstockint == 0) {
                    card_outofstock.setVisibility(View.VISIBLE);
                    card_addcart.setVisibility(View.GONE);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        card_addcart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ArrayList<SelectedProductBean> selectedItemsList = Singleton.getInstance().selectedItemsList;
                if(bean.itemBeanArrayList.size() < selectedItemPosition){
                    selectedItemPosition = 0;
                }

                ItemBean itemBean1 = bean.itemBeanArrayList.get(selectedItemPosition);

                boolean isAvailable = false;
                for(int i=0;i<selectedItemsList.size();i++){
                    if (selectedItemsList.get(i).getId().equalsIgnoreCase(bean.itemBeanArrayList.get(selectedItemPosition).getId())) {
                        isAvailable = true;
                        break;
                    }
                }
                if(!isAvailable){ //Insert item
                    SelectedProductBean bean1 = new SelectedProductBean();

                    bean1.setId(itemBean1.getId());
                    bean1.setProductId(  itemBean1.getProductId() );
                    bean1.setProductMainCatId( itemBean1.getProductMainCatId() );
                    bean1.setProductSubCatId( itemBean1.getProductSubCatId() );
                    bean1.setProductMrp( itemBean1.getProductMrp() );
                    bean1.setProductDiscount( itemBean1.getProductDiscount() );
                    bean1.setProductPrice( itemBean1.getProductPrice() );
                    bean1.setProductUnit( itemBean1.getProductUnit() );
                    bean1.setProductUnitprice( itemBean1.getProductUnitprice() );
                    bean1.setProductStock( itemBean1.getProductStock() );
                    bean1.setProductQuanity(itemBean1.getProductQuanity());
                    bean1.setSupplierId(  itemBean1.getSupplierId() );
                    bean1.setSupplierName( itemBean1.getSupplierName() );
                    bean1.setProductFeatured( itemBean1.getProductFeatured() );
                    bean1.setProductStatus( itemBean1.getProductStatus() );
                    bean1.setCreatedOn( itemBean1.getCreatedOn() );
                    bean1.setUpdatedOn( itemBean1.getUpdatedOn() );
                    bean1.setHasDiscount( itemBean1.getHasDiscount() );

                    bean1.setProductName( bean.getProductName());
                    bean1.setProductMainCat( bean.getProductMainCat() );
                    bean1.setProductSubcat( bean.getProductSubcat() );
                    bean1.setProductLongdesc( bean.getProductLongdesc() );
                    bean1.setProductShortdesc( bean.getProductShortdesc() );
                    bean1.setProductImage1( bean.getProductImage1() );

                   /* bean.setProductImage2( bean.getProductImage2() );
                    bean.setProductImage3( bean.getProductImage3() );*/
                    bean1.setTags( bean.getTags());
                    bean1.setQuantity(1);

                    selectedItemsList.add( bean1 );
                }
                card_addcart.setCardBackgroundColor(Color.parseColor("#76A63F"));

                double price = 0;
                price += Double.parseDouble(itemBean1.getProductUnitprice());
                /*for (int i = 0; i < Singleton.getInstance().selectedItemsList.size(); i++) {
                    if(i == selectedItemPosition){
                        price += Double.parseDouble(Singleton.getInstance().selectedItemsList.get(i).getProductUnitprice()) * Singleton.getInstance().selectedItemsList.get(i).getQuantity();
                    }

                }*/

//                tv_unit_price.setText("Rs. " + price);
                tv_qty.setText("1");
                updateCartValues();


            }
        });

        fab_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(DescriptionActivity.this, ReviewOrdersActivity.class);
                finish();
                startActivity(in);
            }
        });

        updateCartValues();

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

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK,returnIntent);
        finish();
    }

    public class ViewPagerAdapterImages_Description extends PagerAdapter {
        int size;
        Context act;
        View layout;
        TransitionalImageView pageImage;
        float startScale;


        public ViewPagerAdapterImages_Description(Context context, int noofsize) {
            // TODO Auto-generated constructor stub
            size = noofsize;
            act = context;
        }

        @Override
        public int getCount() {
            return size;
        }

        @Override
        public Object instantiateItem(final View container, final int position) {
            LayoutInflater inflater = (LayoutInflater) act
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            layout = inflater.inflate(R.layout.image_zoom, null);
            pageImage = (TransitionalImageView) layout.findViewById(R.id.imageView1);


            pageImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    ZoomAnimation zoomAnimation = new ZoomAnimation(DescriptionActivity.this);
                    zoomAnimation.zoom(v, 600);



                }
            });


            try {

                if (VariableHolder.sViewPagerImages.equals("MAIN")) {
                    pageImage.setScaleType(ImageView.ScaleType.FIT_XY);
                    Glide.with(act).load("http://andhrabasket.com/main/andhrabasket/andhrabasketadmin/" +
                            VariableHolder.arraySliderImages.get(position)).into(pageImage);

                } else {
                    Glide.with(act).load("http://andhrabasket.com/main/andhrabasket/andhrabasketadmin/" + VariableHolder.arrayProductImages.get(position)).into(pageImage);
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            ((ViewPager) container).addView(layout, 0);
            return layout;
        }

        @Override
        public void destroyItem(View arg0, int arg1, Object arg2) {
            ((ViewPager) arg0).removeView((View) arg2);
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == ((View) arg1);
        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        // }

    }

    private void image_layout(View v){


        final Dialog dialog = new Dialog(DescriptionActivity.this);
        dialog.setContentView(R.layout.img_layout);
        //dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        ZoomAnimation zoomAnimation = new ZoomAnimation(DescriptionActivity.this);
        zoomAnimation.zoom(v, 0);

        dialog.show();
    }
}
