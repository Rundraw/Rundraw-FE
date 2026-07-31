package com.example.rundraw_fe;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.rundraw_fe.fragment.CollectFragment;
import com.example.rundraw_fe.fragment.RankingFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class RankingActivity extends BaseActivity {


    private TextView rankingTab;
    private TextView collectTab;

    private View tabUnderline;


    private boolean isRanking = true;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.activity_ranking);

        setupBottomNavigation(R.id.navigation_rank);


        rankingTab =
                findViewById(R.id.rankingTab);

        collectTab =
                findViewById(R.id.collectTab);


        tabUnderline =
                findViewById(R.id.tabUnderline);



        // 최초 화면
        if (savedInstanceState == null) {

            showRankingFragment();

        }



        // 랭킹 클릭
        rankingTab.setOnClickListener(v -> {


            if(!isRanking){

                isRanking = true;

                moveUnderline(
                        rankingTab
                );

                showRankingFragment();

            }

        });



        // 모아보기 클릭
        collectTab.setOnClickListener(v -> {


            if(isRanking){

                isRanking = false;

                moveUnderline(
                        collectTab
                );

                showCollectFragment();

            }

        });

    }



    private void showRankingFragment() {


        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(
                        android.R.anim.slide_in_left,
                        android.R.anim.slide_out_right
                )
                .replace(
                        R.id.courseContainer,
                        new RankingFragment()
                )
                .commit();

    }



    private void showCollectFragment() {


        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(
                        R.anim.slide_in_right,
                        R.anim.slide_out_left
                )
                .replace(
                        R.id.courseContainer,
                        new CollectFragment()
                )
                .commit();

    }



    private void moveUnderline(View target){


        float moveX =
                target.getLeft()
                        - rankingTab.getLeft();



        ObjectAnimator animator =
                ObjectAnimator.ofFloat(
                        tabUnderline,
                        "translationX",
                        moveX
                );


        animator.setDuration(250);

        animator.start();

    }

}