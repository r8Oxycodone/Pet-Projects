package com.example.multipleviewholders.presentation

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.multipleviewholders.databinding.DateBinding
import com.example.multipleviewholders.databinding.LessonBinding

class RecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    class ContentViewHolder(val binding: LessonBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(lessons: Schedule.Lessons) {
            binding.lessonPlace.text = lessons.lessonPlace
            binding.lessonStartTime.text = lessons.lessonStartTime
            binding.lessonEndTime.text = lessons.lessonEndTime
            binding.difference.text = lessons.differenceInLocalTimes
            binding.trainerFullName.text = lessons.trainerFullName
            binding.lessonTab.text = lessons.tab
        }
    }

    class HeaderViewHolder(private val binding: DateBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(date: Schedule.Date) {
            binding.date.text = date.fakeDate
        }
    }

    private val viewHoldersDataList = mutableListOf<Schedule>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_DATE -> ContentViewHolder(
                LessonBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )

            TYPE_LESSON -> HeaderViewHolder(
                DateBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )

            else -> throw IllegalArgumentException()
        }
    }

    override fun getItemCount(): Int = viewHoldersDataList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ContentViewHolder -> {
                holder.apply {
                    bind(viewHoldersDataList[position] as Schedule.Lessons)
                    binding.view.setBackgroundColor(Color.parseColor((viewHoldersDataList[position] as Schedule.Lessons).color))
                    binding.lessonPlace.isSelected = true
                }
            }

            is HeaderViewHolder -> holder.bind(viewHoldersDataList[position] as Schedule.Date)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (viewHoldersDataList[position]) {
            is Schedule.Lessons -> TYPE_DATE
            is Schedule.Date -> TYPE_LESSON
        }
    }

    fun addLessonCard(lessonCard: Schedule) {
        viewHoldersDataList.add(lessonCard)
    }

    companion object {
        const val TYPE_LESSON = 0
        const val TYPE_DATE = 1
    }
}