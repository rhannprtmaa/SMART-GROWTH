package id.co.mondo.ukhuwah.ui.screen.detail

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import id.co.mondo.ukhuwah.R
import id.co.mondo.ukhuwah.data.model.AgeRange
import id.co.mondo.ukhuwah.data.model.AgeResult
import id.co.mondo.ukhuwah.data.model.AllRecipes
import id.co.mondo.ukhuwah.data.model.ChartType
import id.co.mondo.ukhuwah.data.model.ChildMeasure
import id.co.mondo.ukhuwah.data.model.ChildWithMeasured
import id.co.mondo.ukhuwah.data.model.Gender
import id.co.mondo.ukhuwah.ui.common.UiState
import id.co.mondo.ukhuwah.ui.components.AppTopBar
import id.co.mondo.ukhuwah.ui.components.BasicLineChart
import id.co.mondo.ukhuwah.ui.components.DetailItemCard
import id.co.mondo.ukhuwah.ui.components.MpasiItemCard
import id.co.mondo.ukhuwah.ui.theme.Innovillage6thTheme
import id.co.mondo.ukhuwah.ui.theme.Pink4
import id.co.mondo.ukhuwah.ui.viewmodel.HealthViewModel

@Composable
fun StatisticContent(
    navController: NavController,
    child: ChildWithMeasured
) {

    val viewmodel: HealthViewModel = viewModel()
    val recipeState by viewmodel.recipesState.collectAsState()

    var selectedChartIndex by remember { mutableStateOf(0) }
//    var selectedAgeRange by remember { mutableStateOf(AgeRange.Under2Year) }
//    var selectedGender by remember { mutableStateOf(Gender.Female) }

    val gender = when(child.gender) {
        "L" -> {
            Gender.Male
        }
        "P" -> {
            Gender.Female
        }
        else -> Gender.Male
    }

    Log.d("Statistic", "StatisticContent: $gender")


    val age = when(child.ageResult.totalMonths){
        in 0..2 -> {
            AgeRange.Under2Year
        }
        in 2..12 -> {
            AgeRange.Upper2Year
        }
        else -> {
            AgeRange.Under2Year
        }
    }

    val chartTypes = ChartType.entries.toTypedArray()

    val selectedChartType = chartTypes[selectedChartIndex]

    val chartData = provideChartData(
        gender = gender,
        chartType = selectedChartType,
//        age = age
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Pink4),
        contentPadding = PaddingValues(vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {


            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                DetailItemCard(
                    painter = painterResource(R.drawable.babyface),
                    title = "Usia anak",
                    value = "${child.ageResult.years} Tahun " +
                            "${child.ageResult.months} Bulan " +
                            "${child.ageResult.days} Hari",
                    sizeFont = 18,
                    modifier = Modifier.size(height = 130.dp, width = 250.dp)
                )
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    DetailItemCard(
                        painter = painterResource(R.drawable.trust),
                        title = "Berat (kg)",
                        value = child.measurement?.weightKg.toString(),
                    )
                    DetailItemCard(
                        painter = painterResource(R.drawable.length),
                        title = "Tinggi (cm)",
                        value = child.measurement?.heightCm.toString(),
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    DetailItemCard(
                        painter = painterResource(R.drawable.raisinghand),
                        title = "LiLA (cm)",
                        value = child.measurement?.armCm.toString(),
                    )
                    DetailItemCard(
                        painter = painterResource(R.drawable.headbrain),
                        title = "LK (cm)",
                        value = child.measurement?.headCm.toString(),
                    )
                }
            }
        }
        item {
            AppTopBar(
                title = "Laporan Pertumbuhan",
                showBack = false,
                modifier = Modifier.background(Color.White)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp)
                    .background(color = MaterialTheme.colorScheme.primary)
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(color = Color.White, shape = RoundedCornerShape(12.dp))
                        .clickable(
                            onClick = {
                                if (selectedChartIndex > 0) {
                                    selectedChartIndex--
                                }
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowLeft,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }

                Text(
                    text = chartData.title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = 16.sp
                    ),
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1F)
                )
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(color = Color.White, shape = RoundedCornerShape(12.dp))
                        .clickable(
                            onClick = {
                                if (selectedChartIndex < chartTypes.lastIndex) {
                                    selectedChartIndex++
                                }
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }
        item {
            val berat = child.measurement?.weightKg ?: 0.0
            val tinggi = child.measurement?.heightCm ?: 0.0
            val lingkarKepala = child.measurement?.headCm ?: 0.0
            val usia = child.ageResult.totalMonths.toDouble()

            val (childX, childY) = when (selectedChartType) {

                ChartType.BB_USIA -> usia to berat

                ChartType.TB_USIA -> usia to tinggi

                ChartType.BB_TB -> tinggi to berat

                ChartType.LK_USIA -> usia to lingkarKepala
            }

            BasicLineChart(
                modifier = Modifier
                    .padding(horizontal = 20.dp),
                chartData = chartData,
                childY = listOf(childY),
                childX = listOf(childX)
            )
        }
        item {
            OutlinedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .padding(horizontal = 32.dp)
                            .background(
                                color = MaterialTheme.colorScheme.secondary,
                                shape = RoundedCornerShape(12.dp)
                            ),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Berat Normal",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White
                        )
                    }
                    Text(
                        text = "Berat Badan Anak sesuai usia. Lihat kurva BB/TB untuk menilai status gizi anak lebih akurat, dan pantau ulang berat badan dan tinggi badan secara berkala",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Black,
                    )
                }
            }
        }
        item {

            AppTopBar(
                title = "Rekomendasi Makanan",
                showBack = false,
                modifier = Modifier.background(Color.White)
            )
        }
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = "Lihat Semua",
                    style = MaterialTheme.typography.titleSmall.copy(
                        textDecoration = TextDecoration.Underline
                    ),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable {
                        navController.navigate("health")
                    }
                )
            }
        }
        if (recipeState is UiState.Success) {
            val recipes = (recipeState as UiState.Success<List<AllRecipes>>).data
            items(recipes.take(4)) { mpasi ->
                MpasiItemCard(
                    navController = navController,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    fontTitle = MaterialTheme.typography.titleMedium.copy(
                        lineHeight = 16.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    fontBody = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 12.sp
                    ),
                    item = mpasi
                )
            }
        }

    }
}

