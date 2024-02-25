package com.example.aptitudetest.di

import android.content.Context
import androidx.core.content.res.ResourcesCompat
import coil.ImageLoader
import coil.disk.DiskCache
import coil.memory.MemoryCache
import com.example.aptitudetest.BuildConfig
import com.example.aptitudetest.R
import com.example.aptitudetest.data.SpaceXDataSource
import com.example.aptitudetest.data.repository.CrewRepository
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
class DataModule {

    @Singleton
    @Provides
    fun provideCrewRepository(): CrewRepository = CrewRepository(
        provideSpaceXDataSource(
            provideMoshi(),
            provideOkHttpClient()
        )
    )

    @Singleton
    @Provides
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    @Singleton
    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addNetworkInterceptor(HttpLoggingInterceptor())
            .build()
    }

    @Singleton
    @Provides
    fun provideSpaceXDataSource(moshi: Moshi, okHttpClient: OkHttpClient): SpaceXDataSource {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(okHttpClient)
            .build()
            .create(SpaceXDataSource::class.java)
    }

    @Singleton
    @Provides
    fun provideImageLoader(context: Context): ImageLoader {
        return ImageLoader.Builder(context)
            .error(
                ResourcesCompat.getDrawable(
                    context.resources,
                    R.drawable.baseline_sentiment_dissatisfied_24,
                    context.theme
                )
            )
            .memoryCache {
                MemoryCache.Builder(context)
                    .maxSizePercent(0.25)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(context.cacheDir.resolve("image_cache"))
                    .maxSizePercent(0.02)
                    .build()
            }
            .crossfade(1000)
            .build()
    }
}