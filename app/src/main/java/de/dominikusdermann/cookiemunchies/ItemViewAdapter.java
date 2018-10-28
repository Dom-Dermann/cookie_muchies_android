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
    Endpoints endpoints;

    public ItemViewAdapter(Context context, ArrayList<JSONObject> arrayList){
        this.mContext = context;
        this.mList = arrayList;
        endpoints = new Endpoints(context);
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
                endpoints.deleteRequest(mList.get(position));
                return false;
            }
        });
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                // TODO: put the item with changed idDone state
                endpoints.putRequest(mList.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
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
