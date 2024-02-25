package com.example.aptitudetest.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.aptitudetest.R
import com.example.aptitudetest.data.CrewResponseModel
import com.example.aptitudetest.databinding.CrewViewholderBinding

class CrewAdapter(private val crewList: List<CrewResponseModel>) :
    RecyclerView.Adapter<CrewAdapter.CrewViewHolder>() {

    lateinit var binding: CrewViewholderBinding

    inner class CrewViewHolder(var binding: CrewViewholderBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun getItemCount(): Int {
        return crewList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CrewViewHolder {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context), R.layout.crew_viewholder, parent, false
        )
        return CrewViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CrewViewHolder, position: Int) {
        val crewMember = crewList[position]
        holder.binding.crewMember = crewMember

    }
}