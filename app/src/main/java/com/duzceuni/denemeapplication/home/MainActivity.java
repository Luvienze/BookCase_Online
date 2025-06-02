package com.duzceuni.denemeapplication.home;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.duzceuni.denemeapplication.R;
import com.duzceuni.denemeapplication.adapter.ViewPager2Adapter;
import com.duzceuni.denemeapplication.manager.SessionManager;
import com.duzceuni.denemeapplication.profile.ProfileFavoriteBookActivity;
import com.duzceuni.denemeapplication.profile.ProfileHomeActivity;
import com.duzceuni.denemeapplication.profile.ProfileSettingsActivity;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Intent loginIntent;
    private ViewPager2 viewPager2;
    private ImageButton imgBtnSearch, imgBtnReturn, lblAskAI;
    private TextInputEditText txtSearch;
    private FloatingActionButton fabCart, fabProfile, fabFavourite, fabSettings;
    private FirebaseAuth myAuth;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initComponents();
        registerEventHandlers();
    }

    /**
     *  Initialize components
     * */
    private void initComponents(){
        loginIntent = getIntent();
        imgBtnSearch = findViewById(R.id.imgBtnSearch);
        imgBtnReturn = findViewById(R.id.imgBtnReturn);
        lblAskAI = findViewById(R.id.lblAskAI);
        txtSearch = findViewById(R.id.txtSearch);
        fabCart = findViewById(R.id.fabCart);
        fabProfile = findViewById(R.id.fabProfile);
        fabFavourite = findViewById(R.id.fabFavourite);
        fabSettings = findViewById(R.id.fabSettings);
        myAuth = FirebaseAuth.getInstance();
        FirebaseUser user = loginIntent.getParcelableExtra("user");
    }

    /**
     * Register event handlers
     * */
    private void registerEventHandlers(){
        setViewPager2();
        setCategories();
        sendSearchToBookList();
        returnToLogin();
        directToCart();
        goToProfile();
        goToFavorite();
        goToSettings();
        goToAIPage();
    }

    /**
     * Display book images on main page
     * Calls ViewPager2Adapter and sets adapter
     * registerOnPageChangeCallback() methods are required
     * and provides slide action between images
     * */
    private void setViewPager2(){
        viewPager2 = findViewById(R.id.viewPager);
        ViewPager2Adapter viewPager2Adapter = new ViewPager2Adapter(this);
        viewPager2.setAdapter(viewPager2Adapter);

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback(){
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });
    }

    /**
     * Display set of categories on main page
     * Calls RecyclerView and add categories to an array list.
     * Calls adapter class and uses setLayoutManager() to display
     * and send it to adapter.
     *  */
    private void setCategories(){
        RecyclerView recyclerView = findViewById(R.id.recyclerViewCategories);

        String[] categories = getResources().getStringArray(R.array.category_list);
        List<String> categoryList = new ArrayList<>(Arrays.asList(categories));

        CategoryAdapter adapter = new CategoryAdapter(categoryList, this, new CategoryAdapter.OnCategoryClickListener() {
            @Override
            public void onCategoryClick(String category) {
                Intent sendToBookList = new Intent(MainActivity.this, BookListActivity.class);
                sendToBookList.putExtra("category",category);
                startActivity(sendToBookList);
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(adapter);
    }

    /**
     * Send search query to booklist activity
     * */
    private void sendSearchToBookList(){
        imgBtnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String titleOrAuthor = txtSearch.getText().toString();
                Intent sendToBookList = new Intent(MainActivity.this, BookListActivity.class);
                sendToBookList.putExtra("titleOrAuthor", titleOrAuthor);
                startActivity(sendToBookList);
            }
        });
    }

    /**
     * Return to login & logout
     * */
    private void returnToLogin() {
        imgBtnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                class AlertDialogListener implements DialogInterface.OnClickListener {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == AlertDialog.BUTTON_NEUTRAL || which == AlertDialog.BUTTON_NEGATIVE) {
                            Toast.makeText(MainActivity.this, getString(R.string.stay), Toast.LENGTH_SHORT).show();
                        } else if (which == AlertDialog.BUTTON_POSITIVE) {
                            Toast.makeText(MainActivity.this, getString(R.string.loggedOut), Toast.LENGTH_SHORT).show();

                            FirebaseAuth.getInstance().signOut();
                            sessionManager = new SessionManager(MainActivity.this);
                            sessionManager.clearSession();

                            Intent backToLogin = new Intent(MainActivity.this, LoginActivity.class);
                            backToLogin.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(backToLogin);
                            finish();
                        }
                    }
                }

                AlertDialogListener alertDialogListener = new AlertDialogListener();
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.MyAlertDialogTheme);

                builder.setTitle(getString(R.string.alertDialogTitle))
                        .setMessage(getString(R.string.alertDialogMessage))
                        .setPositiveButton(getString(R.string.positiveButton), alertDialogListener)
                        .setNegativeButton(getString(R.string.negativeButton), alertDialogListener)
                        .setNeutralButton(getString(R.string.neutralButton), alertDialogListener);

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }

    /**
     * Go to cart page
     * */
    private void directToCart(){
        fabCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendToCart = new Intent(MainActivity.this, CartActivity.class);
                startActivity(sendToCart);
            }
        });
    }

    /**
     * Go to profile & send user id
     * */
    private void goToProfile(){
        fabProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToProfile = new Intent(MainActivity.this, ProfileHomeActivity.class);
                String uid = myAuth.getCurrentUser().getUid();
                goToProfile.putExtra("currentUser", uid);
                startActivity(goToProfile);
            }
        });
    }

    /**
     * Go to favorites page
     * */
    private void goToFavorite(){
        fabFavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToFavorite = new Intent(MainActivity.this, ProfileFavoriteBookActivity.class);

                startActivity(goToFavorite);
            }
        });
    }

    /**
     * Go to settings page
     * */
    private void goToSettings(){
        fabSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToSettings = new Intent(MainActivity.this, ProfileSettingsActivity.class);
                startActivity(goToSettings);
            }
        });
    }

    /**
     * Go to ai page
     * */
    private void goToAIPage(){
        lblAskAI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToAIPage = new Intent(MainActivity.this, AskAIActivity.class);
                startActivity(goToAIPage);
            }
        });
    }


}
