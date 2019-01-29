package de.dominikusdermann.cookiemunchies;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

public class ItemCreationActivity extends AppCompatActivity {

    AlertDialog.Builder alertDialogBuilder;
    Button button;
    EditText editTextItem;
    JSONObject postParams;
    private Endpoints endpoints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_creation);
        endpoints = new Endpoints(this);

        editTextItem = findViewById(R.id.editTextItem);

        button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String item = editTextItem.getText().toString();
                postParams = new JSONObject();
                try {
                    postParams.put("name", item);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                endpoints.addItem(postParams);
            }
        });


        alertDialogBuilder = new AlertDialog.Builder(ItemCreationActivity.this);
    }
}
