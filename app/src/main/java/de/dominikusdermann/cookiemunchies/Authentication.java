package de.dominikusdermann.cookiemunchies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Authentication {

    private Context mContext;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor spEditor;
    private String jwt;

    public Authentication(Context c) {
        this.mContext = c;
        sharedPreferences = mContext.getSharedPreferences("de.dominikusdermann.cookiemunchies", Context.MODE_PRIVATE);
        spEditor = sharedPreferences.edit();
        jwt = sharedPreferences.getString("jwt", "no-jwt");
    }

    public void logIn(JSONObject userData) {
        String url = "https://cookie-munchies.herokuapp.com/api/auth";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, userData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String jwt = response.getString("jwt");
                    spEditor.putString("jwt", jwt);
                    spEditor.commit();
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("jwt not found", e.toString());
                }
                whoAmI();
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
    }

    public void whoAmI() {
        String url = "https://cookie-munchies.herokuapp.com/api/users/me";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String listId = response.getString("ownsList");
                    spEditor.putString("currentUserList", listId).commit();
                } catch (Exception e) {
                    Log.e("Who am I: ", e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Who am I: ", error.toString());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("x-auth-token", jwt);
                return params;
            }
        };
        VolleySingleton.getInstance(mContext.getApplicationContext()).addToRequestQueue(request);
    }
}
