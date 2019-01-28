package de.dominikusdermann.cookiemunchies;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import org.json.JSONObject;

import java.util.ArrayList;

public class ListsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    public static ListViewAdapter listViewAdapter;
    public static ArrayList<JSONObject> listArray;
    private Endpoints endpoints;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lists);

        // initiate List array
        listArray = new ArrayList<>();

        // get all lists from endpoint
        endpoints = new Endpoints(this);
        endpoints.getAllLists();

        // TODO: get list of lists from endpoint

        // set up the recycler view
        recyclerView = findViewById(R.id.listRecyclerView);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        listViewAdapter = new ListViewAdapter(this, listArray);
        recyclerView.setAdapter(listViewAdapter);
    }
}
