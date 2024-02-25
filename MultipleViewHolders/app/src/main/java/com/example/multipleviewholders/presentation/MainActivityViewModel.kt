package com.example.multipleviewholders.presentation

import androidx.lifecycle.ViewModel
import com.example.multipleviewholders.data.retrofit.FitnesskitRetrofitObject
import com.example.multipleviewholders.data.retrofit.FitnesskitRetrofitService
import com.example.multipleviewholders.data.repository.ScheduleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainActivityViewModel : ViewModel() {

    private val _isReadyToShowActivity = MutableStateFlow(false)
    val isReadyToShowActivity = _isReadyToShowActivity.asStateFlow()
    var scheduleFlow: Flow<Schedule>

    init {
        val fitnesskitRetrofitService =
            FitnesskitRetrofitObject.getInstance().create(FitnesskitRetrofitService::class.java)
        val scheduleRepository = ScheduleRepository(fitnesskitRetrofitService)
        scheduleFlow = scheduleRepository.getSchedule()
        _isReadyToShowActivity.value = true
    }
}