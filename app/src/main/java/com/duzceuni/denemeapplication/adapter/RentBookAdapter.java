package com.duzceuni.denemeapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.duzceuni.denemeapplication.R;
import com.duzceuni.denemeapplication.entity.Books;
import com.duzceuni.denemeapplication.manager.RentManager;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class RentBookAdapter extends RecyclerView.Adapter<RentBookAdapter.RentBookViewHolder> {

    private List<Books> bookList;
    private Context context;
    private RentManager rentManager;

    public RentBookAdapter(List<Books> bookList, Context context, String userId) {
        this.bookList = bookList;
        this.context = context;
        this.rentManager = new RentManager(userId);
    }

    @NonNull
    @Override
    public RentBookAdapter.RentBookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_profile_rented_book_item, parent, false);
        return new RentBookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RentBookAdapter.RentBookViewHolder holder, int position) {
        Books book = bookList.get(position);
        holder.bind(book);

        String isbn = String.valueOf(book.getISBN());
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        holder.btnRefundBook.setOnClickListener(view -> {
            rentManager.returnBook(isbn, new RentManager.RentCallback() {
                @Override
                public void onReturnSuccess() {
                    Toast.makeText(context, R.string.rentBookSuccess, Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onReturnFailed(String error) {
                    Toast.makeText(context, R.string.rentBookFailed + error, Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }
    static class RentBookViewHolder extends RecyclerView.ViewHolder {
        ImageView bookCardImg;
        TextView bookCardTitle, bookCardAuthor, bookCardISBN, bookCardRentDate, bookCardReturnDate;
        MaterialButton btnRefundBook;

        public RentBookViewHolder(@NonNull View itemView) {
            super(itemView);
            bookCardImg = itemView.findViewById(R.id.bookCardImg);
            bookCardTitle = itemView.findViewById(R.id.bookCardTitle);
            bookCardAuthor = itemView.findViewById(R.id.bookCardAuthor);
            bookCardISBN = itemView.findViewById(R.id.bookCardISBN);
            bookCardRentDate = itemView.findViewById(R.id.bookCardRentDate);
            bookCardReturnDate = itemView.findViewById(R.id.bookCardReturnDate);
            btnRefundBook = itemView.findViewById(R.id.btnRefundBook);
        }
        public void bind(Books book) {
            String title = book.getTitle();
            if(title != null && title.length() > 30){
                title = title.substring(0,27) + "...";
            }
            bookCardTitle.setText(title);
            bookCardAuthor.setText(book.getAuthor());
            bookCardISBN.setText(String.valueOf(book.getISBN()));
            bookCardRentDate.setText(book.getRentDate());
            bookCardReturnDate.setText(book.getReturnDate());
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

