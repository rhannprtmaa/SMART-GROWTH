package id.co.raihan.smartgrowth.data.utils

import id.co.raihan.smartgrowth.data.model.AgeResult
import java.time.LocalDate
import java.time.Period

fun calculateAgeFromBirthToNow(
    birth: String
): AgeResult {

    if (birth.isNullOrBlank() || birth == "-") {
        return AgeResult(0, 0, 0, 0, "0 bulan 0 hari")
    }

    val birthDate = LocalDate.parse(birth.take(10))
    val today = LocalDate.now()

    val period = Period.between(birthDate, today)

    val years = period.years
    val months = period.months
    val days = period.days

    val totalMonths = (years * 12) + months

    val monthAndDays = "$totalMonths bulan $days hari"

    return AgeResult(
        years = period.years,
        months = period.months,
        days = period.days,
        totalMonths = totalMonths,
        monthAndDays = monthAndDays
    )
}