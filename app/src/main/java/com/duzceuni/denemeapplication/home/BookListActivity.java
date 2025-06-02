package com.duzceuni.denemeapplication.home;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.duzceuni.denemeapplication.R;
import com.duzceuni.denemeapplication.adapter.BookListAdapter;
import com.duzceuni.denemeapplication.entity.Books;
import com.duzceuni.denemeapplication.entity.Category;
import com.duzceuni.denemeapplication.profile.ProfileFavoriteBookActivity;
import com.duzceuni.denemeapplication.profile.ProfileHomeActivity;
import com.duzceuni.denemeapplication.profile.ProfileSettingsActivity;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BookListActivity extends AppCompatActivity {
    private static final String TAG = "Main Activity";
    private List<Books> allBooks = new ArrayList<>();
    private BookListAdapter bookListAdapter;
    private ImageButton imgBtnSearch, imgBtnReturn;
    private TextInputEditText txtSearch;
    private FloatingActionButton fabCart, fabProfile, fabFavourite, fabSettings;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booklist);

        initComponents();
        registerEventHandlers();
    }

    /**
     *  Initialize components
     * */
    private void initComponents(){
        imgBtnSearch = findViewById(R.id.imgBtnSearch);
        txtSearch = findViewById(R.id.txtSearch);
        fabCart = findViewById(R.id.fabCart);
        fabProfile = findViewById(R.id.fabProfile);
        fabFavourite = findViewById(R.id.fabFavourite);
        fabSettings = findViewById(R.id.fabSettings);
        imgBtnReturn = findViewById(R.id.imgBtnReturn);
        progressBar = findViewById(R.id.progressBar);
    }

    /**
     *  Register event handlers
     * */
    private void registerEventHandlers(){
        setCategories();
        fetchBooks();
        receiveSearchQueryFromMain();
        receiveCategoryFromMain();
        checkEditText();
        searchSetOnClick();
        goToCart();
        returnToMain();
        goToProfile();
        goToSettings();
        goToFavorite();
    }

    /**
     *  Fetch categories from adapter
     **/
    private void setCategories() {
        RecyclerView recyclerView = findViewById(R.id.recyclerViewCategories);

        String[] categories = getResources().getStringArray(R.array.category_list);
        List<String> categoryList = new ArrayList<>(Arrays.asList(categories));

        CategoryAdapter adapter = new CategoryAdapter(categoryList, this, new CategoryAdapter.OnCategoryClickListener() {
            @Override
            public void onCategoryClick(String category) {
                filterBooksByCategory(category);
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(adapter);
    }
    private void fetchBooks() {
        progressBar.setVisibility(View.VISIBLE);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference dbRef = database.getReference("books");
        dbRef.orderByChild("title")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        allBooks.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Books book = snapshot.getValue(Books.class);
                            allBooks.add(book);
                        }

                        bookListAdapter = new BookListAdapter(BookListActivity.this,allBooks);
                        RecyclerView recyclerView = findViewById(R.id.recyclerViewBookList);
                        recyclerView.setLayoutManager(new LinearLayoutManager(BookListActivity.this));
                        recyclerView.setHasFixedSize(true);
                        recyclerView.setItemViewCacheSize(20);
                        recyclerView.setAdapter(bookListAdapter);

                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        progressBar.setVisibility(View.GONE);
                        Log.e("DatabaseError", error.getMessage());
                    }
                });
    }

    /**
     * Filter books by category from main activity request
     * */
    private void receiveCategoryFromMain(){
        String categoryFromMain = getIntent().getStringExtra("category");
        if(categoryFromMain != null && !categoryFromMain.isEmpty()){
            filterBooksByCategory(categoryFromMain);
        }else{
//         Log.w(TAG,"Failed to send category from main to booklist activity.");
//         Toast.makeText(BookListActivity.this,getString(R.string.categoryIsNull), Toast.LENGTH_SHORT).show();
      }
    }

    /**
     *   Filter books by category name
     * */
    void filterBooksByCategory(String categoryName) {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("books");

        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Books> filteredBooks = new ArrayList<>();

                String[] categories = getResources().getStringArray(R.array.category_list);
                List<String> categoryListFromXml = Arrays.asList(categories);

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Books book = snapshot.getValue(Books.class);
                    if (book != null && book.getCategoryList() != null) {

                        for (Category category : book.getCategoryList()) {
                            if (category.getName().equalsIgnoreCase(categoryName)) {
                                filteredBooks.add(book);
                                break;
                            }
                        }
                    }
                }

                if (bookListAdapter != null) {
                    bookListAdapter.updateList(filteredBooks);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("DatabaseError", error.getMessage());
            }
        });
    }

    /**
     *  Seatch books by title or author
     * */
    void loadBooksByTitleOrAuthor(String titleOrAuthor) {
        progressBar.setVisibility(View.VISIBLE);
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("books");
        if(titleOrAuthor.isEmpty()){
            fetchBooks();
            return;
        }

        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Books> bookList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Books book = snapshot.getValue(Books.class);
                    if (book != null) {
                        if (book.getTitle().toLowerCase().contains(titleOrAuthor.toLowerCase()) ||
                                book.getAuthor().toLowerCase().contains(titleOrAuthor.toLowerCase())) {
                            bookList.add(book);
                        }
                    }
                }

                if (bookListAdapter != null) {
                    bookListAdapter.updateList(bookList);
                }

                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    /**
     *  Check search bar
     * */
    private void checkEditText(){
        txtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim();
                loadBooksByTitleOrAuthor(query);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    /**
     *   Search books by search bar
     * */
    private void searchSetOnClick(){
       imgBtnSearch.setOnClickListener(v -> {
           String query = txtSearch.getText().toString().trim();
           if (!query.isEmpty()) {
               loadBooksByTitleOrAuthor(query);
           } else {
               fetchBooks();
               txtSearch.setHint(getString(R.string.hint_searchBar));
           }
       });
   }

   /**
    *  Go to cart
    * */
   private void goToCart(){
        fabCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BookListActivity.this, CartActivity.class));
            }
        });
    }

    /**
     *  Receive search query from search bar in main
     * */
    private void receiveSearchQueryFromMain(){
        String queryFromMain = getIntent().getStringExtra("titleOrAuthor");
        if(queryFromMain != null && !queryFromMain.isEmpty()){
            txtSearch.setText(queryFromMain);
            loadBooksByTitleOrAuthor(queryFromMain);
        }
    }


    /**
     *  Return to main activity
     * */
    private void returnToMain(){
        imgBtnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnToMain = new Intent(BookListActivity.this, MainActivity.class);
                startActivity(returnToMain);
            }
        });
    }

    /**
     *  Go to profile activity
     * */
    private void goToProfile(){
        fabProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToProfile = new Intent(BookListActivity.this, ProfileHomeActivity.class);
                startActivity(goToProfile);
            }
        });
    }

    /**
     *   Go to favorite book activity
     * */
    private void goToFavorite(){
        fabFavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToFavorite = new Intent(BookListActivity.this, ProfileFavoriteBookActivity.class);
                startActivity(goToFavorite);
            }
        });
    }


    /**
     *  Go to settings
     * */
    private void goToSettings(){
        fabSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToSettings = new Intent(BookListActivity.this, ProfileSettingsActivity.class);
                startActivity(goToSettings);
            }
        });

    }
}
