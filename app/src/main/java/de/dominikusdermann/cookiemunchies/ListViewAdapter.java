package de.dominikusdermann.cookiemunchies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;

public class ListViewAdapter extends RecyclerView.Adapter<ListViewAdapter.ViewHolder> {

    Context mContext;
    ArrayList<JSONObject> mListArray;
    SharedPreferences sharedPreferences;


    public ListViewAdapter(Context context, ArrayList<JSONObject> listArray) {
        this.mContext = context;
        this.mListArray = listArray;
        sharedPreferences = mContext.getSharedPreferences("de.dominikusdermann.cookiemunchies", Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_row, viewGroup, false);
        ListViewAdapter.ViewHolder vh = new ListViewAdapter.ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {
        String listOwner;
        try {
            listOwner = mListArray.get(i).getString("name");
            viewHolder.listText.setText(listOwner + "s List");
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        viewHolder.backgroundView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String list_id = mListArray.get(i).getString("list_id");
                    sharedPreferences.edit().putString("currentUserList", list_id).apply();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Intent main = new Intent(mContext, MainActivity.class);
                mContext.startActivity(main);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mListArray.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView listText;
        View backgroundView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            listText = itemView.findViewById(R.id.listNameView);
            backgroundView = itemView.findViewById(R.id.backgroundView);
        }
    }

}
