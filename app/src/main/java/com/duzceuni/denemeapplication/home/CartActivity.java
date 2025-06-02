package com.duzceuni.denemeapplication.home;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.duzceuni.denemeapplication.R;
import com.duzceuni.denemeapplication.adapter.CartAdapter;
import com.duzceuni.denemeapplication.entity.Books;
import com.duzceuni.denemeapplication.manager.CartManager;
import com.duzceuni.denemeapplication.profile.ProfileFavoriteBookActivity;
import com.duzceuni.denemeapplication.profile.ProfileHomeActivity;
import com.duzceuni.denemeapplication.profile.ProfileSettingsActivity;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CartActivity extends AppCompatActivity {

    private RecyclerView recyclerViewBookList;
    private CartAdapter cartAdapter;
    private TextView txtEmptyCart;
    private ImageButton imgBtnReturn;
    private MaterialButton btnRentBooks;
    private FirebaseDatabase firebaseDatabase;
    private FloatingActionButton fabCart, fabProfile, fabFavourite, fabSettings;
    private DatabaseReference dbRef;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        initComponents();
        registerEventHandlers();

        // VIEW BOOKS
        cartAdapter = new CartAdapter(CartManager.getInstance().getCartItems(), txtEmptyCart, recyclerViewBookList, new CartAdapter.OnCartUpdatedListener(){
            @Override
            public void onCartUpdated(){
                checkCartEmpty();
            }
        });
        recyclerViewBookList.setAdapter(cartAdapter);
        CartManager.getInstance().setOnCartUpdatedListener(new CartAdapter.OnCartUpdatedListener() {
            @Override
            public void onCartUpdated() {
                cartAdapter.updateBooks(CartManager.getInstance().getCartItems());
            }
        });
    }

    /**
     *  Initialize components
     * */
    private void initComponents() {
        recyclerViewBookList = findViewById(R.id.recyclerViewBookList);
        txtEmptyCart = findViewById(R.id.txtEmptyCart);
        imgBtnReturn = findViewById(R.id.imgBtnReturn);
        btnRentBooks = findViewById(R.id.btnRentBooks);
        firebaseDatabase = FirebaseDatabase.getInstance();
        fabCart = findViewById(R.id.fabCart);
        fabProfile = findViewById(R.id.fabProfile);
        fabFavourite = findViewById(R.id.fabFavourite);
        fabSettings = findViewById(R.id.fabSettings);
        progressBar = findViewById(R.id.progressBar);
    }

    /**
     *  Register event handlers
     * */
    private void registerEventHandlers() {
        checkRefundStatus();
        getBookList();
        checkCartEmpty();
        returnToBookList();
        rentBooks();
        goToSettings();
        goToFavorite();
        goToProfile();
    }

    /**
     *   Fetch books
     * */
    private void getBookList() {
        if (getIntent() != null) {
            List<Books> tempBooks = getIntent().getParcelableArrayListExtra("cartList");
            if (tempBooks != null) {
                cartAdapter.updateBooks(tempBooks);
            }
        }
        recyclerViewBookList.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewBookList.setHasFixedSize(true);
    }

    /**
     *  Check cart if is empty or not
     * */
    private void checkCartEmpty() {
        if (CartManager.getInstance().getCartItems().isEmpty()) {
            txtEmptyCart.setVisibility(View.VISIBLE);
            recyclerViewBookList.setVisibility(View.GONE);
        } else {
            txtEmptyCart.setVisibility(View.GONE);
            recyclerViewBookList.setVisibility(View.VISIBLE);
        }
    }

    /**
    *  Return to book list activity
    * */
    private void returnToBookList(){
        imgBtnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnBookList = new Intent(CartActivity.this, BookListActivity.class);
                startActivity(returnBookList);
            }
        });
    }


    /**
     *  Rent books as list
     * , save it to database
     *
     * */
    private void rentBooks() {
        btnRentBooks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CartManager.getInstance().getCartItems().isEmpty()) {
                    Toast.makeText(CartActivity.this, getString(R.string.lblCartIsEmpty), Toast.LENGTH_SHORT).show();
                } else {
                    progressBar.setVisibility(View.VISIBLE);

                    List<Books> cartItems = CartManager.getInstance().getCartItems();
                    DatabaseReference usersRef = firebaseDatabase.getReference("users");
                    DatabaseReference booksRef = firebaseDatabase.getReference("books");

                    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    String borrowedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

                    Calendar calendar = Calendar.getInstance();
                    calendar.add(Calendar.DAY_OF_YEAR, 14);
                    String returnDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.getTime());

                    final int[] completedTransactions = {0};

                    for (Books book : cartItems) {
                        String isbn = String.valueOf(book.getISBN());

                        DatabaseReference rentedBooksRef = usersRef.child(userId).child("borrowedBooks").push();

                        Map<String, Object> bookData = new HashMap<>();
                        bookData.put("isbn", isbn);
                        bookData.put("borrowedDate", borrowedDate);
                        bookData.put("returnDate", returnDate);
                        bookData.put("isReturned", false);

                       // REDUCE THE NUMBER OF BOOKS
                        rentedBooksRef.setValue(bookData).addOnCompleteListener(task -> {
                            booksRef.child(isbn).child("copiesAvailable").runTransaction(new Transaction.Handler() {
                                @NonNull
                                @Override
                                public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                                    Integer currentQuantity = currentData.getValue(Integer.class);
                                    if (currentQuantity == null) {
                                        return Transaction.success(currentData);
                                    }

                                    if (currentQuantity > 0) {
                                        currentData.setValue(currentQuantity - 1);
                                    }

                                    return Transaction.success(currentData);
                                }

                                @Override
                                public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                                    completedTransactions[0]++;

                                    if (completedTransactions[0] == cartItems.size()) {
                                        progressBar.setVisibility(View.GONE);

                                        AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this, R.style.MyAlertDialogTheme);
                                        builder.setTitle(getString(R.string.alertRentTitle));
                                        builder.setMessage(getString(R.string.alertRentMessage));
                                        builder.setNegativeButton(getString(R.string.alertRentNegativeButton), new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                finish();
                                            }
                                        });
                                        AlertDialog alertDialog = builder.create();
                                        alertDialog.show();

                                        CartManager.getInstance().clearCart();
                                        cartAdapter.notifyDataSetChanged();
                                        checkCartEmpty();
                                    }
                                }
                            });
                        });
                    }
                }
            }
        });
    }

    /**
     * Check if user have books to refund
     * */
    private void checkRefundStatus() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) return;

        String userId = currentUser.getUid();
        DatabaseReference dbRef = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(userId)
                .child("borrowedBooks");

        dbRef.orderByChild("isReturned").equalTo(false).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean hasOverdue = false;
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Date today = new Date();

                for (DataSnapshot bookSnapshot : snapshot.getChildren()) {
                    String returnDateStr = bookSnapshot.child("returnDate").getValue(String.class);
                    if (returnDateStr != null) {
                        try {
                            Date returnDate = sdf.parse(returnDateStr);
                            if (returnDate != null && today.after(addDays(returnDate, 1))) {
                                hasOverdue = true;
                                break;
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }
                if (hasOverdue) {
                    showOverdueAlert();
                    if (btnRentBooks != null) {
                        btnRentBooks.setVisibility(View.INVISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("checkRefundStatus", "Could not fetch data " + error.getMessage());
            }
        });
    }

    private void showOverdueAlert() {
        new AlertDialog.Builder(this, R.style.MyAlertDialogTheme)
                .setTitle(getString(R.string.refundErrorTitle))
                .setMessage(getString(R.string.refundErrorMessage))
                .setPositiveButton(R.string.tamam, null)
                .show();
    }

    /**
     * Add return date to one day
     * */
    private Date addDays(Date date, int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, days);
        return calendar.getTime();
    }

    /**
     *  Go to profile activity
     * */
    private void goToProfile(){
        fabProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToProfile = new Intent(CartActivity.this, ProfileHomeActivity.class);
                startActivity(goToProfile);
            }
        });
    }

    /**
     *  Go to favorite activity
     * */
    private void goToFavorite(){
        fabFavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToFavorite = new Intent(CartActivity.this, ProfileFavoriteBookActivity.class);
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
                Intent goToSettings = new Intent(CartActivity.this, ProfileSettingsActivity.class);
                startActivity(goToSettings);
            }
        });
    }

}
