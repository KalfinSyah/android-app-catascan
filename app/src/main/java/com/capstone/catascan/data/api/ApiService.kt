package com.capstone.catascan.data.api

import com.capstone.catascan.BuildConfig
import com.capstone.catascan.data.response.GetUserResponse
import com.capstone.catascan.data.response.HistoryResponse
import com.capstone.catascan.data.response.ListHistoryResponse
import com.capstone.catascan.data.response.LoginResponse
import com.capstone.catascan.data.response.NewsResponse
import com.capstone.catascan.data.response.RegisterResponse
import com.capstone.catascan.data.response.UploadFotoProfileResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ApiService {
    @GET("everything")
    fun getNews(
        @Query("q") query: String = "cataract",
        @Query("searchIn") searchIn: String = "title",
        @Query("language") language: String = "en",
        @Query("sortBy") sortBy: String = "publishedAt",
        @Query("apiKey") apiKey: String = BuildConfig.API_KEY
    ): Call<NewsResponse>

    @POST("register")
    fun register(
        @Body body: Map<String, String>
    ): Call<RegisterResponse>

    @POST("login")
    fun login(
        @Body body: Map<String, String>
    ): Call<LoginResponse>

    @Multipart
    @POST("uploadprofile")
    fun uploadProfileImage(
        @Part file: MultipartBody.Part,
        @Header("Authorization") token: String
    ): Call<UploadFotoProfileResponse>

    @Multipart
    @POST("history")
    fun uploadHistory(
        @Part file: MultipartBody.Part,
        @Part("result") result: RequestBody,
        @Header("Authorization") token: String
    ): Call<HistoryResponse>

    @GET("history")
    fun getAllHistory(
        @Header("Authorization") token: String
    ): Call<ListHistoryResponse>

    @GET("getuser")
    fun getUser(
        @Header("Authorization") token: String
    ): Call<GetUserResponse>
}