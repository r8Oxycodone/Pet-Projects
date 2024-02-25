package com.example.multipleviewholders.presentation

import com.squareup.moshi.Json

data class Trainers(
    val id: String,
    @Json(name = "full_name") val fullName: String,
)

data class Lessons(
    val name: String,
    val startTime: String,
    val endTime: String,
    val date: String,
    val place: String,
    val color: String,
    @Json(name = "coach_id") val coachId: String,
    val tab: String,
)

data class FitnessKitJsonObject(
    val trainers: List<Trainers>,
    val lessons: List<Lessons>,
)