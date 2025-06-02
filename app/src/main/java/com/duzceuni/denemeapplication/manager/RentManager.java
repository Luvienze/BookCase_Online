package com.duzceuni.denemeapplication.manager;

import androidx.annotation.NonNull;

import com.duzceuni.denemeapplication.entity.Books;
import com.duzceuni.denemeapplication.entity.Category;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class RentManager {

    private final DatabaseReference userRef;
    private final DatabaseReference booksRef;
    public RentManager(String userId){
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        userRef = db.getReference("users").child(userId);
        booksRef = db.getReference("books");
    }
    /**
     * Get user id from auth
     * */
    public String getUserId() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        return currentUser != null ? currentUser.getUid() : null;
    }
    /**
     * Return book via isbn. Check user first
     * */
    public void returnBook(String isbn, RentCallback callback) {

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            callback.onReturnFailed("User is not registered");
            return;
        }

        String userId = currentUser.getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
        DatabaseReference booksRef = FirebaseDatabase.getInstance().getReference("books");

        userRef.child("borrowedBooks").orderByChild("isbn").equalTo(isbn)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        boolean updated = false;

                        // CHECK ISRETURNED FIELD IN DB
                        for (DataSnapshot bookSnapshot : snapshot.getChildren()) {
                            bookSnapshot.getRef().child("isReturned").setValue(true);
                            updated = true;
                        }

                        if (!updated) {
                            callback.onReturnFailed("Could not find the book or book is already refunded.");
                            return;
                        }

                        // IF IS BOOK RETURNED, INCREASE COPIES SIZE 1
                        booksRef.child(isbn).child("copiesAvailable").runTransaction(new Transaction.Handler() {
                            @NonNull
                            @Override
                            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                                Integer currentVal = currentData.getValue(Integer.class);
                                if (currentVal == null) return Transaction.success(currentData);
                                currentData.setValue(currentVal + 1);
                                return Transaction.success(currentData);
                            }

                            @Override
                            public void onComplete(DatabaseError error, boolean committed, DataSnapshot currentData) {
                                if (error == null && committed) {
                                    callback.onReturnSuccess();
                                } else {
                                    callback.onReturnFailed(error != null ? error.getMessage() : "Unknown error");
                                }
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.onReturnFailed("Database error: " + error.getMessage());
                    }
                });
    }
   /**
    * Load returned books in book history.
    * Check the isReturned field in db.
    * */
    public void getReturnedBooks(BorrowedBooksCallback callback) {
        userRef.child("borrowedBooks").get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                ArrayList<Books> returnedBooks = new ArrayList<>();
                List<DataSnapshot> returnedSnapshots = new ArrayList<>();

                for (DataSnapshot snapshot : task.getResult().getChildren()) {
                    Boolean isReturned = snapshot.child("isReturned").getValue(Boolean.class);
                    if (isReturned != null && isReturned) {
                        returnedSnapshots.add(snapshot);
                    }
                }

                if (returnedSnapshots.isEmpty()) {
                    callback.onBorrowedBooksLoaded(returnedBooks);
                    return;
                }

                //CREATE NEW BOOK OBJECT FROM RETURNED BOOK
                DatabaseReference booksRef = FirebaseDatabase.getInstance().getReference("books");
                AtomicInteger remaining = new AtomicInteger(returnedSnapshots.size());
                for (DataSnapshot snapshot : returnedSnapshots) {
                    String returnedIsbn = snapshot.child("isbn").getValue(String.class);

                    booksRef.child(returnedIsbn).get().addOnCompleteListener(bookTask -> {
                        if (bookTask.isSuccessful() && bookTask.getResult().exists()) {
                            DataSnapshot bookData = bookTask.getResult();

                            long isbn = Long.parseLong(returnedIsbn);
                            String title = bookData.child("title").getValue(String.class);
                            String author = bookData.child("author").getValue(String.class);
                            int pages = bookData.child("pages").getValue(Integer.class) != null
                                    ? bookData.child("pages").getValue(Integer.class) : 0;
                            int copiesAvailable = bookData.child("copiesAvailable").getValue(Integer.class) != null
                                    ? bookData.child("copiesAvailable").getValue(Integer.class) : 0;
                            String image = bookData.child("image").getValue(String.class);

                            List<Category> categoryList = new ArrayList<>();
                            for (DataSnapshot catSnap : bookData.child("categoryList").getChildren()) {
                                Category category = catSnap.getValue(Category.class);
                                if (category != null) categoryList.add(category);
                            }

                            Books book = new Books(isbn, title, author, pages, copiesAvailable, image, categoryList);
                            returnedBooks.add(book);
                        }

                        if (remaining.decrementAndGet() == 0) {
                            callback.onBorrowedBooksLoaded(returnedBooks);
                        }
                    });
                }
            } else {
                callback.onFailed(task.getException() != null ? task.getException().getMessage() : "Unknown error");
            }
        });
    }

    public interface RentCallback {
        void onReturnSuccess();
        void onReturnFailed(String error);
    }
    public interface BorrowedBooksCallback {
        void onBorrowedBooksLoaded(ArrayList<Books> borrowedBooks);
        void onFailed(String error);
    }
}
