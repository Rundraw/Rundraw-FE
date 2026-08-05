package com.example.rundraw_fe.api;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface CourseApiService {

    // 6. 저장된 코스 드래프트(포인트 포함) 상세 조회 API (id로 조회)
    @GET("/api/course/draft/{id}")
    Call<CourseDetailResponse> getCourseDraft(@Path("id") Long courseDraftId);

    // 1. 코스 기록 시작
    @POST("/api/user/me/course/record")
    Call<StartRecordResponse> startRecord(@Body StartRecordRequest request);

    // 2. 실시간 위치 저장
    @POST("/api/user/me/course/point")
    Call<Void> savePoint(@Body PointRequest request);

    // 3. 코스 기록 일시정지
    @POST("/api/user/me/course/record/{recordId}/pause")
    Call<Void> pauseRecord(@Path("recordId") Long recordId, @Body PointRequest request);

    // 4. 코스 기록 재개
    @PATCH("/api/user/me/course/record/{recordId}/resume")
    Call<Void> resumeRecord(@Path("recordId") Long recordId);

    // 5. 코스 기록 종료
    @PATCH("/api/user/me/course/record/{recordId}/finish")
    Call<FinishRecordResponse> finishRecord(@Path("recordId") Long recordId);

    // 검색 / 위치기반 조회
    @GET("/api/course/search")
    Call<List<CourseSummaryDto>> search(@Query("keyword") String keyword,
                                        @Query("sort") String sort,
                                        @Query("lat") Double lat,
                                        @Query("lng") Double lng);

    @GET("/api/course/")
    Call<List<CourseSummaryDto>> getByLocation(@Query("lat") double lat,
                                               @Query("lng") double lng,
                                               @Query("radius") double radius);

    // 그린 코스 저장
    @POST("/api/user/me/draft/course")
    Call<DraftDetailResponse> saveDraft(@Body CreateDraftRequest request);

    // 내비게이션
    @GET("/api/course/{courseId}/navigation")
    Call<NavigationResponse> getNavigation(@Path("courseId") Long courseId);
    @GET("/api/course/draft/{courseDraftId}/navigation")
    Call<NavigationResponse> getNavigationFromDraft(@Path("courseDraftId") Long courseDraftId);


    // --- DTO 클래스들 ---

    // 코스 조회 응답 DTO
    class CourseDetailResponse {
        private String name;
        private List<DraftPointDto> points;

        public String getName() {
            return name;
        }

        public List<DraftPointDto> getPoints() {
            return points;
        }
    }

    // 코스 내 개별 포인트 DTO
    class DraftPointDto {
        private double latitude;
        private double longitude;
        private int sequence;

        public double getLatitude() {
            return latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public int getSequence() {
            return sequence;
        }
    }

    class StartRecordRequest {
        private Long courseDraftId;
        public StartRecordRequest(Long courseDraftId) {
            this.courseDraftId = courseDraftId;
        }
    }

    class StartRecordResponse {
        private Long courseRecordId;
        private String startAt;
        public Long getCourseRecordId() {
            return courseRecordId;
        }
    }

    class PointRequest {
        private Long recordId;
        private int sequence;
        private double latitude;
        private double longitude;

        public PointRequest(Long recordId, int sequence, double latitude, double longitude) {
            this.recordId = recordId;
            this.sequence = sequence;
            this.latitude = latitude;
            this.longitude = longitude;
        }
    }

    class FinishRecordResponse {
        private Long courseRecordId;
        private boolean isCompleted;
        private double distanceKm;
        private int durationSec;

        public double getDistanceKm() {
            return distanceKm;
        }
        public int getDurationSec() {
            return durationSec;
        }
        public boolean isCompleted() {
            return isCompleted;
        }
    }

    class CourseSummaryDto {
        private Long courseId;
        private String name;
        private Integer experienceCount;
        private String description;

        public Long getCourseId() { return courseId; }
        public String getName() { return name; }
        public Integer getExperienceCount() { return experienceCount; }
        public String getDescription() { return description; }
    }

    // 요청 DTO
    class CreateDraftRequest {
        private String name;
        private Long memberId;
        private List<PointDTO> points;

        public CreateDraftRequest(String name, Long memberId, List<PointDTO> points) {
            this.name = name;
            this.memberId = memberId;
            this.points = points;
        }
    }

    // 좌표 하나
    class PointDTO {
        private Integer sequence;
        private Double latitude;
        private Double longitude;

        public PointDTO(Integer sequence, Double latitude, Double longitude) {
            this.sequence = sequence;
            this.latitude = latitude;
            this.longitude = longitude;
        }
    }

    // 응답 DTO
    class DraftDetailResponse {
        private Long courseDraftId;
        private String name;
        private Boolean isSharing;
        private List<PointDTO> points;
        private String createdAt;

        public Long getCourseDraftId() { return courseDraftId; }
    }

    // 내비게이션 응답

    // DTO 클래스들 안, 다른 class들 옆에 추가
    class NavigationResponse {
        private List<InstructionDto> instructions;
        public List<InstructionDto> getInstructions() { return instructions; }
    }

    class InstructionDto {
        private Integer sequence;
        private double latitude;
        private double longitude;
        private String text;
        private double triggerDistanceM;

        public double getLatitude() { return latitude; }
        public double getLongitude() { return longitude; }
        public String getText() { return text; }
        public double getTriggerDistanceM() { return triggerDistanceM; }
    }
}