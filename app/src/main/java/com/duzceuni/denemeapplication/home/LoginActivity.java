package com.duzceuni.denemeapplication.home;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.duzceuni.denemeapplication.R;
import com.duzceuni.denemeapplication.admin.AdminMainActivity;
import com.duzceuni.denemeapplication.manager.SessionManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Activity for Login events
 * */
public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "FirebaseAuthActivity";
    private TextInputEditText txtEmail,txtPassword;
    private Button btnLogin;
    private TextView lblToSignUp;
    private CheckBox checkBoxRememberMe;
    private FirebaseDatabase database;
    private DatabaseReference dbRef;
    private FirebaseAuth myAuth;
    private SharedPreferences sharedPreferences, preferences;
    private ImageButton imgBtnReturn;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkLogin();
        setContentView(R.layout.activity_login);

        initComponents();
        registerEventHandlers();
    }

    /**
     * Initialize components
     * */
    private void initComponents(){
        myAuth = FirebaseAuth.getInstance();
        txtEmail = findViewById(R.id.txtEmail);
        txtPassword = findViewById(R.id.txtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        lblToSignUp = findViewById(R.id.lblToSignUp);
        checkBoxRememberMe = findViewById(R.id.checkBoxRememberMe);
        imgBtnReturn = findViewById(R.id.imgBtnReturn);
        database = FirebaseDatabase.getInstance();
        dbRef = database.getReference();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = myAuth.getCurrentUser();
    }

    /**
     *  Register event handlers
     * */
    private void registerEventHandlers(){
        directToSignUp();
        setUpTextWatcherForEmail();
        setUpTextWatcherForPassword();
        login();
        returnToIntro();
    }

    /**
     *   Send current user to main
     * */
    public void updateUI(FirebaseUser user){
        if (user != null){
            Toast.makeText(LoginActivity.this,user.getEmail() + " " + getString(R.string.loggedIn), Toast.LENGTH_LONG).show();
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra("user",user);
            startActivity(intent);
        }else{
            Toast.makeText(LoginActivity.this,getString(R.string.notLoggedIn), Toast.LENGTH_LONG).show();
        }
    }

    /**
     *  Listens login button on login page and after right entries of data
     *  it directs user to main page or admin page according to checked radio button.
     * */
    private void login() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = txtEmail.getText().toString().trim();
                String password = txtPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                    Toast.makeText(LoginActivity.this, getString(R.string.checkEmptyFields), Toast.LENGTH_SHORT).show();
                    return;
                }

                myAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser firebaseUser = myAuth.getCurrentUser();

                                    if (firebaseUser != null) {
                                        String uid = firebaseUser.getUid();
                                        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(uid);

                                        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (snapshot.exists()) {
                                                    Boolean isBanned = snapshot.child("banned").getValue(Boolean.class);
                                                    // IF USER IS BANNED && SAVE USER TO SHARED PREFERENCES
                                                    if (Boolean.TRUE.equals(isBanned)) {
                                                        showBannedDialog();
                                                        myAuth.signOut();
                                                    } else {
                                                        sessionManager = new SessionManager(LoginActivity.this);
                                                        sessionManager.saveUserEmail(email);

                                                        if (checkBoxRememberMe.isChecked()) {
                                                            sessionManager.saveLogin(email, password);
                                                        }

                                                        goToMain();
                                                        finish();
                                                        updateUI(firebaseUser);
                                                    }
                                                } else {
                                                    Toast.makeText(LoginActivity.this, getString(R.string.cannotFindUser), Toast.LENGTH_SHORT).show();
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                Toast.makeText(LoginActivity.this, getString(R.string.loginFailed), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                } else {
                                    Log.w(TAG, getString(R.string.loginFailed), task.getException());
                                    Toast.makeText(LoginActivity.this, getString(R.string.loginFailed), Toast.LENGTH_SHORT).show();
                                    updateUI(null);
                                }
                            }
                        });
            }
        });
    }

    /**
     *   Show banned user in login activity
     * */
    private void showBannedDialog() {
        new AlertDialog.Builder(LoginActivity.this, R.style.MyAlertDialogTheme)
                .setTitle(getString(R.string.userBannedTitle))
                .setMessage(getString(R.string.userBanned))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.userBannedOk), null)
                .show();
    }

    /**
     *  Go to main activity
     * */
    private void goToMain(){
        Intent goToMain = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(goToMain);
    }

   /**
    *  Check if user is logged in or not
    * */
    private void checkLogin(){
        sessionManager = new SessionManager(this);
        if(sessionManager.isLoggedIn()){
            Intent goToMain = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(goToMain);
            finish();
            return;
        }
    }

    /**
     *  Go to SignUp Page
     * */
    public void directToSignUp(){
        lblToSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent directToSignUpIntent = new Intent(LoginActivity.this,SignUpActivity.class);
                startActivity(directToSignUpIntent);
            }
        });
    }

    /**
     *   Validate email field
     * */
    private void setUpTextWatcherForEmail(){

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                boolean checkEmail = checkEmail(txtEmail);
            }
        };

        txtEmail.addTextChangedListener(textWatcher);

    }

    /**
     *  Validate password field
     * */
    private  void setUpTextWatcherForPassword(){
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                boolean checkPassword = checkPassword(txtPassword);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        txtPassword.addTextChangedListener(textWatcher);

    }


    /**
     * Check email validation
     * */
    private boolean checkEmail(EditText editText){
        String content = editText.getText().toString().trim();
        if(content.isEmpty() || content.length() <= 6){
            Drawable errorImage = ContextCompat.getDrawable(LoginActivity.this, R.drawable.errorimage);
            int height = errorImage.getIntrinsicHeight();
            int width = errorImage.getIntrinsicWidth();
            errorImage.setBounds(0, 0, width, height);
            String errorMessage = getString(R.string.validationText);
            txtEmail.setError(errorMessage,errorImage);
            return false;
        }else{
            editText.setError(null);
            return true;
        }
    }

   /**
    * Check password validation
    * */
    private boolean checkPassword(EditText editText){
        String content = editText.getText().toString().trim();
        if(content.isEmpty() || content.length() <= 6){
            Drawable errorImage = ContextCompat.getDrawable(LoginActivity.this, R.drawable.errorimage);
            int height = errorImage.getIntrinsicHeight();
            int width = errorImage.getIntrinsicWidth();
            errorImage.setBounds(0, 0, width, height);
            String errorMessage = getString(R.string.validationPassword);
            txtPassword.setError(errorMessage,errorImage);
            return false;
        }else{
            editText.setError(null);
            return true;
        }
    }

    /**
     * Go to opening page
     * */
    private void returnToIntro(){
        imgBtnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent returnToIntroPage = new Intent(LoginActivity.this, AdminMainActivity.class);
                startActivity(returnToIntroPage);
            }
        });
    }

}
