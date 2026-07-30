# Walkthrough - Fix Missing Context in RetrofitClient Call

Fixed a compilation error in `RestaurantActivity.kt` where `RetrofitClient.getInstance()` was called without the required `Context` parameter.

## Changes Made

### `app` module

#### [RestaurantActivity.kt](file:///C:/Users/user/StudioProjects/Rundraw-FE/app/src/main/java/com/example/rundraw_fe/RestaurantActivity.kt)

- Updated `loadRestaurantMarkersFromServer()` to pass `this` (the Activity context) to `RetrofitClient.getInstance()`.

```diff
-        val restaurantApi = RetrofitClient.getInstance().create(RestaurantApi::class.java)
+        val restaurantApi = RetrofitClient.getInstance(this).create(RestaurantApi::class.java)
```

## Verification Results

### Automated Tests
- Ran `./gradlew :app:compileDebugKotlin` and verified that the build now completes successfully.

```
Build finished successfully.
```