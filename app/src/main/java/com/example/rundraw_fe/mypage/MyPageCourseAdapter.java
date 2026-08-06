package com.example.rundraw_fe.mypage;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
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

    // 공유 아이콘(ivAction) 클릭 시 호출되는 리스너 (내가 그린 코스 모드 전용)
    public interface OnShareClickListener {
        void onShareClick(int position);
    }

    private OnItemClickListener listener;
    private OnShareClickListener shareClickListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setOnShareClickListener(OnShareClickListener shareClickListener) {
        this.shareClickListener = shareClickListener;
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
        } else if (item instanceof DraftCourseResponse) {
            courseName = ((DraftCourseResponse) item).getName();
        } else if (item != null) {
            courseName = item.toString();
        }

        holder.tvCourseName.setText(courseName);

        holder.statusDot.setVisibility(mode == MODE_STATUS_DOT ? View.VISIBLE : View.GONE);
        holder.tvCount.setVisibility(mode == MODE_COUNT ? View.VISIBLE : View.GONE);
        holder.ivAction.setVisibility(mode == MODE_ICON ? View.VISIBLE : View.GONE);

        // 내가 그린 코스 모드: 공유 아이콘 상태(tint) 반영 + 클릭 리스너 연결
        if (mode == MODE_ICON && item instanceof DraftCourseResponse) {
            DraftCourseResponse draft = (DraftCourseResponse) item;
            boolean isSharing = Boolean.TRUE.equals(draft.getIsSharing());

            holder.ivAction.setImageResource(R.drawable.ic_share);
            holder.ivAction.setColorFilter(
                    ContextCompat.getColor(
                            holder.itemView.getContext(),
                            isSharing ? R.color.share_active_green : R.color.share_inactive_gray
                    )
            );

            holder.ivAction.setOnClickListener(v -> {
                if (shareClickListener != null) {
                    shareClickListener.onShareClick(position);
                }
            });
        }

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