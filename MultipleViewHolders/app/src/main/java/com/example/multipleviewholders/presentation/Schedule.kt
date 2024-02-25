package com.example.multipleviewholders.presentation

import java.time.LocalDate

sealed class Schedule(open val lessonStartTime: String, open val date: LocalDate) {

    data class Lessons(
        val id: Int,
        val trainerFullName: String,
        override val date: LocalDate,
        val fakeDate: String,
        val color: String,
        override val lessonStartTime: String,
        val lessonEndTime: String,
        val lessonDate: String,
        val lessonPlace: String,
        val tab: String,
        val differenceInLocalTimes: String
    ) : Schedule(lessonStartTime, date)

    data class Date(
        override val date: LocalDate, override val lessonStartTime: String, val fakeDate: String
    ) : Schedule(lessonStartTime, date)
}