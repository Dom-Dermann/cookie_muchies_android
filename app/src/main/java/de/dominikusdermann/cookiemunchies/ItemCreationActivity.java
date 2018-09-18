package de.dominikusdermann.cookiemunchies;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ItemCreationActivity extends AppCompatActivity {

    AlertDialog.Builder alertDialogBuilder;
    Button button;
    EditText editTextItem;
    JSONObject postParams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_creation);

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
                postRequest();
            }
        });


        alertDialogBuilder = new AlertDialog.Builder(ItemCreationActivity.this);
    }

    public void postRequest(){
        String postURL = "https://cookie-munchies.herokuapp.com/api/items";

        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, postURL, postParams, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                alertDialogBuilder.setTitle("Server response")
                        .setMessage("Response: " + response.toString())
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // TODO
                            }
                        })
                        .show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(postRequest);
    }
}
