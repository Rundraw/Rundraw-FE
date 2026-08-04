package com.example.rundraw_fe.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.rundraw_fe.api.CourseApiService.CourseSummaryDto;

import java.util.ArrayList;
import java.util.List;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.ViewHolder> {

    public interface OnItemClick { void onClick(CourseSummaryDto course); }

    private List<CourseSummaryDto> items = new ArrayList<>();
    private final OnItemClick listener;

    public CourseAdapter(OnItemClick listener) { this.listener = listener; }

    public void setItems(List<CourseSummaryDto> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_2, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CourseSummaryDto course = items.get(position);
        holder.title.setText(course.getName());
        holder.subtitle.setText("체험 " + course.getExperienceCount() + "회");
        holder.itemView.setOnClickListener(v -> listener.onClick(course));
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, subtitle;
        ViewHolder(View v) {
            super(v);
            title = v.findViewById(android.R.id.text1);
            subtitle = v.findViewById(android.R.id.text2);
        }
    }
}
