package com.example.rundraw_fe;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rundraw_fe.response.RestaurantResponse;

import java.util.ArrayList;
import java.util.List;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.ViewHolder> {

    private List<RestaurantResponse> restaurantList = new ArrayList<>();

    // 서버에서 받아온 맛집 리스트를 어댑터에 꽂아주는 함수
    public void setRestaurantList(List<RestaurantResponse> restaurantList) {
        this.restaurantList = restaurantList;
        notifyDataSetChanged(); // 데이터가 바뀌었다고 화면에 알려줌
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 아이템 하나하나를 그려줄 XML 레이아웃 (일단 기존에 있는 ranking_item을 쓰거나 맛집용 item xml을 만들면 돼!)
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ranking_item, parent, false); // 임시로 ranking_item 활용 가능
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RestaurantResponse restaurant = restaurantList.get(position);

        // 뷰에 데이터 맵핑 (텍스트뷰 ID는 레이아웃에 맞춰서 수정 가능!)
        holder.restaurantName.setText(restaurant.getRestaurantName());
        holder.courseTitle.setText("코스: " + (restaurant.getCourseTitle() != null ? restaurant.getCourseTitle() : "없음"));
    }

    @Override
    public int getItemCount() {
        return restaurantList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView restaurantName;
        TextView courseTitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // 뷰 아이디 연결 (ranking_item의 뷰 아이디를 그대로 쓰거나 수정)
            restaurantName = itemView.findViewById(R.id.courseName); // 예시 매칭
            courseTitle = itemView.findViewById(R.id.count);       // 예시 매칭
        }
    }
}