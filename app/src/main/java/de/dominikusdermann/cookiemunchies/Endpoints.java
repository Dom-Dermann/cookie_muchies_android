package de.dominikusdermann.cookiemunchies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class Endpoints {

    Context mContext;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor prefEditor;
    String jwt;

    public Endpoints(Context c){
        this.mContext = c;
        sharedPreferences = mContext.getSharedPreferences("de.dominikusdermann.cookiemunchies", Context.MODE_PRIVATE);
        prefEditor = sharedPreferences.edit();
        this.jwt = sharedPreferences.getString("jwt", "no-jwt");
    }

    public void getAllLists() {
        String url = "https://cookie-munchies.herokuapp.com/api/lists/all";

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,  new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                Log.i("get all lists:", response.toString());
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject listObject = response.getJSONObject(i);
                        // get id of each list
                        String list_id = listObject.getString("_id");
                        JSONObject owner = listObject.getJSONObject("owner");
                        // first name of the list owner
                        String first_name = owner.getString("first_name");

                        // put owner and list together
                        JSONObject owner_and_id = new JSONObject();
                        owner_and_id.put("name", first_name);
                        owner_and_id.put("list_id", list_id);

                        // add the new object to array list. Structure:
                        //  {
                        //      name: Cosima,
                        //      list_id: 5c2f7cc1fab39d0004d96b4d
                        //  }

                        ListsActivity.listArray.add(owner_and_id);
                        ListsActivity.listViewAdapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("Server error: ", error.toString());
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

    public void getList() {
        // check internet state
        boolean networkState = isNetworkAvailable();
        if (networkState) {
            String currentUserList = sharedPreferences.getString("currentUserList", "no ID");
            String url = "https://cookie-munchies.herokuapp.com/api/lists/" + currentUserList;

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    MainActivity.itemList.clear();
                    try {
                        JSONArray items = response.getJSONArray("items");

                        for (int i = 0; i < items.length(); i++) {
                            MainActivity.itemList.add(items.getJSONObject(i));
                        }
                        // save data to shared preferences as JSON format
                        prefEditor.putString("offlineList", items.toString());
                        prefEditor.apply();

                        MainActivity.itemViewAdapter.notifyDataSetChanged();
                        MainActivity.swipeRefreshLayout.setRefreshing(false);
                    } catch (Exception e) {
                        Log.e("getList: ", e.toString());
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.i("Server error: ", error.toString());
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
        } else {
            // do this when internet access is not available
            // get the list from data saved in shared preferences
            Toast.makeText(mContext, "No internet connection detected. You may be looking at old data.", Toast.LENGTH_SHORT).show();
            // get list from shared preferences and provide it to MainActivities's itemList instead
            String itemList = sharedPreferences.getString("offlineList", null);
            try {
                MainActivity.itemList.clear();
                JSONObject items = new JSONObject(itemList);
                JSONArray jArray = items.getJSONArray("values");
                for (int i=0; i < jArray.length(); i++){
                    JSONObject item = jArray.getJSONObject(i).getJSONObject("nameValuePairs");
                    MainActivity.itemList.add(item);
                }

                MainActivity.itemViewAdapter.notifyDataSetChanged();
                MainActivity.swipeRefreshLayout.setRefreshing(false);
            } catch (Exception e) {
                Log.d("Error in SP", e.toString());
            }
        }
    }

    public void addItem(JSONObject item) {
        String currentUserList = sharedPreferences.getString("currentUserList", "no ID");
        String url = "https://cookie-munchies.herokuapp.com/api/items/" + currentUserList;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, item,  new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i("Server response: ", response.toString());
                Toast.makeText(mContext, "Item successfully added", Toast.LENGTH_SHORT).show();
                Intent main = new Intent(mContext, MainActivity.class);
                mContext.startActivity(main);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(mContext, "Oh no, something went wrong.", Toast.LENGTH_SHORT).show();
                error.printStackTrace();
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

    public void deleteItem(String itemID) {
        String currentUserList = sharedPreferences.getString("currentUserList", "no ID");
        String url = "https://cookie-munchies.herokuapp.com/api/items/" + currentUserList + "/" + itemID;
        Log.i("Delte URL: ", url);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.DELETE, url, null,  new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Toast.makeText(mContext, "successfully deleted" + response.toString(), Toast.LENGTH_SHORT).show();
                getList();
                MainActivity.itemViewAdapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("Server error: ", error.toString());
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

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}


