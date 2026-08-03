package com.example.rundraw_fe

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rundraw_fe.api.RestaurantApi
import com.example.rundraw_fe.auth.RetrofitClient
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

class RestaurantActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mapView: MapView
    private lateinit var googleMap: GoogleMap

    // 우측 상단 숫자와 하단 리사이클러뷰/어댑터 변수
    private lateinit var tvSavedCount: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var restaurantAdapter: RestaurantAdapter

    // 자동완성 검색 관련 변수
    private lateinit var etSearch: EditText
    private lateinit var rvAutocomplete: RecyclerView
    private lateinit var autocompleteAdapter: RestaurantAdapter

    private var allRestaurantList: List<RestaurantResponse> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant)

        // 뷰 연결
        tvSavedCount = findViewById(R.id.tvSavedCount)
        recyclerView = findViewById(R.id.recyclerViewSaved)

        // 하단 맛집 리사이클러뷰 세팅
        restaurantAdapter = RestaurantAdapter()
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = restaurantAdapter

        // 검색창 및 자동완성 리사이클러뷰 뷰 연결
        etSearch = findViewById(R.id.etSearch)
        rvAutocomplete = findViewById(R.id.rvAutocomplete)

        autocompleteAdapter = RestaurantAdapter()
        rvAutocomplete.layoutManager = LinearLayoutManager(this)
        rvAutocomplete.adapter = autocompleteAdapter

        // 글자 입력 감지 리스너 (문법 오류 수정 완료)
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val keyword = s.toString().trim()

                if (keyword.isNotEmpty()) {
                    val filteredList = allRestaurantList.filter {
                        (it.restaurantName?.contains(keyword, ignoreCase = true) == true) ||
                                (it.courseTitle?.contains(keyword, ignoreCase = true) == true)
                    }

                    if (filteredList.isNotEmpty()) {
                        autocompleteAdapter.setRestaurantList(filteredList)
                        rvAutocomplete.visibility = View.VISIBLE
                    } else {
                        rvAutocomplete.visibility = View.GONE
                    }
                } else {
                    rvAutocomplete.visibility = View.GONE
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // 구글 맵뷰 연결 및 생명주기 전달
        mapView = findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        loadRestaurantMarkersFromServer()
    }

    private fun loadRestaurantMarkersFromServer() {
        Log.d("RestaurantActivity", "서버 통신 시도 시작!")

        val restaurantApi = RetrofitClient.getInstance(this).create(RestaurantApi::class.java)

        restaurantApi.getAllRestaurants().enqueue(object : Callback<List<RestaurantResponse>> {
            override fun onResponse(
                call: Call<List<RestaurantResponse>>,
                response: Response<List<RestaurantResponse>>
            ) {
                Log.d("RestaurantActivity", "서버 응답 도착! 코드: ${response.code()}")

                if (response.isSuccessful && response.body() != null) {
                    val restaurantList = response.body()!!

                    allRestaurantList = restaurantList

                    tvSavedCount.text = "${restaurantList.size} 📌"
                    restaurantAdapter.setRestaurantList(restaurantList)

                    googleMap.clear()
                    for (item in restaurantList) {
                        if (item.latitude != null && item.longitude != null) {
                            val position = LatLng(item.latitude, item.longitude)
                            val markerOptions = MarkerOptions()
                                .position(position)
                                .title(item.restaurantName)
                                .snippet("코с: ${item.courseTitle ?: "없음"}")

                            googleMap.addMarker(markerOptions)
                        }
                    }

                    if (restaurantList.isNotEmpty()) {
                        val firstPos = LatLng(restaurantList[0].latitude!!, restaurantList[0].longitude!!)
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(firstPos, 14f))
                    }
                } else {
                    Log.e("RestaurantActivity", "서버 응답 실패: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<RestaurantResponse>>, t: Throwable) {
                Log.e("RestaurantActivity", "통신 에러 발생: ${t.message}")
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