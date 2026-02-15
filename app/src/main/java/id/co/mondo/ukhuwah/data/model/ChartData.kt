package id.co.mondo.ukhuwah.data.model

data class ChartData(
    val title: String,
    val xLabel: String,
    val yLabel: String,
    val xValues: List<Double>,
    val lines: List<List<Double>>
)
