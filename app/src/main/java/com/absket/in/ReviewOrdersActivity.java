package com.absket.in;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.absket.in.CustomFonts.MyTextView;
import com.absket.in.model.SelectedProductBean;
import com.absket.in.model.Singleton;
import com.bumptech.glide.Glide;

import org.json.JSONArray;

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
public class ReviewOrdersActivity extends Activity {

    ProductsListAdapter adapterProducts;


    ProgressDialog progressDialog;
    String sPromoCode = "";
    UserSessionManager session;

    double iTotalAmt = 0;
    String sFirstPurchaseAmt = "";
    String sIsFirstPurchase = "";
    String sMOP = "", sDeliveryAddress = "", sCode = "", sDeliveryTime = "", sRefCashChecked = "";
    String sTotPrice;

    CheckBox cb_referal;
    TextView tv_discapply;
    TextView tv_refapply;
    ImageView img_back;
    FloatingActionButton fab_first;
    ListView list_products;
    CardView card_ordernow;
    TextView tv_total_price;
    EditText edt_promocode;
    TextView tv_code_apply;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_revieworders);
        session = new UserSessionManager(ReviewOrdersActivity.this);

        cb_referal = (CheckBox) findViewById(R.id.cb_referal);
        tv_discapply = (TextView) findViewById(R.id.tv_discapply);
        tv_refapply = (TextView) findViewById(R.id.tv_refapply);
        img_back = (ImageView) findViewById(R.id.img_back);
        fab_first = (FloatingActionButton) findViewById(R.id.fab_first);
        list_products = (ListView) findViewById(R.id.list_products);
        card_ordernow = (CardView) findViewById(R.id.card_ordernow);
        tv_total_price = (TextView) findViewById(R.id.tv_total_price);
        edt_promocode = (EditText) findViewById(R.id.edt_promocode);
        tv_code_apply = (TextView) findViewById(R.id.tv_code_apply);
        sIsFirstPurchase = "";

        if (Singleton.getInstance().selectedItemsList.size() == 0) {
            mAmountCalculation();
            Toast.makeText(ReviewOrdersActivity.this, "No Product placed", Toast.LENGTH_SHORT).show();
            tv_discapply.setVisibility(View.INVISIBLE);
        } else {
            mAmountCalculation();
            adapterProducts = new ProductsListAdapter(ReviewOrdersActivity.this, null);
            list_products.setAdapter(adapterProducts);
        }


        card_ordernow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (session.getIsLoggedIn().equals("") || session.getUserMobile().equals("") || session.getUserAddress().equals("")) {
                    final Dialog dialog = new Dialog(ReviewOrdersActivity.this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.setContentView(R.layout.dialog_warning);
                    CardView card_ok = (CardView) dialog.findViewById(R.id.card_ok);
                    card_ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            if (session.getIsLoggedIn().equals("")) {
                                Toast.makeText(ReviewOrdersActivity.this, "Please login before placing an order!", Toast.LENGTH_SHORT).show();
                            } else if (session.getIsLoggedIn().equals("true")) {
                                Intent in = new Intent(ReviewOrdersActivity.this, MyProfileActivity.class);
                                startActivity(in);
                            }
                        }
                    });
                    dialog.show();
                } else {

                    int count = 0;
                    for(SelectedProductBean bean: Singleton.getInstance().selectedItemsList){
                        count += bean.getQuantity();
                    }
                    if(count == 0){
                        Toast.makeText(ReviewOrdersActivity.this, "No Products in the cart", Toast.LENGTH_SHORT).show();
                        finish();
                    }else{
                        OpenMOP_Dialog();
                    }
                }
            }
        });


        tv_code_apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sPromoCode = edt_promocode.getText().toString();
                if (!sPromoCode.equals("")) {
                    CallingPromoCodeRetrofit();
                } else {
                    Toast.makeText(ReviewOrdersActivity.this, "Please enter a code to verify", Toast.LENGTH_SHORT).show();
                }

            }
        });


        fab_first.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                iTotalAmt = iTotalAmt - Double.valueOf(sFirstPurchaseAmt).doubleValue();
                tv_total_price.setText("Rs. " + iTotalAmt);
                sTotPrice = "" + iTotalAmt;
                Toast.makeText(ReviewOrdersActivity.this, "Offer Applied", Toast.LENGTH_SHORT).show();
                fab_first.setVisibility(View.INVISIBLE);
                sIsFirstPurchase = "yes";
                tv_discapply.setVisibility(View.VISIBLE);
            }
        });

        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
            }
        });


        cb_referal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cb_referal.isChecked()) {
                    sCode = "1";
                    sRefCashChecked = "yes";
                    RefCashTask();
                } else {
                    sCode = "0";
                    sRefCashChecked = "";
                    mAmountCalculation();
                    tv_refapply.setVisibility(View.GONE);
                }
            }
        });

    }


    public void CallingCheckFirstPurchse() {

        progressDialog = new ProgressDialog(ReviewOrdersActivity.this);
        progressDialog.setMessage("Checking...");
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Please wait");
        progressDialog.show();
        RestAdapter Radapter = new RestAdapter.Builder().setEndpoint(DomainName.sURL + "checkfirstpurchase.php").build();
        RetrofitAPI api = Radapter.create(RetrofitAPI.class);
        Log.d("mobilenumberis",session.getUserMobile());
        Log.d("custidis",session.getCustomerId());
        Log.d("totalprice",sTotPrice);
        api.CheckFirstPurchase(session.getUserMobile(), session.getCustomerId(), sTotPrice,new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {

                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(response.getBody().in()));
                    String output = reader.readLine();
                    progressDialog.dismiss();
                    if ( output.contains("none")) {
                        Toast.makeText(ReviewOrdersActivity.this, "No Offers Found", Toast.LENGTH_SHORT).show();
                        fab_first.setVisibility(View.GONE);
                    } else if (output.contains("min")) {
                        Toast.makeText(ReviewOrdersActivity.this, "Please add some more products to avail first purchase offer", Toast.LENGTH_SHORT).show();
                        fab_first.setVisibility(View.GONE);
                    }
                    else if (output.contains("no refferal")) {
                        Toast.makeText(ReviewOrdersActivity.this, "This offer not applicable", Toast.LENGTH_SHORT).show();
                        fab_first.setVisibility(View.GONE);
                    }
                    else {

            sFirstPurchaseAmt = output.replaceAll("\"", "");
            fab_first.setVisibility(View.VISIBLE);
        Toast.makeText(ReviewOrdersActivity.this, "Click On Rupee Icon to get discount offer of Rs. " + sFirstPurchaseAmt, Toast.LENGTH_LONG).show();
}
                } catch (IOException e) {
                    if (progressDialog.isShowing()) progressDialog.dismiss();
                    e.printStackTrace();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                if (progressDialog.isShowing()) progressDialog.dismiss();
                fab_first.setVisibility(View.GONE);
                Toast.makeText(ReviewOrdersActivity.this, "Check Your Internet Connection", Toast.LENGTH_SHORT).show();

            }
        });

    }

    public class ProductsListAdapter extends BaseAdapter {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        JSONArray jsonArray;


        public ProductsListAdapter(Activity activity, JSONArray jArray) {
            jsonArray = jArray;
            inflater = (LayoutInflater) activity.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }


        public class ViewHolder {
            CardView card_row_products;
            ImageView img_product;
            MyTextView tv_pname, tv_unit_price, tv_mrp, tv_qty, tv_price, tv_size, tv_size_price;
            ImageView img_minus, img_plus;
        }

        @Override
        public int getCount() {
            return Singleton.getInstance().selectedItemsList.size();
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

                vi = inflater.inflate(R.layout.rows_reviewproducts_new, null);

                holder = new ViewHolder();
                holder.card_row_products = (CardView) vi.findViewById(R.id.card_row_products);
                holder.img_product = (ImageView) vi.findViewById(R.id.img_product);
                holder.tv_pname = (MyTextView) vi.findViewById(R.id.tv_pname);
                holder.tv_mrp = (MyTextView) vi.findViewById(R.id.tv_mrp);
                holder.img_minus = (ImageView) vi.findViewById(R.id.img_minus);
                holder.img_plus = (ImageView) vi.findViewById(R.id.img_plus);
                holder.tv_qty = (MyTextView) vi.findViewById(R.id.tv_qtya);
                // holder.tv_mrp.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                holder.tv_price = (MyTextView) vi.findViewById(R.id.tv_price);
                holder.tv_size = (MyTextView) vi.findViewById(R.id.tv_size);
                holder.tv_size_price = (MyTextView) vi.findViewById(R.id.tv_size_price);


                vi.setTag(holder);
            } else{
                holder = (ViewHolder) vi.getTag();
            }


            Glide.with(ReviewOrdersActivity.this).load("http://andhrabasket.com/main/andhrabasket/andhrabasketadmin/" + Singleton.getInstance().selectedItemsList.get(position).getProductImage1()).into(holder.img_product);
            holder.tv_pname.setText(Singleton.getInstance().selectedItemsList.get(position).getProductName());

            double price = Double.parseDouble(Singleton.getInstance().selectedItemsList.get(position).getProductUnitprice()) * Singleton.getInstance().selectedItemsList.get(position).getQuantity();

            holder.tv_size_price.setText("Rs. " + price);
            holder.tv_price.setText("Rs. " + Singleton.getInstance().selectedItemsList.get(position).getProductUnitprice());
            holder.tv_size.setText(Singleton.getInstance().selectedItemsList.get(position).getProductUnit());
            holder.tv_mrp.setVisibility(View.GONE);

            holder.tv_qty.setText(""+Singleton.getInstance().selectedItemsList.get(position).getQuantity());


            holder.img_minus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int quantity = Singleton.getInstance().selectedItemsList.get(position).getQuantity() - 1;

                    if (quantity == 0) {
                        Singleton.getInstance().selectedItemsList.remove(position);
                    }else{
                        Singleton.getInstance().selectedItemsList.get(position).setQuantity(quantity);
                    }
                    mAmountCalculation();
                    adapterProducts.notifyDataSetChanged();
                }
            });


            holder.img_plus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int quantity = Singleton.getInstance().selectedItemsList.get(position).getQuantity();
                    Singleton.getInstance().selectedItemsList.get(position).setQuantity(quantity + 1);

                    mAmountCalculation();
                    adapterProducts.notifyDataSetChanged();

                }
            });


            return vi;
        }
    }


    public void mAmountCalculation() {
        try {
            double price = 0;
            for (int i = 0; i < Singleton.getInstance().selectedItemsList.size(); i++) {
                price += Double.parseDouble(Singleton.getInstance().selectedItemsList.get(i).getProductUnitprice()) * Singleton.getInstance().selectedItemsList.get(i).getQuantity();
            }

            iTotalAmt = price;

            tv_total_price.setText("Rs. " + price);
            sTotPrice = ""+price;
            edt_promocode.setText("");
            edt_promocode.setEnabled(true);
            tv_code_apply.setText("Apply");
            tv_code_apply.setEnabled(true);
            cb_referal.setChecked(false);

            if (session.getIsLoggedIn().equals("true")) {
                CallingCheckFirstPurchse();
            } else if (!session.getIsLoggedIn().equals("true")) {
                Toast.makeText(ReviewOrdersActivity.this, "Please Login to Continue", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(ReviewOrdersActivity.this, "Price Not Available", Toast.LENGTH_SHORT).show();
        }


    }


    public void CallingPromoCodeRetrofit() {
        progressDialog = new ProgressDialog(ReviewOrdersActivity.this);
        progressDialog.setMessage("Applying...");
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Please wait");
        progressDialog.show();
        RestAdapter Radapter = new RestAdapter.Builder().setEndpoint(DomainName.sURL + "applycoupon.php").build();
        RetrofitAPI api = Radapter.create(RetrofitAPI.class);
        api.CouponCode(sTotPrice, sPromoCode, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {

                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(response.getBody().in()));
                    String output = reader.readLine();
                    if (progressDialog.isShowing()) progressDialog.dismiss();
                    output = output.replaceAll("\"", "");
                    output = output.replaceAll(" ", "");
                    if (output.equals("0")) {
                        Toast.makeText(ReviewOrdersActivity.this, "PromoCode not valid", Toast.LENGTH_SHORT).show();
                    } else {
                        sTotPrice = output;
                        tv_code_apply.setEnabled(false);
                        edt_promocode.setEnabled(false);
                        tv_code_apply.setText("Applied");
                        tv_total_price.setText("Rs. " + output);
                        sTotPrice = output;
                        Toast.makeText(ReviewOrdersActivity.this, "PromoCode Applied Successfully", Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    if (progressDialog.isShowing()) progressDialog.dismiss();
                    e.printStackTrace();
                }


            }

            @Override
            public void failure(RetrofitError error) {
                if (progressDialog.isShowing()) progressDialog.dismiss();
                Toast.makeText(ReviewOrdersActivity.this, "Connection Error", Toast.LENGTH_SHORT).show();

            }
        });
    }


    public void PlacingOrderRetrofit() {
        progressDialog = new ProgressDialog(ReviewOrdersActivity.this);
        progressDialog.setMessage("Placing Order...");
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Please wait");
        progressDialog.show();


        ArrayList<String > arrayPId = new ArrayList<>();
        ArrayList<String> arrayPName = new ArrayList<>();
        ArrayList<String> arrayPImage1 = new ArrayList<>();
        ArrayList<String> arrayPrice = new ArrayList<>();
        ArrayList<String> arrayQty = new ArrayList<>();
        ArrayList<String> arrayUnit = new ArrayList<>();
        double price = 0;
        for(SelectedProductBean bean: Singleton.getInstance().selectedItemsList){
            price += Double.parseDouble( bean.getProductUnitprice()) * bean.getQuantity();

            arrayPId.add( bean.getProductId());
            arrayPName.add( bean.getProductName());
            arrayPImage1.add( bean.getProductImage1());
            arrayPrice.add( bean.getProductUnitprice());
            arrayQty.add( ""+bean.getQuantity());
            arrayUnit.add(bean.getProductUnit());
        }
        sTotPrice = "" + price ;

        Log.d("stotalprice",sTotPrice);

        RestAdapter Radapter = new RestAdapter.Builder().setEndpoint(DomainName.sURL + "placeorder.php").build();
        RetrofitAPI api = Radapter.create(RetrofitAPI.class);
        api.PlaceOrder(session.getCustomerId(), session.getUserName(), session.getUserEmail(),
                session.getUserMobile(), arrayPId, arrayPName, arrayPImage1, arrayPId,
                arrayPId, arrayPrice, arrayQty,arrayUnit,Double.valueOf(iTotalAmt).toString(),session.getUserAddress(),sMOP,sDeliveryTime,"yes", new Callback<Response>() {
                    @Override
                    public void success(Response response, Response response2) {

                        try {
                            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getBody().in()));
                            String output = reader.readLine();
                            String sOrderId = output.replaceAll("\"","");
                            progressDialog.dismiss();
                            Toast.makeText(ReviewOrdersActivity.this, "Order Placed Successfully! Check MyOrders!", Toast.LENGTH_SHORT).show();

                            Singleton.getInstance().selectedItemsList.clear();

                            if(sRefCashChecked.equals("yes"))
                            {
                                CallingRefUseRetrofit();

                            }

                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(ReviewOrdersActivity.this);
                            alertDialog.setTitle("Cash On Delivery!");
                            alertDialog.setMessage("Your order is successfully placed and your Booking Order ID is "+sOrderId);
                            alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int which) {

                                    Intent returnIntent = new Intent();
                                    setResult(Activity.RESULT_OK,returnIntent);
                                    finish();

                                    dialog.cancel();
                                }
                            });

                            alertDialog.show();

                            /*
                            Intent in = new Intent(ReviewOrdersActivity.this,PaymentWebView.class);
                            in.putExtra("bno",sOrderId);
                            in.putExtra("totprice",sTotPrice);
                            finish();
                            startActivity(in);*/

                        }
                        catch (IOException e)
                        {
                            if(progressDialog.isShowing()) progressDialog.dismiss();
                            e.printStackTrace();
                            Toast.makeText(ReviewOrdersActivity.this, "Something Wrong Try Again", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        if(progressDialog.isShowing()) progressDialog.dismiss();
                        Toast.makeText(ReviewOrdersActivity.this, "Connection Error", Toast.LENGTH_SHORT).show();

                    }
                });


    }

    public void CallingRefUseRetrofit() {
        RestAdapter Radapter = new RestAdapter.Builder().setEndpoint(DomainName.sURL + "refuse.php").build();
        RetrofitAPI api = Radapter.create(RetrofitAPI.class);
        api.RefUse(session.getUserId(), session.getUserName(), session.getRefCodeSignUp(), sFirstPurchaseAmt, sTotPrice, session.getRefCode(), new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {

                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(response.getBody().in()));
                    String output = reader.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }


    public void OpenMOP_Dialog() {
        final Dialog dialog = new Dialog(ReviewOrdersActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_payment);
        final RadioButton rbtn_cod = (RadioButton) dialog.findViewById(R.id.rbtn_cod);
        final RadioButton rbtn_online = (RadioButton) dialog.findViewById(R.id.rbtn_online);
        final RadioButton rbtn_9to12 = (RadioButton) dialog.findViewById(R.id.rbtn_9to12);
        final RadioButton rbtn_2to8 = (RadioButton) dialog.findViewById(R.id.rbtn_2to8);
        CardView card_proceed = (CardView) dialog.findViewById(R.id.card_proceed);
        TextView tv_username = (TextView) dialog.findViewById(R.id.tv_username);
        TextView tv_mobile = (TextView) dialog.findViewById(R.id.tv_mobile);
        final EditText edt_address = (EditText) dialog.findViewById(R.id.edt_address);


        tv_username.setText("Name : " + session.getUserName());
        tv_mobile.setText("Mobile : " + session.getUserMobile());
        edt_address.setText(session.getUserAddress());

        rbtn_cod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rbtn_cod.setChecked(true);
                rbtn_online.setChecked(false);
                sMOP = "COD";
            }
        });

        rbtn_online.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rbtn_cod.setChecked(false);
                rbtn_online.setChecked(true);
                sMOP = "ONLINE";
            }
        });

        rbtn_9to12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sDeliveryTime = "9AM to 12PM";
                rbtn_9to12.setChecked(true);
                rbtn_2to8.setChecked(false);
            }
        });
        rbtn_2to8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sDeliveryTime = "2PM to 8PM";
                rbtn_9to12.setChecked(false);
                rbtn_2to8.setChecked(true);
            }
        });


        card_proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sDeliveryAddress = edt_address.getText().toString();
                if (sDeliveryAddress.equals("")) {
                    Toast.makeText(ReviewOrdersActivity.this, "Enter Delivery Address", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    session.setUserAddress(sDeliveryAddress);
                }
                switch (sMOP) {
                    case "COD":
                        dialog.dismiss();
                        PlacingOrderRetrofit();
                        break;
                    case "ONLINE":
                        dialog.dismiss();
                        Intent in = new Intent(ReviewOrdersActivity.this, PaymentWebView.class);

                        ArrayList<String > arrayPId = new ArrayList<>();
                        ArrayList<String> arrayPName = new ArrayList<>();
                        ArrayList<String> arrayPImage1 = new ArrayList<>();
                        ArrayList<String> arrayPrice = new ArrayList<>();
                        ArrayList<String> arrayQty = new ArrayList<>();
                        ArrayList<String> arrayUnit = new ArrayList<>();
                        double price = 0;
                        for(SelectedProductBean bean: Singleton.getInstance().selectedItemsList){
                            price += Double.parseDouble( bean.getProductUnitprice()) * bean.getQuantity();

                            arrayPId.add( bean.getProductId());
                            arrayPName.add( bean.getProductName());
                            arrayPImage1.add( bean.getProductImage1());
                            arrayPrice.add( bean.getProductUnitprice());
                            arrayQty.add( ""+bean.getQuantity());
                            arrayUnit.add(bean.getProductUnit());
                        }
                        sTotPrice = "" + price ;
                        String finalprice=Double.valueOf(iTotalAmt).toString();
                        in.putExtra("totprice", finalprice);
                        Log.d("totalpriceis",finalprice);
                        in.putExtra("firstamt", sFirstPurchaseAmt);
                        in.putExtra("firstpurchase", sIsFirstPurchase);
                        in.putExtra("address", sDeliveryAddress);
                        in.putStringArrayListExtra("id",arrayPId);
                        in.putStringArrayListExtra("name",arrayPName);
                        in.putStringArrayListExtra("image",arrayPImage1);
                        in.putStringArrayListExtra("price",arrayPrice);
                        in.putStringArrayListExtra("qty",arrayQty);
                        in.putStringArrayListExtra("unit",arrayUnit);
                        in.putExtra("referalcash", sRefCashChecked);
                        in.putExtra("dtime", sDeliveryTime);
                        finish();
                        startActivity(in);
                        break;
                    default:
                        Toast.makeText(ReviewOrdersActivity.this, "Please Select Mode Of Payment!", Toast.LENGTH_SHORT).show();
                }


            }
        });

        dialog.show();
    }


    public void RefCashTask() {

        double price = 0;
        for(SelectedProductBean bean: Singleton.getInstance().selectedItemsList){
            price += Double.parseDouble( bean.getProductUnitprice()) * bean.getQuantity();
        }
        sTotPrice = "" + price ;

        RestAdapter Radapter = new RestAdapter.Builder().setEndpoint(DomainName.sURL + "referalcash.php").build();
        RetrofitAPI api = Radapter.create(RetrofitAPI.class);
        api.GetReferalCash(session.getRefCodeSignUp(), session.getRefCode(), sCode, sTotPrice, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(response.getBody().in()));
                    String output = reader.readLine();
                    String sm[] = output.split("@");
                    if (sm[0].replaceAll("\"", "").replaceAll(" ", "").equals("0")) {
                        Toast.makeText(ReviewOrdersActivity.this, "No Referal Cash Found", Toast.LENGTH_SHORT).show();
                        cb_referal.setChecked(false);
                        sRefCashChecked = "";
                        double iPrice = Double.parseDouble(sm[1].replaceAll("\"", ""));
                        iTotalAmt = iPrice;
                        tv_refapply.setVisibility(View.GONE);
                        sTotPrice = "" + iTotalAmt;

                    } else {
                        if (sTotPrice.equals(sm[1])) {
                            Toast.makeText(ReviewOrdersActivity.this, "No Referal Cash Found", Toast.LENGTH_SHORT).show();
                            cb_referal.setChecked(false);
                            sRefCashChecked = "";
                            tv_total_price.setText("Rs. " + iTotalAmt);
                            tv_refapply.setVisibility(View.GONE);
                            sTotPrice = "" + iTotalAmt;
                        } else {
                            sRefCashChecked = "yes";
                            double iPrice = Double.parseDouble(sm[1].replaceAll("\"", ""));
                            iTotalAmt = iPrice;
                            tv_total_price.setText("Rs. " + iTotalAmt);
                            tv_refapply.setVisibility(View.VISIBLE);
                            sTotPrice = "" + iTotalAmt;
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(ReviewOrdersActivity.this, "Check Your internet connection", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK,returnIntent);
        finish();
    }
}
