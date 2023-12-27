package com.example.aram;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PostViewHolder extends RecyclerView.ViewHolder {
    public ImageView post_image, emoticon, bookmark;
    public TextView username, likes, origin, origin_writer, postdate,description;

    public PostViewHolder(@NonNull View itemView){
        super(itemView);

        post_image = itemView.findViewById(R.id.post_image);
        //emoticon = itemView.findViewById(R.id.emoticon);    //이미지
        bookmark = itemView.findViewById(R.id.bookmark);
        username = itemView.findViewById(R.id.username);
        likes = itemView.findViewById(R.id.likes);  //얼마나 좋아하는지 수치
        origin = itemView.findViewById(R.id.origin);
        origin_writer = itemView.findViewById(R.id.origin_writer);
        postdate = itemView.findViewById(R.id.postdate);
        description = itemView.findViewById(R.id.description);

    }
}
