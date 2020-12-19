package com.absket.in;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.Toast;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Sreejith on 28-03-2017.
 */
public class PaymentWebView extends Activity {

    WebView webview;
    String url_s;
    String sPaymentId;
    ProgressDialog progressDialog;
    int iCount=0;
    UserSessionManager session;

    SQLiteDatabase BasketSQL;
    ArrayList<String> arrayPId = new ArrayList<>();
    ArrayList<String> arrayPName = new ArrayList<>();
    ArrayList<String> arrayPImage1 = new ArrayList<>();
    ArrayList<String> arrayPrice = new ArrayList<>();
    ArrayList<String> arrayQty = new ArrayList<>();
    ArrayList<String> arrayUnit = new ArrayList<>();
    String sTotPrice="";
    String sIsFirstPurchase="";
    String sFirstPurchaseAmt="",sDeliveryTime="",sReferalChecked="";
    LinearLayout laymain;

    @SuppressLint("JavascriptInterface")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        webview = (WebView) findViewById(R.id.webview);
        session = new UserSessionManager(PaymentWebView.this);
        laymain = (LinearLayout) findViewById(R.id.laymain);

        Bundle b = getIntent().getExtras();
        //String sBno = b.getString("bno");
        sTotPrice = b.getString("totprice");
        Log.d("totalfinalpriceis",sTotPrice);

