package id.co.mondo.ukhuwah.data.utils

import android.util.Log
import id.co.mondo.ukhuwah.data.model.AgeResult
import java.time.LocalDate
import java.time.Period

fun calculateAgeFromBirthToMeasure(
    birth: String,
    measuredAt: String
): AgeResult {
    Log.d("calculateAge", "birth: $birth, measuredAt: $measuredAt")

    // 1️⃣ Cegah crash (WAJIB)
    if (birth.isBlank() || measuredAt.isBlank()) return AgeResult(0, 0, 0, 0, "0 bulan 0 hari")
    if (birth == "-" || measuredAt == "-") return AgeResult(0, 0, 0, 0, "0 bulan 0 hari")


    // 2️⃣ Ambil tanggal saja (aman untuk timestamp)
    val birthDate = LocalDate.parse(birth)
    val measureDate = LocalDate.parse(measuredAt)

    // 3️⃣ Hitung usia
    val period = Period.between(birthDate, measureDate)

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