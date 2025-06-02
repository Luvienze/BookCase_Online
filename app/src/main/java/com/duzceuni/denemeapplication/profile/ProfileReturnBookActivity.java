package com.duzceuni.denemeapplication.profile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.duzceuni.denemeapplication.R;
import com.duzceuni.denemeapplication.adapter.RentBookAdapter;
import com.duzceuni.denemeapplication.entity.Books;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ProfileReturnBookActivity extends AppCompatActivity {

    private ImageButton imgBtnReturn;
    private ProgressBar progressBar;
    private RecyclerView recyclerViewRentedBook;
    private RentBookAdapter rentBookAdapter;
    private List<Books> rentedBooks = new ArrayList<>();
    private FirebaseDatabase database;
    private DatabaseReference dbRef;

    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_rented_books);

        initComponents();
        registerEventHandlers();
    }

    /**
     * Initialize components
     * */
    private void initComponents() {
        progressBar = findViewById(R.id.progressBar);
        imgBtnReturn = findViewById(R.id.imgBtnReturn);
        database = FirebaseDatabase.getInstance();
        dbRef = database.getReference("users");

        if (userId.isEmpty()) {
            Log.e("ProfileReturnBook", "User email not found in session!");
            finish();
            return;
        }

        recyclerViewRentedBook = findViewById(R.id.recyclerViewRentedBook);
        recyclerViewRentedBook.setLayoutManager(new LinearLayoutManager(this));
        rentBookAdapter = new RentBookAdapter(rentedBooks, this, userId);
        recyclerViewRentedBook.setAdapter(rentBookAdapter);
    }

    /**
     * Register event handlers
     * */
    private void registerEventHandlers(){
        loadRentedBooks();
        returnToProfile();
    }

    /**
     * Load borrowed books from database and fetch from books
     * */
    private void loadRentedBooks() {
        progressBar.setVisibility(View.VISIBLE);
        DatabaseReference rentedBooksRef = dbRef.child(userId).child("borrowedBooks");

        rentedBooksRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                rentedBooks.clear();
                List<String> isbnsToFetch = new ArrayList<>();

                for (DataSnapshot rentSnapshot : snapshot.getChildren()) {
                    Boolean isReturned = rentSnapshot.child("isReturned").getValue(Boolean.class);
                    if (isReturned != null && !isReturned) {
                        String isbn = rentSnapshot.child("isbn").getValue(String.class);
                        if (isbn != null) {
                            isbnsToFetch.add(isbn);
                        }
                    }
                }

                if (isbnsToFetch.isEmpty()) {
                    progressBar.setVisibility(View.GONE);
                    rentBookAdapter.notifyDataSetChanged();
                } else {
                    fetchBooks(isbnsToFetch);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("RentLoad", getString(R.string.errorFetchBook), error.toException());
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    /**
     * Fetch book list from books
     * */
    private void fetchBooks(List<String> isbns) {
        final int[] loadedCount = {0};

        for (String isbn : isbns) {
            DatabaseReference bookRef = FirebaseDatabase.getInstance().getReference("books").child(isbn);
            String currentIsbn = isbn;

            bookRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Books book = snapshot.getValue(Books.class);

                    if (book != null) {
                        DatabaseReference rentRef = dbRef.child(userId).child("borrowedBooks");
                        rentRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot rentSnapshot) {
                                for (DataSnapshot borrowEntry : rentSnapshot.getChildren()) {
                                    String storedIsbn = borrowEntry.child("isbn").getValue(String.class);

                                    if (currentIsbn.equals(storedIsbn)) {
                                        String rentDate = borrowEntry.child("borrowedDate").getValue(String.class);
                                        String returnDate = borrowEntry.child("returnDate").getValue(String.class);

                                        book.setRentDate(rentDate);
                                        book.setReturnDate(returnDate);
                                        break;
                                    }
                                }

                                rentedBooks.add(book);
                                checkIfAllLoaded();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                checkIfAllLoaded();
                            }
                        });
                    } else {
                        checkIfAllLoaded();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    checkIfAllLoaded();
                }

                private void checkIfAllLoaded() {
                    loadedCount[0]++;
                    if (loadedCount[0] == isbns.size()) {
                        rentBookAdapter.notifyDataSetChanged();
                        progressBar.setVisibility(View.GONE);
                    }
                }
            });
        }
    }

    /**
    * Return to profile home page
    * */
    private void returnToProfile(){
        imgBtnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent returnToProfile = new Intent(ProfileReturnBookActivity.this, ProfileHomeActivity.class);
                startActivity(returnToProfile);
            }
        });
    }


}
