package com.example.rundraw_fe;


import android.os.Bundle;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class CollectFragment extends Fragment {

    private RecyclerView recyclerView;
    public CollectFragment(){
        super(R.layout.collect_fragment);
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ){
        super.onViewCreated(view, savedInstanceState);
        recyclerView =
                view.findViewById(R.id.collectRecyclerView);
        recyclerView.setLayoutManager(
                new LinearLayoutManager(requireContext())
        );

        // TODO
        // 모아보기 API 호출
        // Adapter 연결
    }


}