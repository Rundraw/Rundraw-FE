package com.example.rundraw_fe;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rundraw_fe.response.RankingResponse;

import java.util.ArrayList;
import java.util.List;


public class RankingAdapter extends RecyclerView.Adapter<RankingAdapter.ViewHolder> {


    private List<RankingResponse.Course> courseList =
            new ArrayList<>();


    public void setCourseList(
            List<RankingResponse.Course> courseList
    ){
        this.courseList = courseList;
        notifyDataSetChanged();
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        View view =
                LayoutInflater.from(parent.getContext())
                        .inflate(
                                R.layout.ranking_item,
                                parent,
                                false
                        );

        return new ViewHolder(view);
    }



    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position
    ) {

        RankingResponse.Course course =
                courseList.get(position);


        holder.rankNumber.setText(
                String.valueOf(position + 1)
        );


        holder.courseName.setText(
                course.getName()
        );


        holder.count.setText(
                String.valueOf(
                        course.getExperienceCount()
                )
        );

    }



    @Override
    public int getItemCount() {
        return courseList.size();
    }



    static class ViewHolder extends RecyclerView.ViewHolder {


        TextView rankNumber;
        TextView courseName;
        TextView count;



        public ViewHolder(
                @NonNull View itemView
        ) {
            super(itemView);


            rankNumber =
                    itemView.findViewById(
                            R.id.rankNumber
                    );


            courseName =
                    itemView.findViewById(
                            R.id.courseName
                    );


            count =
                    itemView.findViewById(
                            R.id.count
                    );
        }
    }
}