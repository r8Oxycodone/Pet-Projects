package com.example.aptitudetest.presentation.fragments

import android.content.res.Configuration
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewModelScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import coil.ImageLoader
import com.example.aptitudetest.data.SpaceXDataSource
import com.example.aptitudetest.databinding.FragmentLaunchesBinding
import com.example.aptitudetest.di.App
import com.example.aptitudetest.presentation.adapters.LaunchesAdapter
import com.example.aptitudetest.presentation.viewmodels.LaunchesFragmentViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

class LaunchesFragment : Fragment() {
    @Inject
    lateinit var spaceXDataSource: SpaceXDataSource

    private var binding: FragmentLaunchesBinding? = null

    @Inject
    lateinit var imageLoader: ImageLoader

    private val viewModel: LaunchesFragmentViewModel by viewModels {
        LaunchesFragmentViewModel.provideFactory(
            spaceXDataSource, this
        )
    }
    private val adapter by lazy {
        LaunchesAdapter(requireContext().applicationContext, imageLoader)
    }

    private var isNeededToRetry = false

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            if (isNeededToRetry) {
                adapter.retry()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        (requireContext().applicationContext as App).appComponent.inject(this)
        binding = FragmentLaunchesBinding.inflate(layoutInflater, container, false)

        val networkRequest =
            NetworkRequest.Builder().addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR).build()

        val connectivityManager = ContextCompat.getSystemService(
            requireContext(), ConnectivityManager::class.java
        ) as ConnectivityManager
        connectivityManager.requestNetwork(networkRequest, networkCallback)

        LinearSnapHelper().attachToRecyclerView(binding?.launchesRecyclerView)
        binding?.launchesRecyclerView?.layoutManager = when (resources.configuration.orientation) {
            Configuration.ORIENTATION_PORTRAIT -> {
                LinearLayoutManager(requireContext().applicationContext)
            }

            Configuration.ORIENTATION_LANDSCAPE -> {
                GridLayoutManager(requireContext().applicationContext, 2)
            }

            else -> {
                LinearLayoutManager(requireContext().applicationContext)
            }
        }

        binding?.launchesRecyclerView?.adapter = adapter

        adapter.addLoadStateListener { loadStates ->
            binding?.swipeToRefresh?.isRefreshing = loadStates.refresh is LoadState.Loading
            viewModel.viewModelScope.launch {
                if (loadStates.refresh is LoadState.Error) {
                    isNeededToRetry = true
                }
            }
        }

        binding?.swipeToRefresh?.setOnRefreshListener {
            adapter.refresh()
        }
        return binding?.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.launchesFlow.collectLatest { pagingData ->
                    adapter.submitData(pagingData)
                }
            }
        }
    }

}