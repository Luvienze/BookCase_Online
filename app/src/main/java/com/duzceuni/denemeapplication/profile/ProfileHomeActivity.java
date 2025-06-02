package com.duzceuni.denemeapplication.profile;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.duzceuni.denemeapplication.R;
import com.duzceuni.denemeapplication.admin.AdminMainActivity;
import com.duzceuni.denemeapplication.entity.Users;
import com.duzceuni.denemeapplication.home.LoginActivity;
import com.duzceuni.denemeapplication.home.MainActivity;
import com.duzceuni.denemeapplication.manager.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileHomeActivity extends AppCompatActivity {

    private ImageView profileEditIcon;
    private ProgressBar progressBar;
    private TextView lblUsername, lblEmail, txtMembershipDate;
    private MaterialButton btnBookHistory, btnFavoriteBooks, btnReturnBook, btnLogout,btnVeriSil;
    private ImageButton imgBtnReturn;
    private FirebaseAuth myAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference dbRef;
    private SessionManager sessionManager;
    private String UserId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_home);

        initComponents();
        registerEventHandlers();
    }

    /**
     * Initialize components
     * */
    private void initComponents(){
        profileEditIcon = findViewById(R.id.profileEditIcon);
        progressBar = findViewById(R.id.progressBar);
        lblUsername = findViewById(R.id.lblUsername);
        lblEmail = findViewById(R.id.lblEmail);
        txtMembershipDate = findViewById(R.id.txtMembershipDate);
        btnBookHistory = findViewById(R.id.btnBookHistory);
        btnFavoriteBooks = findViewById(R.id.btnFavoriteBooks);
        btnReturnBook = findViewById(R.id.btnReturnBook);
        btnLogout = findViewById(R.id.btnLogout);
        imgBtnReturn = findViewById(R.id.imgBtnReturn);
        btnVeriSil = findViewById(R.id.btnVeriSil);
        firebaseDatabase = FirebaseDatabase.getInstance();
    }

    /**
     * Register event handlers
     * */
    private void registerEventHandlers(){
        setTextFields();
        goToEditUser();
        goToPastBooks();
        goToFavoriteBooks();
        goToReturnBooks();
        returnToBookList();
        logout();
        HesapSilButton();
    }

    /**
     * Set card field credentials from database and session manager
     * */
    private void setTextFields() {
        sessionManager = new SessionManager(ProfileHomeActivity.this);
        String savedEmail = sessionManager.getUserEmail();

        if (!savedEmail.isEmpty()) {
            dbRef = firebaseDatabase.getReference("users");
            progressBar.setVisibility(View.VISIBLE);
            dbRef.orderByChild("email").equalTo(savedEmail)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            progressBar.setVisibility(View.GONE);
                            if (snapshot.exists()) {
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    Users user = dataSnapshot.getValue(Users.class);
                                    if (user != null) {
                                        UserId = user.getId();
                                        String username = user.getFirstname() + " " + user.getLastname();
                                        lblUsername.setText(username);
                                        lblEmail.setText(user.getEmail());
                                        txtMembershipDate.setText(user.getRegisteredAt());
                                    } else {
                                        Toast.makeText(ProfileHomeActivity.this, getString(R.string.cannotFindUser), Toast.LENGTH_LONG).show();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(ProfileHomeActivity.this, getString(R.string.cannotFindUser), Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }

    /**
    * Go to edit user page
    * */
    private void goToEditUser(){
        profileEditIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToEditProfile = new Intent(ProfileHomeActivity.this, ProfileEditUserActivity.class);
                startActivity(goToEditProfile);
            }
        });
    }

    /**
     * Go to book history page
     * */
    private void goToPastBooks(){
        btnBookHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToPastBooks = new Intent(ProfileHomeActivity.this, ProfileBookHistory.class);
                startActivity(goToPastBooks);
            }
        });
    }

    /**
     * Go to favorite book list page
     * */
    private void goToFavoriteBooks(){
        btnFavoriteBooks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToFavoriteBooks = new Intent(ProfileHomeActivity.this, ProfileFavoriteBookActivity.class);
                startActivity(goToFavoriteBooks);
            }
        });
    }

    /**
     * Go to borrowed book page
     * */
    private void goToReturnBooks(){
        btnReturnBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToReturnBooks = new Intent(ProfileHomeActivity.this,ProfileReturnBookActivity.class);
                startActivity(goToReturnBooks);
            }
        });
    }

   /**
    * Return to booklist activity
    * */
    private void returnToBookList(){
        imgBtnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent returnToBookList = new Intent(ProfileHomeActivity.this, MainActivity.class);
                startActivity(returnToBookList);
            }
        });
    }

    /**
     * Logout user
     * */
    private void logout() {
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                class AlertDialogListener implements DialogInterface.OnClickListener {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == AlertDialog.BUTTON_NEUTRAL || which == AlertDialog.BUTTON_NEGATIVE) {
                            Toast.makeText(ProfileHomeActivity.this, getString(R.string.stay), Toast.LENGTH_SHORT).show();
                        } else if (which == AlertDialog.BUTTON_POSITIVE) {
                            Toast.makeText(ProfileHomeActivity.this, getString(R.string.loggedOut), Toast.LENGTH_SHORT).show();

                            FirebaseAuth.getInstance().signOut();

                            sessionManager = new SessionManager(ProfileHomeActivity.this);
                            sessionManager.clearSession();

                            Intent backToLogin = new Intent(ProfileHomeActivity.this, LoginActivity.class);
                            backToLogin.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(backToLogin);
                            finish();
                        }
                    }
                }

                AlertDialogListener alertDialogListener = new AlertDialogListener();
                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileHomeActivity.this, R.style.MyAlertDialogTheme);

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
    private void HesapSilButton()
    {
        btnVeriSil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTwoButtonAlertDialog();
            }
        });
    }
    private void showTwoButtonAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.uyarı);

        builder.setMessage(R.string.alert_user_silme);

        builder.setPositiveButton(R.string.userAlertDialogYes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                progressBar.setVisibility(View.VISIBLE);
                FirebaseUser user1 = FirebaseAuth.getInstance().getCurrentUser();
                if (user1 != null) {
                    user1.delete()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {

                                    DatabaseReference dbRef1 = FirebaseDatabase.getInstance().getReference("users");
                                    dbRef1.child(String.valueOf(UserId)).removeValue()
                                            .addOnSuccessListener(aVoid -> {
                                                SessionManager sessionManager = new SessionManager(ProfileHomeActivity.this);
                                                sessionManager.clearSession();
                                                Toast.makeText(ProfileHomeActivity.this, R.string.hesap_silindi, Toast.LENGTH_SHORT).show();
                                                progressBar.setVisibility(View.GONE);
                                                dialog.dismiss();
                                                Intent yeni_intent = new Intent(ProfileHomeActivity.this, AdminMainActivity.class);
                                                startActivity(yeni_intent);
                                            })
                                            .addOnFailureListener(e -> {
                                                Toast.makeText(getApplicationContext(), R.string.Toast_hata, Toast.LENGTH_SHORT).show();
                                                progressBar.setVisibility(View.GONE);
                                                dialog.dismiss();
                                            });

                                } else {
                                    Toast.makeText(getApplicationContext(), R.string.Toast_hata, Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });

        builder.setNegativeButton(R.string.iptal, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}



