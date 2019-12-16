package com.example.mobiledevproject.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobiledevproject.R;
import com.example.mobiledevproject.activity.GroupActivity;
import com.example.mobiledevproject.config.API;
import com.example.mobiledevproject.config.StorageConfig;
import com.example.mobiledevproject.model.Group;
import com.example.mobiledevproject.model.GroupCreate;
import com.example.mobiledevproject.model.User;
import com.example.mobiledevproject.util.HttpUtil;
import com.example.mobiledevproject.util.StatusCodeUtil;
import com.example.mobiledevproject.util.Utility;
import com.google.android.material.card.MaterialCardView;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ListRcvAdapter extends RecyclerView.Adapter<ListRcvAdapter.GroupViewHolder> {

    private static final String TAG = "ListRcvAdapter";

    private Context context;
    private List<GroupCreate> infoList;

    public ListRcvAdapter(Context context, List<GroupCreate> infoList) {
        this.context = context;
        this.infoList = infoList;
    }

    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_group, parent, false);

        return new GroupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupViewHolder holder, int position) {

        GroupCreate group = infoList.get(position);

        holder.groupNameTv.setText(group.getGroupName());
        holder.descriptionTv.setText(group.getDescription());

        //  可以封装一下
        holder.groupMvc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick: "+position);
                GroupCreate groupSrc = infoList.get(position);
                int groupId = groupSrc.getGroupId();
                Log.i(TAG, "onClick: groupid="+groupId);
                String token = Utility.getData(context, StorageConfig.SP_KEY_TOKEN);
                String url = API.CIRCLE+groupId+"/members/";
                Log.i(TAG, "onClick: "+url);
                Group group = new Group(groupSrc);


                HttpUtil.getRequestWithToken(url, token, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String responseBody = response.body().string();
                        Log.i(TAG, "onResponse: "+responseBody);
                        JsonObject jsonObject;
                        if ((jsonObject = StatusCodeUtil.isNormalResponse(responseBody)) != null) {
                            int status = jsonObject.get("status").getAsInt();
                            if (StatusCodeUtil.isNormalStatus(status)) {
                                //  正确

                                JsonArray data = jsonObject.get("data").getAsJsonArray();
                                for(JsonElement member:data){
                                    JsonObject cur = member.getAsJsonObject();
                                    User user = new User();
                                    user.setUserId(cur.get("UserID").getAsInt());
                                    user.setUserName(cur.get("Username").getAsString());
                                    group.getMemberList().add(user);
                                }


                                Intent intent = new Intent(context, GroupActivity.class);
                                intent.putExtra("group_info", group);
                                context.startActivity(intent);

                            } else {
                                Log.i(TAG, "onResponse: " + status);
                            }
                        } else {
                            Log.i(TAG, "onResponse: 响应内容错误");
                        }
                    }
                });


            }
        });
    }

    @Override
    public int getItemCount() {
        return infoList.size();
    }

    //  添加数据
    public void addData(GroupCreate createdGroup, int position){
        infoList.add(position, createdGroup);
        Log.i(TAG, "addData: new item");
        notifyItemInserted(position);
    }

    public void addData(int position){
        infoList.add(position, new GroupCreate("te", "te"));
        Log.i(TAG, "addData: new item");
        notifyItemInserted(position);
    }


    public static class GroupViewHolder extends RecyclerView.ViewHolder{
        TextView groupNameTv;
        TextView descriptionTv;
        MaterialCardView groupMvc;
        View itemView;

        public GroupViewHolder(View itemView){
            super(itemView);
            this.itemView = itemView;
            groupMvc = itemView.findViewById(R.id.mcv_group_name);
            groupNameTv = itemView.findViewById(R.id.tv_group_name);
            descriptionTv = itemView.findViewById(R.id.tv_group_description);
        }
    }

}
