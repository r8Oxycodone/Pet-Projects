package com.example.aptitudetest.data.repository

import com.example.aptitudetest.data.CrewResponseModel
import com.example.aptitudetest.data.SpaceXDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.transform

class CrewRepository(private val spaceXDataSource: SpaceXDataSource) {
    suspend fun fetchCrews(): Flow<CrewResponseModel>? =
        spaceXDataSource.getCrews().body()?.asFlow()?.transform {
            emit(it)
        }?.buffer()
}