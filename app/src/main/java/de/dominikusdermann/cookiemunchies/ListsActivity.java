package de.dominikusdermann.cookiemunchies;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import org.json.JSONObject;

import java.util.ArrayList;

public class ListsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    public static ListViewAdapter listViewAdapter;
    public static ArrayList<JSONObject> listArray;
    private Endpoints endpoints;
    public static Button myButton;
    private Authentication authentication;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lists);

        // initiate List array
        listArray = new ArrayList<>();

        // get all lists from endpoint
        endpoints = new Endpoints(this);
        endpoints.getAllLists();
        authentication = new Authentication(this);

        // set up the recycler view
        recyclerView = findViewById(R.id.listRecyclerView);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        listViewAdapter = new ListViewAdapter(this, listArray);
        recyclerView.setAdapter(listViewAdapter);

        // initiate my list button
        myButton = findViewById(R.id.myButton2);
        myButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                authentication.whoAmI();
            }
        });
    }
}
