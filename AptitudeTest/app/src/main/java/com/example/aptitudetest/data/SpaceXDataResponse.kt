package com.example.aptitudetest.data

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.parcelize.Parcelize

@JvmInline
value class Sort(
    @Json(name = "date_utc") val dateUtc: String
)

@JvmInline
value class UTCLaunchDate(
    @Json(name = "\$gte") val utcLaunchDate: String
)

data class Query(
    @Json(name = "date_utc") val utcLaunchDate: UTCLaunchDate
)

data class Body(
    val query: Query, val options: Options
)

data class Options(
    val limit: Int, val page: Int, val sort: Sort
)

@Parcelize
data class Cores(
    val flight: Int?
) : Parcelable

@Parcelize
data class Patch(
    val small: String?, val large: String?
) : Parcelable

@Parcelize
data class Links(
    val patch: Patch
) : Parcelable

@Parcelize
data class Docs(
    val crew: List<String?>,
    val details: String?,
    val success: Boolean?,
    val name: String?,
    val cores: List<Cores>,
    val links: Links,
    @Json(name = "static_fire_date_utc") val staticFireDateUtc: String?,
    @Json(name = "date_local") val dateLocal: String
) : Parcelable

data class LaunchesResponseModel(
    val docs: List<Docs>,
)

data class CrewResponseModel(
    val image: String?,
    val id: String,
    val name: String,
    val agency: String,
    val status: String,
    val launches: List<String>
)