package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

public class Envelope_Management_Page extends AppCompatActivity {

    private ImageButton envelope_menubutton, saving_icon, food_icon, travel_icon, improvement_icon,
            insurance_icon, free_icon;

    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_envelope_management_page);

        envelope_menubutton = findViewById(R.id.envelope_menubutton);
        saving_icon = findViewById(R.id.saving_icon);
        food_icon = findViewById(R.id.food_icon);
        travel_icon = findViewById(R.id.travel_icon);
        improvement_icon = findViewById(R.id.improvement_icon);
        insurance_icon = findViewById(R.id.insurance_icon);
        free_icon = findViewById(R.id.free_icon);
        drawerLayout = findViewById(R.id.drawer_layout);

        saving_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent a = new Intent(Envelope_Management_Page.this, Saving_Envelope_Review.class);
                startActivity(a);
            }
        });

        food_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent a = new Intent(Envelope_Management_Page.this, Food_Envelope_Review.class);
                startActivity(a);
            }
        });

        travel_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent a = new Intent(Envelope_Management_Page.this, Travel_Envelope_Review.class);
                startActivity(a);
            }
        });

        improvement_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent a = new Intent(Envelope_Management_Page.this, Improvement_Envelope_Review.class);
                startActivity(a);
            }
        });

        insurance_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent a = new Intent(Envelope_Management_Page.this, Insurance_Envelope_Review.class);
                startActivity(a);
            }
        });

        free_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent a = new Intent(Envelope_Management_Page.this, Free_Envelope_Review.class);
                startActivity(a);
            }
        });

        envelope_menubutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(Envelope_Management_Page.this, menupage.class);
                startActivity(intent);
                finish();
            }
        });
    }
    public void ClickMenu(View view)
    {
        //open drawer
        openDrawer(drawerLayout);
    }

    public static void openDrawer(DrawerLayout drawerLayout)
    {
        //open drawer layout
        drawerLayout.openDrawer(GravityCompat.START);
    }

    public  void ClickLogo(View view)
    {
        //close drawer
        closeDrawer(drawerLayout);
    }

    public static void closeDrawer(DrawerLayout drawerLayout)
    {
        //close drawer layout
        //Check Condition
        if(drawerLayout.isDrawerOpen(GravityCompat.START))
        {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    public void Click_Summary(View view){
        //recreate activity
        redirectActivity(this,MainActivity.class);
    }

    public void ClickEnvelope(View view){
        recreate();
    }

    public void ClickTransaction(View view){
        redirectActivity(this,Transaction.class);
    }

    public void ClickPersonalProfile(View view){
        redirectActivity(this,Personal_Profile.class);
    }

    public void ClickLogout(View view)
    {
        redirectActivity(this,LoginPage.class);
    }


    public static void redirectActivity(Activity activity, Class aclass) {
        Intent intent = new Intent(activity,aclass);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        activity.startActivity(intent);
    }
}