package com.example.fittubertrial1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;


import com.google.android.material.navigation.NavigationView;


public class Search extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private WebView webview;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        //Remove status bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        webview=(WebView) findViewById(R.id.WebView1);
        webview.setWebChromeClient(new WebChromeClient());
        webview.loadUrl("https://fittuberr.000webhostapp.com/");
        webview.getSettings().setDomStorageEnabled(true);
        //webview.getSettings().setUseWideViewPort(true);

        //Drawer code
        drawerLayout=findViewById(R.id.drawer_layout);
        navigationView=findViewById(R.id.nav_view);
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle=new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START);
        else if(webview.canGoBack())
            webview.goBack();
        else
            super.onBackPressed();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch(menuItem.getItemId())
        {
            case R.id.admin_login:
                Intent intent=new Intent(Search.this,LoginActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.lefttoright,R.anim.lefttoright);
                break;

            case R.id.share:
                Intent shareIntent=new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                String shareBody="Hi,download this amazing application now:-https://play.google.com/store/apps/details?id=com.digilocker.android&hl=en_IN&hl=en";
                String shareSub="Fit Tuber App";
                shareIntent.putExtra(Intent.EXTRA_SUBJECT,shareSub);
                shareIntent.putExtra(Intent.EXTRA_SUBJECT,shareBody);
                startActivity(Intent.createChooser(shareIntent,"Share Using"));
                break;

            case R.id.rate_us:
                try{
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.digilocker.android&hl=en_IN")));
                }
                catch (ActivityNotFoundException e)
                {
                    Toast.makeText(this,"Unable to open, Please try again",Toast.LENGTH_SHORT).show();
                    //startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse("https://play.google.com/store/apps/details?id=com.digilocker.android&hl=en_IN")));
                }
                break;
        }
        return false;
    }
}
