package com.example.aptitudetest.presentation.fragments

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import coil.ImageLoader
import coil.load
import com.example.aptitudetest.R
import com.example.aptitudetest.data.SpaceXDataSource
import com.example.aptitudetest.data.repository.CrewRepository
import com.example.aptitudetest.databinding.FragmentLaunchesDetailsBinding
import com.example.aptitudetest.di.App
import com.example.aptitudetest.presentation.adapters.CrewAdapter
import com.example.aptitudetest.presentation.viewmodels.LaunchesDetailsFragmentViewModel
import kotlinx.coroutines.launch
import java.io.IOException
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class LaunchesDetailsFragment : Fragment() {

    @Inject
    lateinit var crewRepository: CrewRepository

    @Inject
    lateinit var imageLoader: ImageLoader

    @Inject
    lateinit var spaceXDataSource: SpaceXDataSource
    private var binding: FragmentLaunchesDetailsBinding? = null
    private val args: LaunchesDetailsFragmentArgs by navArgs()

    private val viewModel: LaunchesDetailsFragmentViewModel by viewModels {
        LaunchesDetailsFragmentViewModel.provideFactory(
            crewRepository, this
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        (requireContext().applicationContext as App).appComponent.inject(this)
        binding = FragmentLaunchesDetailsBinding.inflate(layoutInflater, container, false)
        viewModel.clearList()
        return binding?.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.fetchCrews { setDataToLayout() }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setDataToLayout() {
        binding?.apply {
            detailedNameTextView.text = getString(R.string.name_text_view, args.docsArg.name)
            detailedMissionIconImageView.load(
                args.docsArg.links.patch.large, imageLoader = imageLoader
            )
            detailedFlightsTextView.text = getString(
                R.string.flight_text_view, args.docsArg.cores.firstOrNull()?.flight.toString()
            )
            detailedSuccessTextView.text = getString(
                R.string.success_text_view, args.docsArg.success.toString()
            )

            detailsTextView.text = args.docsArg.details ?: ""
            if (detailsTextView.text == "") {
                detailsBottomDivider.visibility = View.GONE
                detailsTopDivider.visibility = View.GONE
            }

            val zonedDateTime = ZonedDateTime.parse(args.docsArg.dateLocal)
            val dateTimeFormatter = DateTimeFormatter.ofPattern("HH-mm dd-MM-yyyy")
            detailedDateTextView.text = getString(
                R.string.detailed_date_text_view, dateTimeFormatter.format(zonedDateTime)
            )

            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    try {
                        viewModel.crewResponseModelFlow.collect { value ->
                            args.docsArg.crew.forEach {
                                if (value.id == it) viewModel.addCrewMemberToList(value)
                            }
                        }
                        crewMembersRecyclerView.layoutManager =
                            LinearLayoutManager(requireContext())
                        crewMembersRecyclerView.adapter = CrewAdapter(viewModel.crewList)
                    } catch (e: IOException) {
                        Log.d("IOException", "load: ${e.stackTraceToString()}")
                    }
                }
            }
        }
    }
}