package id.co.raihan.smartgrowth.ui.screen.family

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import id.co.raihan.smartgrowth.data.model.ProfileChild
import id.co.raihan.smartgrowth.data.model.UpdateProfileChild
import id.co.raihan.smartgrowth.ui.common.UiState
import id.co.raihan.smartgrowth.ui.components.AppTopBar
import id.co.raihan.smartgrowth.ui.components.GenderField
import id.co.raihan.smartgrowth.ui.components.TextFieldCustom
import id.co.raihan.smartgrowth.ui.components.TextFieldDateCustom
import id.co.raihan.smartgrowth.ui.theme.Innovillage6thTheme
import id.co.raihan.smartgrowth.ui.viewmodel.ChildViewModel

@Composable
fun ProfileChildrenScreen(
    navController: NavController,
    id: Int,
    contentPadding: PaddingValues
) {

    val context = LocalContext.current

    val childViewModel: ChildViewModel = viewModel()
    val state by childViewModel.childId.collectAsState()
    val updateState by childViewModel.updateChildId.collectAsState()


    var name by remember { mutableStateOf("") }
    var nik by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("") }
    var tb by remember { mutableStateOf("") }
    var bb by remember { mutableStateOf("") }
    var lk by remember { mutableStateOf("") }

    var isEditMode by remember { mutableStateOf(false) }
    var enabled by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(id) {
        childViewModel.getChildById(id)
    }

    LaunchedEffect(state) {
        when (state) {
            is UiState.Success -> {
                val child = (state as UiState.Success<ProfileChild>).data
                Log.d("ProfileChildrenScreen", "Child: $child")
                name = child.name
                nik = child.nik.orEmpty()
                gender = child.gender
                birthDate = child.birth
                tb = child.heightCm.toString()
                bb = child.weightKg.toString()
                lk = child.headCm.toString()
            }

            is UiState.Error -> {
                Toast.makeText(
                    context,
                    (state as UiState.Error).message,
                    Toast.LENGTH_SHORT
                ).show()
            }

            else -> Unit

        }
    }

    LaunchedEffect(updateState) {
        if (updateState is UiState.Success) {
            Toast.makeText(
                context,
                "Data berhasil diupdate",
                Toast.LENGTH_SHORT
            ).show(
            )
        }
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

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Text(
                    text = "Update",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            text = {
                Text(text = "Yakin ki mau Simpan perubahan?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        val nikValue = nik.ifBlank { null }
                        childViewModel.updateChild(
                            UpdateProfileChild(
                                id = id,
                                name = name,
                                nik = nikValue,
                                gender = gender,
                                birth = birthDate,
                                heightCm = tb.toDouble(),
                                weightKg = bb.toDouble(),
                                headCm = lk.toDouble()
                            )
                        )
                        enabled = false
                        isEditMode = false
                        showDialog = false
                    }
                ) {
                    Text(
                        text = "Simpan",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    )
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        enabled = false
                        isEditMode = false
                        showDialog = false
                    }
                ) {
                    Text(
                        text = "Kembali",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    )
                }
            }
        )
    }



    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding),
    ) {
        AppTopBar(
            title = "Profile Anak",
            showBack = true,
            onBackClick = {
                navController.popBackStack()
            },
            showEdit = true,
            isEditMode = isEditMode,
            onSaveClick = {
                if (isEditMode) {
                    showDialog = true
                } else {
                    enabled = true
                    isEditMode = true
                }
            }
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TextFieldCustom(
                modifier = Modifier.fillMaxWidth(),
                values = name,
                label = "Nama",
                onValueChange = {
                    name = it
                },
                keyboardType = KeyboardType.Text,
                enabled = enabled
            )
            TextFieldCustom(
                modifier = Modifier.fillMaxWidth(),
                values = nik,
                label = "NIK",
                onValueChange = {
                    nik = it
                },
                keyboardType = KeyboardType.Number,
                enabled = enabled
            )
            GenderField(
                selectedGender = gender,
                onGenderSelected = {
                    gender = it
                },
                enabled = enabled
            )
            TextFieldDateCustom(
                modifier = Modifier.fillMaxWidth(),
                value = birthDate,
                label = "Tanggal Lahir",
                onDateSelected = { birthDate = it },
                enabled = enabled
            )
            Text(
                text = "Data kelahiran",
                style = MaterialTheme.typography.titleMedium,
                color = Color.Black
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TextFieldCustom(
                        modifier = Modifier.weight(1f),
                        values = tb,
                        label = "TB",
                        onValueChange = {
                            tb = it
                        },
                        keyboardType = KeyboardType.Decimal,
                        enabled = enabled
                    )
                    TextFieldCustom(
                        modifier = Modifier.weight(1f),
                        values = bb,
                        label = "BB",
                        onValueChange = {
                            bb = it
                        },
                        keyboardType = KeyboardType.Decimal,
                        enabled = enabled
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TextFieldCustom(
                        modifier = Modifier.weight(1f),
                        values = lk,
                        label = "LK",
                        onValueChange = {
                            lk = it
                        },
                        keyboardType = KeyboardType.Decimal,
                        enabled = enabled
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewProfileChildrenScreen() {
    Innovillage6thTheme {
        ProfileChildrenScreen(
            navController = rememberNavController(),
            contentPadding = PaddingValues(),
            id = 1
        )
    }
}