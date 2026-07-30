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

    private final List<String> courseList;
    private final int mode;

    public MyPageCourseAdapter(List<String> courseList, int mode) {
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
        holder.tvCourseName.setText(courseList.get(position));

        holder.statusDot.setVisibility(mode == MODE_STATUS_DOT ? View.VISIBLE : View.GONE);
        holder.tvCount.setVisibility(mode == MODE_COUNT ? View.VISIBLE : View.GONE);
        holder.ivAction.setVisibility(mode == MODE_ICON ? View.VISIBLE : View.GONE);
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