package com.example.mobiledevproject.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.mobiledevproject.R;

import java.util.List;

public class PhotoAdapter extends BaseAdapter {
    private Context context;
    private List<String> data;
    private LayoutInflater inflater;


    public PhotoAdapter(Context context, List<String> data) {
        this.context = context;
        this.data = data;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        if(data == null)
            return 0;
        //处理图片满了之后的加号
        if(data.size() == 7){
            return data.size() - 1;
        }
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
            convertView = inflater.inflate(R.layout.fragment_griditem,null);
            viewHolder = new ViewHolder();
            viewHolder.imageView = convertView.findViewById(R.id.iv_image);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final String path=data.get(position);
        if (path.equals("PHOTO_TAKING")){
            viewHolder.imageView.setImageResource(R.drawable.ic_checkin_photo_taking_24dp);
        }else {
            RequestOptions options = new RequestOptions()
                    .centerCrop();
            Glide.with(context)
                    .load(path)
                    .apply(options)
                    .into(viewHolder.imageView);
        }
        return convertView;
    }
    private class ViewHolder {
        private ImageView imageView;
    }
}
