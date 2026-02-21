package id.co.mondo.ukhuwah.ui.components

import androidx.compose.ui.graphics.Color
import id.co.mondo.ukhuwah.data.model.ChartData
import id.co.mondo.ukhuwah.data.model.ChartType

data class GrowthStatus(
    val title: String,
    val description: String,
    val color: Color
)

fun getGrowthStatus(
    chartData: ChartData,
    chartType: ChartType,
    childX: Double,
    childY: Double
): GrowthStatus {

    val xIndex = chartData.xValues
        .mapIndexed { index, value ->
            index to kotlin.math.abs(value - childX)
        }
        .minByOrNull { it.second }
        ?.first ?: return GrowthStatus(
        "Data tidak valid",
        "Nilai tidak ditemukan dalam kurva referensi.",
        Color.Gray
    )

    val lines = chartData.lines.map { it[xIndex] }

    if (lines.size < 7) {
        return GrowthStatus(
            "Kurva tidak valid",
            "Data standar pertumbuhan tidak lengkap.",
            Color.Gray
        )
    }

    return when (chartType) {
        ChartType.BB_TB -> {

            when {
                childY <= lines[0] -> GrowthStatus(
                    "Sangat Kurus",
                    "Berat badan sangat rendah dibanding tinggi badan.",
                    Color.Red
                )

                childY <= lines[2] -> GrowthStatus(
                    "Kurus",
                    "Berat badan kurang proporsional terhadap tinggi.",
                    Color(0xFFFF9800)
                )

                childY <= lines[5] -> GrowthStatus(
                    "Normal",
                    "Berat badan proporsional terhadap tinggi badan.",
                    Color(0xFF2E7D32)
                )

                else -> GrowthStatus(
                    "Gemuk",
                    "Berat badan berlebih dibanding tinggi badan.",
                    Color(0xFFFFC107)
                )
            }
        }
        ChartType.TB_USIA -> {

            when {
                childY <= lines[0] -> GrowthStatus(
                    "Sangat Pendek",
                    "Tinggi badan sangat rendah dibanding usia.",
                    Color.Red
                )

                childY <= lines[2] -> GrowthStatus(
                    "Pendek",
                    "Tinggi badan di bawah standar usia.",
                    Color(0xFFFF9800)
                )

                childY <= lines[5] -> GrowthStatus(
                    "Normal",
                    "Tinggi badan sesuai usia.",
                    Color(0xFF2E7D32)
                )

                else -> GrowthStatus(
                    "Tinggi",
                    "Tinggi badan di atas rata-rata usia.",
                    Color(0xFF1976D2)
                )
            }
        }

        else -> GrowthStatus(
            "Belum didukung",
            "Chart ini belum memiliki status klasifikasi.",
            Color.Gray
        )
    }
}

