package de.dominikusdermann.cookiemunchies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    public static ItemViewAdapter itemViewAdapter;
    public static ArrayList<JSONObject> itemList;
    public static SwipeRefreshLayout swipeRefreshLayout;
    private Endpoints endpoints;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set up shared preferences
        sharedPreferences = getSharedPreferences("de.dominikusdermann.cookiemunchies", Context.MODE_PRIVATE);

        // set up queue and get JSON data from server
        endpoints = new Endpoints(this);
        endpoints.getList();

        // initialize item list
        itemList = new ArrayList<>();

        // set up refresh layout
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                endpoints.getList();
            }
        });

        // set up the recycler view
        recyclerView = findViewById(R.id.itemListRecView);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // set up adapter
        itemViewAdapter = new ItemViewAdapter(MainActivity.this, itemList);
        recyclerView.setAdapter(itemViewAdapter);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.addItemMenuButton:
                Intent itemCreation = new Intent(this, ItemCreationActivity.class);
                startActivity(itemCreation);
                break;
            case R.id.infoMenuButton:
                Intent info = new Intent(this, InfoActivity.class);
                startActivity(info);
                break;
            case R.id.switchListButton:
                Intent swithching = new Intent(this, ListsActivity.class);
                startActivity(swithching);
                break;
            case R.id.logoutMenuItem:
                sharedPreferences.edit().remove("jwt").commit();
                Intent login = new Intent(this, LoginActivity.class);
                this.startActivity(login);
        }
        return super.onOptionsItemSelected(item);
    }
}
