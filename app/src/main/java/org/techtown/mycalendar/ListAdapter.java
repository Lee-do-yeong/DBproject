package org.techtown.mycalendar;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {
    List<Data> datas = null;
    ArrayList<Data> arrayList;
    Context context;

    public ListAdapter(Context context, ArrayList<Data> datas){
        this.context = context;
        this.datas = datas;
        arrayList = new ArrayList<Data>();
        arrayList.addAll(datas);
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = inflater.inflate(R.layout.list_item, viewGroup, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        Data data = datas.get(position);
        viewHolder.setItem(data);
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public void addItem(Data item) {
        datas.add(item);
    }

    public void setItems(ArrayList<Data> items) {
        this.datas = items;
    }

    public Data getItem(int position) {
        return datas.get(position);
    }

    public void setItem(int position, Data item) {
        datas.set(position, item);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView todo, date, time, location, memo;

        public ViewHolder(View itemView) {
            super(itemView);

            todo = itemView.findViewById(R.id.tv_todo);
            date = itemView.findViewById(R.id.tv_date);
            time = itemView.findViewById(R.id.tv_time);
            location = itemView.findViewById(R.id.tv_location);
            memo = itemView.findViewById(R.id.tv_memo);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION){
                        Intent intent = new Intent(v.getContext(), MapActivity.class);
                        intent.putExtra("position", position);
                        intent.putExtra("location", "전북대학교 " + location.getText().toString());
                        Log.d("###", "카드뷰 position: " + position);
                        v.getContext().startActivity(intent);
                    }
                }
            });
        }

        public void setItem(Data item) {
            todo.setText(item.getTodo());
            date.setText(item.getDate());
            time.setText(item.getTime());
            location.setText(item.getLocation());
            memo.setText(item.getMemo());
        }
    }
}
