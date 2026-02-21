package id.co.mondo.ukhuwah.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import id.co.mondo.ukhuwah.data.model.AllChild
import id.co.mondo.ukhuwah.ui.theme.Innovillage6thTheme
import id.co.mondo.ukhuwah.ui.theme.Pink4


@Composable
fun SelectChildren(
    selectedChild: String,
    onChildSelected: (Int) -> Unit,
    children: List<AllChild>,
    isUser: Boolean = true,
    isAdmin: Boolean = false
) {

    var showDropdown by remember { mutableStateOf(false) }

    var isSearchMode by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    val queryChild = if (searchQuery.isBlank()) {
        children
    } else {
        children.filter {
            it.name.contains(searchQuery, ignoreCase = true)
        }
    }

    if (isSearchMode) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    IconButton(
                        onClick = {
                            isSearchMode = false
                            searchQuery = ""
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                    SearchField(
                        query = searchQuery,
                        onQueryChange = { searchQuery = it }
                    )
                }

                if (queryChild.isNotEmpty()) {
                    OutlinedCard {
                        LazyColumn(Modifier.fillMaxWidth()) {
                            items(queryChild) { child ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            onChildSelected(child.id)

                                            isSearchMode = false
                                            searchQuery = ""
                                        }
                                        .padding(12.dp)
                                ) {
                                    Column(
                                        verticalArrangement = Arrangement.spacedBy(2.dp)
                                    ) {
                                        Text(
                                            text = child.name,
                                            style = MaterialTheme.typography.titleMedium,
                                            color = Color.DarkGray
                                        )
                                        Text(
                                            text = child.nik ?: "-",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color.Gray
                                        )
                                    }
                                }
                            }
                        }
                    }
                } else {
                    Text(
                        text = "User tidak ditemukan",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.primary)
                .padding(horizontal = 20.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "Pilih Profile Anak",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                textAlign = TextAlign.Center,
                color = Color.White
            )
        }

        if (isAdmin) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()

            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Pink4)
                        .padding(horizontal = 20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = selectedChild,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color.Black,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = {
                            isSearchMode = true
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search"
                        )
                    }
                }
            }
        }
        if (isUser) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()

            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Pink4)
                        .padding(horizontal = 20.dp)
                        .clickable(
                            onClick = {
                                showDropdown = true
                            }
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = selectedChild,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color.Black,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = Color.Black
                    )
                }
                DropdownMenu(
                    expanded = showDropdown,
                    onDismissRequest = { showDropdown = false },
                    modifier = Modifier
                        .background(Color.White)
                        .width(200.dp)
                        .align(Alignment.TopEnd),
                    offset = DpOffset(0.dp, 4.dp)
                ) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = "Pilih Anak",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = Color.Black
                            )
                        },
                        onClick = {
                            onChildSelected(-1)
                            showDropdown = false
                        }
                    )
                    children.forEach { child ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = child.name,
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = Color.Black
                                )
                            },
                            onClick = {
                                onChildSelected(child.id)
                                showDropdown = false
                            }
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSelectChildren() {
    Innovillage6thTheme {
        var selectedChild by remember { mutableStateOf("Semua") }
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            SelectChildren(
                selectedChild = selectedChild,
                onChildSelected = { id ->
                    selectedChild = id.toString()
                },
                children = listOf(
                    AllChild(id = 1, name = "Anak 1", nik = "1234567890123456"),
                    AllChild(id = 1, name = "Anak 1", nik = "1234567890123456"),
                    AllChild(id = 1, name = "Anak 1", nik = "1234567890123456")
                ),
                isUser = false,
                isAdmin = true

            )
        }
    }
}
