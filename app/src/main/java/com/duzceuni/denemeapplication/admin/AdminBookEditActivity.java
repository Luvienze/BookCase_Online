package com.duzceuni.denemeapplication.admin;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.duzceuni.denemeapplication.R;
import com.duzceuni.denemeapplication.entity.Books;
import com.duzceuni.denemeapplication.entity.Category;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class AdminBookEditActivity extends AppCompatActivity {
    private SharedPreferences sharedPref;
    private Button btn_menu,btn_saveImage;
    private DrawerLayout drawerLayout;
    private NavigationView nv_slide;
    private ImageView AdminBookImage;
    Uri imageUri;
    StorageReference storageReference;
    private ActivityResultLauncher<Intent> imageChooserLauncher;
    private EditText editText_AdminBookName,editText_AdminBookISBN,editText_AdminBookAuthor,editText_AdminBookPages,editText_AdminBookCopies,editText_AdminBookDescription;
    private Boolean image_kontrol=false,kategori_kontrol=true,name_kontrol=true,isbn_kontrol=true,author_kontrol=true,pages_kontrol=true,copies_kontrol=true,description_kontrol=true;
    private FirebaseDatabase database;
    private DatabaseReference dbRef;
    private TextView spinnerText;
    private String[] categories;
    List<String> secilenKategoriler = new ArrayList<>();
    private boolean[] selectedItems;
    private ProgressBar progressBar;
    Books mainBook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_book_add);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        AdminBookImage = findViewById(R.id.AdminBookImage);

        imageChooserLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult o) {
                        if (o.getResultCode() == RESULT_OK) {
                            Intent data = o.getData();
                            if (data != null && data.getData() != null) {
                                imageUri = data.getData();
                                AdminBookImage.setImageURI(imageUri);
                                ImageViewCompat.setImageTintList(AdminBookImage, null);
                                image_kontrol = true;
                            }
                        }
                    }
                }
        );
        sharedPref = getSharedPreferences("admin_config", Context.MODE_PRIVATE);
        initCompanents();


    }
    private void initCompanents() {
        drawerLayout = findViewById(R.id.main);
        nv_slide = findViewById(R.id.ng_slide);
        btn_menu = findViewById(R.id.btn_menu);
        btn_saveImage = findViewById(R.id.btn_saveImage);
        //AdminBookImage = findViewById(R.id.AdminBookImage);

        editText_AdminBookName = findViewById(R.id.editText_AdminBookName);
        editText_AdminBookAuthor = findViewById(R.id.editText_AdminBookAuthor);
        editText_AdminBookPages = findViewById(R.id.editText_AdminBookPages);
        editText_AdminBookISBN = findViewById(R.id.editText_AdminBookISBN);
        editText_AdminBookCopies = findViewById(R.id.editText_AdminBookCopies);
        editText_AdminBookDescription = findViewById(R.id.editText_AdminBookDescription);

        spinnerText = findViewById(R.id.spinner_kategori);
        progressBar = findViewById(R.id.progressBar);
        database = FirebaseDatabase.getInstance();
        dbRef = database.getReference();

        //categoryList = new ArrayList<>(Arrays.asList(categories));
        //categoryArray = categoryList.toArray(new String[0]);
        //selectedItems = new boolean[categoryArray.length];
        kitabiYakala();
    }
    private void registerEventHandlers() {
        btn_menu.setOnClickListener(new View.OnClickListener() {
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
                menuClass.logout(menuItem,AdminBookEditActivity.this,sharedPref);
                if (menuItem.getItemId()==R.id.snv_turnoff)
                    finishAffinity();

                //drawerLayout.closeDrawer(nv_slide);
                return true;
            }
        });



        btn_saveImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(secilenKategoriler.isEmpty())
                    kategori_kontrol = false;
                else
                    kategori_kontrol = true;
                if (name_kontrol && author_kontrol && isbn_kontrol && pages_kontrol && kategori_kontrol&&copies_kontrol&&description_kontrol)
                {
                    oncekiniSilme();
                    progressBar.setVisibility(View.VISIBLE);
                }
                else
                    showSingleButtonAlertDialog();
            }
        });
        AdminBookImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                // ActivityResultLauncher ile intent'i başlatın
                imageChooserLauncher.launch(intent);
            }
        });

        TextWatcher EditTextNameKontrol = new TextWatcher() {   //kitap adi kontrol
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                name_kontrol = EditTextKontrol(editText_AdminBookName);
            }
        };
        TextWatcher EditTextAuthorKontrol = new TextWatcher() {   //yazar kontrol
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                author_kontrol = EditTextKontrol(editText_AdminBookAuthor);
            }
        };
        TextWatcher EditTextPagesKontrol = new TextWatcher() {   //sayfa kontrol
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                pages_kontrol = EditTextKontrol2(editText_AdminBookPages);
            }
        };
        TextWatcher EditTextCopiesKontrol = new TextWatcher() {   //copies (adet) kontrol
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                copies_kontrol = EditTextKontrol2(editText_AdminBookCopies);
            }
        };
        TextWatcher EditTextIsbnKontrol = new TextWatcher() {   //isbn kontrol
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                isbn_kontrol = EditTextKontrol(editText_AdminBookISBN);
            }
        };
        TextWatcher EditTextDescriptionKontrol = new TextWatcher() {   //isbn kontrol
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                description_kontrol = EditTextKontrol(editText_AdminBookDescription);
            }
        };
        editText_AdminBookName.addTextChangedListener(EditTextNameKontrol);
        editText_AdminBookAuthor.addTextChangedListener(EditTextAuthorKontrol);
        editText_AdminBookPages.addTextChangedListener(EditTextPagesKontrol);
        editText_AdminBookISBN.addTextChangedListener(EditTextIsbnKontrol);
        editText_AdminBookCopies.addTextChangedListener(EditTextCopiesKontrol);
        editText_AdminBookDescription.addTextChangedListener((EditTextDescriptionKontrol));


        spinnerText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AdminBookEditActivity.this,R.style.MyAlertDialogTheme);
                builder.setTitle(R.string.kategori);

                builder.setMultiChoiceItems(categories, selectedItems, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        if (isChecked) {
                            secilenKategoriler.add(categories[which]);
                        } else {
                            secilenKategoriler.remove(categories[which]);
                        }
                    }
                });

                builder.setPositiveButton(R.string.tamam, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        spinnerText.setText(String.join(", ", secilenKategoriler));
                    }
                });

                builder.setNegativeButton(R.string.iptal, null);
                builder.show();
            }
        });


    }
    private boolean EditTextKontrol(EditText editText) {
        String kontrol_string = editText.getText().toString();
        if (kontrol_string.length()<3)
        {
            String error = getString(R.string.edittext3);
            editText.setError(error);
            return false;
        }
        else
            return true;
    }
    private boolean EditTextKontrol2(EditText editText)  {
        Integer sayi;
        if (editText.getText().toString().equals(""))
            sayi = 0;
        else
            sayi = Integer.parseInt(editText.getText().toString());
        if (sayi>1)
        {
            return true;
        }
        else
        {
            String error = getString(R.string.edittext2);
            editText.setError(error);
            return false;
        }

    }
    private void showSingleButtonAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.MyAlertDialogTheme);
        builder.setTitle(R.string.uyarı);
        builder.setMessage(R.string.alart_dialog1);
        builder.setPositiveButton(R.string.tamam, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }
    public void oncekiniSilme()
    {
        DatabaseReference dbRef2 = FirebaseDatabase.getInstance().getReference("books");
        String kitapKey = String.valueOf(mainBook.getISBN());
        dbRef2.child(kitapKey).removeValue()
                .addOnSuccessListener(aVoid -> {
                    uploadImage();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), R.string.Toast_hata, Toast.LENGTH_SHORT).show();
                });
    }
    private void uploadImage() {
        if(image_kontrol)
        {
            long timestamp = System.currentTimeMillis();
            if (imageUri != null) {
                storageReference = FirebaseStorage.getInstance().getReference("book-images/" + timestamp);
                storageReference.putFile(imageUri)
                        .addOnSuccessListener(taskSnapshot -> storageReference.getDownloadUrl()
                                .addOnSuccessListener(uri -> {
                                    // Görsel yüklendikten sonra Books objesi oluştur
                                    String imageUrl = uri.toString();
                                    String title = editText_AdminBookName.getText().toString();
                                    String author = editText_AdminBookAuthor.getText().toString();
                                    int sayfa_sayisi = Integer.parseInt(editText_AdminBookPages.getText().toString());
                                    int adet_sayisi = Integer.parseInt(editText_AdminBookCopies.getText().toString());
                                    long isbn = Long.valueOf(editText_AdminBookISBN.getText().toString());
                                    String description = editText_AdminBookDescription.getText().toString();
                                    List<Category> sendCategory = new ArrayList<>();

                                    for (int i = 0; i < secilenKategoriler.size(); i++) {
                                        Category category = new Category(secilenKategoriler.get(i).toString());
                                        sendCategory.add(category);
                                    }

                                    Books book = new Books(
                                            isbn,
                                            title,
                                            author,
                                            sayfa_sayisi,
                                            adet_sayisi,
                                            description,
                                            imageUrl,
                                            sendCategory
                                    );

                                    dbRef.child("books").child(String.valueOf(isbn)).setValue(book)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    progressBar.setVisibility(View.GONE);
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(AdminBookEditActivity.this, R.string.Toast_kitap_basarili, Toast.LENGTH_SHORT).show();
                                                        Intent yeni_intent = new Intent(AdminBookEditActivity.this,AdminHomeActivity.class);
                                                        startActivity(yeni_intent);
                                                    } else {
                                                        Toast.makeText(AdminBookEditActivity.this, R.string.Toast_hata, Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                }))
                        .addOnFailureListener(e -> {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(AdminBookEditActivity.this, R.string.Toast_hata, Toast.LENGTH_SHORT).show();
                        });
            }
        }else{
            String imageUrl = mainBook.getImage();
            String title = editText_AdminBookName.getText().toString();
            String author = editText_AdminBookAuthor.getText().toString();
            int sayfa_sayisi = Integer.parseInt(editText_AdminBookPages.getText().toString());
            int adet_sayisi = Integer.parseInt(editText_AdminBookCopies.getText().toString());
            long isbn = Long.valueOf(editText_AdminBookISBN.getText().toString());
            String description = editText_AdminBookDescription.getText().toString();
            List<Category> sendCategory = new ArrayList<>();

            for (int i = 0; i < secilenKategoriler.size(); i++) {
                Category category = new Category(secilenKategoriler.get(i).toString());
                sendCategory.add(category);
            }

            Books book = new Books(
                    isbn,
                    title,
                    author,
                    sayfa_sayisi,
                    adet_sayisi,
                    description,
                    imageUrl,
                    sendCategory
            );

            dbRef.child("books").child(String.valueOf(isbn)).setValue(book)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            progressBar.setVisibility(View.GONE);
                            if (task.isSuccessful()) {
                                Toast.makeText(AdminBookEditActivity.this, R.string.Toast_kitap_basarili, Toast.LENGTH_SHORT).show();
                                Intent yeni_intent = new Intent(AdminBookEditActivity.this,AdminHomeActivity.class);
                                startActivity(yeni_intent);
                            } else {
                                Toast.makeText(AdminBookEditActivity.this, R.string.Toast_hata, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
    private void kitabiYakala()
    {
        long isbn = getIntent().getLongExtra("isbn", -1);
        if (isbn == -1) {
            Toast.makeText(this, R.string.Toast_hata, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        dbRef.child("books").child(String.valueOf(isbn)).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().exists()) {
                    mainBook = task.getResult().getValue(Books.class);
                    if (mainBook != null) {
                        fillUIWithBookData(mainBook);

                    } else {
                        Toast.makeText(this, R.string.Toast_hata, Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } else {
                    Toast.makeText(this, R.string.Toast_hata, Toast.LENGTH_SHORT).show();
                    finish();
                }
            } else {
                Toast.makeText(this, R.string.Toast_hata, Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
    private void fillUIWithBookData(Books book) {
        editText_AdminBookISBN.setText(String.valueOf(book.getISBN()));
        editText_AdminBookName.setText(book.getTitle());
        editText_AdminBookAuthor.setText(book.getAuthor());
        editText_AdminBookCopies.setText(String.valueOf(book.getCopiesAvailable()));
        editText_AdminBookPages.setText(String.valueOf(book.getPages()));
        editText_AdminBookDescription.setText(book.getDescription());

        Glide.with(this)
                .load(book.getImage())
                .placeholder(R.drawable.ic_downloading)
                .into(AdminBookImage);
        ImageViewCompat.setImageTintList(AdminBookImage, null);

        categories = getResources().getStringArray(R.array.category_list);
        selectedItems = new boolean[categories.length];
        secilenKategoriler.clear();

        for (int i = 0; i < categories.length; i++) {
            for (Category selected : book.getCategoryList()) {
                if (categories[i].equalsIgnoreCase(selected.getName())) {
                    selectedItems[i] = true;
                    secilenKategoriler.add(categories[i]);
                    break;
                }
            }
        }

        spinnerText.setText(String.join(", ", secilenKategoriler));
        registerEventHandlers();
    }
    public boolean onKeyDown(int keyCode, KeyEvent keyEvent) {   //geri tusuna basınca olacak
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                Intent intent = new Intent(AdminBookEditActivity.this, AdminHomeActivity.class);
                startActivity(intent);
                break;
        }
        return true;
    }

}