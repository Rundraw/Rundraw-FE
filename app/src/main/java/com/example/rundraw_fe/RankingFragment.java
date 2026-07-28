package com.example.rundraw_fe;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class RankingFragment extends Fragment {
    private RecyclerView recyclerView;

    public RankingFragment() {
        super(R.layout.ranking_fragment);
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView =
                view.findViewById(R.id.rankingRecyclerView);
        recyclerView.setLayoutManager(
                new LinearLayoutManager(requireContext())
        );
        // TODO
        // 랭킹 API 호출
        // Adapter 연결
    }
}