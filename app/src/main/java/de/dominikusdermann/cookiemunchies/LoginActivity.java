package de.dominikusdermann.cookiemunchies;

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
    private EditText emailText;
    private EditText passwordText;
    private Authentication authenticator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // set up edit text fields
        emailText = findViewById(R.id.email_view);
        passwordText = findViewById(R.id.password_view);

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
            Toast.makeText(this, "upps, something went wrong.", Toast.LENGTH_SHORT).show();
        }
    }
}
