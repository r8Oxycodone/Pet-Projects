package com.example.aptitudetest.presentation.viewmodels

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.savedstate.SavedStateRegistryOwner
import com.example.aptitudetest.data.CrewResponseModel
import com.example.aptitudetest.data.repository.CrewRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch

class LaunchesDetailsFragmentViewModel(private val crewRepository: CrewRepository) : ViewModel() {
    private val _crewList: MutableList<CrewResponseModel> = mutableListOf()
    val crewList: List<CrewResponseModel>
        get() = _crewList

    private var _crewResponseModelFlow: Flow<CrewResponseModel> = emptyFlow()
    val crewResponseModelFlow: Flow<CrewResponseModel>
        get() = _crewResponseModelFlow


    fun fetchCrews(callback: () -> Unit) {
        viewModelScope.launch {
            fetch(callback)
        }
    }

    private suspend inline fun fetch(callback: () -> Unit) {
        _crewResponseModelFlow = checkNotNull(crewRepository.fetchCrews())
        callback.invoke()
    }

    fun clearList() {
        _crewList.clear()
    }

    fun addCrewMemberToList(crewMember: CrewResponseModel) {
        _crewList.add(crewMember)
    }

    @Suppress("UNCHECKED_CAST")
    companion object {
        fun provideFactory(
            crewRepository: CrewRepository,
            owner: SavedStateRegistryOwner,
        ): AbstractSavedStateViewModelFactory =
            object : AbstractSavedStateViewModelFactory(owner, null) {
                override fun <T : ViewModel> create(
                    key: String, modelClass: Class<T>, handle: SavedStateHandle
                ): T {
                    return LaunchesDetailsFragmentViewModel(crewRepository) as T
                }
            }
    }
}