package com.absket.in;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.absket.in.CustomFonts.MyTextView;

public class GetStartedActivity extends Activity {

 MyTextView btnStarted,btnSignup,btnSkip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_started);
        btnStarted=(MyTextView)findViewById(R.id.btn_started);
        btnSignup=(MyTextView)findViewById(R.id.btn_signup);
        btnSkip=(MyTextView)findViewById(R.id.btn_skip);
        btnStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnStarted.setBackground(getResources().getDrawable(R.drawable.get_start_selected));
                btnSignup.setBackground(getResources().getDrawable(R.drawable.get_started_unselected));
                btnSignup.setTextColor(getResources().getColor(R.color.toolbarColor));
                btnStarted.setTextColor(getResources().getColor(R.color.white));
                Intent loginIntent=new Intent(GetStartedActivity.this,NewSignupActivity.class);
                loginIntent.putExtra("active_tab","login");
                startActivity(loginIntent);
             //   StartActivity(loginIntent);


            }
        });

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnStarted.setBackground(getResources().getDrawable(R.drawable.get_started_unselected));
                btnSignup.setBackground(getResources().getDrawable(R.drawable.get_start_selected));
                btnSignup.setTextColor(getResources().getColor(R.color.white));
                btnStarted.setTextColor(getResources().getColor(R.color.toolbarColor));
                Intent loginIntent=new Intent(GetStartedActivity.this,NewSignupActivity.class);
                loginIntent.putExtra("active_tab","signup");
                startActivity(loginIntent);
            }
        });

        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(GetStartedActivity.this, MainActivity_New.class);
                startActivity(in);
            }
        });
    }
}
