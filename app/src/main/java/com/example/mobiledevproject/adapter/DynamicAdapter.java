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

import java.io.File;
import java.util.List;

public class DynamicAdapter extends RecyclerView.Adapter<DynamicAdapter.ViewHolder> {
    private Context context;
    private List<MessageBean> messageList;
    private LayoutInflater inflater;
    private PhotoAdapter photoAdapter;

    public DynamicAdapter(Context context, List<MessageBean> messageList) {
        this.context = context;
        this.messageList = messageList;
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
        viewHolder.content.setText(messageList.get(position).getContent());

        //首先本地获取，本地获取失败从网上获取  。。逻辑不对之后再改
        List<String> localImagePaths = messageList.get(position).getLocalImagePath();
        List<String> onlineImagePaths = messageList.get(position).getOnlineImagePath();
        List<String> imagePaths;
        imagePaths = messageList.get(position).getLocalImagePath();
        for(int i = 0;onlineImagePaths != null && i < imagePaths.size() - 1;++i) {
            if(!new File(imagePaths.get(i)).exists())
                imagePaths.set(i,onlineImagePaths.get(i));
        }
        System.out.println("--------------------");
        System.out.println(imagePaths.size());
        if(imagePaths.size() == 0) {
            imagePaths = messageList.get(position).getOnlineImagePath();

        }
        photoAdapter = new PhotoAdapter(this.context,imagePaths);
        viewHolder.images.setAdapter(photoAdapter);

        viewHolder.time.setText(messageList.get(position).getTime());
    }

    @Override
    public int getItemCount() {
        return messageList.size();
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
