package id.co.mondo.ukhuwah.data.utils

import id.co.mondo.ukhuwah.data.dto.MeasureDto
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun formatDate(millis: Long): String {
    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale("id"))
    return formatter.format(Date(millis))
}

fun formatDateToMonthYear(date: String?): Pair<String, String>? {
    if (date.isNullOrBlank() || date.length < 7) return null

    val year = date.substring(0, 4)
    val month = date.substring(5, 7)

    return year to month
}

fun filterMeasuredByMonthYear(
    measurements: List<MeasureDto>?,
    month: String,
    year: String
): List<MeasureDto> {
    return measurements?.filter { measure ->
        val date = measure.measuredAt ?: return@filter false

        val measureYear = date.substring(0, 4)   // "2025"
        val measureMonth = date.substring(5, 7)  // "12"

        measureYear == year && measureMonth == month
    } ?: emptyList()
}


