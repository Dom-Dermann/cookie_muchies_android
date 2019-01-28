package de.dominikusdermann.cookiemunchies;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class InfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        ImageButton goOnline = findViewById(R.id.goOnlineButton);
        goOnline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("https://dom-dermann.github.io/cookie_munchies/login");
                Intent goOnline = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(goOnline);
            }
        });
    }
}
