package com.duzceuni.denemeapplication.adapter;

import android.text.TextUtils;
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
import com.duzceuni.denemeapplication.manager.CartManager;
import com.duzceuni.denemeapplication.entity.Category;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private List<Books> cartList;
    private TextView txtEmptyCart;
    private RecyclerView recyclerViewBookList;

    private OnCartUpdatedListener listener;

    public CartAdapter(List<Books> cartList, TextView txtEmptyCart, RecyclerView recyclerViewBookList, OnCartUpdatedListener listener) {
        this.cartList = cartList;
        this.txtEmptyCart = txtEmptyCart;
        this.recyclerViewBookList = recyclerViewBookList;
        this.listener = listener;
    }
    public CartAdapter(List<Books> cartList){
        this.cartList = cartList;
    }

    @NonNull
    @Override
    public CartAdapter.CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book_cart,parent,false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartAdapter.CartViewHolder holder, int position) {
        Books book = cartList.get(position);
        holder.bind(book);

        // REMOVE BOOK FROM CART
        holder.btnRemoveBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = holder.getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    Books removedBook = cartList.get(pos);
                    cartList.remove(pos);
                    CartManager.getInstance().removeFromCart(removedBook);
                    notifyItemRemoved(pos);
                    notifyItemRangeChanged(pos, cartList.size());

                    Toast.makeText(v.getContext(), R.string.lblBookRemoved, Toast.LENGTH_SHORT).show();

                    if (listener != null) {
                        listener.onCartUpdated();
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    /**
     * Update book list
     * */
    public void updateBooks(List<Books> newBooks) {
        this.cartList = newBooks;
        notifyDataSetChanged();
        if (cartList.isEmpty()) {
            txtEmptyCart.setVisibility(View.VISIBLE);
            recyclerViewBookList.setVisibility(View.GONE);
        } else {
            txtEmptyCart.setVisibility(View.GONE);
            recyclerViewBookList.setVisibility(View.VISIBLE);
        }
    }
    public interface OnCartUpdatedListener {
        void onCartUpdated();
    }
    static class CartViewHolder extends RecyclerView.ViewHolder{

        ImageView bookCardImg;
        TextView bookCardTitle, bookCardAuthor, bookCardCategory, bookCardISBN, txtEmptyCart;
        MaterialButton btnRemoveBook;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            bookCardImg = itemView.findViewById(R.id.bookCardImg);
            bookCardTitle = itemView.findViewById(R.id.bookCardTitle);
            bookCardAuthor = itemView.findViewById(R.id.bookCardAuthor);
            bookCardCategory = itemView.findViewById(R.id.bookCardCategory);
            bookCardISBN = itemView.findViewById(R.id.bookCardISBN);
            btnRemoveBook = itemView.findViewById(R.id.btnRemoveBook);
            txtEmptyCart = itemView.findViewById(R.id.txtEmptyCart);
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
