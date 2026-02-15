package id.co.mondo.ukhuwah.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisGuidelineComponent
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisLabelComponent
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisLineComponent
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberEnd
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.continuous
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.common.component.rememberShapeComponent
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianLayerRangeProvider
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import id.co.mondo.ukhuwah.data.model.ChartData
import id.co.mondo.ukhuwah.ui.screen.detail.provideMaleBbTb2Year
import id.co.mondo.ukhuwah.ui.theme.Innovillage6thTheme
import id.co.mondo.ukhuwah.ui.theme.abu

@Composable
fun BasicLineChart(
    modifier: Modifier = Modifier,
    chartData: ChartData,
    childX: List<Double>,
    childY: List<Double>
) {
    // 1️⃣ Menyimpan & mengelola data chart
    val modelProducer = remember { CartesianChartModelProducer() }

    val scrollState = rememberVicoScrollState()

    val x = chartData.xValues
    val lines = chartData.lines



    // 2️⃣ Mengisi data chart (sekali saat pertama render)
    LaunchedEffect(chartData) {
        modelProducer.runTransaction {
            lineSeries {
                lines.forEach { line ->
                    series(x, line)
                }
                series(childX, childY)
            }
        }
    }

    // 🔹 Hitung range dari semua garis
    val allYValues = chartData.lines.flatten()

    val minY = allYValues.minOrNull() ?: 0.0
    val maxY = allYValues.maxOrNull() ?: 100.0

    val minX = chartData.xValues.minOrNull() ?: 0.0
    val maxX = chartData.xValues.maxOrNull() ?: 100.0

// 🔹 Pertimbangkan titik anak juga
    val childMinY = childY.minOrNull() ?: minY
    val childMaxY = childY.maxOrNull() ?: maxY

    val finalMinY = minOf(minY, childMinY)
    val finalMaxY = maxOf(maxY, childMaxY)


    val lineLayer = rememberLineCartesianLayer(
        rangeProvider = CartesianLayerRangeProvider.fixed(
            minX = minX,
            maxX = maxX,
            minY = (finalMinY - 2),
            maxY = (finalMaxY + 2)
        ),
        lineProvider = LineCartesianLayer.LineProvider.series(
            // 🔴 Line 1
            LineCartesianLayer.rememberLine(
                stroke = LineCartesianLayer.LineStroke.continuous(
                    thickness = 2.dp,
                    cap = StrokeCap.Round
                ),
                fill = LineCartesianLayer.LineFill.single(fill(Color.Black))
            ),
            LineCartesianLayer.rememberLine(
                stroke = LineCartesianLayer.LineStroke.continuous(
                    thickness = 2.dp,
                    cap = StrokeCap.Round
                ),
                fill = LineCartesianLayer.LineFill.single(fill(Color.Red))
            ),
            LineCartesianLayer.rememberLine(
                stroke = LineCartesianLayer.LineStroke.continuous(
                    thickness = 2.dp,
                    cap = StrokeCap.Round
                ),
                fill = LineCartesianLayer.LineFill.single(fill(Color.Yellow))
            ),
            LineCartesianLayer.rememberLine(
                stroke = LineCartesianLayer.LineStroke.continuous(
                    thickness = 2.dp,
                    cap = StrokeCap.Round
                ),
                fill = LineCartesianLayer.LineFill.single(fill(Color.Green))
            ),
            LineCartesianLayer.rememberLine(
                stroke = LineCartesianLayer.LineStroke.continuous(
                    thickness = 2.dp,
                    cap = StrokeCap.Round
                ),
                fill = LineCartesianLayer.LineFill.single(fill(Color.Yellow))
            ),
            LineCartesianLayer.rememberLine(
                stroke = LineCartesianLayer.LineStroke.continuous(
                    thickness = 2.dp,
                    cap = StrokeCap.Round
                ),
                fill = LineCartesianLayer.LineFill.single(fill(Color.Red))
            ),
            LineCartesianLayer.rememberLine(
                stroke = LineCartesianLayer.LineStroke.continuous(
                    thickness = 2.dp,
                    cap = StrokeCap.Round
                ),
                fill = LineCartesianLayer.LineFill.single(fill(Color.Black))
            ),
            LineCartesianLayer.rememberLine(
                stroke = LineCartesianLayer.LineStroke.continuous(
                    thickness = 0.dp
                ),
                fill = LineCartesianLayer.LineFill.single(fill(Color.Transparent)),
                pointProvider = LineCartesianLayer.PointProvider.single(
                    point = LineCartesianLayer.Point(
                        component = rememberShapeComponent(
                            fill = fill(Color.Blue),
                            strokeFill = fill(Color.White),
                            strokeThickness = 2.dp
                        ),
                        sizeDp = 12f
                    ),
                )
            )
        ),
    )


    // 3️⃣ Menampilkan chart
    OutlinedCard(
        modifier = modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
    ) {
        CartesianChartHost(
            chart = rememberCartesianChart(
                lineLayer,
                startAxis = VerticalAxis.rememberStart(
                    title = chartData.yLabel,
                    titleComponent = rememberAxisLabelComponent(
                        textSize = 12.sp,
                        color = Color.Black
                    ),
                    label = rememberAxisLabelComponent(
                        textSize = 8.sp,
                        color = Color.Black
                    ),
                    itemPlacer = remember {
                        VerticalAxis.ItemPlacer.step({ 2.0 })
                    },
                    line = rememberAxisLineComponent(),
                    tick = null,
                    guideline = rememberAxisGuidelineComponent(),
                ),
                endAxis = VerticalAxis.rememberEnd(
                    label = rememberAxisLabelComponent(
                        textSize = 8.sp,
                        color = Color.Black
                    ),
                    itemPlacer = remember {
                        VerticalAxis.ItemPlacer.step({ 2.0 })
                    },
                    line = rememberAxisLineComponent(),
                    tick = null,
                    guideline = rememberAxisGuidelineComponent()
                ),
                bottomAxis = HorizontalAxis.rememberBottom(
                    title = chartData.xLabel,
                    titleComponent = rememberAxisLabelComponent(
                        textSize = 12.sp,
                        color = Color.Black
                    ),
                    label = rememberAxisLabelComponent(
                        textSize = 8.sp,
                        color = Color.Black
                    ),
                    line = rememberAxisLineComponent(),
                    tick = null,
                    guideline = rememberAxisGuidelineComponent(),
                )
            ),
            modelProducer = modelProducer,
            scrollState = scrollState,
            modifier = Modifier
                .fillMaxWidth()
                .height(275.dp)
                .padding(12.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLinechart() {
    Innovillage6thTheme {
        Column(
            Modifier
                .fillMaxSize()
                .background(abu)
        ) {
            Text("Diagram")
            BasicLineChart(
                modifier = Modifier
                    .padding(horizontal = 20.dp),
                chartData = provideMaleBbTb2Year(),
                childX = listOf(60.0),
                childY = listOf(6.0)
            )
        }
    }
}