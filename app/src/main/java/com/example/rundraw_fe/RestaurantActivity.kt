package com.example.rundraw_fe

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rundraw_fe.api.RestaurantApi
import com.example.rundraw_fe.auth.RetrofitClient
import com.example.rundraw_fe.response.ApiResponse
import com.example.rundraw_fe.response.RestaurantResponse
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.google.android.gms.maps.model.Marker

class RestaurantActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mapView: MapView
    private lateinit var googleMap: GoogleMap

    private lateinit var tvSavedCount: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var restaurantAdapter: RestaurantAdapter

    private lateinit var etSearch: EditText
    private lateinit var rvAutocomplete: RecyclerView

    private val markerMap = mutableMapOf<Long, Marker>()
    private var selectedRestaurantCourseId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant)

        tvSavedCount = findViewById(R.id.tvSavedCount)
        recyclerView = findViewById(R.id.recyclerViewSaved)

        restaurantAdapter = RestaurantAdapter()
        selectedRestaurantCourseId = intent.getLongExtra("restaurantCourseId", -1)

        restaurantAdapter.setOnRestaurantClickListener { restaurant ->
            if (!::googleMap.isInitialized) {
                return@setOnRestaurantClickListener
            }

            if (restaurant.latitude != null && restaurant.longitude != null) {
                val position = LatLng(restaurant.latitude!!, restaurant.longitude!!)

                googleMap.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(
                                position,
                                17f
                        )
                )

                // 기존 마커 찾기
                val marker = markerMap[restaurant.restaurantCourseId]

                if (marker != null) {
                    marker.showInfoWindow()
                }
            }
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = restaurantAdapter

        etSearch = findViewById(R.id.etSearch)
        rvAutocomplete = findViewById(R.id.rvAutocomplete)
        etSearch.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
            ) {}

            override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
            ) {
                val keyword = s.toString().trim()

                if (keyword.isNotEmpty()) {
                    if (::googleMap.isInitialized) {
                        searchRestaurantOnMap(keyword)
                    }
                } else {
                    if (::googleMap.isInitialized) {
                        loadRestaurantMarkersFromServer()
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        mapView = findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        loadRestaurantMarkersFromServer()
    }

    /**
     * 전체 맛집 조회
     */
    private fun loadRestaurantMarkersFromServer() {
        val api = RetrofitClient.getInstance(this)
                .create(RestaurantApi::class.java)

        api.getRestaurant()
                .enqueue(object : Callback<ApiResponse<List<RestaurantResponse>>> {

                    override fun onResponse(
                            call: Call<ApiResponse<List<RestaurantResponse>>>,
                            response: Response<ApiResponse<List<RestaurantResponse>>>
                    ) {
                        if (!response.isSuccessful) return

                        val restaurantList = response.body()?.result ?: emptyList()

                        tvSavedCount.text = "${restaurantList.size} 📌"

                        restaurantAdapter.setRestaurantList(restaurantList)

                        googleMap.clear()
                        markerMap.clear()

                        // 마커 생성
                        restaurantList.forEach { restaurant ->
                            if (restaurant.latitude != null && restaurant.longitude != null) {
                                val marker = googleMap.addMarker(
                                        MarkerOptions()
                                                .position(
                                                        LatLng(
                                                                restaurant.latitude!!,
                                                                restaurant.longitude!!
                                                        )
                                                )
                                                .title(restaurant.restaurantName)
                                                .snippet(restaurant.courseName)
                                )

                                if (marker != null) {
                                    markerMap[restaurant.restaurantCourseId] = marker
                                }
                            }
                        }

                        // -------------------------------
                        // 홈에서 넘어온 맛집이 있는 경우
                        // -------------------------------
                        if (selectedRestaurantCourseId != -1L) {
                            val marker = markerMap[selectedRestaurantCourseId]

                            if (marker != null) {
                                googleMap.animateCamera(
                                        CameraUpdateFactory.newLatLngZoom(
                                                marker.position,
                                                17f
                                        )
                                )

                                marker.showInfoWindow()

                                // 중요!!
                                return
                            }
                        }

                        // -------------------------------
                        // 일반 진입 시 첫 번째 맛집으로 이동
                        // -------------------------------
                        val first = restaurantList.firstOrNull {
                            it.latitude != null && it.longitude != null
                        }

                        if (first != null) {
                            googleMap.moveCamera(
                                    CameraUpdateFactory.newLatLngZoom(
                                            LatLng(
                                                    first.latitude!!,
                                                    first.longitude!!
                                            ),
                                            14f
                                    )
                            )
                        }
                    }

                    override fun onFailure(
                            call: Call<ApiResponse<List<RestaurantResponse>>>,
                            t: Throwable
                    ) {
                        Log.e("Restaurant", t.message ?: "")
                    }
                })
    }

    /**
     * 검색 후 지도 표시
     */
    private fun searchRestaurantOnMap(keyword: String) {
        val api = RetrofitClient.getInstance(this)
                .create(RestaurantApi::class.java)

        api.searchRestaurant(keyword)
                .enqueue(object : Callback<ApiResponse<List<RestaurantResponse>>> {

                    override fun onResponse(
                            call: Call<ApiResponse<List<RestaurantResponse>>>,
                            response: Response<ApiResponse<List<RestaurantResponse>>>
                    ) {
                        if (response.isSuccessful) {
                            val list = response.body()?.result ?: emptyList()

                            googleMap.clear()

                            list.forEach { restaurant ->
                                if (restaurant.latitude != null && restaurant.longitude != null) {
                                    val position = LatLng(restaurant.latitude!!, restaurant.longitude!!)

                                    googleMap.addMarker(
                                            MarkerOptions()
                                                    .position(position)
                                                    .title(restaurant.restaurantName)
                                                    .snippet(restaurant.courseName)
                                    )
                                }
                            }

                            val first = list.firstOrNull {
                                it.latitude != null && it.longitude != null
                            }

                            if (first != null) {
                                googleMap.animateCamera(
                                        CameraUpdateFactory.newLatLngZoom(
                                                LatLng(
                                                        first.latitude!!,
                                                        first.longitude!!
                                                ),
                                                15f
                                        )
                                )
                            }
                        }
                    }

                    override fun onFailure(
                            call: Call<ApiResponse<List<RestaurantResponse>>>,
                            t: Throwable
                    ) {
                        Log.e("SearchRestaurant", t.message ?: "")
                    }
                })
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        mapView.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        mapView.onDestroy()
        super.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }
}