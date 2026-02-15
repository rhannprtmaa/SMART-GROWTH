package id.co.mondo.ukhuwah.ui.screen

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import id.co.mondo.ukhuwah.data.model.ListMeasure
import id.co.mondo.ukhuwah.ui.common.UiState
import id.co.mondo.ukhuwah.ui.components.AppTopBar
import id.co.mondo.ukhuwah.ui.components.HistoryCard
import id.co.mondo.ukhuwah.ui.components.SelectChildren
import id.co.mondo.ukhuwah.ui.theme.Innovillage6thTheme
import id.co.mondo.ukhuwah.ui.viewmodel.ChildViewModel

@Composable
fun HistoryScreen(
    navController: NavController,
    contentPadding: PaddingValues
) {
    val context = LocalContext.current

    val childViewModel: ChildViewModel = viewModel()
    val measureWithChildState by childViewModel.allListMeasureState.collectAsState()

    val childState by childViewModel.allChildState.collectAsState()


//    val yearList = remember {
//        getAvailableYears()
//    }
//    var selectedYear by remember { mutableStateOf(yearList.first()) }
//    val monthList = remember(selectedYear) {
//        getAvailableMonthsByYear(selectedYear)
//    }
//    var selectedMonth by remember { mutableStateOf(monthList.first()) }
//
//
    var selectedChild by remember { mutableStateOf(-1) }


    LaunchedEffect(Unit) {
        childViewModel.getAllChildMeasure()
    }

    val childrenForSelect = when (childState) {
        is UiState.Success -> (childState as UiState.Success).data
        else -> emptyList()
    }

    val selectedChildName = childrenForSelect
        .firstOrNull { it.id == selectedChild }
        ?.name
        ?: "Pilih Anak"



    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
    ) {
        AppTopBar(
            title = "Riwayat Pertumbuhan",
            showBack = false
        )
        SelectChildren(
            selectedChild = selectedChildName,
            onChildSelected = {
                selectedChild = it
            },
            children = childrenForSelect
        )
        Spacer(Modifier.height(12.dp))
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(horizontal = 20.dp),
//            horizontalArrangement = Arrangement.spacedBy(8.dp)
//        ) {
//            FilterDropdown(
//                selectedValue = selectedMonth,
//                items = monthList,
//                onItemSelected = {
//                    selectedMonth = it
//                },
//                modifier = Modifier.weight(1f)
//            )
//            FilterDropdown(
//                selectedValue = selectedYear,
//                items = yearList,
//                onItemSelected = {
//                    selectedYear = it
//                },
//                modifier = Modifier.width(150.dp)
//            )
//        }
//        Spacer(Modifier.height(12.dp))

        when (measureWithChildState) {
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

            is UiState.Success -> {
                val children = (measureWithChildState as UiState.Success<List<ListMeasure>>).data
                Log.d("HistoryScreen", "Children: $children")

                val filteredChildren = if (selectedChild == -1) {
                    children
                } else {
                    children.filter {
                        it.childrenId == selectedChild
                    }
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredChildren) { measure ->
                        HistoryCard(
                            measure = ListMeasure(
                                measureId = measure.measureId,
                                name = measure.name,
                                measuredAt = measure.measuredAt,
                                headCm = measure.headCm,
                                weightKg = measure.weightKg,
                                armCm = measure.armCm,
                                heightCm = measure.heightCm,
                                ageResult = measure.ageResult,
                                childrenId = measure.childrenId,
                            ),
                            onClick = {
//                                navController.navigate("detail/${measure.measurements?.id}")
                            }
                        )
                    }
                }
            }

            is UiState.Empty -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Tidak ada data",
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
            }

            is UiState.Error -> {
                Toast.makeText(
                    context,
                    (measureWithChildState as UiState.Error).message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewHistoryScreen() {
    Innovillage6thTheme {
        HistoryScreen(
            navController = rememberNavController(),
            contentPadding = PaddingValues()
        )
    }
}