package com.duzceuni.denemeapplication.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.duzceuni.denemeapplication.R;
import com.duzceuni.denemeapplication.entity.Books;
import com.duzceuni.denemeapplication.entity.Category;
import com.duzceuni.denemeapplication.manager.FavoriteManager;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FavoriteBookListAdapter extends RecyclerView.Adapter<FavoriteBookListAdapter.BookListViewHolder> {

    private List<Books> bookList;
    private Context context;

    public FavoriteBookListAdapter(Context context, List<Books> bookList){
        this.context = context;
        this.bookList = bookList;
    }

    @NonNull
    @Override
    public FavoriteBookListAdapter.BookListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_profile_favorite_book, parent, false);
        return new BookListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteBookListAdapter.BookListViewHolder holder, int position) {
        Books book = bookList.get(position);
        holder.bind(book);
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FavoriteManager favoriteManager = new FavoriteManager(userId);

        favoriteManager.isFavorite(book.getISBN(), new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean isFavorite = snapshot.exists();
                holder.favorite_checked.setVisibility(isFavorite ? View.VISIBLE : View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
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
                        bookList.remove(holder.getAdapterPosition());
                        notifyItemRemoved(holder.getAdapterPosition());
                        notifyItemRangeChanged(holder.getAdapterPosition(), bookList.size());
                    }).start();
        });
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    static class BookListViewHolder extends RecyclerView.ViewHolder{

        ImageView bookCardImg;
        TextView bookCardTitle, bookCardAuthor, bookCardCategory, bookCardISBN;
        MaterialButton btnAddToCart;
        CardView bookListCardView;
        ImageButton favorite_checked;

        public BookListViewHolder(@NonNull View itemView) {
            super(itemView);
            bookCardImg = itemView.findViewById(R.id.bookCardImg);
            bookCardTitle = itemView.findViewById(R.id.bookCardTitle);
            bookCardAuthor = itemView.findViewById(R.id.bookCardAuthor);
            bookCardCategory = itemView.findViewById(R.id.bookCardCategory);
            bookCardISBN = itemView.findViewById(R.id.bookCardISBN);
            btnAddToCart = itemView.findViewById(R.id.btnAddToCart);
            bookListCardView = itemView.findViewById(R.id.bookListCardView);
            favorite_checked = itemView.findViewById(R.id.favorite_checked);
        }

        public void bind(Books book){
            String title = book.getTitle();
            if(title != null && title.length() > 30){
                title = title.substring(0,27) + "...";
            }
            bookCardTitle.setText(title);
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
        }
    }
}
