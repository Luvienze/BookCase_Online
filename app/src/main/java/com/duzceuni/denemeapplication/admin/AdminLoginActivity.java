package com.duzceuni.denemeapplication.admin;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.duzceuni.denemeapplication.R;

public class AdminLoginActivity extends AppCompatActivity {

    private EditText EditText_username,EditText_password;
    private Button btn_login;
    private CheckBox checkBox;
    private boolean boolean_username,boolean_password;
    private SharedPreferences sharedPref;
    private String username = "Admin",password = "123458";
    private Boolean boolean_remamberme = false;
    private ImageButton imgBtnReturn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        sharedPref = getSharedPreferences("admin_config", Context.MODE_PRIVATE);
        initCompanents();
        registerEventHandlers();
    }
    private void initCompanents()
    {
        EditText_username = findViewById(R.id.editText_userName);
        EditText_password = findViewById(R.id.editText_password);
        btn_login = findViewById(R.id.btn_login);
        checkBox = findViewById(R.id.checkBox_remamberme);
        imgBtnReturn = findViewById(R.id.imgBtnReturn);
    }
    private void registerEventHandlers()
    {
        TextWatcher EditTextKontrol = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                boolean_username = KontrolEdici(EditText_username);
                boolean_password = KontrolEdici(EditText_password);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        EditText_username.addTextChangedListener(EditTextKontrol);
        EditText_password.addTextChangedListener(EditTextKontrol);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (boolean_username & boolean_password)
                {
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putBoolean("remamberme",boolean_remamberme);
                    editor.apply();
                    Intent Intent_admin_home = new Intent(AdminLoginActivity.this,AdminHomeActivity.class);
                    startActivity(Intent_admin_home);
                }
                else {
                    String error = getString(R.string.Adminloginhata);
                    Toast.makeText(AdminLoginActivity.this,error,Toast.LENGTH_SHORT).show();
                }
            }
        });
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    boolean_remamberme = true;
                else
                    boolean_remamberme = false;
            }
        });

        returnToOpening();
    }
    private boolean KontrolEdici(EditText editText)  //Edittext'in icerigini kontrol eder
    {
        if (editText.length() >= 3 || editText.length() == 0) {
            if (editText.getId() == R.id.editText_userName && username.equals(editText.getText().toString()))
                return true;
            else if (editText.getId() == R.id.editText_password && password.equals(editText.getText().toString()))
                return true;
             else
                return false;
        } else {
            String error = getString(R.string.edittext3);
            editText.setError(error);
            return false;
        }
    }
    public boolean onKeyDown(int keyCode, KeyEvent keyEvent) {   //geri tusuna basınca olacak
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                Intent intent = new Intent(AdminLoginActivity.this, AdminMainActivity.class);
                startActivity(intent);
                break;
        }
        return true;
    }

    private void returnToOpening(){
        imgBtnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent returnToOpening = new Intent(AdminLoginActivity.this, AdminMainActivity.class);
                startActivity(returnToOpening);
            }
        });
    }

}