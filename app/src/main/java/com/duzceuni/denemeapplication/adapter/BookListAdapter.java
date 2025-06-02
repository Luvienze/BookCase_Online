package com.duzceuni.denemeapplication.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.duzceuni.denemeapplication.R;
import com.duzceuni.denemeapplication.entity.Books;
import com.duzceuni.denemeapplication.manager.CartManager;
import com.duzceuni.denemeapplication.entity.Category;
import com.duzceuni.denemeapplication.manager.FavoriteManager;
import com.duzceuni.denemeapplication.home.BookDetailActivity;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class BookListAdapter extends RecyclerView.Adapter<BookListAdapter.BookListViewHolder> {

    private List<Books> booksList;
    private Context context;
    public BookListAdapter(Context context, List<Books> bookList) {
        this.context = context;
        this.booksList = bookList;
    }

    @NonNull
    @Override
    public BookListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book, parent, false);
        return new BookListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookListViewHolder holder, int position){
        Books book = booksList.get(position);
        CartManager cartManager = CartManager.getInstance();
        holder.bind(book);

        checkCopies(String.valueOf(book.getISBN()), holder.btnAddToCart);

        holder.btnAddToCart.setOnClickListener(v -> {
            if (!cartManager.isInCart(book) && !cartManager.isFull()) {
                boolean added = cartManager.addToCart(book);
                if (added) {
                    Toast.makeText(v.getContext(), R.string.lblBookAdded, Toast.LENGTH_SHORT).show();
                    notifyDataSetChanged();
                } else {
                    Toast.makeText(v.getContext(), R.string.lblBookCouldntAdded, Toast.LENGTH_SHORT).show();
                }
            }
        });

        // CHECK FAVORITE ICON VISIBILITY
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FavoriteManager favoriteManager = new FavoriteManager(userId);
        favoriteManager.isFavorite(book.getISBN(), new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean isFavorite = snapshot.exists();
                holder.favorite_checked.setVisibility(isFavorite ? View.VISIBLE : View.INVISIBLE);
                holder.favorite_unchecked.setVisibility(isFavorite ? View.INVISIBLE: View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.favorite_unchecked.setOnClickListener(v -> {
            favoriteManager.addFavorite(book.getISBN());
            holder.favorite_unchecked.animate()
                    .alpha(0f)
                    .scaleX(0.5f)
                    .scaleY(0.5f)
                    .setDuration(200)
                    .withEndAction(() -> {
                        holder.favorite_unchecked.setVisibility(View.INVISIBLE);

                        holder.favorite_checked.setAlpha(0f);
                        holder.favorite_checked.setScaleX(0.5f);
                        holder.favorite_checked.setScaleY(0.5f);
                        holder.favorite_checked.setVisibility(View.VISIBLE);
                        holder.favorite_checked.animate()
                                .alpha(1f)
                                .scaleX(1f)
                                .scaleY(1f)
                                .setDuration(200)
                                .start();
                    }).start();
        });

        holder.favorite_checked.setOnClickListener(v -> {
            favoriteManager.removeFavorite(book.getISBN());

            holder.favorite_checked.animate()
                    .alpha(0f)
                    .scaleX(0.5f)
                    .scaleY(0.5f)
                    .setDuration(200)
                    .withEndAction(() -> {
                        holder.favorite_checked.setVisibility(View.INVISIBLE);

                        holder.favorite_unchecked.setAlpha(0f);
                        holder.favorite_unchecked.setScaleX(0.5f);
                        holder.favorite_unchecked.setScaleY(0.5f);
                        holder.favorite_unchecked.setVisibility(View.VISIBLE);
                        holder.favorite_unchecked.animate()
                                .alpha(1f)
                                .scaleX(1f)
                                .scaleY(1f)
                                .setDuration(200)
                                .start();

                    }).start();
        });

    }

    @Override
    public int getItemCount() {
        return booksList.size();
    }

    /**
     * Update book list
     * */
    public void updateList(List<Books> newBooks) {
        this.booksList = newBooks;
        notifyDataSetChanged();
    }

    /**
     * Set visibility of add to cart button if there is enough copies
     * */
    public void checkCopies(String bookId, MaterialButton button) {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("books").child(bookId);

        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Books book = snapshot.getValue(Books.class);
                if (book != null) {
                    if (book.getCopiesAvailable() == 0) {
                        button.setEnabled(false);
                        button.setText(R.string.btnOutOfStock);
                    } else {
                        button.setEnabled(true);
                        button.setText(R.string.btnRent);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    static class BookListViewHolder extends RecyclerView.ViewHolder {
        ImageView bookCardImg;
        TextView bookCardTitle, bookCardAuthor, bookCardCategory, bookCardISBN;
        MaterialButton btnAddToCart;
        CardView bookListCardView;
        ImageButton favorite_unchecked, favorite_checked;

        public BookListViewHolder(@NonNull View itemView) {
            super(itemView);
            bookCardImg = itemView.findViewById(R.id.bookCardImg);
            bookCardTitle = itemView.findViewById(R.id.bookCardTitle);
            bookCardAuthor = itemView.findViewById(R.id.bookCardAuthor);
            bookCardCategory = itemView.findViewById(R.id.bookCardCategory);
            bookCardISBN = itemView.findViewById(R.id.bookCardISBN);
            btnAddToCart = itemView.findViewById(R.id.btnAddToCart);
            bookListCardView = itemView.findViewById(R.id.bookListCardView);
            favorite_unchecked = itemView.findViewById(R.id.favorite_unchecked);
            favorite_checked = itemView.findViewById(R.id.favorite_checked);
        }
        public void bind(Books book) {
            String title = book.getTitle();
            if(title != null && title.length() > 30){
                title = title.substring(0,27) + "...";
            }
            bookCardTitle.setText(title);
            bookCardTitle.setText(book.getTitle());
            bookCardAuthor.setText(book.getAuthor());
            List<String> categoryNames = new ArrayList<>();
            if (book.getCategoryList() != null) {
                for (int i = 0; i < Math.min(book.getCategoryList().size(), 2); i++) {
                    Category category = book.getCategoryList().get(i);
                    if (category != null && category.getName() != null) {
                        categoryNames.add(category.getName());
                    }
                }
            }

            if (book.getCategoryList().size() > 2) {
                categoryNames.add("...");
            }
            bookCardCategory.setText(TextUtils.join(", ", categoryNames));
            bookCardISBN.setText(String.valueOf(book.getISBN()));

            if (book.getImage() != null && !book.getImage().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(book.getImage())
                        .placeholder(R.mipmap.ic_image_placeholder_foreground)
                        .error(R.mipmap.ic_image_error_foreground)
                        .into(bookCardImg);
            }

            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), BookDetailActivity.class);
                intent.putExtra("isbn", book.getISBN());
                intent.putExtra("title", book.getTitle());
                intent.putExtra("author", book.getAuthor());
                intent.putExtra("copiesAvailable", book.getCopiesAvailable());
                intent.putExtra("pages", book.getPages());
                intent.putExtra("description", book.getDescription());
                List<String> categories = new ArrayList<>();
                for (Category category : book.getCategoryList()) {
                    if (category != null && category.getName() != null) {
                        categories.add(category.getName());
                    }
                }
                intent.putExtra("categories", TextUtils.join(", ", categories));
                intent.putExtra("image", book.getImage());
                v.getContext().startActivity(intent);
            });

        }
    }
}
