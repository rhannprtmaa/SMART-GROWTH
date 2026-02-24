package id.co.mondo.ukhuwah.ui.screen.detail

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import id.co.mondo.ukhuwah.R
import id.co.mondo.ukhuwah.data.model.AgeResult
import id.co.mondo.ukhuwah.data.model.AllRecipes
import id.co.mondo.ukhuwah.data.model.ChartType
import id.co.mondo.ukhuwah.data.model.ChildMeasure
import id.co.mondo.ukhuwah.data.model.ChildWithMeasured
import id.co.mondo.ukhuwah.data.model.Gender
import id.co.mondo.ukhuwah.data.model.insertRekomendasiAi
import id.co.mondo.ukhuwah.ui.common.UiState
import id.co.mondo.ukhuwah.ui.components.AiRecommendationText
import id.co.mondo.ukhuwah.ui.components.AppTopBar
import id.co.mondo.ukhuwah.ui.components.BasicLineChart
import id.co.mondo.ukhuwah.ui.components.DetailItemCard
import id.co.mondo.ukhuwah.ui.components.MpasiItemCard
import id.co.mondo.ukhuwah.ui.components.getGrowthStatus
import id.co.mondo.ukhuwah.ui.theme.Innovillage6thTheme
import id.co.mondo.ukhuwah.ui.theme.Pink4
import id.co.mondo.ukhuwah.ui.viewmodel.ChildViewModel
import id.co.mondo.ukhuwah.ui.viewmodel.HealthViewModel

@Composable
fun StatisticContent(
    navController: NavController,
    child: ChildWithMeasured
) {

    val context = LocalContext.current

    val viewmodel: HealthViewModel = viewModel()
    val childViewModel: ChildViewModel = viewModel()


    val recipeState by viewmodel.recipesAgeState.collectAsState()

    val rekomendasiAiState by childViewModel.rekomendasiAiState.collectAsState()


    var selectedChartIndex by remember { mutableStateOf(0) }

    var showDialog by remember { mutableStateOf(false) }

    val gender = when (child.gender) {
        "L" -> {
            Gender.Male
        }

        "P" -> {
            Gender.Female
        }

        else -> Gender.Male
    }

    LaunchedEffect(child) {
        viewmodel.getALlRecipesByAge(child.ageResult.totalMonths)
    }




    Log.d("Statistic", "StatisticContent: $gender")

    val chartTypes = ChartType.entries.toTypedArray()

    val selectedChartType = chartTypes[selectedChartIndex]

    val chartData = provideChartData(
        gender = gender,
        chartType = selectedChartType,
    )

    if (showDialog) {
        Dialog(
            onDismissRequest = {
                showDialog = false
            }
        ) {
            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(500.dp)
                    .padding(16.dp)
            ) {
                when (val state = rekomendasiAiState) {
                    is UiState.Success -> {
                        val data = state.data

                        LazyColumn(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {

                            item {
                                Text(
                                    text = "Hasil Analisis Gizi",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontSize = 16.sp
                                    ),
                                    color = MaterialTheme.colorScheme.primary,
                                )
                            }

                            item {
                                Divider()
                            }

                            // 🔹 Diagnosis Section
                            item {
                                Text(
                                    text = "Diagnosis",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontSize = 14.sp
                                    ),
                                    color = MaterialTheme.colorScheme.primary,
                                )
                            }

                            item {
                                Text(
                                    text = "TB/U : ${data.ttAge}",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 10.sp
                                    ),
                                )
                                Text(
                                    text = "BB/U : ${data.bbAge}",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 10.sp
                                    )
                                )
                                Text(
                                    text = "BB/TB : ${data.bbTt}",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 10.sp
                                    )
                                )
                            }
                            item {
                                Divider()
                            }
                            item {
                                Text(
                                    text = "Rekomendasi",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontSize = 14.sp
                                    ),
                                    color = MaterialTheme.colorScheme.primary,
                                )
                            }

                            item {
                                AiRecommendationText(data.desc)
                            }

                            item {
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                        }
                    }

                    is UiState.Error -> {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = state.message,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }

                    is UiState.Loading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }

                    is UiState.Empty -> {
                        Box(
                            modifier = Modifier.padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Belum ada data")
                        }
                    }
                }
            }

        }
    }

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
                    value = child.ageResult.monthAndDays,
                    sizeFont = 18,
                    modifier = Modifier.size(height = 130.dp, width = 225.dp)
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

            val growthStatus = getGrowthStatus(
                chartData = chartData,
                chartType = selectedChartType,
                childX = childX,
                childY = childY
            )

            BasicLineChart(
                modifier = Modifier
                    .padding(horizontal = 20.dp),
                chartData = chartData,
                childY = listOf(childY),
                childX = listOf(childX)
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                onClick = {
                    showDialog = true
                    childViewModel.getRekomendasiAi(
                        insert = insertRekomendasiAi(
                            sex = if (child.gender == "L") 1 else 0,
                            age = child.ageResult.totalMonths,
                            height = child.measurement?.heightCm ?: 0.0,
                            weight = child.measurement?.weightKg ?: 0.0
                        )
                    )
                }
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
                            text = growthStatus.title,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White
                        )
                    }
                    Text(
                        text = growthStatus.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Black,
                    )
                }
            }
        }

        if (recipeState is UiState.Success) {
            val recipes = (recipeState as UiState.Success<List<AllRecipes>>).data
            if (recipes.isNotEmpty()) {
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
                items(recipes) { mpasi ->
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
            } else {
                item {
                    AppTopBar(
                        title = "Rekomendasi",
                        showBack = false,
                        modifier = Modifier.background(Color.White)
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
                                .padding(horizontal = 16.dp, vertical = 12.dp),
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
                                    text = "Bayi usia 0–5 bulan cukup ASI eksklusif",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontSize = 12.sp
                                    ),
                                    color = Color.White
                                )
                            }
                        }
                    }
                }

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