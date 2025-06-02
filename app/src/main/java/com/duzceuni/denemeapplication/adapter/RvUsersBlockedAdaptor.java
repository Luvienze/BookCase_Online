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

public class RvUsersBlockedAdaptor extends RecyclerView.Adapter<RvUsersBlockedAdaptor.CardViewKullaniciBlockedTutucu>{

    private Context context;
    private List<Users> users;
    public RvUsersBlockedAdaptor(Context context, List<Users> users) {
        this.context = context;
        this.users = users;
    }
    public void setFilterList(List<Users> filteredList) {
        users = filteredList;
        notifyDataSetChanged();
    }
    public class CardViewKullaniciBlockedTutucu extends RecyclerView.ViewHolder {
        public TextView textCard_UserName,textCard_dogum,textCard_cinsiyet,textCard_mail,textCard_phone;
        public CardViewKullaniciBlockedTutucu(View view)
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
    public RvUsersBlockedAdaptor.CardViewKullaniciBlockedTutucu onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_users_blocked,parent,false);
        return new RvUsersBlockedAdaptor.CardViewKullaniciBlockedTutucu(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RvUsersBlockedAdaptor.CardViewKullaniciBlockedTutucu holder, int position) {
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
