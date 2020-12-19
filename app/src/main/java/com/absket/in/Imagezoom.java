package com.absket.in;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ImageView;


public class Imagezoom extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zoomimage);
        Bitmap bitmap = (Bitmap)this.getIntent().getParcelableExtra("Bitmap");
        ImageView viewBitmap = (ImageView)findViewById(R.id.bitmapview);

        viewBitmap.setImageBitmap(bitmap);

        ZoomAnimation zoomAnimation = new ZoomAnimation(Imagezoom.this);
        zoomAnimation.zoom(viewBitmap, 0);
    }
}
