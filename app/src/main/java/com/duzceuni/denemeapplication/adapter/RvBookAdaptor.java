package com.duzceuni.denemeapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.duzceuni.denemeapplication.R;
import com.duzceuni.denemeapplication.entity.Books;

import java.util.List;

public class RvBookAdaptor extends RecyclerView.Adapter<RvBookAdaptor.CardViewNesneTutucu> {
    private Context context;
    private List<Books> kitaplar;
    public RvBookAdaptor(Context context, List<Books> kitaplar) {
        this.context = context;
        this.kitaplar = kitaplar;
    }
    public void setFilterList(List<Books> filteredList) {
        kitaplar = filteredList;
        notifyDataSetChanged();
    }

    public class CardViewNesneTutucu extends RecyclerView.ViewHolder {
        public TextView textCard_BookName,textCard_isbn,textCard_adet,textCard_yazar;
        public CardViewNesneTutucu(View view)
        {
            super(view);
            textCard_BookName = view.findViewById(R.id.textCard_UserName);
            textCard_isbn = view.findViewById(R.id.textCard_isbn);
            textCard_adet = view.findViewById(R.id.textCard_cinsiyet);
            textCard_yazar = view.findViewById(R.id.textCard_yazar);
        }
    }
    @NonNull
    @Override
    public CardViewNesneTutucu onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_books,parent,false);
        return new CardViewNesneTutucu(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewNesneTutucu holder, int position) {
        Books kitap = kitaplar.get(position);
        //String adet = String.valueOf(R.string.adet);
        holder.textCard_BookName.setText(kitap.getTitle());
        String adet = context.getString(R.string.adet);
        holder.textCard_adet.setText(adet+": "+ kitap.getCopiesAvailable());
        holder.textCard_isbn.setText("ISBN: "+ kitap.getISBN());
        holder.textCard_yazar.setText((kitap.getAuthor()));
    }


    @Override
    public int getItemCount() {
        return kitaplar.size();
    }

    public void removeItem(int position) {
        kitaplar.remove(position); // bookList, adaptörün veri listesi olmalı
        notifyItemRemoved(position);
    }
}
