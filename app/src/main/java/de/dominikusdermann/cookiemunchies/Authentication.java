package de.dominikusdermann.cookiemunchies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.security.spec.ECField;

public class Authentication {

    private Context mContext;
    private SharedPreferences sharedPreferences;

    public Authentication(Context c) {
        this.mContext = c;
    }

    public JSONObject logIn(JSONObject userData) {
        String url = "http://10.0.2.2:3223/api/auth";
        JSONObject res = new JSONObject();
        sharedPreferences = mContext.getSharedPreferences("de.dominikusdermann.cookiemunchies", Context.MODE_PRIVATE);
        final SharedPreferences.Editor spEditor = sharedPreferences.edit();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, userData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("Pure response: ", response.toString());
                try {
                    String jwt = response.getString("jwt");
                    spEditor.putString("jwt", jwt);
                    spEditor.commit();
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("jwt not found", e.toString());
                }
                Intent main = new Intent(mContext, MainActivity.class);
                mContext.startActivity(main);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(mContext, "Invalid username or password", Toast.LENGTH_SHORT).show();
            }
        });
        VolleySingleton.getInstance(mContext.getApplicationContext()).addToRequestQueue(request);
        return res;
    }
}
