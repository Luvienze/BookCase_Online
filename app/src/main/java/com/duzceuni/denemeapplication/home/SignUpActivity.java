package com.duzceuni.denemeapplication.home;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.duzceuni.denemeapplication.R;
import com.duzceuni.denemeapplication.entity.Users;
import com.duzceuni.denemeapplication.manager.SessionManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Activity for Sign Up events
 * */
public class SignUpActivity extends AppCompatActivity {
    private final static String TAG ="Empty Field";
    private TextInputEditText txtFirstname;
    private TextInputEditText txtLastname;
    private TextInputEditText txtPhoneNumber;
    private TextInputEditText txtEmail;
    private TextInputEditText txtPassword;
    private RadioButton radioBtnFemale, radioBtnMale, radioBtnOther;
    private DatePicker datePickerDates;
    private MaterialButton btnSignUp;
    private ImageButton imgBtnSearch;
    private FirebaseAuth mAuth;
    private FirebaseDatabase firebaseDatabase;
    private Boolean name_kontrol = false,lastname_kontrol = false,
            gender_kontrol = false,birthdate_kontrol = false,phone_kontrol = false,mail_kontrol = false,
            password_kontrol = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        initComponents();
        registerEventHandlers();
    }

    /**
     *  Initialize components
     * */
    private void initComponents() {
        txtFirstname = findViewById(R.id.txtFirstname);
        txtLastname = findViewById(R.id.txtLastname);
        txtPhoneNumber = findViewById(R.id.txtPhoneNumber);
        txtEmail = findViewById(R.id.txtEmail);
        txtPassword = findViewById(R.id.txtPassword);
        radioBtnFemale = findViewById(R.id.radioBtnFemale);
        radioBtnMale = findViewById(R.id.radioBtnMale);
        radioBtnOther = findViewById(R.id.radioBtnOther);
        datePickerDates = findViewById(R.id.datePickerDates);
        btnSignUp = findViewById(R.id.btnSignUp);
        imgBtnSearch = findViewById(R.id.imgBtnSearch);
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

    }

    /**
     *  Register event handlers
     * */
    private void registerEventHandlers() {
        returnToLogin();
        signUp();
        firstNameValidation();
        lastNameValidation();
        phoneNumberValidation();
        emailValidation();
        passwordValidation();
        genderControl();
        dateOfBirthControl();

    }

    /**
     * Firstname validation text watcher
     * */
    private void firstNameValidation(){
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                name_kontrol = threeCharsControl(txtFirstname);

            }
        };txtFirstname.addTextChangedListener(watcher);
    }

    /**
     * Last name validation text watcher
     * */
    private void lastNameValidation(){
        TextWatcher EditTexLastnameKontrol = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                lastname_kontrol = threeCharsControl(txtLastname);
            }
        };txtLastname.addTextChangedListener(EditTexLastnameKontrol);
    }

    /**
     * Phone number validation text watcher
     * */
    private void phoneNumberValidation(){
        TextWatcher EditTextPhoneKontrol = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                phone_kontrol = phoneControl(txtPhoneNumber);
            }
        };txtPhoneNumber.addTextChangedListener(EditTextPhoneKontrol);
    }

    /**
     *  Email validation text watcher
     * */
    private void emailValidation(){
        TextWatcher EditTextMailKontrol = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mail_kontrol = mailControl(txtEmail);
            }
        }; txtEmail.addTextChangedListener(EditTextMailKontrol);
    }

    /**
     * Password validation text watcher
     * */
    private void passwordValidation(){
        TextWatcher EditTextPasswordKontrol = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                password_kontrol = passwordControl(txtPassword);
            }
        };txtPassword.addTextChangedListener(EditTextPasswordKontrol);
    }

    /**
     * Check if field contains at least 3 chars
    * */
    private boolean threeCharsControl (EditText editText) {
        String kontrol_string = editText.getText().toString();
        if (kontrol_string.length()<3)
        {
            editText.setError(getText(R.string.capsControl));
            return false;
        }
        else
            return true;
    }

    /**
     * Check if field contains at least 10 numbers
     * */
    private boolean phoneControl (EditText editText) {
        String kontrol_string = editText.getText().toString();
        if (kontrol_string.length()<10)
        {
            editText.setError(getString(R.string.phoneControl));
            return false;
        }
        else
            return true;
    }

   /**
    *  Check if mail contains some required chars
    * */
    private boolean mailControl (EditText editText) {
        String email = editText.getText().toString().trim();
        String emailPattern = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        if (!TextUtils.isEmpty(email) && email.matches(emailPattern))
            return true;
        else
        {
            editText.setError(getString(R.string.emailControl));
            return false;
        }
    }

    /**
     * Check if password contains at least 6 chars
     * */
    private boolean passwordControl (EditText editText) {
        String kontrol_string = editText.getText().toString();
        if (kontrol_string.length() <= 5)
        {
            editText.setError(getString(R.string.validationPassword));
            return false;
        }
        else
            return true;
    }

   /**
    *  Check if gender buttons are selected
    * */
    private boolean genderControl () {
        Boolean gender = false;
        if (radioBtnFemale.isChecked()) {
            gender = true;
        } else if (radioBtnMale.isChecked()) {
            gender = true;
        } else if (radioBtnOther.isChecked()) {
            gender = true;
        }
        return  gender;
    }

   /**
    * Check dates of birth date
    * */
    private boolean dateOfBirthControl () {
        Boolean birth = false;
        if (datePickerDates.getDayOfMonth()>0 && datePickerDates.getMonth()>0 && datePickerDates.getYear()>0)
            birth = true;
        return birth;
    }

   /**
    *  Sign up user via validation. Save user to both firebase auth and realtime database
    * */
    private void signUp() {
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gender_kontrol = genderControl();
                birthdate_kontrol = dateOfBirthControl();
                if (name_kontrol && lastname_kontrol && gender_kontrol && birthdate_kontrol && phone_kontrol &&
                mail_kontrol && password_kontrol)
                {
                    String firstName = txtFirstname.getText().toString();
                    String lastName = txtLastname.getText().toString();
                    String password = txtPassword.getText().toString();
                    String email = txtEmail.getText().toString();
                    String phoneNumber = txtPhoneNumber.getText().toString();
                    int day = datePickerDates.getDayOfMonth();
                    int month = datePickerDates.getMonth();
                    int year = datePickerDates.getYear();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    String currentDate = sdf.format(new Date());

                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                                        String userId = firebaseUser.getUid();
                                        Users user = new Users();
                                        user.setId(userId);
                                        user.setFirstname(firstName);
                                        user.setLastname(lastName);
                                        user.setEmail(email);
                                        user.setPhoneNumber(phoneNumber);
                                        user.setBirthOfDate(day + "/" + (month + 1) + "/" + year);
                                        user.setBanned(false);
                                        user.setRegisteredAt(currentDate );

                                        if (radioBtnFemale.isChecked()) {
                                            user.setGender("Kadın");
                                        } else if (radioBtnMale.isChecked()) {
                                            user.setGender("Erkek");
                                        } else if (radioBtnOther.isChecked()) {
                                            user.setGender("Diger");
                                        }

                                        // Save to Realtime Database
                                        DatabaseReference dbRef = firebaseDatabase.getReference("users").child(userId);
                                        dbRef.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> dbTask) {
                                                if (dbTask.isSuccessful()) {
                                                    Intent returnToLogin = new Intent(SignUpActivity.this, LoginActivity.class);
                                                    SessionManager manager = new SessionManager(SignUpActivity.this);
                                                    manager.saveLogin(email,password);
                                                    startActivity(returnToLogin);
                                                } else {
                                                    Toast.makeText(SignUpActivity.this, R.string.databaseError, Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });

                                    } else {
                                        Toast.makeText(SignUpActivity.this, R.string.authenticationError, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }else
                {
                    showSingleButtonAlertDialog();
                }

            }
        });
    }

    /**
     * Set alert dialog for empty fields
     * */
    private void showSingleButtonAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyAlertDialogTheme);
        builder.setTitle(getString(R.string.alert));
        builder.setMessage(getString(R.string.emptyFieldsError));
        builder.setPositiveButton(getString(R.string.tamam), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    /**
     * Return to login page
     * */
    private void returnToLogin(){
        imgBtnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backToLogin = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(backToLogin);
            }
        });
    }

}