        sFirstPurchaseAmt = b.getString("firstamt");
        sIsFirstPurchase = b.getString("firstpurchase");
        arrayPId.addAll(b.getStringArrayList("id"));
        arrayPImage1.addAll(b.getStringArrayList("image"));
        arrayPName.addAll(b.getStringArrayList("name"));
        arrayPrice.addAll(b.getStringArrayList("price"));
        arrayQty.addAll(b.getStringArrayList("qty"));
        arrayUnit.addAll(b.getStringArrayList("unit"));
        sDeliveryTime = b.getString("dtime");
        sReferalChecked = b.getString("referalcash");
        VariableHolder.sTotalPrice = sTotPrice;
        Random random = new Random();
        int ra = random.nextInt(100000);
        VariableHolder.sProductName = session.getCustomerId()+session.getUserName()+""+ra;
        GetPaymentLink();

    }

    private final class PayUJavaScriptInterface {
        PayUJavaScriptInterface() {
        }


        public void success( long id, final String paymentId) {
            runOnUiThread(new Runnable() {
                public void run() {

                    Toast.makeText(PaymentWebView.this, "Status is txn is success "+" payment id is "+paymentId, Toast.LENGTH_LONG).show();
                    //String str="Status is txn is success "+" payment id is "+paymentId;
                    // new MainActivity().writeStatus(str);

                    //TextView  txtview;
                    //txtview = (TextView) findViewById(R.id.textView1);
                    // txtview.setText("Status is txn is success "+" payment id is "+paymentId);

                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(PaymentWebView.this);

                    // Setting Dialog Title
                    alertDialog.setTitle("Transaction successfull!");

                    // Setting Dialog Message
                    alertDialog.setMessage("Status is txn is success "+" payment id is "+paymentId);

                    // On pressing Settings button
                    alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int which) {
                            //call the asynctask to update the result
                            dialog.cancel();

                        }
                    });

                    // on pressing cancel button


                    // Showing Alert Message
                    alertDialog.show();



                }
            });




        }



    }


    public void GetPaymentStaus()
    {
        RestAdapter Radapter = new RestAdapter.Builder().setEndpoint(DomainName.sURL+"updatepayment.php").build();
        RetrofitAPI api = Radapter.create(RetrofitAPI.class);
        api.UpdatePaymentStatus(VariableHolder.sBookingNumber,sPaymentId, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(response.getBody().in()));
                    String output = reader.readLine();
                    if(progressDialog.isShowing())
                    progressDialog.dismiss();

                    if(output.contains("true") || output.contains("1"))
                    {
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(PaymentWebView.this);
                        alertDialog.setTitle("Transaction successfull!");
                        alertDialog.setMessage("Booking Number "+" is "+VariableHolder.sBookingNumber);
                        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int which) {
                                finish();
                                dialog.cancel();
                            }
                        });

                        alertDialog.show();
                    }
                    else {
                        if(progressDialog.isShowing())
                        progressDialog.dismiss();
                        finish();
                        Toast.makeText(PaymentWebView.this, "Update Failed! Call Customer Care Now", Toast.LENGTH_LONG).show();
                    }
                }
                catch (IOException e)
                {
                    if(progressDialog.isShowing())
                    progressDialog.dismiss();
                    finish();
                    Toast.makeText(PaymentWebView.this, "Update Failed! Call Customer Care", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }


            }

            @Override
            public void failure(RetrofitError error) {

                if(progressDialog.isShowing())
                progressDialog.dismiss();
                finish();
                Toast.makeText(PaymentWebView.this, "Connection Error! Contact BTS Customer Care", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void GetPaymentLink()
    {
        progressDialog = new ProgressDialog(PaymentWebView.this);
        progressDialog.setMessage("Generating Payment Link...");
        progressDialog.setTitle("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        RestAdapter Radapter = new RestAdapter.Builder().setEndpoint(DomainName.sURL+"payment.php").build();
        RetrofitAPI api = Radapter.create(RetrofitAPI.class);
        api.GetPaymentLink(session.getUserName(), session.getUserMobile(), VariableHolder.sProductName, VariableHolder.sTotalPrice, session.getUserEmail(),"yes",new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {

                try{
                    BufferedReader reader = new BufferedReader(new InputStreamReader(response.getBody().in()));
                    String output = reader.readLine();
                    VariableHolder.sPaymentUrl = output.replaceAll("\"","");
                    progressDialog.dismiss();
                    url_s = VariableHolder.sPaymentUrl;
                    Snackbar.make(laymain,"Please Wait...Loading...",Snackbar.LENGTH_LONG).show();
                    WebViewLoad();
                }
                catch (IOException e)
                {
                    progressDialog.dismiss();
                    e.printStackTrace();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                progressDialog.dismiss();
                Toast.makeText(PaymentWebView.this, "Check Your Internet Connection", Toast.LENGTH_SHORT).show();

            }
        });

    }


    public void WebViewLoad()
    {
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setDomStorageEnabled(true);
        webview.getSettings().setLoadWithOverviewMode(true);
        webview.getSettings().setUseWideViewPort(true);
        webview.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webview.getSettings().setSupportMultipleWindows(true);
        webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webview.addJavascriptInterface(new PayUJavaScriptInterface(), "InstaMojo");
        HashMap<String, String> headerMap = new HashMap<>();
        //put all headers in this header map
        headerMap.put("header", "Location: "+url_s);

        webview.loadUrl(url_s.toString(),headerMap);


        webview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                if(url.contains("http://andhrabasket.com/main/andhrabasket/andhrabasketphp/success.php") && url.contains("payment_id") && url.contains("payment_request_id"))
                {


                    iCount = iCount+1;
                    if(iCount==1) {
                        String sComplete[] = url.split("&");
                        String sHalf[] = sComplete[0].split("=");
                        sPaymentId = sHalf[1].replaceAll("\"", "");
                        progressDialog = new ProgressDialog(PaymentWebView.this);
                        progressDialog.setMessage("Payment Successfull");
                        progressDialog.setTitle("Please Wait...");
                        progressDialog.setCancelable(false);
                        progressDialog.show();
                        //GetPaymentStaus();

                            PlacingOrderRetrofit();
                    }

                }
                else if(url.contains("Status=Failed"))
                {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(PaymentWebView.this);

                    // Setting Dialog Title
                    alertDialog.setTitle("Transaction Failed!");

                    // Setting Dialog Message
                    alertDialog.setMessage("Please contact customer care for further enquiry");

                    // On pressing Settings button
                    alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int which) {
                            //call the asynctask to update the result
                            dialog.cancel();

                        }
                    });

                    // on pressing cancel button


                    // Showing Alert Message
                    alertDialog.show();
                }

            }

            @SuppressWarnings("unused")
            public void onReceivedSslError(WebView view) {
                Log.e("Error", "Exception caught!");
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

        });
    }


    public void CallingRefUseRetrofit()
    {
        RestAdapter Radapter = new RestAdapter.Builder().setEndpoint(DomainName.sURL+"refuse.php").build();
        RetrofitAPI api = Radapter.create(RetrofitAPI.class);
        api.RefUse(session.getUserId(), session.getUserName(), session.getRefCodeSignUp(), sFirstPurchaseAmt, sTotPrice,session.getRefCode(), new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {

                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(response.getBody().in()));
                    String output = reader.readLine();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }


            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    public void PlacingOrderRetrofit()
    {
        RestAdapter Radapter = new RestAdapter.Builder().setEndpoint(DomainName.sURL+"placeorder.php").build();
        RetrofitAPI api = Radapter.create(RetrofitAPI.class);
        api.PlaceOrder(session.getCustomerId(), session.getUserName(), session.getUserEmail(),
                session.getUserMobile(), arrayPId, arrayPName, arrayPImage1, arrayPId/*mrp*/,
                arrayPId, arrayPrice, arrayQty,arrayUnit,sTotPrice,session.getUserAddress(),"ONLINE",sDeliveryTime,"yes", new Callback<Response>() {
                    @Override
                    public void success(Response response, Response response2) {

                        try {
                            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getBody().in()));
                            String output = reader.readLine();
                            String sOrderId = output.replaceAll("\"","");
                            VariableHolder.sBookingNumber = sOrderId;
                            Toast.makeText(PaymentWebView.this, "Order Placed Successfully! Check MyOrders!", Toast.LENGTH_SHORT).show();

                            Intent openIntent = new Intent(getApplicationContext(), MainActivity_New.class);
                            openIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            getApplicationContext().startActivity(openIntent);
                            if(sReferalChecked.equals("yes"))
                            {
                                CallingRefUseRetrofit();
                            }

                            GetPaymentStaus();


                        }
                        catch (IOException e)
                        {
                            if(progressDialog.isShowing()) progressDialog.dismiss();
                            e.printStackTrace();
                            Toast.makeText(PaymentWebView.this, "Something Wrong Try Again", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        if(progressDialog.isShowing()) progressDialog.dismiss();
                        Toast.makeText(PaymentWebView.this, "Connection Error", Toast.LENGTH_SHORT).show();

                    }
                });
    }


}
