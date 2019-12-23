package de.dominikusdermann.cookiemunchies;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import org.json.JSONException;
import org.json.JSONObject;

public class ItemCreationActivity extends AppCompatActivity {

    AlertDialog.Builder alertDialogBuilder;
    Button button;
    EditText editTextItem;
    JSONObject postParams;
    private Endpoints endpoints;
    private AdView adView;

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

        // initialize ads
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {

            }
        });

        // load ads
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }
}
