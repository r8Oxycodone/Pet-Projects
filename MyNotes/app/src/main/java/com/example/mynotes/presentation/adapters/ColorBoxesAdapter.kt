package com.example.mynotes.presentation.adapters

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.recyclerview.widget.RecyclerView
import com.example.mynotes.R
import com.example.mynotes.domain.utils.Environment

class ColorBoxesAdapter(
    private val boxesList: List<View>,
    private val colorsList: IntArray,
    private val sendColorCode: (Int) -> Unit
) :
    RecyclerView.Adapter<ColorBoxesAdapter.MyViewHolder>() {

    private var lastCheckedPosition = -1

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val colorView: RadioButton

        init {
            colorView = view.findViewById(R.id.color_blob1)

            colorView.setOnClickListener {
                val copyOfLastCheckedPosition = lastCheckedPosition
                lastCheckedPosition = absoluteAdapterPosition
                notifyItemChanged(copyOfLastCheckedPosition)
                notifyItemChanged(lastCheckedPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.color_spinner_item, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return boxesList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.colorView.isChecked = position == lastCheckedPosition

        holder.colorView.backgroundTintList = ColorStateList.valueOf(colorsList[position])

        holder.colorView.setOnCheckedChangeListener { compoundButton, _ ->
            if (compoundButton.isChecked) {
                Environment.colorState = colorsList[holder.absoluteAdapterPosition]
                sendColorCode(Environment.colorCode)
            }
        }
    }
}