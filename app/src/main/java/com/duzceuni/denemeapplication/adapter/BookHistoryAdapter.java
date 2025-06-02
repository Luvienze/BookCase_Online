package com.duzceuni.denemeapplication.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.duzceuni.denemeapplication.R;
import com.duzceuni.denemeapplication.entity.Books;
import com.duzceuni.denemeapplication.entity.Category;
import com.duzceuni.denemeapplication.manager.RentManager;

import java.util.ArrayList;
import java.util.List;

public class BookHistoryAdapter extends RecyclerView.Adapter<BookHistoryAdapter.BookHistoryViewHolder> {

    private List<Books> bookList;
    private Context context;
    private RentManager rentManager;
    public BookHistoryAdapter(){}

    public BookHistoryAdapter(List<Books> bookList, Context context, String userId){
        this.bookList = bookList;
        this.context = context;
        this.rentManager = new RentManager(userId);
    }

    @NonNull
    @Override
    public BookHistoryAdapter.BookHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_profile_book_history_item,parent,false);
        return new BookHistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookHistoryAdapter.BookHistoryViewHolder holder, int position) {
        Books book = bookList.get(position);
        holder.bind(book);
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    public void setBookList(List<Books> bookList) {
        this.bookList = bookList;
        notifyDataSetChanged();
    }

    static class BookHistoryViewHolder extends RecyclerView.ViewHolder{

        ImageView bookCardImg;
        TextView bookCardTitle, bookCardAuthor, bookCardCategory,bookCardISBN;

        public BookHistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            bookCardImg = itemView.findViewById(R.id.bookCardImg);
            bookCardTitle = itemView.findViewById(R.id.bookCardTitle);
            bookCardAuthor = itemView.findViewById(R.id.bookCardAuthor);
            bookCardCategory = itemView.findViewById(R.id.bookCardCategory);
            bookCardISBN = itemView.findViewById(R.id.bookCardISBN);


        }

        public void bind(Books book){
            String title = book.getTitle();
            if(title != null && title.length() > 30){
                title = title.substring(0,27) + "...";
            }
            bookCardTitle.setText(title);
            bookCardAuthor.setText(book.getAuthor());
            bookCardISBN.setText(String.valueOf(book.getISBN()));
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
