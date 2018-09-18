package de.dominikusdermann.cookiemunchies;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.JsonReader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private ItemViewAdapter itemViewAdapter;
    private ArrayList<JSONObject> itemList;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set up queue and get JSON data from server
        getRequest();

        // initialize item list
        itemList = new ArrayList<>();

        // set up refresh layout
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getRequest();
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
        }
        return super.onOptionsItemSelected(item);
    }

    private void getRequest(){
        String url = "https://cookie-munchies.herokuapp.com/api/items";
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.i("Response", "received");
                itemList.clear();
                try {
                    for (int i=0; i < response.length(); i++) {
                        JSONObject item = response.getJSONObject(i);
                        itemList.add(item);
                    }
                    // update recyclerView and stop showing refresh symbol
                    itemViewAdapter.notifyDataSetChanged();
                    swipeRefreshLayout.setRefreshing(false);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "Could not get update.", Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(MainActivity.this, "Couldn't connect to server.", Toast.LENGTH_SHORT).show();
            }
        });
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);
    }
}
