package com.example.rundraw_fe.mypage;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rundraw_fe.R;

import java.util.List;

public class MyPageCourseAdapter extends RecyclerView.Adapter<MyPageCourseAdapter.ViewHolder> {

    public static final int MODE_STATUS_DOT = 0; // 코스 모아보기
    public static final int MODE_COUNT = 1;       // 스크랩한 코스
    public static final int MODE_ICON = 2;        // 내가 그린 코스

    private final List<? extends MyPageCourseItem> courseList;
    private final int mode;

    public MyPageCourseAdapter(List<? extends MyPageCourseItem> courseList, int mode) {
        this.courseList = courseList;
        this.mode = mode;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_mypage_course, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MyPageCourseItem item = courseList.get(position);
        holder.tvCourseName.setText(item.getDisplayName());

        holder.statusDot.setVisibility(mode == MODE_STATUS_DOT ? View.VISIBLE : View.GONE);
        holder.tvCount.setVisibility(mode == MODE_COUNT ? View.VISIBLE : View.GONE);
        holder.ivAction.setVisibility(mode == MODE_ICON ? View.VISIBLE : View.GONE);

        if (mode == MODE_STATUS_DOT) {
            if (Boolean.TRUE.equals(item.getIsCompleted())) {
                holder.statusDot.setBackgroundResource(R.drawable.dot_green);
            }
        } else if (mode == MODE_COUNT) {
            Integer count = item.getCount();
            holder.tvCount.setText(count != null ? String.valueOf(count) : "0");
        }
        // MODE_ICON(내가 그린 코스)의 ivAction 아이콘 처리는 실제 공유 아이콘 리소스 확인 후 채울게요
    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCourseName;
        TextView tvCount;
        View statusDot;
        ImageView ivAction;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCourseName = itemView.findViewById(R.id.tvCourseName);
            tvCount = itemView.findViewById(R.id.tvCount);
            statusDot = itemView.findViewById(R.id.statusDot);
            ivAction = itemView.findViewById(R.id.ivAction);
        }
    }
}