package com.example.rundraw_fe.mypage;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rundraw_fe.R;
import com.example.rundraw_fe.response.MypageCommentResponse;

import java.util.List;

public class MyPageCommentAdapter extends RecyclerView.Adapter<MyPageCommentAdapter.ViewHolder> {

    private final List<MypageCommentResponse> commentList;

    public MyPageCommentAdapter(List<MypageCommentResponse> commentList) {
        this.commentList = commentList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_mypage_comment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MypageCommentResponse comment = commentList.get(position);
        holder.tvContent.setText(comment.getContent());
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvContent;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvContent = itemView.findViewById(R.id.tvCommentContent);
        }
    }
}