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
import com.duzceuni.denemeapplication.entity.Books;
import com.duzceuni.denemeapplication.manager.FavoriteManager;
import com.duzceuni.denemeapplication.adapter.FavoriteBookListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ProfileFavoriteBookActivity extends AppCompatActivity {

    private FavoriteManager favoriteManager;
    private ImageButton imgBtnReturn;
    private ProgressBar progressBar;
    private RecyclerView recyclerViewFavoriteBookList;
    private FavoriteBookListAdapter adapter;
    private List<Books> favoriteBooks = new ArrayList<>();
    private DatabaseReference bookRef;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_favorite_booklist);

        initComponents();
        registerEventHandlers();
    }

    /**
     * Initialize components
     * */
    private void initComponents(){
        progressBar = findViewById(R.id.progressBar);
        imgBtnReturn = findViewById(R.id.imgBtnReturn);
        recyclerViewFavoriteBookList = findViewById(R.id.recyclerViewFavoriteBookList);
        recyclerViewFavoriteBookList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FavoriteBookListAdapter(this, favoriteBooks);
        recyclerViewFavoriteBookList.setAdapter(adapter);

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        favoriteManager = new FavoriteManager(userId);
    }

    /**
     * Register event handlers
     * */
    private void registerEventHandlers(){
        loadFavoriteBooks();
        returnToProfileHome();
    }

    /**
     * Load favorite books from database
     * */
    private void loadFavoriteBooks() {
        progressBar.setVisibility(View.VISIBLE);
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference favoritesRef = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(currentUserId)
                .child("favoriteBooks");

        //UPDATE UI
        favoritesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long total = snapshot.getChildrenCount();
                if (total == 0) {
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                final int[] loadedCount = {0};

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String isbnStr = dataSnapshot.getKey();
                    if (isbnStr != null) {
                        try {
                            long isbn = Long.parseLong(isbnStr);
                            fetchBookByISBN(isbn, total, loadedCount);
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                            loadedCount[0]++;
                            if (loadedCount[0] == total) {
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("loadFavoriteBooks", getString(R.string.errorFetchBook)+ error.getMessage());
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    /**
     * Fetch book details from database
     * */
    private void fetchBookByISBN(long isbn, long total, int[] loadedCount) {
        DatabaseReference bookRef = FirebaseDatabase.getInstance()
                .getReference("books")
                .child(String.valueOf(isbn));

        bookRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Books book = snapshot.getValue(Books.class);
                if (book != null) {
                    favoriteBooks.add(book);
                    adapter.notifyDataSetChanged();
                }

                loadedCount[0]++;
                if (loadedCount[0] == total) {
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("fetchBookByISBN", getString(R.string.errorFetchBook) + error.getMessage());
                loadedCount[0]++;
                if (loadedCount[0] == total) {
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }


    /**
     * Return to profile home page
     * */
    private void returnToProfileHome(){
        imgBtnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent returnToProfileHome = new Intent(ProfileFavoriteBookActivity.this, ProfileHomeActivity.class);
                startActivity(returnToProfileHome);
            }
        });
    }

}
