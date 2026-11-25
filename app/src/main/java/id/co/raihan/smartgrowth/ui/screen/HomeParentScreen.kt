package id.co.raihan.smartgrowth.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import id.co.raihan.smartgrowth.data.model.Article
import id.co.raihan.smartgrowth.data.model.MpasiGuideList
import id.co.raihan.smartgrowth.data.model.menuItemsParent
import id.co.raihan.smartgrowth.ui.common.UiState
import id.co.raihan.smartgrowth.ui.components.ArticleItemCard
import id.co.raihan.smartgrowth.ui.components.CardChild
import id.co.raihan.smartgrowth.ui.components.CardMpasiGuide
import id.co.raihan.smartgrowth.ui.components.EmptyChildCard
import id.co.raihan.smartgrowth.ui.components.HeaderParent
import id.co.raihan.smartgrowth.ui.components.NavItemCard
import id.co.raihan.smartgrowth.ui.screen.health.MpasGuideDetailDialog
import id.co.raihan.smartgrowth.ui.theme.Innovillage6thTheme
import id.co.raihan.smartgrowth.ui.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeParentScreen(
    navController: NavController,
    contentPadding: PaddingValues,
) {

    val context = LocalContext.current

    val viewmodel: UserViewModel = viewModel()
    val state by viewmodel.userProfileState.collectAsState()

    var showDialog by remember { mutableStateOf(false) }
    var selectedMpasiGuide by remember { mutableIntStateOf(0) }


    val articles = Article.entries

    LaunchedEffect(Unit) {
        viewmodel.getProfileUser()
    }

    if (state is UiState.Loading) {
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

    val user = when (state) {
        is UiState.Success -> (state as UiState.Success).data
        else -> null
    }

    if (showDialog && selectedMpasiGuide != 0) {
        MpasGuideDetailDialog(
            data = MpasiGuideList.first { it.id == selectedMpasiGuide },
            onDismiss = {
                showDialog = false
                selectedMpasiGuide = 0
            }
        )
    }




    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentPadding = contentPadding,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                    )
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 20.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(
                        space = 12.dp,
                        alignment = Alignment.CenterVertically
                    )
                ) {
                    HeaderParent(
                        name = user?.name ?: "",
                        onClickNotification = {
                            navController.navigate("notification")
                        },
                        onClickProfile = {

                        }
                    )
                    Text(
                        text = "Profile Anak",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 18.sp
                        ),
                        color = Color.White
                    )
                }
            }
        }
        item {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .offset(y = -40.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val child = user?.children ?: emptyList()
                    if (child.isEmpty()) {
                        item {
                            EmptyChildCard()
                        }
                    } else {
                        items(child) { child ->
                            CardChild(
                                child = child,
                                onClick = {
                                    navController.navigate("profile-children/${child.id}")
                                }
                            )
                        }
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(
                        space = 24.dp,
                        alignment = Alignment.CenterHorizontally
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    menuItemsParent.forEach { item ->
                        NavItemCard(
                            title = item.title,
                            color = item.color,
                            icon = item.icon,
                            onClick = {
                                navController.navigate(item.routes)
                            }
                        )
                    }
                }
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Panduan MPASI",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 12.sp,
                        ),
                        color = Color.Black,
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
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
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Artikel Kesehatan",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 12.sp,
                        ),
                        color = Color.Black,
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(articles) { article ->
                            ArticleItemCard(
                                modifier = Modifier
                                    .size(
                                        width = 225.dp,
                                        height = 200.dp
                                    ),
                                item = article
                            )
                        }
                    }
                }
            }
        }
    }

}

@Preview(showBackground = true)
@Composable
fun PreviewHomeParentScreen() {
    Innovillage6thTheme {
        HomeParentScreen(navController = rememberNavController(), contentPadding = PaddingValues())
    }
}