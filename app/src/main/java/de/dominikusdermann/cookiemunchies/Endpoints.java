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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class Endpoints {

    Context mContext;
    private SharedPreferences sharedPreferences;
    String jwt;

    public Endpoints(Context c){
        this.mContext = c;
        sharedPreferences = mContext.getSharedPreferences("de.dominikusdermann.cookiemunchies", Context.MODE_PRIVATE);
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
        String currentUserList = sharedPreferences.getString("currentUserList", "no ID");
        Log.i("Current User List : ", currentUserList);
        String url = "https://cookie-munchies.herokuapp.com/api/lists/" + currentUserList;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,  new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i("Server response: ", response.toString());
                MainActivity.itemList.clear();
                try {
                    JSONArray items = response.getJSONArray("items");
                    for( int i = 0; i < items.length(); i++) {
                        MainActivity.itemList.add(items.getJSONObject(i));
                    }
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
}
