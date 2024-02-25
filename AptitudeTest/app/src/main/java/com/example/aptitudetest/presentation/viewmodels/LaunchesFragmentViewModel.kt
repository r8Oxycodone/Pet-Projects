package com.example.aptitudetest.presentation.viewmodels

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.savedstate.SavedStateRegistryOwner
import com.example.aptitudetest.data.SpaceXDataSource
import com.example.aptitudetest.data.repository.LaunchesPagingSource

class LaunchesFragmentViewModel(spaceXDataSource: SpaceXDataSource) :
    ViewModel() {

    val launchesFlow = Pager(
        config = PagingConfig(
            pageSize = 10
        )
    ) {
        LaunchesPagingSource(spaceXDataSource = spaceXDataSource)
    }.flow.cachedIn(viewModelScope)

    @Suppress("UNCHECKED_CAST")
    companion object {
        fun provideFactory(
            spaceXDataSource: SpaceXDataSource,
            owner: SavedStateRegistryOwner,
        ): AbstractSavedStateViewModelFactory =
            object : AbstractSavedStateViewModelFactory(owner, null) {
                override fun <T : ViewModel> create(
                    key: String,
                    modelClass: Class<T>,
                    handle: SavedStateHandle
                ): T {
                    return LaunchesFragmentViewModel(spaceXDataSource) as T
                }
            }
    }

}