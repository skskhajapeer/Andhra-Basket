package com.absket.in;

import android.app.Activity;
import android.app.ProgressDialog;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.bluehomestudio.luckywheel.LuckyWheel;
import com.bluehomestudio.luckywheel.WheelItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Sreejith on 29-04-2017.
 */
public class SpinWheelActivity extends Activity {

    LuckyWheel spinWheel;
    Button btn_continue;

    ArrayList<String> arrayPId = new ArrayList<>();
    ArrayList<String> arrayPName = new ArrayList<>();
    ArrayList<String> arrayPImage1 = new ArrayList<>();
    ArrayList<String> arrayMRP = new ArrayList<>();
    ArrayList<String> arrayDiscount = new ArrayList<>();
    ArrayList<String> arrayPrice = new ArrayList<>();
    ArrayList<String> arrayQty = new ArrayList<>();
    String sTotPrice;

    ProgressDialog progressDialog;
    UserSessionManager session;
    SQLiteDatabase BasketSQL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spinwheel);
        spinWheel=(LuckyWheel)findViewById(R.id.lwv);
        btn_continue=(Button)findViewById(R.id.btn_continue);
        session = new UserSessionManager(SpinWheelActivity.this);


        List<WheelItem> wheelItems = new ArrayList<>();

        wheelItems.add(new WheelItem(Color.parseColor("#82B1FF"), BitmapFactory.decodeResource(getResources(), R.drawable.ten)));

        wheelItems.add(new WheelItem(Color.parseColor("#82B1FF"), BitmapFactory.decodeResource(getResources(), R.drawable.twenty)));

        wheelItems.add(new WheelItem(Color.parseColor("#82B1FF"), BitmapFactory.decodeResource(getResources(), R.drawable.thirty)));

        wheelItems.add(new WheelItem(Color.parseColor("#EEFF41"), BitmapFactory.decodeResource(getResources(), R.drawable.fourty)));

        wheelItems.add(new WheelItem(Color.parseColor("#82B1FF"), BitmapFactory.decodeResource(getResources(), R.drawable.fifty)));

        wheelItems.add(new WheelItem(Color.parseColor("#82B1FF"), BitmapFactory.decodeResource(getResources(), R.drawable.sixty)));

        wheelItems.add(new WheelItem(Color.parseColor("#82B1FF"), BitmapFactory.decodeResource(getResources(), R.drawable.seventy)));

        wheelItems.add(new WheelItem(Color.parseColor("#EEFF41"), BitmapFactory.decodeResource(getResources(), R.drawable.ten)));

        wheelItems.add(new WheelItem(Color.parseColor("#82B1FF"), BitmapFactory.decodeResource(getResources(), R.drawable.twenty)));

        wheelItems.add(new WheelItem(Color.parseColor("#82B1FF"), BitmapFactory.decodeResource(getResources(), R.drawable.thirty)));

        wheelItems.add(new WheelItem(Color.parseColor("#82B1FF"), BitmapFactory.decodeResource(getResources(), R.drawable.fourty)));

        wheelItems.add(new WheelItem(Color.parseColor("#EEFF41"), BitmapFactory.decodeResource(getResources(), R.drawable.fifty)));



        spinWheel.addWheelItems(wheelItems);


        Random r = new Random(); int i1 = r.nextInt(10 - 1) + 1;

        spinWheel.rotateWheelTo(i1);

        BasketSQL = SpinWheelActivity.this.openOrCreateDatabase("ABASKET",MODE_PRIVATE,null);
        BasketSQL.execSQL("CREATE TABLE IF NOT EXISTS tbl_cart (product_id VARCHAR,product_name VARCHAR,product_image1 VARCHAR,product_mrp VARCHAR,product_discount VARCHAR,product_price VARCHAR,quantity VARCHAR)");



        Bundle b = getIntent().getExtras();
        arrayPId.addAll(b.getStringArrayList("pid"));
        arrayPName.addAll(b.getStringArrayList("pname"));
        arrayDiscount.addAll(b.getStringArrayList("discount"));
        arrayPImage1.addAll(b.getStringArrayList("pimage"));
        arrayPrice.addAll(b.getStringArrayList("price"));
        arrayMRP.addAll(b.getStringArrayList("mrp"));
        arrayQty.addAll(b.getStringArrayList("qty"));
        sTotPrice = b.getString("tprice");


        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(session.getUserMobile().equals("") || session.getUserAddress().equals(""))
                {
                    Toast.makeText(SpinWheelActivity.this, "Please update your mobile number and address from profile", Toast.LENGTH_SHORT).show();

                }
                else
                {
                    PlacingOrderRetrofit();
                }

            }
        });


    }


    public void PlacingOrderRetrofit()
    {
        /*progressDialog = new ProgressDialog(SpinWheelActivity.this);
        progressDialog.setMessage("Placing Order...");
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Please wait");
        progressDialog.show();
        RestAdapter Radapter = new RestAdapter.Builder().setEndpoint(DomainName.sURL+"placeorder.php").build();
        RetrofitAPI api = Radapter.create(RetrofitAPI.class);
        api.PlaceOrder(session.getCustomerId(), session.getUserName(), session.getUserEmail(),
                session.getUserMobile(), arrayPId, arrayPName, arrayPImage1, arrayMRP,
                arrayDiscount, arrayPrice, arrayQty,sTotPrice, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {

                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(response.getBody().in()));
                    String output = reader.readLine();
                    progressDialog.dismiss();
                    Toast.makeText(SpinWheelActivity.this, "Order Placed Successfully! Check MyOrders!", Toast.LENGTH_SHORT).show();
                    BasketSQL.execSQL("DELETE FROM tbl_cart");
                    finish();
                }
                catch (IOException e)
                {
                    if(progressDialog.isShowing()) progressDialog.dismiss();
                    e.printStackTrace();
                    Toast.makeText(SpinWheelActivity.this, "Something Wrong Try Again", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                if(progressDialog.isShowing()) progressDialog.dismiss();
                Toast.makeText(SpinWheelActivity.this, "Connection Error", Toast.LENGTH_SHORT).show();

            }
        });
*/

    }
}
