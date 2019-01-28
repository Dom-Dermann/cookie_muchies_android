package de.dominikusdermann.cookiemunchies;

import android.app.ListActivity;
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
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static de.dominikusdermann.cookiemunchies.MainActivity.swipeRefreshLayout;

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

    public void getRequest(){
        String url = "https://cookie-munchies.herokuapp.com/api/items";
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.i("Response", "received");
                MainActivity.itemList.clear();
                try {
                    for (int i=0; i < response.length(); i++) {
                        JSONObject item = response.getJSONObject(i);
                        MainActivity.itemList.add(item);
                    }
                    // update recyclerView and stop showing refresh symbol
                    MainActivity.itemViewAdapter.notifyDataSetChanged();
                    MainActivity.swipeRefreshLayout.setRefreshing(false);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(mContext, "Could not get update.", Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(mContext, "Couldn't connect to server.", Toast.LENGTH_SHORT).show();
            }
        });
        VolleySingleton.getInstance(mContext.getApplicationContext()).addToRequestQueue(request);
    }

    public void postRequest(JSONObject postParams){
        String postURL = "https://cookie-munchies.herokuapp.com/api/items";

        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, postURL, postParams, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Toast.makeText(mContext, "Saved your new item", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        VolleySingleton.getInstance(mContext.getApplicationContext()).addToRequestQueue(postRequest);
    }

    // delete item
    public void deleteRequest(JSONObject obj){
        // get ID of object to be modified
        try {
            String _id = obj.getString("_id");
            String url = "https://cookie-munchies.herokuapp.com/api/items/" + _id;

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.DELETE, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Toast.makeText(mContext, "Item succfully deleted. Congrats!", Toast.LENGTH_SHORT).show();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    Toast.makeText(mContext, "Couldn't connect to server.", Toast.LENGTH_SHORT).show();
                }
            });
            VolleySingleton.getInstance(mContext.getApplicationContext()).addToRequestQueue(request);
        } catch (JSONException jse) {
            jse.printStackTrace();
            Toast.makeText(mContext, "Oh no, something went wrong", Toast.LENGTH_SHORT).show();
        }
    }

    // put information that task is done
    public void putRequest(JSONObject obj){
        // get ID of object to be modified
        try {
            String _id = obj.getString("_id");
            String url = "https://cookie-munchies.herokuapp.com/api/items/" + _id;
            Boolean done = obj.getBoolean("isDone");

            if (done == false) {
                obj.put("isDone", true);
            } else {
                obj.put("isDone", false);
            }

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, url, obj, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Toast.makeText(mContext, "Item completed. Congrats!", Toast.LENGTH_SHORT).show();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    Toast.makeText(mContext, "Couldn't connect to server.", Toast.LENGTH_SHORT).show();
                }
            });
            VolleySingleton.getInstance(mContext.getApplicationContext()).addToRequestQueue(request);
        } catch (JSONException jse) {
            jse.printStackTrace();
            Toast.makeText(mContext, "Oh no, something went wrong", Toast.LENGTH_SHORT).show();
        }
    }
}
