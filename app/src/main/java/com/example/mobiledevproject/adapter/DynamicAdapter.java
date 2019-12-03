package com.example.mobiledevproject.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobiledevproject.R;
import com.example.mobiledevproject.model.MessageBean;

import java.util.List;

public class DynamicAdapter extends RecyclerView.Adapter<DynamicAdapter.ViewHolder> {
    private Context context;
    private List<MessageBean> data;
    private LayoutInflater inflater;
    private BitmapAdapter bitmapAdapter;

    public DynamicAdapter(Context context, List<MessageBean> data,BitmapAdapter bitmapAdapter) {
        this.context = context;
        this.data = data;
        this.bitmapAdapter = bitmapAdapter;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.fragment_dynamic,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        viewHolder.content.setText(data.get(position).getContent());
        viewHolder.images.setAdapter(bitmapAdapter);
        viewHolder.time.setText(data.get(position).getTime());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView content;
        private GridView images;
        private TextView time;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            content = itemView.findViewById(R.id.dynamic_content_tv);
            images = itemView.findViewById(R.id.dynamic_images_gv);
            time = itemView.findViewById(R.id.dynamic_time_tv);
        }
    }

}
