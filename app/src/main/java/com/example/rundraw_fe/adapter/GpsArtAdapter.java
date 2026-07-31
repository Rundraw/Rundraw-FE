package com.example.rundraw_fe.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.rundraw_fe.R;
import com.example.rundraw_fe.response.GpsArtResponse;
import com.example.rundraw_fe.response.PointResponse;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import java.util.ArrayList;
import java.util.List;

public class GpsArtAdapter extends RecyclerView.Adapter<GpsArtAdapter.ViewHolder>{
    private Context context;
    private List<GpsArtResponse> items =
            new ArrayList<>();
    public void setItems(
            List<GpsArtResponse> items
    ){
        this.items = items;
        notifyDataSetChanged();
    }
    private OnItemClickListener listener;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ){
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_gps_art, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position
    ){

        GpsArtResponse gpsArt = items.get(position);


        holder.name.setText(
                gpsArt.getName()
        );


        holder.likeCount.setText(
                String.valueOf(
                        gpsArt.getLikeCount()
                )
        );



        holder.itemView.setOnClickListener(v -> {

            if(listener != null){

                Log.d(
                        "COURSE_ID",
                        "클릭한 courseId : " + gpsArt.getId()
                );

                listener.onClick(
                        gpsArt.getId()
                );

            }

        });


        holder.mapView.onCreate(null);

        holder.mapView.getMapAsync(
                googleMap -> {

                    googleMap.clear();

                    drawRoute(
                            googleMap,
                            gpsArt
                    );

                }
        );

    }

    private void drawRoute(
            GoogleMap googleMap,
            GpsArtResponse gpsArt
    ){
        boolean success = googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style));
        if (!success) {
            Log.e("MapStyle", "스타일 파싱 실패 - JSON을 확인하세요");
        }
        List<PointResponse> points = gpsArt.getPoints();

        if(points == null || points.isEmpty()){
            return;
        }

        List<LatLng> route = new ArrayList<>();

        for(PointResponse point : points){
            route.add(
                    new LatLng(
                            point.getLatitude(),
                            point.getLongitude()
                    )
            );
        }

        // 경로 선
        googleMap.addPolyline(
                new PolylineOptions()
                        .addAll(route)
                        .width(10)
                        .color(Color.rgb(255, 165, 0))
        );
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.get(0), 15));
        googleMap.getUiSettings().setScrollGesturesEnabled(false);
        googleMap.getUiSettings().setZoomGesturesEnabled(false);
    }

    @Override
    public int getItemCount(){
        return items.size();
    }
    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView name;
        TextView likeCount;
        MapView mapView;

        public ViewHolder(
                @NonNull View itemView
        ){
            super(itemView);
            name = itemView.findViewById(R.id.gpsArtName);
            likeCount = itemView.findViewById(R.id.likeCount);
            mapView = itemView.findViewById(R.id.gpsArtMap);
        }
    }
    public GpsArtAdapter(
            Context context,
            OnItemClickListener listener
    ){
        this.context = context;
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onClick(Long courseId);
    }
}