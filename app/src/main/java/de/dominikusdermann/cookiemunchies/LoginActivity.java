package de.dominikusdermann.cookiemunchies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private Button loginButton;
    private Button registerButton;
    private EditText emailText;
    private EditText passwordText;
    private Authentication authenticator;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // set up edit text fields
        emailText = findViewById(R.id.email_view);
        passwordText = findViewById(R.id.password_view);

        // if user already has a token on their phone, skip the login screen and bring them directly into the app
        sharedPreferences = this.getSharedPreferences("de.dominikusdermann.cookiemunchies", Context.MODE_PRIVATE);
        String jwt = sharedPreferences.getString("jwt", "no-jwt");
        if ( jwt != "no-jwt") {
            Intent main = new Intent(this, MainActivity.class);
            this.startActivity(main);
        }

        // assign button functionality
        loginButton = findViewById(R.id.buttonLogin);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String emailProvided = emailText.getText().toString();
                    String passwordProvided = passwordText.getText().toString();

                    if (isValidEmail(emailProvided)) {
                        validateUser(emailProvided, passwordProvided);
                    } else {
                        Toast.makeText(LoginActivity.this, "That's not an email address.", Toast.LENGTH_SHORT).show();
                    }
                    
                } catch(Exception e) {
                    Log.d("Exception from input", e.toString());
                }
            }
        });

        registerButton = findViewById(R.id.registerButton);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent register = new Intent(LoginActivity.this, RegisterActivity.class);
                LoginActivity.this.startActivity(register);
            }
        });


    }

    private static boolean isValidEmail(CharSequence email) {
        if (email == null) {
            return false;
        }
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void validateUser(String email, String pass) {
        // set up authenticator
        JSONObject userData = new JSONObject();

        try {
            userData.put("email", email);
            userData.put("password", pass);
            this.authenticator = new Authentication(this);
            authenticator.logIn(userData);
        } catch (Exception e) {
            Toast.makeText(this, "whoops, something went wrong.", Toast.LENGTH_SHORT).show();
        }
    }
}
