package de.dominikusdermann.cookiemunchies;

import android.content.ClipData;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ItemViewAdapter extends RecyclerView.Adapter<ItemViewAdapter.ViewHolder>{

    Context mContext;
    ArrayList<JSONObject> mList;

    public ItemViewAdapter(Context context, ArrayList<JSONObject> arrayList){
        this.mContext = context;
        this.mList = arrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row, parent, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        try {
            String name = mList.get(position).getString("name");
            Boolean done = mList.get(position).getBoolean("isDone");
            holder.itemText.setText(name);
            holder.checkBox.setChecked(done);
        } catch (JSONException je) {
            je.printStackTrace();
            Toast.makeText(mContext, "Couldn't load an item.", Toast.LENGTH_SHORT).show();
        }

        holder.itemText.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                // TODO: implement long click delete
                deleteRequest(mList.get(position));
                return false;
            }
        });
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                // TODO: put the item with changed idDone state
                try {
                    putRequest(mList.get(position), mList.get(position).getBoolean("isDone"));
                } catch (JSONException je) {
                    je.printStackTrace();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    // put information that task is done
    public void putRequest(JSONObject obj, Boolean done){
        // get ID of object to be modified
        try {
            String _id = obj.getString("_id");
            String url = "https://cookie-munchies.herokuapp.com/api/items/" + _id;

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



    public static class ViewHolder  extends RecyclerView.ViewHolder {

        TextView itemText;
        CheckBox checkBox;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemText = itemView.findViewById(R.id.itemText);
            checkBox = itemView.findViewById(R.id.checkBox);
        }
    }
}
