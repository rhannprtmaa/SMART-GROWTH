package id.co.mondo.ukhuwah.data.model

import kotlinx.serialization.Serializable

@Serializable
data class AgeResult(
    val years: Int,
    val months: Int,
    val days: Int,
    val totalMonths: Int,
    val monthAndDays: String
)

@Serializable
data class AgeMonthDayResult(
    val months: Int,
    val days: Int
)

@Serializable
data class AgeMonthResult(
    val months: Int
)