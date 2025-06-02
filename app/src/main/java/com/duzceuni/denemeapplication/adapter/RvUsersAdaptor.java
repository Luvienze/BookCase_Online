package com.duzceuni.denemeapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.duzceuni.denemeapplication.R;
import com.duzceuni.denemeapplication.entity.Users;

import java.util.List;

public class RvUsersAdaptor extends RecyclerView.Adapter<RvUsersAdaptor.CardViewKullaniciTutucu> {
    private Context context;
    private List<Users> users;

    public RvUsersAdaptor(Context context, List<Users> users) {
        this.context = context;
        this.users = users;
    }
    public void setFilterList(List<Users> filteredList) {
        users = filteredList;
        notifyDataSetChanged();
    }
    public class CardViewKullaniciTutucu extends RecyclerView.ViewHolder {
        public TextView textCard_UserName,textCard_dogum,textCard_cinsiyet,textCard_mail,textCard_phone;
        public CardViewKullaniciTutucu(View view)
        {
            super(view);
            textCard_UserName = view.findViewById(R.id.textCard_UserName);
            textCard_dogum = view.findViewById(R.id.textCard_dogum);
            textCard_cinsiyet = view.findViewById(R.id.textCard_cinsiyet);
            textCard_mail = view.findViewById(R.id.textCard_mail);
            textCard_phone = view.findViewById(R.id.textCard_phone);
        }
    }
    @NonNull
    @Override
    public RvUsersAdaptor.CardViewKullaniciTutucu onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_users,parent,false);
        return new RvUsersAdaptor.CardViewKullaniciTutucu(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RvUsersAdaptor.CardViewKullaniciTutucu holder, int position) {
        Users adminUsers = users.get(position);
        holder.textCard_UserName.setText(adminUsers.getFirstname()+" "+adminUsers.getLastname());
        String dogum = context.getString(R.string.dogum);
        String gender = context.getString(R.string.cinsiyet);
        holder.textCard_dogum.setText(dogum+adminUsers.getBirthOfDate());
        holder.textCard_cinsiyet.setText(gender+adminUsers.getGender());
        holder.textCard_mail.setText(adminUsers.getEmail());
        holder.textCard_phone.setText("Tel: "+adminUsers.getPhoneNumber());
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public void removeItem(int position) {
        users.remove(position); // bookList, adaptörün veri listesi olmalı
        notifyItemRemoved(position);
    }
}
