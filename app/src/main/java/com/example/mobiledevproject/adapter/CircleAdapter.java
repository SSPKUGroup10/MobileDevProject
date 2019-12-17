package com.example.mobiledevproject.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.mobiledevproject.R;
import com.example.mobiledevproject.model.User;
import com.example.mobiledevproject.model.UserBean;

import java.util.List;

public class CircleAdapter extends BaseAdapter {
    List<UserBean> data;
    LayoutInflater inflater;
    Context context;

    public CircleAdapter(Context context, List<UserBean> data) {
        this.context = context;
        this.data = data;
        this.inflater = LayoutInflater.from(context);
    }


    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.fragment_circle_item, null);
            viewHolder = new ViewHolder();
            viewHolder.imageView = convertView.findViewById(R.id.cirlce_iv);
            viewHolder.textView = convertView.findViewById(R.id.circle_tv);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.textView.setText(data.get(position).getName());

        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.ic_circle_panda_24dp)
                .error(R.drawable.ic_circle_panda_24dp);

        Glide.with(context)
               // .load(data.get(position).getImagePath())
                .load(R.drawable.ic_circle_panda_24dp)
                .apply(options)
                .into(viewHolder.imageView);
        return convertView;
    }

    private class ViewHolder {
        ImageView imageView;
        TextView textView;
    }
}
