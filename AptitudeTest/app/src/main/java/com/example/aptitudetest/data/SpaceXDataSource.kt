package com.example.aptitudetest.data

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface SpaceXDataSource {

    @POST("v4/launches/query")
    suspend fun getLaunches(
        @Body body: com.example.aptitudetest.data.Body,
    ): Response<LaunchesResponseModel>

    @GET("v4/crew")
    suspend fun getCrews(): Response<List<CrewResponseModel>>
}