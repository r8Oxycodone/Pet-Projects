package com.example.multipleviewholders.data.repository

import com.example.multipleviewholders.data.retrofit.FitnesskitRetrofitService
import com.example.multipleviewholders.presentation.Lessons
import com.example.multipleviewholders.presentation.Schedule
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class ScheduleRepository(
    private val fitnesskitRetrofitService: FitnesskitRetrofitService,
    private val isReadyToShowActivity: MutableStateFlow<Boolean>
) {
    private fun parseTime(lesson: Lessons): String {
        val startTime = LocalTime.parse(lesson.startTime)
        val endTime = LocalTime.parse(lesson.endTime)
        val intervalInMinutes = startTime.until(endTime, ChronoUnit.MINUTES).toInt()

        return when {
            intervalInMinutes / 60 == 0 -> "${intervalInMinutes % 60} мин."
            intervalInMinutes % 60 == 0 -> "${intervalInMinutes / 60} ч."
            else -> "${intervalInMinutes / 60} ч. ${intervalInMinutes % 60} мин."
        }
    }

    fun getSchedule(): Flow<Schedule> = flow {
        val scheduleList = mutableListOf<Schedule>()
        val trainersHashMap = hashMapOf<String, String>()

        val formatter = DateTimeFormatter.ofPattern("EEEE, dd MMMM")
        val lessonsList = fitnesskitRetrofitService.getLessonsList(2)

        for (trainer in checkNotNull(lessonsList.body()).trainers) {
            trainersHashMap[trainer.id] = trainer.fullName
        }

        var lastLessonsFormattedDate = ""

        for ((id, lesson) in checkNotNull(lessonsList.body()).lessons.sortedWith(
            compareBy({ it.date },
                { it.startTime })
        ).withIndex()) {

            val localDate = LocalDate.parse(lesson.date)
            val date = formatter.format(localDate)

            if (lastLessonsFormattedDate != date) {
                lastLessonsFormattedDate = date
                scheduleList.add(
                    Schedule.Date(
                        localDate, lesson.startTime, date
                    )
                )
            }

            val parsedTime = parseTime(lesson)

            scheduleList.add(
                Schedule.Lessons(
                    id + 1,
                    trainersHashMap[lesson.coachId].toString(),
                    localDate,
                    date,
                    lesson.color,
                    lesson.startTime,
                    lesson.endTime,
                    lesson.date,
                    lesson.place,
                    lesson.tab,
                    parsedTime
                )
            )
        }

        for (lesson in scheduleList.sortedWith(
            compareBy({ it.date }, { it.lessonStartTime })
        )) emit(lesson)
        isReadyToShowActivity.value = true
    }
}