package id.co.raihan.smartgrowth.ui.screen.health

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import id.co.raihan.smartgrowth.R
import id.co.raihan.smartgrowth.ui.common.UiState
import id.co.raihan.smartgrowth.ui.components.AppTopBar
import id.co.raihan.smartgrowth.ui.components.NutritionContent
import id.co.raihan.smartgrowth.ui.components.SimpleDescriptionContent
import id.co.raihan.smartgrowth.ui.theme.Innovillage6thTheme
import id.co.raihan.smartgrowth.ui.theme.Pink4
import id.co.raihan.smartgrowth.ui.viewmodel.HealthViewModel

@Composable
fun DetailMpasiScreen(
    navController: NavController,
    contentPadding: PaddingValues,
    id: Int
) {

    val viewModel: HealthViewModel = viewModel()
    val recipeByIdState by viewModel.recipeByIdState.collectAsState()

    LaunchedEffect(id) {
        viewModel.getRecipeById(id)
    }

    when (recipeByIdState) {
        is UiState.Loading -> {

        }

        is UiState.Error -> {

        }

        is UiState.Success -> {
            val item = (recipeByIdState as UiState.Success).data

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding)
            ) {
                AppTopBar(
                    title = "Resep Detail",
                    showBack = true,
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    item {
                        Image(
                            painter = painterResource(R.drawable.bubursotoayamsantan),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp)
                                .height(200.dp)
                                .shadow(8.dp, RoundedCornerShape(percent = 25))
                        )
                    }
                    item {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(24.dp)
                        ) {
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    lineHeight = 24.sp
                                )
                            )
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.babyface),
                                    contentDescription = null,
                                    tint = Pink4,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = item.age,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontSize = 12.sp
                                    ),
                                    color = Color.Gray
                                )
                            }
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(24.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.eye),
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.secondary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = "-",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontSize = 12.sp
                                        )
                                    )
                                }
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.favorite),
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.secondary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = "-",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontSize = 12.sp
                                        )
                                    )
                                }
                                Icon(
                                    painter = painterResource(R.drawable.bookmark),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }

                    item {
                        SimpleDescriptionContent(
                            title = "Bahan",
                            items = item.recipeIngredients
                        )
                    }
                    if (item.recipeSpices.isNotEmpty()) {
                        item {
                            SimpleDescriptionContent(
                                title = "Bumbu",
                                items = item.recipeSpices
                            )
                        }

                    }
                    item {
                        SimpleDescriptionContent(
                            title = "Cara Membuat",
                            items = item.recipeSteps
                        )
                    }
                    item {
                        SimpleDescriptionContent(
                            title = "Buah",
                            items = item.fruits
                        )
                    }
                    item {
                        NutritionContent(
                            nutritions = item.nutritions
                        )
                    }
                }
            }
        }

        else -> Unit
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewDetailMpasiScreen() {
    Innovillage6thTheme {
        DetailMpasiScreen(
            navController = rememberNavController(),
            contentPadding = PaddingValues(),
            id = 1
        )
    }
}
