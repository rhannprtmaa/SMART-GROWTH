package id.co.raihan.smartgrowth.ui.screen.health

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import id.co.raihan.smartgrowth.data.model.AllRecipes
import id.co.raihan.smartgrowth.data.model.Article
import id.co.raihan.smartgrowth.data.model.MpasiGuideList
import id.co.raihan.smartgrowth.ui.common.UiState
import id.co.raihan.smartgrowth.ui.components.AppTopBar
import id.co.raihan.smartgrowth.ui.components.ArticleItemCard
import id.co.raihan.smartgrowth.ui.components.CardMpasiGuide
import id.co.raihan.smartgrowth.ui.components.MpasiItemCard
import id.co.raihan.smartgrowth.ui.theme.Innovillage6thTheme
import id.co.raihan.smartgrowth.ui.viewmodel.HealthViewModel

@Composable
fun HealthScreen(
    navController: NavController,
    contentPadding: PaddingValues
) {

    val viewmodel: HealthViewModel = viewModel()
    val recipeState by viewmodel.recipesState.collectAsState()

    val tabs = listOf("Artikel", "MPASI")
    var selectedTabIndex by remember { mutableStateOf(1) }

    var showDialog by remember { mutableStateOf(false) }
    var selectedMpasiGuide by remember { mutableIntStateOf(0) }

    val articles = Article.entries

    if (showDialog && selectedMpasiGuide != 0) {
        MpasGuideDetailDialog(
            data = MpasiGuideList.first { it.id == selectedMpasiGuide },
            onDismiss = {
                showDialog = false
                selectedMpasiGuide = 0
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
    ) {
        AppTopBar(
            title = "Informasi Kesehatan",
            showBack = true,
            onBackClick = {
                navController.popBackStack()
            },
            showBookmark = true,
            onBookmarkClick = {

            }
        )
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = Color.White,
            divider = {}
        ) {
            tabs.forEachIndexed { index, title ->
                val selected = selectedTabIndex == index

                Tab(
                    selected = selected,
                    onClick = { selectedTabIndex = index },
                    modifier = Modifier
                        .background(
                            Color.White,
                        ),
                    text = {
                        Text(
                            text = title,
                            color = if (selected)
                                Color.Black
                            else
                                Color.Gray,
                            style = if (selected) {
                                MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold
                                )
                            } else {
                                MaterialTheme.typography.titleMedium
                            }
                        )
                    }
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth(),
            contentPadding = PaddingValues(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {

            when (selectedTabIndex) {
                0 -> {
                    items(articles) { article ->
                        ArticleItemCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp),
                            item = article
                        )
                    }
                }

                1 -> {
                    item {
                        Text(
                            text = "Panduan MPASI",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontSize = 16.sp
                            ),
                            modifier = Modifier.padding(horizontal = 20.dp)
                        )
                    }
                    item {
                        LazyRow(
                            modifier = Modifier
                                .fillMaxWidth(),
                            contentPadding = PaddingValues(horizontal = 20.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(MpasiGuideList) { item ->
                                CardMpasiGuide(
                                    item = item,
                                    onClick = {
                                        selectedMpasiGuide = item.id
                                        showDialog = true
                                    }
                                )
                            }
                        }
                    }
                    item {
                        Text(
                            text = "Resep MPASI",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontSize = 16.sp
                            ),
                            modifier = Modifier.padding(horizontal = 20.dp)
                        )
                    }
                    when (recipeState) {
                        is UiState.Error -> {
                            val message = (recipeState as UiState.Error).message
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = message,
                                        style = MaterialTheme.typography.titleMedium,
                                    )
                                }
                            }
                        }

                        is UiState.Success -> {
                            val recipes = (recipeState as UiState.Success<List<AllRecipes>>).data
                            items(recipes) { recipe ->
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
                                    item = recipe
                                )
                            }
                        }

                        is UiState.Empty -> {
                            item {
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
                        }

                        is UiState.Loading -> {
                            item {
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
                        }
                    }

                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewDetailScreen() {
    Innovillage6thTheme {
        HealthScreen(
            navController = rememberNavController(),
            contentPadding = PaddingValues()
        )
    }
}