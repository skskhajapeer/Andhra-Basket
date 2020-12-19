package com.absket.in;

/**
 * Created by Sreejith on 08-10-2015.
 */

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;


public class ViewPagerAdapterImages extends PagerAdapter {
    int size;
    Context act;
    View layout;
    ImageView pageImage;



    public ViewPagerAdapterImages(Context context, int noofsize) {
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
        layout = inflater.inflate(R.layout.pages, null);
        pageImage = (ImageView) layout.findViewById(R.id.imageView1);



        pageImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {




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
