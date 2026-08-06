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
    private OnRestaurantClickListener listener;
    public interface OnRestaurantClickListener {
        void onClick(RestaurantResponse restaurant);
    }

    public void setOnRestaurantClickListener(OnRestaurantClickListener listener) {
        this.listener = listener;
    }

    public void setRestaurantList(List<RestaurantResponse> restaurantList) {
        this.restaurantList.clear();
        if (restaurantList != null) {
            this.restaurantList.addAll(restaurantList);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(
                        R.layout.item_saved_restaurant,
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
        RestaurantResponse restaurant = restaurantList.get(position);

        holder.restaurantName.setText(
                restaurant.getRestaurantName() != null
                        ? restaurant.getRestaurantName()
                        : "이름 없음"
        );

        holder.courseTag.setText(
                restaurant.getCourseName() != null
                        ? restaurant.getCourseName()
                        : "코스 없음"
        );

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onClick(restaurant);
            }
        });
    }

    @Override
    public int getItemCount() {
        return restaurantList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView restaurantName;
        TextView courseTag;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            restaurantName = itemView.findViewById(R.id.tvRestaurantName);
            courseTag = itemView.findViewById(R.id.btnCourseTag);
        }
    }
}