package com.imapp.taskreminder.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;

import com.imapp.taskreminder.R;

public class AboutAppActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_app);

        if (getSupportActionBar() != null ){
            getSupportActionBar().setElevation(0);
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimaryDark)));
            getSupportActionBar().setTitle(Html.fromHtml("<font color='"+getResources().getColor(R.color.colorPrimary)+"'>"+getResources().getString(R.string.title_about_app)+"</font>"));
        }
    }
}
