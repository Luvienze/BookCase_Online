package com.duzceuni.denemeapplication.home;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.duzceuni.denemeapplication.R;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    private List<String> categories;
    private Context context;
    private String selectedCategory = "";
    private OnCategoryClickListener listener;
    private int selectedPosition = -1;
    public interface OnCategoryClickListener {
        void onCategoryClick(String category);
    }
    public CategoryAdapter(List<String> categories, Context context, OnCategoryClickListener listener) {
        this.categories = categories;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        String category = categories.get(position);
        holder.categoryTextView.setText(category);

        // SET SELECTED CATEGORY
        if (position == selectedPosition) {
            holder.categoryTextView.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_category_selected));
            holder.categoryTextView.setTextColor(ContextCompat.getColor(context, android.R.color.white));
        } else {
            holder.categoryTextView.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_category));
            holder.categoryTextView.setTextColor(ContextCompat.getColor(context, android.R.color.black));
        }

        // SEND CATEGORY TO BOOKLIST TO FILTER BOOKS BY CATEGORY
        holder.itemView.setOnClickListener(v -> {

            int clickedPosition = holder.getAdapterPosition();
            if(clickedPosition == RecyclerView.NO_POSITION) return;

            int previousPosition = selectedPosition;
            selectedPosition = clickedPosition;

            notifyItemChanged(previousPosition);
            notifyItemChanged(selectedPosition);

            if(context instanceof BookListActivity){
                ((BookListActivity) context).filterBooksByCategory(category);
            }else{
                Intent intent = new Intent(context, BookListActivity.class);
                intent.putExtra("category",category);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView categoryTextView;

        public CategoryViewHolder(View itemView) {
            super(itemView);
            categoryTextView = itemView.findViewById(R.id.textCategory);
        }
    }

}
