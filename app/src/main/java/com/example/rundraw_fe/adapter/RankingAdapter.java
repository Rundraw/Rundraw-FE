package com.example.rundraw_fe.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rundraw_fe.R;
import com.example.rundraw_fe.response.RankingResponse;

import java.util.ArrayList;
import java.util.List;


public class RankingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private List<RankingResponse.Course> courseList =
            new ArrayList<>();


    private final OnCourseClickListener listener;
    private boolean showTop3;


    private static final int TYPE_TOP3 = 0;
    private static final int TYPE_NORMAL = 1;



    public interface OnCourseClickListener {

        void onClick(Long courseId);

    }



    public RankingAdapter(
            OnCourseClickListener listener,
            boolean showTop3
    ){
        this.listener = listener;
        this.showTop3 = showTop3;
    }



    public void setCourseList(
            List<RankingResponse.Course> courseList
    ){

        this.courseList = courseList;

        notifyDataSetChanged();

    }



    @Override
    public int getItemViewType(int position) {

        if(showTop3 && position == 0){

            return TYPE_TOP3;

        }

        return TYPE_NORMAL;
    }



    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ){

        if(viewType == TYPE_TOP3){

            View view =
                    LayoutInflater.from(parent.getContext())
                            .inflate(
                                    R.layout.top3_header,
                                    parent,
                                    false
                            );

            return new Top3ViewHolder(view);

        }


        View view =
                LayoutInflater.from(parent.getContext())
                        .inflate(
                                R.layout.ranking_item,
                                parent,
                                false
                        );


        return new NormalViewHolder(view);

    }




    @Override
    public void onBindViewHolder(
            @NonNull RecyclerView.ViewHolder holder,
            int position
    ){


        // =========================
        // TOP 3
        // =========================

        if(holder instanceof Top3ViewHolder){


            Top3ViewHolder topHolder =
                    (Top3ViewHolder) holder;


            int topCount =
                    Math.min(3, courseList.size());



            for(int i = 0; i < topCount; i++){


                RankingResponse.Course course =
                        courseList.get(i);



                // 순위 표시
                topHolder.ranks[i]
                        .setText(
                                String.valueOf(i + 1)
                        );



                topHolder.names[i]
                        .setText(
                                course.getName()
                        );


                topHolder.counts[i]
                        .setText(
                                String.valueOf(
                                        course.getExperienceCount()
                                )
                        );



                int index = i;



                topHolder.layouts[i]
                        .setOnClickListener(v -> {


                            if(listener != null){

                                listener.onClick(
                                        courseList.get(index).getId()
                                );

                            }

                        });


            }


            return;

        }





        // =========================
        // 일반 리스트 (4위부터)
        // =========================


        NormalViewHolder normal =
                (NormalViewHolder) holder;



        int realPosition;

        if(showTop3){

            realPosition = position + 2;

        }else{

            realPosition = position;

        }



        RankingResponse.Course course =
                courseList.get(realPosition);



        normal.rankNumber
                .setText(
                        String.valueOf(realPosition + 1)
                );



        normal.courseName
                .setText(
                        course.getName()
                );



        normal.count
                .setText(
                        String.valueOf(
                                course.getExperienceCount()
                        )
                );



        normal.itemView.setOnClickListener(v -> {


            if(listener != null){

                listener.onClick(
                        course.getId()
                );

            }

        });


    }





    @Override
    public int getItemCount() {

        if(courseList.isEmpty()){
            return 0;
        }


        if(!showTop3){

            return courseList.size();

        }


        if(courseList.size() <= 3){

            return 1;

        }


        return courseList.size() - 2;
    }






    // =========================
    // TOP3 ViewHolder
    // =========================


    static class Top3ViewHolder
            extends RecyclerView.ViewHolder {


        TextView[] ranks = new TextView[3];

        TextView[] names = new TextView[3];

        TextView[] counts = new TextView[3];

        View[] layouts = new View[3];



        public Top3ViewHolder(
                @NonNull View itemView
        ){

            super(itemView);



            // 순위 원
            ranks[0] =
                    itemView.findViewById(R.id.topRank1);

            ranks[1] =
                    itemView.findViewById(R.id.topRank2);

            ranks[2] =
                    itemView.findViewById(R.id.topRank3);




            names[0] =
                    itemView.findViewById(R.id.topName1);

            names[1] =
                    itemView.findViewById(R.id.topName2);

            names[2] =
                    itemView.findViewById(R.id.topName3);




            counts[0] =
                    itemView.findViewById(R.id.topCount1);

            counts[1] =
                    itemView.findViewById(R.id.topCount2);

            counts[2] =
                    itemView.findViewById(R.id.topCount3);




            layouts[0] =
                    itemView.findViewById(R.id.top1Layout);

            layouts[1] =
                    itemView.findViewById(R.id.top2Layout);

            layouts[2] =
                    itemView.findViewById(R.id.top3Layout);

        }

    }






    // =========================
    // 일반 ViewHolder
    // =========================


    static class NormalViewHolder
            extends RecyclerView.ViewHolder {


        TextView rankNumber;

        TextView courseName;

        TextView count;



        public NormalViewHolder(
                @NonNull View itemView
        ){

            super(itemView);



            rankNumber =
                    itemView.findViewById(R.id.rankNumber);



            courseName =
                    itemView.findViewById(R.id.courseName);



            count =
                    itemView.findViewById(R.id.count);

        }

    }

}