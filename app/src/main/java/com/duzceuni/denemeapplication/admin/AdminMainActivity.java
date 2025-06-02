package com.duzceuni.denemeapplication.admin;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.duzceuni.denemeapplication.R;
import com.duzceuni.denemeapplication.home.LoginActivity;

public class AdminMainActivity extends AppCompatActivity {
    private Button btn_admin,btn_users;
    private Animation anim1,anim2;
    private SharedPreferences sharedPref,sharedPref_theme;
    private Integer theme;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        sharedPref = getSharedPreferences("admin_config", Context.MODE_PRIVATE);
        sharedPref_theme = getSharedPreferences("theme", Context.MODE_PRIVATE);
        theme = sharedPref_theme.getInt("theme",2);
        switch (theme){
            case 0:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case 1:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            case 2:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
        }

        //Animasyon tanimlari
        anim1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.giris_soldan_saga1);
        anim2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.giris_soldan_saga2);

        initComponents();
        registerEventHandlers();
    }
    private void initComponents() {
        btn_admin = findViewById(R.id.btn_admin);
        btn_users = findViewById(R.id.btn_users);

        btn_users.setAnimation(anim1);  //sayfa baslar baslamaz animasyonu oynatır
        btn_admin.setAnimation(anim2);
    }
    private void registerEventHandlers() {
        //goToAdminLogin();
        goToUserLogin();
        goToAdminLogin();
    }
    private void goToAdminLogin(){
        btn_admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean skip_login = sharedPref.getBoolean("remamberme",false);
                if (skip_login)
                {
                    Intent login_yonlendirme1 = new Intent(AdminMainActivity.this,AdminHomeActivity.class);
                    startActivity(login_yonlendirme1);  //admin anasayfa ekranına yonlendiriyor
                }
                else
                {
                    Intent login_yonlendirme2 = new Intent(AdminMainActivity.this,AdminLoginActivity.class);
                    startActivity(login_yonlendirme2);  //admin giris ekranına yonlendiriyor
                }
            }
        });
    }
    private void goToUserLogin(){
        btn_users.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToSignUpActivity = new Intent(AdminMainActivity.this, LoginActivity.class);
                startActivity(goToSignUpActivity);
            }
        });
    }
    public boolean onKeyDown(int keyCode, KeyEvent keyEvent) {   //geri tusuna basınca olacak
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                finishAffinity();
                break;
        }
        return true;
    }
}