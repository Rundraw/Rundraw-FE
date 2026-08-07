package com.example.rundraw_fe.mypage;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rundraw_fe.R;
import com.example.rundraw_fe.api.RankingApi;
import com.example.rundraw_fe.auth.RetrofitClient;
import com.example.rundraw_fe.response.ApiResponse;
import com.example.rundraw_fe.response.MypageCommentResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


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

        holder.tvDelete.setOnClickListener(v -> {
            int currentPosition = holder.getAdapterPosition();
            if (currentPosition == RecyclerView.NO_POSITION) {
                return;
            }

            Long commentId = commentList.get(currentPosition).getCommentId();
            RankingApi apiService = RetrofitClient.getInstance(v.getContext()).create(RankingApi.class);
            apiService.deleteMyComment(commentId).enqueue(new Callback<ApiResponse<Object>>() {
                @Override
                public void onResponse(Call<ApiResponse<Object>> call, Response<ApiResponse<Object>> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        commentList.remove(currentPosition);
                        notifyItemRemoved(currentPosition);
                        notifyItemRangeChanged(currentPosition, commentList.size());
                        Log.d("DeleteComment", "댓글 삭제 성공");
                    } else {
                        Log.e("DeleteComment", "댓글 삭제 실패 : " + response.code());
                    }
                }
                @Override
                public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {
                    Log.e("DeleteComment", "네트워크 오류 : " + t.getMessage());
                }
            });
        });
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvContent;
        TextView tvDelete;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvContent = itemView.findViewById(R.id.tvCommentContent);
            tvDelete = itemView.findViewById(R.id.tvDelete);
        }
    }
}