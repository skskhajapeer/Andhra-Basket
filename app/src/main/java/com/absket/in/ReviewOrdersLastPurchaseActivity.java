package com.absket.in;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONArray;

import java.util.ArrayList;

/**
 * Created by Sreejith on 07-04-2017.
 */
public class ReviewOrdersLastPurchaseActivity extends Activity {

    ProductsListAdapter adapterProducts;
    SQLiteDatabase BasketSQL;
    ArrayList<String> arrayPId;
    ArrayList<String> arrayPName;
    ArrayList<String> arrayPImage1;
    ArrayList<String> arrayMRP;
    ArrayList<String> arrayDiscount;
    ArrayList<String> arrayPrice;
    ArrayList<String> arrayQty;
    CardView card_ordernow;
    TextView tv_total_price;
    ListView list_products;
    String sTotPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_revieworders);

         card_ordernow=(CardView)findViewById(R.id.card_ordernow);
         tv_total_price=(TextView)findViewById(R.id.tv_total_price);
        list_products=(ListView)findViewById(R.id.list_products);

        BasketSQL = ReviewOrdersLastPurchaseActivity.this.openOrCreateDatabase("ABASKET",MODE_PRIVATE,null);
        BasketSQL.execSQL("CREATE TABLE IF NOT EXISTS tbl_last_cart (product_id VARCHAR,product_name VARCHAR,product_image1 VARCHAR,product_mrp VARCHAR,product_discount VARCHAR,product_price VARCHAR,quantity VARCHAR)");
        Cursor c = BasketSQL.rawQuery("SELECT * FROM tbl_last_cart",null);
        if(c.getCount()>0)
        {
            arrayPId = new ArrayList<>();
            arrayPName = new ArrayList<>();
            arrayPImage1 = new ArrayList<>();
            arrayMRP = new ArrayList<>();
            arrayDiscount = new ArrayList<>();
            arrayPrice = new ArrayList<>();
            arrayQty = new ArrayList<>();
            while(c.moveToNext())
            {
                arrayPId.add(c.getString(0));
                arrayPName.add(c.getString(1));
                arrayPImage1.add(c.getString(2));
                arrayMRP.add(c.getString(3));
                arrayDiscount.add(c.getString(4));
                arrayPrice.add(c.getString(5));
                arrayQty.add(c.getString(6));
            }
            mAmountCalculation();
            adapterProducts = new ProductsListAdapter(ReviewOrdersLastPurchaseActivity.this,null);
            list_products.setAdapter(adapterProducts);
        }
        else
        {
            Toast.makeText(ReviewOrdersLastPurchaseActivity.this, "No Product placed", Toast.LENGTH_SHORT).show();
        }

        card_ordernow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent in = new Intent(ReviewOrdersLastPurchaseActivity.this,SpinWheelActivity.class);
                in.putStringArrayListExtra("pid",arrayPId);
                in.putStringArrayListExtra("pname",arrayPName);
                in.putStringArrayListExtra("pimage",arrayPImage1);
                in.putStringArrayListExtra("mrp",arrayMRP);
                in.putStringArrayListExtra("discount",arrayDiscount);
                in.putStringArrayListExtra("price",arrayPrice);
                in.putStringArrayListExtra("qty",arrayQty);
                in.putExtra("tprice",sTotPrice);
                finish();
                startActivity(in);*/
            }
        });

    }

    public  class ProductsListAdapter extends BaseAdapter {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        JSONArray jsonArray;


        public ProductsListAdapter(Activity activity, JSONArray jArray) {
            jsonArray = jArray;
            inflater = (LayoutInflater) activity.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }


        public  class ViewHolder {
            CardView card_row_products;
            ImageView img_product;
            TextView tv_pname,tv_unit_price,tv_mrp,tv_qty,tv_price;
            ImageView img_minus,img_plus;
        }

        @Override
        public int getCount() {
            return arrayPImage1.size();
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

                vi = inflater.inflate(R.layout.rows_products, null);

                holder = new ViewHolder();
                holder.card_row_products = (CardView) vi.findViewById(R.id.card_row_products);
                holder.img_product = (ImageView) vi.findViewById(R.id.img_product);
                holder.tv_pname = (TextView) vi.findViewById(R.id.tv_pname);
                holder.tv_unit_price = (TextView) vi.findViewById(R.id.tv_unit_price);
                holder.tv_mrp = (TextView) vi.findViewById(R.id.tv_mrp);
               // holder.img_minus = (ImageView) vi.findViewById(R.id.img_minus);
               // holder.img_plus = (ImageView) vi.findViewById(R.id.img_plus);
               // holder.tv_qty = (TextView) vi.findViewById(R.id.tv_qty);
                holder.tv_mrp.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                holder.tv_unit_price.setVisibility(View.GONE);
                holder.tv_price = (TextView) vi.findViewById(R.id.tv_price);


                vi.setTag(holder);
            } else

                holder = (ViewHolder) vi.getTag();
            Glide.with(ReviewOrdersLastPurchaseActivity.this).load("http://andhrabasket.com/main/andhrabasket/andhrabasketadmin/"+arrayPImage1.get(position)).into(holder.img_product);

            holder.tv_pname.setText(arrayPName.get(position));
           // holder.tv_unit_price.setText(arrayUnit.get(position)+" "+arrayUnitPrice.get(position));
            holder.tv_mrp.setText(arrayMRP.get(position));
            holder.tv_price.setText("Rs. "+arrayPrice.get(position));


            Cursor cmain = BasketSQL.rawQuery("SELECT * FROM tbl_last_cart WHERE product_id='"+arrayPId.get(position)+"'",null);
            if(cmain.getCount()>0)
            {
                String sOldQty="";
                while (cmain.moveToNext())
                {
                    sOldQty = cmain.getString(6);
                }
                holder.tv_qty.setText(sOldQty);
            }
            else
            {
                holder.tv_qty.setText("0");
            }



            holder.img_minus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int i1 = Integer.parseInt(holder.tv_qty.getText().toString());
                    if(i1==0)
                    {
                        //Toast.makeText(ProductsListAdapter.this, "", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        i1 = i1-1;
                        String sQty = ""+i1;
                        if(i1==0)
                        {
                            BasketSQL.execSQL("DELETE FROM tbl_last_cart WHERE product_id='"+arrayPId.get(position)+"'");
                            holder.tv_qty.setText(""+i1);
                            arrayPId.remove(position);
                            arrayPName.remove(position);
                            arrayPImage1.remove(position);
                            arrayPrice.remove(position);
                            arrayDiscount.remove(position);
                            arrayMRP.remove(position);
                            arrayQty.remove(position);
                            adapterProducts.notifyDataSetChanged();
                        }
                        else
                        {
                            Double dPrice = Double.parseDouble(arrayPrice.get(position));
                            Double dTotPrice = i1*dPrice;
                            BasketSQL.execSQL("UPDATE tbl_last_cart SET product_price='"+dTotPrice+"',quantity='"+sQty+"' WHERE product_id='"+arrayPId.get(position)+"'");
                            holder.tv_qty.setText(""+i1);

                        }

                    }
                    mAmountCalculation();
                }
            });


            holder.img_plus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int i1 = Integer.parseInt(holder.tv_qty.getText().toString());
                    i1 = i1+1;
                    String sQty = ""+i1;
                    holder.tv_qty.setText(""+i1);

                    Double dPrice = Double.parseDouble(arrayPrice.get(position));
                    Double dTotPrice = i1*dPrice;

                    Cursor c = BasketSQL.rawQuery("SELECT * FROM tbl_last_cart WHERE product_id='"+arrayPId.get(position)+"'",null);
                    if(c.getCount()>0)
                    {
                        BasketSQL.execSQL("UPDATE tbl_last_cart SET product_price='"+dTotPrice+"',quantity='"+sQty+"' WHERE product_id='"+arrayPId.get(position)+"'");
                    }
                    else {
                        BasketSQL.execSQL("INSERT INTO tbl_last_cart (product_id,product_name,product_image1,product_mrp,product_discount,product_price,quantity) VALUES ('" + arrayPId.get(position) + "','" + arrayPName.get(position) + "','" + arrayPImage1.get(position) + "','" + arrayMRP.get(position) + "','" + arrayDiscount.get(position) + "','" + dTotPrice + "','" + sQty + "')");
                    }
                    mAmountCalculation();

                }
            });




            return vi;
        }
    }



    public void mAmountCalculation()
    {
        Cursor c = BasketSQL.rawQuery("SELECT * FROM tbl_last_cart",null);
        double iTotalAmt=0;
        if(c.getCount()>0) {
            while (c.moveToNext()) {
                String sAmt = c.getString(5);
                double iAmt = Double.parseDouble(sAmt);
                iTotalAmt = iTotalAmt + iAmt;
            }

            tv_total_price.setText("Rs. "+iTotalAmt);
            sTotPrice = ""+iTotalAmt;

        }
    }
}
