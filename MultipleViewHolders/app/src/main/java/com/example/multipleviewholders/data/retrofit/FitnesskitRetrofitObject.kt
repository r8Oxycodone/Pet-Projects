package com.example.multipleviewholders.data.retrofit

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object FitnesskitRetrofitObject {

    fun getInstance(): Retrofit {
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        return Retrofit.Builder()
            .baseUrl("https://olimpia.fitnesskit-admin.ru/schedule/")
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }
}