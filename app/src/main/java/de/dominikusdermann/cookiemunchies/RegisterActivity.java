package de.dominikusdermann.cookiemunchies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {

    Button submitButton;
    TextView firstName;
    TextView lastName;
    TextView eMail;
    TextView password;
    Endpoints endpoints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // import text fields
        firstName = findViewById(R.id.first_name_Text);
        lastName = findViewById(R.id.lastNameText);
        eMail = findViewById(R.id.emailText);
        password = findViewById(R.id.editText4);

        // set up endpoints
        endpoints = new Endpoints(this);

        // set up submit button
        submitButton = findViewById(R.id.submitRegistration);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: submit data
                // get all data from text fields
                String fname = firstName.getText().toString();
                String lname = lastName.getText().toString();
                String mail = eMail.getText().toString();
                String passwd = password.getText().toString();

                // put all data in JSON format for submitting
                JSONObject userData = new JSONObject();
                try {
                    userData.put("first_name", fname);
                    userData.put("last_name", lname);
                    userData.put("email", mail);
                    userData.put("password", passwd);
                } catch (Exception e) {
                    Log.d("JSON con exception:", e.toString());
                }

                Log.d("user data:", userData.toString());

                // submit JSON object to backend
                endpoints.addUser(userData);

                Intent login = new Intent(RegisterActivity.this, LoginActivity.class);
                RegisterActivity.this.startActivity(login);
            }
        });
    }
}
