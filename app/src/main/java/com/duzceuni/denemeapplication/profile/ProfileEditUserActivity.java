package com.duzceuni.denemeapplication.profile;

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
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.duzceuni.denemeapplication.R;
import com.duzceuni.denemeapplication.manager.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileEditUserActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private ImageButton imgBtnReturn;

    private TextInputEditText profileEditTextFirstname, profileEditTextLastname, profileEditTextEmail,
    profileEditTextPhone,txtPassword;

    private RadioButton radioBtnFemale, radioBtnMale, radioBtnOther;

    private DatePicker datePickerDates;

    private MaterialButton btnSaveChanges;

    private FirebaseDatabase database;

    private DatabaseReference dbRef;

    private FirebaseAuth myAuth;

    private SessionManager sessionManager;
    private Boolean name_kontrol = true,lastname_kontrol = true,
            gender_kontrol = true,birthdate_kontrol = true,phone_kontrol = true,mail_kontrol = true,
            password_kontrol = true;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit_user);

        initComponents();
        registerEventHandlers();
    }

    /**
     * Initialize components
     * */
    private void initComponents(){
        imgBtnReturn = findViewById(R.id.imgBtnReturn);
        progressBar = findViewById(R.id.progressBar);
        profileEditTextFirstname = findViewById(R.id.profileEditTextFirstname);
        profileEditTextLastname = findViewById(R.id.profileEditTextLastname);
        profileEditTextEmail = findViewById(R.id.profileEditTextEmail);
        profileEditTextPhone = findViewById(R.id.profileEditTextPhone);
        txtPassword = findViewById(R.id.txtPassword);
        radioBtnFemale = findViewById(R.id.radioBtnFemale);
        radioBtnMale = findViewById(R.id.radioBtnMale);
        radioBtnOther = findViewById(R.id.radioBtnOther);
        datePickerDates = findViewById(R.id.datePickerDates);
        btnSaveChanges = findViewById(R.id.btnSaveChanges);
        database = FirebaseDatabase.getInstance();
        dbRef = database.getReference("users");
        myAuth = FirebaseAuth.getInstance();

    }

    /**
     * Register event handlers
     * */
    private void registerEventHandlers(){
        setFields();
        updateUserInfo();
        returnToProfileHome();
        firstNameValidation();
        lastNameValidation();
        emailValidation();
        phoneNumberValidation();
        passwordValidation();
        genderControl();
        dateOfBirthControl();

    }
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
                name_kontrol = threeCharsControl(profileEditTextFirstname);

            }
        };profileEditTextFirstname.addTextChangedListener(watcher);
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
                lastname_kontrol = threeCharsControl(profileEditTextLastname);
            }
        };profileEditTextLastname.addTextChangedListener(EditTexLastnameKontrol);
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
                phone_kontrol = phoneControl(profileEditTextPhone);
            }
        };profileEditTextPhone.addTextChangedListener(EditTextPhoneKontrol);
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
                mail_kontrol = mailControl(profileEditTextEmail);
            }
        }; profileEditTextEmail.addTextChangedListener(EditTextMailKontrol);
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
        if (kontrol_string.length()<=6)
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
     * Set all fields as user credentials from database
     * Fetch user first via auth.
     * */
    private void setFields(){
        String currentUserId = myAuth.getCurrentUser().getUid();
        dbRef.child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String firstName = snapshot.child("firstname").getValue(String.class);
                    String lastName = snapshot.child("lastname").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class);
                    String phone = snapshot.child("phoneNumber").getValue(String.class);
                    String gender = snapshot.child("gender").getValue(String.class);
                    String dateOfBirth = snapshot.child("dateOfBirth").getValue(String.class);

                    profileEditTextFirstname.setText(firstName);
                    profileEditTextLastname.setText(lastName);
                    profileEditTextEmail.setText(email);
                    profileEditTextPhone.setText(phone);

                    if("Kadın".equals(gender)){
                        radioBtnFemale.setChecked(true);
                    } else if ("Erkek".equals(gender)) {
                        radioBtnMale.setChecked(true);
                    }else {
                        radioBtnOther.setChecked(true);
                    }

                    if(dateOfBirth != null && !dateOfBirth.isEmpty()){
                        String[] dateParts = dateOfBirth.split("-");
                        int year = Integer.parseInt(dateParts[0]);
                        int month = Integer.parseInt(dateParts[1]);
                        int day = Integer.parseInt(dateParts[2]);
                        datePickerDates.updateDate(year, month, day);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    /**
     * Save changes to email & password first to firebase auth,
     * then save changes to realtime database
     * */
    private void updateUserInfo() {
        btnSaveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gender_kontrol = genderControl();
                birthdate_kontrol = dateOfBirthControl();
                if (name_kontrol && lastname_kontrol && gender_kontrol && birthdate_kontrol && phone_kontrol &&
                        mail_kontrol && password_kontrol)
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ProfileEditUserActivity.this, R.style.MyAlertDialogTheme);
                    builder.setTitle(getString(R.string.userAlertDialogTitle))
                            .setMessage(getString(R.string.userAlertDialogMessage))
                            .setPositiveButton(getString(R.string.userAlertDialogYes), (dialog, id) -> {
                                String currentUserId = myAuth.getCurrentUser().getUid();

                                String updatedFirstName = profileEditTextFirstname.getText().toString().trim();
                                String updatedLastName = profileEditTextLastname.getText().toString().trim();
                                String updatedEmail = profileEditTextEmail.getText().toString().trim();
                                String updatedPhone = profileEditTextPhone.getText().toString().trim();
                                String updatedGender = radioBtnFemale.isChecked() ? "Kadın" :
                                        radioBtnMale.isChecked() ? "Erkek" : "Diger";
                                String updatedDob = datePickerDates.getYear() + "-" + (datePickerDates.getMonth() + 1) + "-" + datePickerDates.getDayOfMonth();
                                String newPassword = txtPassword.getText().toString().trim();

                                if (updatedFirstName.isEmpty() || updatedLastName.isEmpty() || updatedEmail.isEmpty() || updatedPhone.isEmpty()) {
                                    Toast.makeText(ProfileEditUserActivity.this, getString(R.string.userAlertDialogError), Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                progressBar.setVisibility(View.VISIBLE);

                                if (myAuth.getCurrentUser() != null && !updatedEmail.equals(myAuth.getCurrentUser().getEmail())) {
                                    myAuth.getCurrentUser().updateEmail(updatedEmail)
                                            .addOnCompleteListener(task -> {
                                                if (task.isSuccessful()) {
                                                    sessionManager = new SessionManager(ProfileEditUserActivity.this);
                                                    sessionManager.saveUserEmail(updatedEmail);

                                                    if (!newPassword.isEmpty()) {
                                                        myAuth.getCurrentUser().updatePassword(newPassword)
                                                                .addOnCompleteListener(passwordTask -> {
                                                                    if (passwordTask.isSuccessful()) {

                                                                        updateDatabase(currentUserId, updatedFirstName, updatedLastName, updatedEmail, updatedPhone, updatedGender, updatedDob);
                                                                    } else {
                                                                        Toast.makeText(ProfileEditUserActivity.this, getString(R.string.passwordUpdateError) + passwordTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                                        progressBar.setVisibility(View.GONE);
                                                                    }
                                                                });
                                                    } else {

                                                        updateDatabase(currentUserId, updatedFirstName, updatedLastName, updatedEmail, updatedPhone, updatedGender, updatedDob);
                                                    }
                                                } else {
                                                    Toast.makeText(ProfileEditUserActivity.this, getString(R.string.emailUpdateError) + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                    progressBar.setVisibility(View.GONE);
                                                }
                                            });
                                } else {

                                    if (!newPassword.isEmpty()) {
                                        myAuth.getCurrentUser().updatePassword(newPassword)
                                                .addOnCompleteListener(passwordTask -> {
                                                    if (passwordTask.isSuccessful()) {
                                                        updateDatabase(currentUserId, updatedFirstName, updatedLastName, updatedEmail, updatedPhone, updatedGender, updatedDob);
                                                    } else {
                                                        Toast.makeText(ProfileEditUserActivity.this, getString(R.string.passwordUpdateError) + passwordTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                        progressBar.setVisibility(View.GONE);
                                                    }
                                                });
                                    } else {
                                        updateDatabase(currentUserId, updatedFirstName, updatedLastName, updatedEmail, updatedPhone, updatedGender, updatedDob);
                                    }
                                }

                            })
                            .setNegativeButton(getString(R.string.userAlertDialogNo), (dialog, id) -> {
                                dialog.dismiss();
                            });

                    builder.create().show();
                }
                else
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
     * Push changes to database
     * */
    private void updateDatabase(String userId, String firstName, String lastName, String email, String phone, String gender, String dob) {
        dbRef.child(userId).child("firstname").setValue(firstName);
        dbRef.child(userId).child("lastname").setValue(lastName);
        dbRef.child(userId).child("email").setValue(email);
        dbRef.child(userId).child("phoneNumber").setValue(phone);
        dbRef.child(userId).child("gender").setValue(gender);
        dbRef.child(userId).child("dateOfBirth").setValue(dob);

        Toast.makeText(ProfileEditUserActivity.this, getString(R.string.savesChanges), Toast.LENGTH_SHORT).show();
        progressBar.setVisibility(View.GONE);
    }

    /**
     * Return to profile home page
     * */
    private void returnToProfileHome(){
        imgBtnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent returnToProfileHome = new Intent(ProfileEditUserActivity.this, ProfileHomeActivity.class);
                startActivity(returnToProfileHome);
            }
        });
    }
}
