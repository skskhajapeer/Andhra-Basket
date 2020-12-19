package com.absket.in;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;


import it.sephiroth.android.library.bottomnavigation.BottomNavigation;


public class HomeMainActivity extends AppCompatActivity {


    BottomNavigation bottomNavigation;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homemain_activity);
        bottomNavigation=(BottomNavigation)findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnMenuItemClickListener(new BottomNavigation.OnMenuItemSelectionListener() {
            @Override
            public void onMenuItemSelect(int i, int i1, boolean b) {


            }

            @Override
            public void onMenuItemReselect(int i, int i1, boolean b) {

            }
        });


    }
}
