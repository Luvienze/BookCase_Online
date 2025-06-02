package com.duzceuni.denemeapplication.profile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.duzceuni.denemeapplication.R;
import com.duzceuni.denemeapplication.adapter.BookHistoryAdapter;
import com.duzceuni.denemeapplication.entity.Books;
import com.duzceuni.denemeapplication.manager.RentManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class ProfileBookHistory extends AppCompatActivity {

    private ImageButton imgBtnReturn;
    private RecyclerView recyclerViewRentedBook;
    private BookHistoryAdapter bookHistoryAdapter;
    private List<Books> rentedBooks = new ArrayList<>();
    private FirebaseDatabase database;
    private DatabaseReference dbRef;
    private RentManager rentManager;
    private ProgressBar progressBar;
    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_book_history);

        initComponents();
        registerEventHandlers();
    }

    /**
     * Initialize components
     * */
    private void initComponents(){
        imgBtnReturn = findViewById(R.id.imgBtnReturn);
        progressBar = findViewById(R.id.progressBar);
        database = FirebaseDatabase.getInstance();
        dbRef = database.getReference("users");

        if (userId.isEmpty()) {
            Log.e("ProfileReturnBook", "User email not found in session!");
            finish();
            return;
        }

        recyclerViewRentedBook = findViewById(R.id.recyclerViewRentedBook);
        recyclerViewRentedBook.setLayoutManager(new LinearLayoutManager(this));
        bookHistoryAdapter = new BookHistoryAdapter(rentedBooks,this, userId);
        recyclerViewRentedBook.setAdapter(bookHistoryAdapter);
    }
    /**
     * Register event handlers
     * */
    private void registerEventHandlers(){
        returnToProfile();
        loadRentedBooks();
    }
    /**
     * Load refunded books
     * */
    public void loadRentedBooks() {
        progressBar.setVisibility(View.VISIBLE);
        rentManager = new RentManager(userId);
        rentManager.getReturnedBooks(new RentManager.BorrowedBooksCallback() {
            @Override
            public void onBorrowedBooksLoaded(ArrayList<Books> returnedBooks) {
                progressBar.setVisibility(View.GONE);
                bookHistoryAdapter = new BookHistoryAdapter();
                bookHistoryAdapter.setBookList(returnedBooks);
                recyclerViewRentedBook.setAdapter(bookHistoryAdapter);
            }

            @Override
            public void onFailed(String error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ProfileBookHistory.this, "Hata: " + error, Toast.LENGTH_SHORT).show();
            }
        });

    }

   /**
    * Return to profile page
    * */
    private void returnToProfile(){
        imgBtnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent returnToProfile = new Intent(ProfileBookHistory.this, ProfileHomeActivity.class);
                startActivity(returnToProfile);
            }
        });
    }

}
