package com.example.restaurant.network;


import com.example.restaurant.network.response.RestaurantSearchResponseModel;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface Api {

    @POST("api/v1/company_signup")
    Call<RestaurantSearchResponseModel> signup(@Body Map login);


    @GET("v2.1/search")
    Call<RestaurantSearchResponseModel> getRestaurantListLatLon(@Query("q") String q,@Query("lat") Double lat,
                                                          @Query("lon") Double lon, @Query("start") int start,
                                                          @Query("count") int count);  @GET("v2.1/search")

    Call<RestaurantSearchResponseModel> getRestaurantList(@Query("q") String q, @Query("start") int start,
                                                          @Query("count") int count);

   // @Query("date") String date

}