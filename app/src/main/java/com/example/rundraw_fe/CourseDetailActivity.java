package com.example.rundraw_fe;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.rundraw_fe.api.RankingApi;
import com.example.rundraw_fe.auth.RetrofitClient;
import com.example.rundraw_fe.listener.CommentMenuListener;
import com.example.rundraw_fe.request.CreateCommentRequest;
import com.example.rundraw_fe.request.UpdateCommentRequest;
import com.example.rundraw_fe.response.ApiResponse;
import com.example.rundraw_fe.response.CommentResponse;
import com.example.rundraw_fe.response.CourseDetailResponse;
import com.example.rundraw_fe.response.PaginationResponse;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.example.rundraw_fe.adapter.CommentAdapter;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CourseDetailActivity extends AppCompatActivity {

    private ImageView ivLike;
    private ImageView ivBookmark;
    private TextView tvLikeCount;
    private TextView tvBookmarkCount;
    private TextView tvCommentCount;
    private TextView tvWriter;
    private TextView tvCourseName;
    private TextView tvLevel;
    private TextView tvDescription;
    private MapView courseMap;
    private GoogleMap googleMap;
    private ImageView ivComment;
    private EditText etComment;
    private Button btnCommentSend;
    private boolean isLiked = false;
    private boolean isBookmarked = false;
    private int likeCount = 0;
    private int bookmarkCount = 0;
    private RankingApi apiService;
    private Long courseId;
    private BottomSheetBehavior commentBehavior;
    private CommentAdapter commentAdapter;
    private RecyclerView commentRecyclerView;
    private View commentInputLayout;
    private Long editingCommentId = null;
    private ImageButton btnBack;
    private AppCompatButton btnStart;
    private Long courseDraftId;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_detail);

        // View 연결
        courseMap = findViewById(R.id.courseMap);
        commentRecyclerView = findViewById(R.id.commentRecyclerView);
        ivLike = findViewById(R.id.ivLike);
        ivBookmark = findViewById(R.id.ivBookmark);
        tvLikeCount = findViewById(R.id.tvLikeCount);
        tvBookmarkCount = findViewById(R.id.tvBookmarkCount);
        tvCommentCount = findViewById(R.id.tvCommentCount);
        tvWriter = findViewById(R.id.tvWriter);
        tvCourseName = findViewById(R.id.tvCourseName);
        tvLevel = findViewById(R.id.tvLevel);
        tvDescription = findViewById(R.id.tvDescription);
        ivComment = findViewById(R.id.ivComment);
        etComment = findViewById(R.id.etComment);
        btnCommentSend = findViewById(R.id.btnCommentSend);
        btnBack = findViewById(R.id.btnBack);
        btnStart = findViewById(R.id.btnStart);

        courseMap.onCreate(savedInstanceState);
        courseMap.getMapAsync(map -> {
            googleMap = map;
            googleMap.getUiSettings().setScrollGesturesEnabled(false);
            googleMap.getUiSettings().setZoomGesturesEnabled(false);
        });
        commentRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        commentAdapter = new CommentAdapter(
                new CommentMenuListener() {
                    @Override
                    public void onEdit(CommentResponse comment) {
                        startEditComment(comment);
                    }
                    @Override
                    public void onDelete(CommentResponse comment) {
                        apiService.deleteComment(courseId, comment.getId())
                                .enqueue(new Callback<ApiResponse<Object>>() {
                                    @Override
                                    public void onResponse(
                                            Call<ApiResponse<Object>> call,
                                            Response<ApiResponse<Object>> response
                                    ) {
                                        if(response.isSuccessful()){
                                            loadComments();
                                            loadCourseDetail();
                                        }else{
                                            try {
                                                Log.e("COMMENT_DELETE_ERROR", response.errorBody().string());
                                            }catch(Exception e){
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                    @Override
                                    public void onFailure(
                                            Call<ApiResponse<Object>> call,
                                            Throwable t
                                    ) {}
                                });
                    }
                }
        );
        commentRecyclerView.setAdapter(commentAdapter);

        // 전달 받은 courseId
        courseId = getIntent().getLongExtra("courseId", 0L);
        Log.d("COURSE_DETAIL", "courseId = " + courseId);
        apiService = RetrofitClient.getInstance(this).create(RankingApi.class);
        LinearLayout commentSheet = findViewById(R.id.commentSheet);

        // 댓글 BottomSheet 초기 설정
        commentBehavior = BottomSheetBehavior.from(commentSheet);
        commentBehavior.setHideable(true);
        commentBehavior.setPeekHeight(0);
        commentBehavior.setDraggable(true);
        commentBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        commentInputLayout = findViewById(R.id.commentInputLayout);

        // 댓글 입력창 포커스 시 BottomSheet 확장
        etComment.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                // BottomSheet 열기
                commentBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                // 댓글 입력창을 30px 위로 이동
                commentInputLayout.animate()
                        .translationY(-70)
                        .setDuration(200)
                        .start();

            } else {
                // 포커스 해제 시 원래 위치
                commentInputLayout.animate()
                        .translationY(0)
                        .setDuration(200)
                        .start();
                commentBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });

        // 상세 조회
        loadCourseDetail();

        // 뒤로 가기
        btnBack.setOnClickListener(v -> {
            finish();
        });

        // 댓글 조회 버튼 클릭 이벤트
        ivComment.setOnClickListener(v -> {
            int state = commentBehavior.getState();
            if(state == BottomSheetBehavior.STATE_HIDDEN || state == BottomSheetBehavior.STATE_COLLAPSED){
                commentBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                loadComments();
            }else{
                commentBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });

        // 댓글 작성
        btnCommentSend.setOnClickListener(v -> {
            String content = etComment.getText().toString().trim();
            if(content.isEmpty()){return;}

            // 수정 모드
            if(editingCommentId != null){
                UpdateCommentRequest request = new UpdateCommentRequest(content);
                apiService.updateComment(courseId, editingCommentId, request)
                        .enqueue(
                                new Callback<ApiResponse<Object>>() {
                                    @Override
                                    public void onResponse(
                                            Call<ApiResponse<Object>> call,
                                            Response<ApiResponse<Object>> response
                                    ) {
                                        if(response.isSuccessful()){
                                            etComment.setText("");
                                            editingCommentId = null;
                                            btnCommentSend.setText("등록");
                                            loadComments();
                                        }else{
                                            Log.e("COMMENT_UPDATE", "수정 실패 : " + response.code());
                                        }
                                    }

                                    @Override
                                    public void onFailure(
                                            Call<ApiResponse<Object>> call,
                                            Throwable t
                                    ){
                                        Log.e("COMMENT_UPDATE", "네트워크 오류", t);
                                    }
                                }
                        );
            }

            // 작성 모드
            else{
                CreateCommentRequest request = new CreateCommentRequest(content);
                apiService.createComment(courseId, request)
                        .enqueue(
                                new Callback<ApiResponse<Object>>() {
                                    @Override
                                    public void onResponse(
                                            Call<ApiResponse<Object>> call,
                                            Response<ApiResponse<Object>> response
                                    ){
                                        if(response.isSuccessful()){
                                            etComment.setText("");
                                            loadComments();
                                        }
                                    }

                                    @Override
                                    public void onFailure(
                                            Call<ApiResponse<Object>> call,
                                            Throwable t
                                    ){}
                                }
                        );
            }
        });

        // 좋아요 클릭
        ivLike.setOnClickListener(v -> {
            if(isLiked){
                apiService.deleteLike(courseId)
                        .enqueue(
                                new Callback<ApiResponse<Object>>() {
                                    @Override
                                    public void onResponse(
                                            Call<ApiResponse<Object>> call,
                                            Response<ApiResponse<Object>> response
                                    ) {
                                        if(response.isSuccessful()){
                                            isLiked = false;
                                            likeCount--;
                                            tvLikeCount.setText(String.valueOf(likeCount));
                                            ivLike.setImageResource(R.drawable.ic_heart_empty);
                                        }
                                    }

                                    @Override
                                    public void onFailure(
                                            Call<ApiResponse<Object>> call,
                                            Throwable t
                                    ){}
                                }
                        );
            }else{
                apiService.createLike(courseId)
                        .enqueue(
                                new Callback<ApiResponse<Object>>() {
                                    @Override
                                    public void onResponse(
                                            Call<ApiResponse<Object>> call,
                                            Response<ApiResponse<Object>> response
                                    ) {
                                        if(response.isSuccessful()){
                                            isLiked = true;
                                            likeCount++;
                                            tvLikeCount.setText(String.valueOf(likeCount));
                                            ivLike.setImageResource(R.drawable.ic_heart_full);
                                        }
                                    }

                                    @Override
                                    public void onFailure(
                                            Call<ApiResponse<Object>> call,
                                            Throwable t
                                    ){}
                                }
                        );
            }
        });

        // 북마크 클릭
        ivBookmark.setOnClickListener(v -> {
            if(isBookmarked){
                // 북마크 삭제 API
                apiService.deleteBookmark(courseId)
                        .enqueue(
                                new Callback<ApiResponse<Object>>() {
                                    @Override
                                    public void onResponse(
                                            Call<ApiResponse<Object>> call,
                                            Response<ApiResponse<Object>> response
                                    ) {
                                        if(response.isSuccessful()){
                                            isBookmarked = false;
                                            bookmarkCount--;
                                            tvBookmarkCount.setText(String.valueOf(bookmarkCount));
                                            ivBookmark.setImageResource(R.drawable.ic_bookmark_empty);
                                        }
                                    }

                                    @Override
                                    public void onFailure(
                                            Call<ApiResponse<Object>> call,
                                            Throwable t
                                    ){
                                        Log.e("BOOKMARK", "북마크 삭제 실패", t);
                                    }
                                }
                        );
            }else{
                // 북마크 생성 API
                apiService.createBookmark(courseId)
                        .enqueue(
                                new Callback<ApiResponse<Object>>() {
                                    @Override
                                    public void onResponse(
                                            Call<ApiResponse<Object>> call,
                                            Response<ApiResponse<Object>> response
                                    ) {
                                        if(response.isSuccessful()){
                                            isBookmarked = true;
                                            bookmarkCount++;
                                            tvBookmarkCount.setText(String.valueOf(bookmarkCount));
                                            ivBookmark.setImageResource(R.drawable.ic_bookmark_full);
                                        }
                                    }

                                    @Override
                                    public void onFailure(
                                            Call<ApiResponse<Object>> call,
                                            Throwable t
                                    ){
                                        Log.e("BOOKMARK", "북마크 생성 실패", t);
                                    }
                                }
                        );
            }
        });

        btnStart.setOnClickListener(v -> {
            if (courseDraftId == null) {
                Log.e("COURSE_DETAIL", "courseDraftId가 없습니다.");
                return;
            }

            Intent intent = new Intent(CourseDetailActivity.this, NavigateActivity.class);
            intent.putExtra("courseDraftId", courseDraftId);
            startActivity(intent);
        });
    }

    private void loadCourseDetail(){
        apiService.getCourseDetail(courseId)
                .enqueue(new Callback<ApiResponse<CourseDetailResponse>>() {
                    @Override
                    public void onResponse(
                            Call<ApiResponse<CourseDetailResponse>> call,
                            Response<ApiResponse<CourseDetailResponse>> response
                    ) {
                        if(response.isSuccessful() && response.body()!=null){
                            CourseDetailResponse data = response.body().getResult();
                            // courseDraftId 저장
                            courseDraftId = data.getCoursedraftId();
                            // 제목
                            tvCourseName.setText(data.getName());
                            // 작성자
                            tvWriter.setText(data.getUser());
                            // 난이도
                            tvLevel.setText(getLevelText(data.getLevelType()));
                            // 설명
                            tvDescription.setText(data.getContent());
                            // 좋아요
                            likeCount = data.getLikeCount();
                            tvLikeCount.setText(String.valueOf(likeCount));
                            isLiked = Boolean.TRUE.equals(data.getIsLike());
                            if(isLiked){
                                ivLike.setImageResource(R.drawable.ic_heart_full);
                            }else{
                                ivLike.setImageResource(R.drawable.ic_heart_empty);
                            }
                            // 북마크
                            bookmarkCount = data.getBookmarkCount();
                            tvBookmarkCount.setText(String.valueOf(bookmarkCount));
                            isBookmarked = Boolean.TRUE.equals(data.getIsBookmark());
                            if(isBookmarked){
                                ivBookmark.setImageResource(R.drawable.ic_bookmark_full);
                            }else{
                                ivBookmark.setImageResource(R.drawable.ic_bookmark_empty);
                            }
                            // 댓글
                            tvCommentCount.setText(String.valueOf(data.getCommentCount()));
                            // 지도 경로 표시
                            if(data.getPoints() != null && !data.getPoints().isEmpty()){
                                drawCourseRoute(data.getPoints());
                            }
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<ApiResponse<CourseDetailResponse>> call,
                            Throwable t
                    ){}
                    private String getLevelText(String levelType) {
                        if (levelType == null) {
                            return "";
                        }
                        switch (levelType) {
                            case "BEGINNER":
                                return "초급";
                            case "INTERMEDIATE":
                                return "중급";
                            case "ADVANCED":
                                return "상급";
                            default:
                                return levelType;
                        }
                    }
                });
    }
    private void drawCourseRoute(
            List<CourseDetailResponse.Point> points
    ){
        if(googleMap == null || points == null || points.isEmpty()){
            return;
        }

        googleMap.clear();
        List<LatLng> route = new ArrayList<>();
        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        for(CourseDetailResponse.Point point : points){
            LatLng latLng = new LatLng(point.getLatitude(), point.getLongitude());
            route.add(latLng);

            // 모든 좌표 포함
            boundsBuilder.include(latLng);
        }

        // 경로 그리기
        googleMap.addPolyline(
                new PolylineOptions()
                        .addAll(route)
                        .width(10)
                        .color(android.graphics.Color.rgb(255,165,0))
        );

        // 코스 전체 중앙으로 이동
        LatLngBounds bounds = boundsBuilder.build();

        googleMap.animateCamera(
                CameraUpdateFactory.newLatLngBounds(
                        bounds,
                        100   // 여백(px)
                )
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        courseMap.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        courseMap.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        courseMap.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        courseMap.onLowMemory();
    }

    // 댓글 조회
    private void loadComments(){

        apiService.getComments(
                courseId,
                20,
                "-1",
                ""
        ).enqueue(
                new Callback<ApiResponse<PaginationResponse<CommentResponse>>>() {
                    @Override
                    public void onResponse(
                            Call<ApiResponse<PaginationResponse<CommentResponse>>> call,
                            Response<ApiResponse<PaginationResponse<CommentResponse>>> response
                    ){
                        Log.d("COMMENT", "응답 코드 : " + response.code());

                        if(response.isSuccessful() && response.body()!=null){
                            PaginationResponse<CommentResponse> result = response.body().getResult();
                            for(CommentResponse comment : result.getData()){
                                Log.d("COMMENT_CHECK", "id="
                                        + comment.getId()
                                        + ", content="
                                        + comment.getContent()
                                        + ", isMine="
                                        + comment.getIsMine()
                                );
                            }
                            Log.d("COMMENT", "댓글 개수 : " + result.getData().size());
                            commentAdapter.setItems(result.getData());
                        }else{
                            Log.e("COMMENT",
                                    "응답 실패 code="
                                            + response.code()
                                            + " message="
                                            + response.message()
                            );

                            if(response.errorBody()!=null){
                                try {
                                    Log.e("COMMENT_ERROR",
                                            response.errorBody()
                                                    .string()
                                    );
                                } catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<ApiResponse<PaginationResponse<CommentResponse>>> call,
                            Throwable t
                    ){
                        Log.e("COMMENT", "네트워크 실패", t);
                    }
                }
        );
    }


    private void startEditComment(CommentResponse comment){

        editingCommentId = comment.getId();
        etComment.setText(comment.getContent());
        etComment.setSelection(etComment.getText().length());
        btnCommentSend.setText("수정");
        etComment.requestFocus();
        InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        imm.showSoftInput(
                etComment,
                InputMethodManager.SHOW_IMPLICIT
        );
        etComment.setHint("댓글 수정");
    }

    private Long getCurrentUserId(){
        return getSharedPreferences(
                "USER",
                MODE_PRIVATE
        ).getLong(
                "userId",
                -1L
        );
    }
}