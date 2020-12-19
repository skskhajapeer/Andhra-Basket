package com.absket.in;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;


/**
 * Created by Sreejith on 03-05-2017.
 */
public class SelectionScreenActivity extends Activity {

    CardView card_login;
    CardView card_signup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);
        card_login=(CardView)findViewById(R.id.card_login) ;
        card_signup=(CardView)findViewById(R.id.card_signup) ;

        card_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(SelectionScreenActivity.this,LoginActivity.class);
                finish();
                startActivity(in);
            }
        });

        card_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(SelectionScreenActivity.this,SignUpActivity.class);
                finish();
                startActivity(in);
            }
        });
    }
}
