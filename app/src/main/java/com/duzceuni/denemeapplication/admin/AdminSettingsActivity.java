package com.duzceuni.denemeapplication.admin;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.duzceuni.denemeapplication.R;
import com.google.android.material.navigation.NavigationView;

public class AdminSettingsActivity extends AppCompatActivity {

    private Button btn_menu_settings;
    private DrawerLayout drawerLayout;
    private NavigationView nv_slide;
    private SharedPreferences sharedPref,sharedPref_theme;
    private AutoCompleteTextView autoCompleteTextView_theme,autoCompleteTextView_language;
    private String[] theme;
    private ArrayAdapter<String> arrayAdapter_theme,arrayAdapter_language;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_settings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        theme = getResources().getStringArray(R.array.theme);
        arrayAdapter_theme = new ArrayAdapter<>(this, R.layout.dropdown_theme, theme);

        initCompanents();
        registerEventHandlers();
    }
    private void initCompanents()
    {
        sharedPref = getSharedPreferences("admin_config", Context.MODE_PRIVATE);
        sharedPref_theme = getSharedPreferences("theme", Context.MODE_PRIVATE);
        btn_menu_settings = findViewById(R.id.btn_menu_settings);
        drawerLayout = findViewById(R.id.main);
        nv_slide = findViewById(R.id.ng_slide);

        autoCompleteTextView_theme = findViewById(R.id.autoCompleteTextView_theme);
        autoCompleteTextView_theme.setAdapter(arrayAdapter_theme);
    }
    private void registerEventHandlers()
    {
        btn_menu_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerOpen(nv_slide)) {
                    drawerLayout.closeDrawer(nv_slide);
                } else {
                    drawerLayout.openDrawer(nv_slide);
                }
            }
        });
        nv_slide.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    MenuClass menuClass = new MenuClass();
                    menuClass.logout(menuItem,AdminSettingsActivity.this,sharedPref);
                if (menuItem.getItemId()==R.id.snv_turnoff)
                    finishAffinity();

                //drawerLayout.closeDrawer(nv_slide);
                return true;
            }
        });

        autoCompleteTextView_theme.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
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
                SharedPreferences.Editor editor = sharedPref_theme.edit();
                editor.putInt("theme",position);
                editor.apply();
                Toast.makeText(AdminSettingsActivity.this,"Tema değiştirildi.",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(AdminSettingsActivity.this,AdminSettingsActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                //overridePendingTransition(R.anim.giris_soldan_saga1, R.anim.giris_soldan_saga2);
            }
        });

    }
    public boolean onKeyDown(int keyCode, KeyEvent keyEvent) {   //geri tusuna basınca olacak
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                Intent intent = new Intent(AdminSettingsActivity.this,AdminHomeActivity.class);
                startActivity(intent);
                break;
        }
        return true;
    }
}
