package de.dominikusdermann.cookiemunchies;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static de.dominikusdermann.cookiemunchies.MainActivity.swipeRefreshLayout;

public class Endpoints {

    Context mContext;

    public Endpoints(Context c){
        this.mContext = c;
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
