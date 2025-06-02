package com.duzceuni.denemeapplication.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.duzceuni.denemeapplication.R;
import com.duzceuni.denemeapplication.entity.Books;
import com.duzceuni.denemeapplication.manager.CartManager;
import com.duzceuni.denemeapplication.entity.Category;
import com.duzceuni.denemeapplication.manager.FavoriteManager;
import com.duzceuni.denemeapplication.profile.ProfileFavoriteBookActivity;
import com.duzceuni.denemeapplication.profile.ProfileHomeActivity;
import com.duzceuni.denemeapplication.profile.ProfileSettingsActivity;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class BookDetailActivity extends AppCompatActivity {
    private ImageView itemBookDetailImage;
    private TextView itemBookDetailTitleTxt, itemBookDetailAuthorTxt, itemBookDetailCategoryTxt, itemBookDetailCopiesTxt, itemBookDetailDescriptionTxt,
            itemBookDetailISBNTxt, itemBookDetailPagesTxt;
    private MaterialButton itemBookDetailRentButton;
    private ImageButton imgBtnReturn, favorite_checked, favorite_unchecked;
    private FloatingActionButton fabCart, fabProfile, fabFavourite, fabSettings;
    private FavoriteManager favoriteManager;
    private String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_book_detail);

        initComponents();
        registerEventHandlers();

    }
    /**
     *  Initialize components
     * */
    private void initComponents(){
        itemBookDetailImage = findViewById(R.id.itemBookDetailImage);
        itemBookDetailISBNTxt = findViewById(R.id.itemBookDetailISBNTxt);
        itemBookDetailTitleTxt = findViewById(R.id.itemBookDetailTitleTxt);
        itemBookDetailAuthorTxt = findViewById(R.id.itemBookDetailAuthorTxt);
        itemBookDetailCategoryTxt = findViewById(R.id.itemBookDetailCategoryTxt);
        itemBookDetailCopiesTxt = findViewById(R.id.itemBookDetailCopiesTxt);
        itemBookDetailPagesTxt = findViewById(R.id.itemBookDetailPagesTxt);
        itemBookDetailDescriptionTxt = findViewById(R.id.itemBookDetailDescriptionTxt);
        itemBookDetailRentButton = findViewById(R.id.itemBookDetailRentButton);
        imgBtnReturn = findViewById(R.id.imgBtnReturn);
        favorite_checked = findViewById(R.id.favorite_checked);
        favorite_unchecked = findViewById(R.id.favorite_unchecked);
        favoriteManager = new FavoriteManager(userId);
        fabCart = findViewById(R.id.fabCart);
        fabProfile = findViewById(R.id.fabProfile);
        fabFavourite = findViewById(R.id.fabFavourite);
        fabSettings = findViewById(R.id.fabSettings);
    }
    /**
     *  Register event handlers
     * */
    private void registerEventHandlers(){
        fetchBook();
        setButtonEnabledOrNot();
        rentBook();
        returnToBookList();
        directToCart();
        goToSettings();
        goToFavorite();
        goToProfile();
    }

    /**
     *  Fetch book and set fields
     * */
    private void fetchBook() {
        Intent intent = getIntent();
        long isbn = intent.getLongExtra("isbn",0);
        String title = intent.getStringExtra("title");
        String author = intent.getStringExtra("author");
        String categories = intent.getStringExtra("categories");
        int pages = intent.getIntExtra("pages",0);
        String description = intent.getStringExtra("description");
        String image = intent.getStringExtra("image");
        int copiesAvailable = intent.getIntExtra("copiesAvailable",0);

        itemBookDetailISBNTxt.setText(String.valueOf(isbn));
        itemBookDetailTitleTxt.setText(title);
        itemBookDetailAuthorTxt.setText(author);
        itemBookDetailCategoryTxt.setText(categories);
        itemBookDetailDescriptionTxt.setText(description);
        itemBookDetailPagesTxt.setText(String.valueOf(pages));
        itemBookDetailCopiesTxt.setText(String.valueOf(copiesAvailable));
        Glide.with(this)
                .load(image)
                .placeholder(R.mipmap.ic_image_placeholder_foreground)
                .error(R.mipmap.ic_image_error_foreground)
                .into(itemBookDetailImage);

        handleFavorite(isbn, userId);
    }

    /**
     *  Rent book via rent manager
     * */
    private void rentBook() {
        itemBookDetailRentButton.setOnClickListener(v -> {
            Books book = new Books();
            book.setISBN(Long.parseLong(itemBookDetailISBNTxt.getText().toString()));
            book.setTitle(itemBookDetailTitleTxt.getText().toString());
            book.setAuthor(itemBookDetailAuthorTxt.getText().toString());

            String categoryText = itemBookDetailCategoryTxt.getText().toString();
            String[] categoryNames = categoryText.split(",");

            List<Category> categoryList = new ArrayList<>();
            for (String categoryName : categoryNames) {
                categoryName = categoryName.trim();
                if (!categoryName.isEmpty()) {
                    categoryList.add(new Category(categoryName));
                }
            }
            book.setCategoryList(categoryList);

            CartManager cartManager = CartManager.getInstance();
            if (cartManager.addToCart(book)) {
                Toast.makeText(BookDetailActivity.this, getString(R.string.lblBookAdded), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(BookDetailActivity.this, getString(R.string.lblBookCouldntAdded), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     *  Set button visibility due to copies
     * */
    private void setButtonEnabledOrNot(){
        int copies = Integer.parseInt(itemBookDetailCopiesTxt.getText().toString());

        if (copies <= 0) {
            itemBookDetailRentButton.setEnabled(false);
            itemBookDetailRentButton.setText(R.string.btnOutOfStock);
            itemBookDetailRentButton.setBackgroundColor(getResources().getColor(R.color.gray));
        } else {
            itemBookDetailRentButton.setEnabled(true);
            itemBookDetailRentButton.setText(R.string.btnRent);
            itemBookDetailRentButton.setBackgroundColor(getResources().getColor(R.color.primary));
        }
    }

    /**
     *  Return to book list activity
     * */
    private void returnToBookList(){
        imgBtnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnToBookList = new Intent(BookDetailActivity.this, BookListActivity.class);
                startActivity(returnToBookList);
            }
        });
    }

    /**
     *  Go to cart activity
     * */
    private void directToCart(){
        fabCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent directToCart = new Intent(BookDetailActivity.this, CartActivity.class);
                startActivity(directToCart);
            }
        });
    }

    /**
     *  Handle favorite
     * */
    private void handleFavorite(long isbn, String userId) {
        favoriteManager = new FavoriteManager(userId);

        favoriteManager.isFavorite(isbn, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean isFavorite = snapshot.exists();

                favorite_checked.setVisibility(isFavorite ? View.VISIBLE : View.INVISIBLE);
                favorite_unchecked.setVisibility(isFavorite ? View.INVISIBLE : View.VISIBLE);

                favorite_unchecked.setOnClickListener(v -> {
                    favoriteManager.addFavorite(isbn);

                    favorite_unchecked.animate()
                            .alpha(0f)
                            .scaleX(0.5f)
                            .scaleY(0.5f)
                            .setDuration(200)
                            .withEndAction(() -> {
                                favorite_unchecked.setVisibility(View.INVISIBLE);

                                favorite_checked.setAlpha(0f);
                                favorite_checked.setScaleX(0.5f);
                                favorite_checked.setScaleY(0.5f);
                                favorite_checked.setVisibility(View.VISIBLE);
                                favorite_checked.animate()
                                        .alpha(1f)
                                        .scaleX(1f)
                                        .scaleY(1f)
                                        .setDuration(200)
                                        .start();
                            }).start();
                });

                favorite_checked.setOnClickListener(v -> {
                    favoriteManager.removeFavorite(isbn);

                    favorite_checked.animate()
                            .alpha(0f)
                            .scaleX(0.5f)
                            .scaleY(0.5f)
                            .setDuration(200)
                            .withEndAction(() -> {
                                favorite_checked.setVisibility(View.INVISIBLE);

                                favorite_unchecked.setAlpha(0f);
                                favorite_unchecked.setScaleX(0.5f);
                                favorite_unchecked.setScaleY(0.5f);
                                favorite_unchecked.setVisibility(View.VISIBLE);
                                favorite_unchecked.animate()
                                        .alpha(1f)
                                        .scaleX(1f)
                                        .scaleY(1f)
                                        .setDuration(200)
                                        .start();
                            }).start();
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(BookDetailActivity.this, "Favori bilgisi alınamadı", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     *  Go to profile activity
     * */
    private void goToProfile(){
        fabProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToProfile = new Intent(BookDetailActivity.this, ProfileHomeActivity.class);
                startActivity(goToProfile);
            }
        });
    }

    /**
     *  Go to favorite book list
     **/
    private void goToFavorite(){
        fabFavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToFavorite = new Intent(BookDetailActivity.this, ProfileFavoriteBookActivity.class);
                startActivity(goToFavorite);
            }
        });
    }

    /**
     *  Go to settings activity
     * */
    private void goToSettings(){
        fabSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToSettings = new Intent(BookDetailActivity.this, ProfileSettingsActivity.class);
                startActivity(goToSettings);
            }
        });
    }

}