@Preview(showBackground = true)
@Composable
fun PreviewStaticContent() {
    Innovillage6thTheme {
        StatisticContent(
            navController = rememberNavController(),
            child = ChildWithMeasured(
                id = 1,
                name = "Rafiq",
                ageResult = AgeResult(
                    years = 1,
                    months = 1,
                    days = 1,
                    totalMonths = 1,
                    monthAndDays = "1 bulan 1 hari"
                ),
                gender = "L",
                measurement = ChildMeasure(
                    id = 1,
                    measuredAt = "2023-01-01",
                    heightCm = 160.0,
                    weightKg = 50.0,
                    armCm = 0.0,
                    headCm = 0.0
                )
            )
        )
    }
}

//    val yearList = remember {
//        getAvailableYears()
//    }
//    var selectedYear by remember { mutableStateOf(yearList.first()) }
//    val monthList = remember(selectedYear) {
//        getAvailableMonthsByYear(selectedYear)
//    }
//    var selectedMonth by remember { mutableStateOf(monthList.first()) }

//item {
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(horizontal = 20.dp),
//                horizontalArrangement = Arrangement.spacedBy(8.dp)
//            ) {
//                FilterDropdown(
//                    selectedValue = selectedMonth,
//                    items = monthList,
//                    onItemSelected = {
//                        selectedMonth = it
//                    },
//                    modifier = Modifier.weight(1f)
//                )
//                FilterDropdown(
//                    selectedValue = selectedYear,
//                    items = yearList,
//                    onItemSelected = {
//                        selectedYear = it
//                    },
//                    modifier = Modifier.width(150.dp)
//                )
//            }
//        }