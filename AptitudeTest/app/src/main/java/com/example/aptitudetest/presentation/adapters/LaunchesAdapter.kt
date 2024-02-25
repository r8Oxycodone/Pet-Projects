package com.example.aptitudetest.presentation.adapters

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.navigation.findNavController
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.ImageLoader
import coil.load
import com.example.aptitudetest.R
import com.example.aptitudetest.data.Docs
import com.example.aptitudetest.databinding.LaunchViewholderBinding
import com.example.aptitudetest.presentation.fragments.LaunchesFragmentDirections
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class LaunchesAdapter(private val context: Context, private val imageLoader: ImageLoader) :
    PagingDataAdapter<Docs, LaunchesAdapter.LaunchesViewHolder>(LAUNCH_DIFF_CALLBACK) {

    inner class LaunchesViewHolder(
        val binding: LaunchViewholderBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(launch: Docs) {
            setDataToLayout(binding, launch)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LaunchesViewHolder =
        LaunchesViewHolder(
            LaunchViewholderBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            )
        )

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: LaunchesViewHolder, position: Int) {
        val launch = getItem(position)
        holder.binding.launch = launch
        if (launch != null) {
            holder.bind(launch)
        }

        holder.itemView.setOnClickListener {
            val action = LaunchesFragmentDirections.actionLaunchesFragmentToLaunchesDetailsFragment(
                checkNotNull(launch)
            )
            it.findNavController().navigate(action)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setDataToLayout(binding: LaunchViewholderBinding, launch: Docs) {
        binding.apply {
            nameTextView.isSelected = true

            fireDateTextView.text = if (!launch.staticFireDateUtc.isNullOrBlank()) {
                val zonedDateTime = ZonedDateTime.parse(launch.staticFireDateUtc)
                val dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
                context.getString(
                    R.string.fire_date_text_view, dateTimeFormatter.format(zonedDateTime)
                )
            } else context.getString(
                R.string.fire_date_text_view, "null"
            )

            missionIconImageView.load(launch.links.patch.small, imageLoader = imageLoader)
        }
    }

    companion object {
        private val LAUNCH_DIFF_CALLBACK = object : DiffUtil.ItemCallback<Docs>() {
            override fun areItemsTheSame(oldItem: Docs, newItem: Docs): Boolean =
                oldItem.name == newItem.name

            override fun areContentsTheSame(oldItem: Docs, newItem: Docs): Boolean =
                oldItem == newItem
        }
    }
}