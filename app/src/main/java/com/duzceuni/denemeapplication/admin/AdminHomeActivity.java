package com.duzceuni.denemeapplication.admin;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.duzceuni.denemeapplication.R;
import com.duzceuni.denemeapplication.adapter.RvBookAdaptor;
import com.duzceuni.denemeapplication.entity.Books;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdminHomeActivity extends AppCompatActivity {

    private SharedPreferences sharedPref;
    private Button btn_menu;
    private DrawerLayout drawerLayout;
    private NavigationView nv_slide;
    private DatabaseReference dbRef;
    private RecyclerView rv;
    private RvBookAdaptor adaptor;
    List<Books> ogelerListesi = new ArrayList<>();
    private SearchView searchView;
    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        sharedPref = getSharedPreferences("admin_config", Context.MODE_PRIVATE);
        FirebaseApp.initializeApp(this);
        initCompanents();
        registerEventHandlers();
    }
    private void initCompanents()
    {
        drawerLayout = findViewById(R.id.main);
        nv_slide = findViewById(R.id.ng_slide);
        btn_menu = findViewById(R.id.btn_menu);
        searchView = findViewById(R.id.AdminBookSearch);
        rv = findViewById(R.id.rv);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(this));
        progressBar = findViewById(R.id.progressBar);

        dbRef = FirebaseDatabase.getInstance().getReference("books");
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot kitapSnapshot : dataSnapshot.getChildren()) {
                    Books kitap = kitapSnapshot.getValue(Books.class);
                    if (kitap != null) {
                        ogelerListesi.add(kitap);
                    };
                }
                adaptor = new RvBookAdaptor(AdminHomeActivity.this, ogelerListesi);
                rv.setAdapter(adaptor);
                adaptor.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    private void registerEventHandlers()
    {
        btn_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerOpen(nv_slide)) {
                    drawerLayout.closeDrawer(nv_slide);
                } else {
                    drawerLayout.openDrawer(nv_slide);
                }
            }
        });
        nv_slide.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                MenuClass menuClass = new MenuClass();
                menuClass.logout(menuItem,AdminHomeActivity.this,sharedPref);
                if (menuItem.getItemId()==R.id.snv_turnoff)
                   finishAffinity();

                //drawerLayout.closeDrawer(nv_slide);
                return true;
            }
        });

        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                if (direction == ItemTouchHelper.LEFT) {
                    Books kitap = new Books();
                    kitap = ogelerListesi.get(position);
                    DatabaseReference dbRef1 = FirebaseDatabase.getInstance().getReference("books");
                    long kitapKey = kitap.getISBN(); // örnek key
                    dbRef1.child(String.valueOf(kitapKey)).removeValue()
                            .addOnSuccessListener(aVoid -> {
                                adaptor.removeItem(position);
                                Toast.makeText(getApplicationContext(), R.string.Toast_kitap_silindi, Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(getApplicationContext(), R.string.Toast_hata, Toast.LENGTH_SHORT).show();
                            });
                } else if (direction == ItemTouchHelper.RIGHT) {
                    Intent intent = new Intent(AdminHomeActivity.this, AdminBookEditActivity.class);
                    Books kitap1 = new Books();
                    kitap1 = ogelerListesi.get(position);
                    //intent.putExtra("book_edit",kitap1);
                    intent.putExtra("isbn", kitap1.getISBN());
                    startActivity(intent);
                    adaptor.notifyItemChanged(position); // Düzenleme yapılmadığı için item'ı geri yükle
                }
            }

            @Override
            public void onChildDraw(@NonNull Canvas c,
                                    @NonNull RecyclerView recyclerView,
                                    @NonNull RecyclerView.ViewHolder viewHolder,
                                    float dX, float dY,
                                    int actionState,
                                    boolean isCurrentlyActive) {

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

                Paint paint = new Paint();
                RectF background;
                float height = (float) viewHolder.itemView.getBottom() - viewHolder.itemView.getTop();

                if (dX > 0) { // sağa kaydırma
                    paint.setColor(Color.parseColor("#4CAF50")); // yeşil
                    background = new RectF((float) viewHolder.itemView.getLeft(),
                            (float) viewHolder.itemView.getTop(),
                            dX, (float) viewHolder.itemView.getBottom());
                    c.drawRect(background, paint);

                    Drawable icon = ContextCompat.getDrawable(AdminHomeActivity.this, R.drawable.ic_edit); // düzenle ikonu
                    int iconMargin = (int) (height - icon.getIntrinsicHeight()) / 2;
                    int iconTop = viewHolder.itemView.getTop() + iconMargin;
                    int iconLeft = viewHolder.itemView.getLeft() + iconMargin;
                    int iconRight = iconLeft + icon.getIntrinsicWidth();
                    int iconBottom = iconTop + icon.getIntrinsicHeight();

                    icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                    icon.draw(c);
                } else if (dX < 0) { // sola kaydırma
                    paint.setColor(Color.parseColor("#F44336")); // kırmızı
                    background = new RectF((float) viewHolder.itemView.getRight() + dX,
                            (float) viewHolder.itemView.getTop(),
                            (float) viewHolder.itemView.getRight(),
                            (float) viewHolder.itemView.getBottom());
                    c.drawRect(background, paint);

                    Drawable icon = ContextCompat.getDrawable(AdminHomeActivity.this, R.drawable.ic_delete); // sil ikonu
                    int iconMargin = (int) (height - icon.getIntrinsicHeight()) / 2;
                    int iconTop = viewHolder.itemView.getTop() + iconMargin;
                    int iconRight = viewHolder.itemView.getRight() - iconMargin;
                    int iconLeft = iconRight - icon.getIntrinsicWidth();
                    int iconBottom = iconTop + icon.getIntrinsicHeight();

                    icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                    icon.draw(c);
                }
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(rv);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterList(newText);
                return true;
            }

        });

    }
    private void filterList(String text) {
        List<Books> filteredList = new ArrayList<>();
        for (Books kitap:ogelerListesi)
        {
            if (kitap.getTitle().toLowerCase().contains(text.toLowerCase())
                    | kitap.getAuthor().toLowerCase().contains(text.toLowerCase()))
            {
                filteredList.add(kitap);
                adaptor.setFilterList(filteredList);

            }
        }
        if (filteredList.isEmpty())
        {
            //Toast.makeText(this,"Sonuç bulunamadı",Toast.LENGTH_SHORT).show();
        }
        else
        {
            adaptor = new RvBookAdaptor(this,filteredList);
            rv.setAdapter(adaptor);
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent keyEvent) {   //geri tusuna basınca olacak
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                finishAffinity();
                break;
        }
        return true;
    }
}