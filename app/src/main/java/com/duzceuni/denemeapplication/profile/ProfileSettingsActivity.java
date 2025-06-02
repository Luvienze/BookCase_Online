package com.duzceuni.denemeapplication.profile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.duzceuni.denemeapplication.R;
import com.duzceuni.denemeapplication.admin.MenuClass;
import com.duzceuni.denemeapplication.home.MainActivity;
import com.google.android.material.navigation.NavigationView;

public class ProfileSettingsActivity extends AppCompatActivity {

    private NavigationView nv_slide;
    private SharedPreferences sharedPref,sharedPref_theme;
    private AutoCompleteTextView autoCompleteTextView_theme;
    private String[] theme;
    private ArrayAdapter<String> arrayAdapter_theme;
    private ImageButton imgBtnSearch;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile_settings);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        theme = getResources().getStringArray(R.array.theme);
        arrayAdapter_theme = new ArrayAdapter<>(this, R.layout.dropdown_theme, theme);

        initComponents();
        registerEventHandlers();
    }

    /**
     * Initialize components
     * */
    private void initComponents() {
        imgBtnSearch = findViewById(R.id.imgBtnSearch);
        sharedPref = getSharedPreferences("admin_config", Context.MODE_PRIVATE);
        sharedPref_theme = getSharedPreferences("theme", Context.MODE_PRIVATE);
        nv_slide = findViewById(R.id.ng_slide);

        autoCompleteTextView_theme = findViewById(R.id.autoCompleteTextView_theme);
        autoCompleteTextView_theme.setAdapter(arrayAdapter_theme);
    }
    private void registerEventHandlers() {
        returnToHome();
        setNavigationItemSelect();
        setAutoCompleteTextViewTheme();
    }

    private void setNavigationItemSelect(){
        nv_slide.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                MenuClass menuClass = new MenuClass();
                menuClass.logout(menuItem, ProfileSettingsActivity.this,sharedPref);
                if (menuItem.getItemId()==R.id.snv_turnoff)
                    finishAffinity();

                return true;
            }
        });
    }

    private void setAutoCompleteTextViewTheme(){
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
                Toast.makeText(ProfileSettingsActivity.this,getString(R.string.themeIsChanges),Toast.LENGTH_LONG).show();
                Intent intent = new Intent(ProfileSettingsActivity.this,ProfileSettingsActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);

            }
        });
    }

    /**
     * Return to home
     * */
    private void returnToHome(){
        imgBtnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent returnToHome = new Intent(ProfileSettingsActivity.this, MainActivity.class);
                startActivity(returnToHome);
            }
        });
    }


}
