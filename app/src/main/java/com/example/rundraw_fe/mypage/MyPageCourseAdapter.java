package com.example.rundraw_fe.mypage;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rundraw_fe.R;
import com.example.rundraw_fe.response.CourseRecordResponse;
import com.example.rundraw_fe.response.DraftCourseResponse;
import com.example.rundraw_fe.response.ScrapCourseResponse;

import java.util.List;

public class MyPageCourseAdapter extends RecyclerView.Adapter<MyPageCourseAdapter.ViewHolder> {

    public static final int MODE_STATUS_DOT = 0; // 코스 모아보기
    public static final int MODE_COUNT = 1;       // 스크랩한 코스
    public static final int MODE_ICON = 2;        // 내가 그린 코스

    private final List<?> courseList;
    private final int mode;

    public interface OnItemClickListener {
        void onItemClick(int position, String courseName);
    }

    private OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public MyPageCourseAdapter(List<?> courseList, int mode) {
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
        Object item = courseList.get(position);
        String courseName = "";

        if (item instanceof CourseRecordResponse) {
            courseName = ((CourseRecordResponse) item).getCourseName();
        } else if (item instanceof ScrapCourseResponse) {
            courseName = ((ScrapCourseResponse) item).getDisplayName();
        } else if (item instanceof DraftCourseResponse) { // 👈 이 부분을 추가해 줘야 해!
            courseName = ((DraftCourseResponse) item).getName();
        }else if (item != null) {
            courseName = item.toString();
        }

        holder.tvCourseName.setText(courseName);

        holder.statusDot.setVisibility(mode == MODE_STATUS_DOT ? View.VISIBLE : View.GONE);
        holder.tvCount.setVisibility(mode == MODE_COUNT ? View.VISIBLE : View.GONE);
        holder.ivAction.setVisibility(mode == MODE_ICON ? View.VISIBLE : View.GONE);

        String finalCourseName = courseName;
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(position, finalCourseName);
            }
        });
    }

    @Override
    public int getItemCount() {
        return courseList != null ? courseList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCourseName;
        TextView tvCount;
        View statusDot;
        ImageView ivAction;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCourseName = itemView.findViewById(R.id.tvCourseName);
            tvCount = itemView.findViewById(R.id.tvCount);
            statusDot = itemView.findViewById(R.id.statusDot);
            ivAction = itemView.findViewById(R.id.ivAction);
        }
    }
}