package com.example.multipleviewholders.data.retrofit

import com.example.multipleviewholders.presentation.FitnessKitJsonObject
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface FitnesskitRetrofitService {

    @GET("get_v3")
    suspend fun getLessonsList(
        @Query("club_id") clubId: Int,
    ): Response<FitnessKitJsonObject>
}