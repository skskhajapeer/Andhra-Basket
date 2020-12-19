package com.absket.in.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.absket.in.CustomFonts.MyTextViewBold;
import com.absket.in.ListProductsActivity;
import com.absket.in.R;
import com.absket.in.SpecialShopsActivity;
import com.absket.in.VariableHolder;
import com.bumptech.glide.Glide;

import org.json.JSONArray;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_FOOTER = 2;
    ArrayList<String> mList = new ArrayList<>();
    private Context context;
    JSONArray jsonArray;


    String [] result;
    int [] imageId;


    public MyAdapter(Context context, String[] osNameList, int[] osImages) {
        this.result=osNameList;
        this.imageId=osImages;

      this.context=context;
    }


    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position)) {
            return TYPE_HEADER;

        } else if (isPositionFooter(position)) {
            return TYPE_FOOTER;
        }

        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

    private boolean isPositionFooter(int position) {
        return position > mList.size();
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.rows_main_category, parent, false);
        return new MyAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {


      //  Glide.with(context).load("http://andhrabasket.com/main/andhrabasket/andhrabasketadmin/" + VariableHolder.arrayImages.get(position)).into(holder.img_category);

        //Toast.makeText(context,"Hai Hello",Toast.LENGTH_LONG).show();

        holder.tv_categoryname.setText(result[position]);
        holder.img_category.setImageResource(imageId[position]);


        holder.tv_categoryname.setText(VariableHolder.arrayCategory.get(position));
        holder.linear_main_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (VariableHolder.arrayCategory.get(position).contains("Special")) {
                    Intent in = new Intent(context, SpecialShopsActivity.class);
                    in.putExtra("maincategory", VariableHolder.arrayCategory.get(position));
                    in.putExtra("subcategory", "");
                    context.startActivity(in);
                } else {
                    Intent in = new Intent(context, ListProductsActivity.class);
                    in.putExtra("maincategory", VariableHolder.arrayCategory.get(position));
                    in.putExtra("subcategory", "");
                    context.startActivity(in);
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        // Add two more counts to accomodate header and footer

        return VariableHolder.arrayCategory.size();

    }


    // The ViewHolders for Header, Item and Footer
    public class MyViewHolder extends RecyclerView.ViewHolder {

       RelativeLayout linear_main_category;
        MyTextViewBold tv_categoryname;
        ImageView img_category;
        public MyViewHolder(View itemView) {
            super(itemView);
            linear_main_category=(RelativeLayout)itemView.findViewById(R.id.linear_main_category);
            tv_categoryname = (MyTextViewBold) itemView.findViewById(R.id.tv_categoryname);
            img_category=(ImageView)itemView.findViewById(R.id.img_category);


        }
    }


}
