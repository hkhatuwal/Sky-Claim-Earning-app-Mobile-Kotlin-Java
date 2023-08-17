package com.sgamerapps.android.api

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.sgamerapps.android.data.Count
import com.sgamerapps.android.data.RedeemPlan
import com.sgamerapps.android.data.User
import retrofit2.Call
import retrofit2.http.*


interface Api {


    @Headers("Accept: application/json")
    @GET("config")
    fun config(): Call<JsonArray>?


    @Headers("Accept: application/json")
    @POST("login-or-register-with-email")
    fun loginWithMail(@Body body: Map<String,String> ): Call<JsonObject>?

    @Headers("Accept: application/json")
    @GET("get-today-count")
    fun getTodayCount(@Query("count_type") countType:String): Call<Count>?
    @Headers("Accept: application/json")
    @POST("update-count")
    fun updateCount(@Query("count_type") countType:String): Call<Count>?
    @Headers("Accept: application/json")
    @POST("update-count")
    fun updateWallet(@Query("count_type") countType:String): Call<Count>?

    @Headers("Accept: application/json")
    @POST("add-to-wallet")
    fun addToWallet(@Body data:Map<String,String>): Call<User>?

    @Headers("Accept: application/json")
    @GET("plans")
    fun getRedeemPlans(): Call<List<RedeemPlan>>?

    @Headers("Accept: application/json")
    @POST("redeem")
    fun redeem(@Body data:Map<String,String>): Call<JsonObject>?

    @Headers("Accept: application/json")
    @POST("add-referral")
    fun addReferral(@Body data:Map<String,String>): Call<JsonObject>?



}