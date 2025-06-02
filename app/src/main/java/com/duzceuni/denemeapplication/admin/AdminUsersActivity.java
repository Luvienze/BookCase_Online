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
import com.duzceuni.denemeapplication.adapter.RvUsersAdaptor;
import com.duzceuni.denemeapplication.entity.Users;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdminUsersActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private NavigationView nv_slide;
    private Button btn_menu;
    private SharedPreferences sharedPref;
    private DatabaseReference dbRef;
    private FirebaseDatabase database;
    private RecyclerView rv;
    private RvUsersAdaptor adaptor;
    private SearchView searchView;
    List<Users> users = new ArrayList<>();
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_users);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initCompanents();
        registerEventHandlers();
    }
    private void initCompanents()
    {
        sharedPref = getSharedPreferences("admin_config", Context.MODE_PRIVATE);
        drawerLayout = findViewById(R.id.main);
        nv_slide = findViewById(R.id.ng_slide);
        btn_menu = findViewById(R.id.btn_menu);
        searchView = findViewById(R.id.AdminUsersSearch);
        searchView.clearFocus();
        rv = findViewById(R.id.rv);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(this));
        progressBar = findViewById(R.id.progressBar);

        database = FirebaseDatabase.getInstance();

        dbRef = FirebaseDatabase.getInstance().getReference("users");
        Query query = dbRef.orderByChild("banned").equalTo(false);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                users.clear();
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    Users user = userSnapshot.getValue(Users.class);
                    users.add(user);
                }
                adaptor = new RvUsersAdaptor(AdminUsersActivity.this, users);
                rv.setAdapter(adaptor);
                adaptor.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                //Toast.makeText(AdminUsersActivity.this, "Veri alınamadı.", Toast.LENGTH_SHORT).show();
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
                menuClass.logout(menuItem, AdminUsersActivity.this,sharedPref);
                if (menuItem.getItemId()==R.id.snv_turnoff)
                    finishAffinity();

                //drawerLayout.closeDrawer(nv_slide);
                return true;
            }
        });
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

        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
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
                    Users adminUsers = new Users();
                    adminUsers = users.get(position);
                    String Id = adminUsers.getId();
                    DatabaseReference dbRef1 = FirebaseDatabase.getInstance().getReference("users");
                    dbRef1.child(Id).child("banned").setValue(true);
                    adaptor.removeItem(position);
                    Toast.makeText(AdminUsersActivity.this,R.string.Toast_engel2,Toast.LENGTH_SHORT).show();

                } else if (direction == ItemTouchHelper.RIGHT) {
                    //Toast.makeText(AdminUsersActivity.this, "Düzenle açılacak", Toast.LENGTH_SHORT).show();
                    //adaptor.notifyItemChanged(position); // Düzenleme yapılmadığı için item'ı geri yükle
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

                } else if (dX < 0) { // sola kaydırma
                    paint.setColor(Color.parseColor("#F44336")); // kırmızı
                    background = new RectF((float) viewHolder.itemView.getRight() + dX,
                            (float) viewHolder.itemView.getTop(),
                            (float) viewHolder.itemView.getRight(),
                            (float) viewHolder.itemView.getBottom());
                    c.drawRect(background, paint);

                    Drawable icon = ContextCompat.getDrawable(AdminUsersActivity.this, R.drawable.ic_block); // sil ikonu
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
    }
    private void filterList(String text) {
            List<Users> filteredList = new ArrayList<>();
            for (Users adminUsers:users)
            {
                if (adminUsers.getFirstname().toLowerCase().contains(text.toLowerCase())
                        | adminUsers.getEmail().toLowerCase().contains(text.toLowerCase()))
                {
                    filteredList.add(adminUsers);
                    adaptor.setFilterList(filteredList);
                }
            }
            if (filteredList.isEmpty())
            {
                //Toast.makeText(this,"Sonuç bulunamadı",Toast.LENGTH_SHORT).show();
            }
            else
            {
                adaptor = new RvUsersAdaptor(this,filteredList);
                rv.setAdapter(adaptor);
            }
    }
    public boolean onKeyDown(int keyCode, KeyEvent keyEvent) {   //geri tusuna basınca olacak
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                Intent intent = new Intent(AdminUsersActivity.this, AdminHomeActivity.class);
                startActivity(intent);
                break;
        }
        return true;
    }
}