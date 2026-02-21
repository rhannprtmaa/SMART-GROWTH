package id.co.mondo.ukhuwah.ui.screen.detail

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import id.co.mondo.ukhuwah.ui.common.UiState
import id.co.mondo.ukhuwah.ui.components.AppTopBar
import id.co.mondo.ukhuwah.ui.components.SelectChildren
import id.co.mondo.ukhuwah.ui.theme.Innovillage6thTheme
import id.co.mondo.ukhuwah.ui.viewmodel.AuthViewModel
import id.co.mondo.ukhuwah.ui.viewmodel.ChildViewModel

@Composable
fun DetailScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    contentPadding: PaddingValues,

//    childIdFromNav: Int
) {
    val context = LocalContext.current

    val childViewModel: ChildViewModel = viewModel()
    val measuredChildState by childViewModel.childWithMeasureState.collectAsState()

    val childState by childViewModel.allChildState.collectAsState()
    val userRole by authViewModel.userRole.collectAsState()


    val tabs = listOf("Statistik", "Perjalanan")
    var selectedTabIndex by remember { mutableStateOf(0) }





    var selectedChild by remember { mutableStateOf(-1) }

    val childrenForSelect = when (childState) {
        is UiState.Success -> (childState as UiState.Success).data
        else -> emptyList()
    }

    val child = childrenForSelect
        .firstOrNull { it.id == selectedChild }
        ?.name ?: "Pilih Anak"


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
    ) {
        AppTopBar(
            title = "Statistik Pertumbuhan",
            showBack = false
        )
        SelectChildren(
            selectedChild = child,
            onChildSelected = { id ->
                selectedChild = id
                childViewModel.getChildWithMeasure(id)
            },
            children = childrenForSelect,
            isUser = userRole == "Parent",
            isAdmin = userRole == "Admin"
        )
        Spacer(Modifier.height(12.dp))
        when {
            selectedChild  == -1 -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Silahkan pilih Profile Anak",
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
            }
            measuredChildState is UiState.Loading -> {
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

            measuredChildState is UiState.Success -> {
                val child = (measuredChildState as UiState.Success).data

                StatisticContent(
                    navController = navController,
                    child = child
                )
//                when (selectedTabIndex) {
//                    0 -> {
//                    }
//
//                    1 -> GrowthContent()
//                }
            }

            measuredChildState is UiState.Empty -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Belum ada data pengukuran",
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
            }

            measuredChildState is UiState.Error -> {
                Toast.makeText(
                    context,
                    (measuredChildState as UiState.Error).message,
                    Toast.LENGTH_SHORT
                ).show()
            }

        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewDetailScreen() {
    Innovillage6thTheme {
        DetailScreen(
            navController = rememberNavController(),
            contentPadding = PaddingValues(),
            authViewModel = viewModel()
//            childIdFromNav = -1
        )
    }
}