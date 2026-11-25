package id.co.raihan.smartgrowth.ui.screen.auth

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import id.co.raihan.smartgrowth.ui.common.UiState
import id.co.raihan.smartgrowth.ui.components.ButtonCustom
import id.co.raihan.smartgrowth.ui.components.TextFieldCustom
import id.co.raihan.smartgrowth.ui.theme.Innovillage6thTheme
import id.co.raihan.smartgrowth.ui.viewmodel.AuthViewModel

@Composable
fun ForgotPasswordScreen(authViewModel: AuthViewModel) {

    val context = LocalContext.current

    val forgotState by authViewModel.forgotPasswordState.collectAsState()

    var email: String by remember { mutableStateOf("") }

    when(forgotState){
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
        is UiState.Error -> {
            Toast.makeText(
                context,
                (forgotState as UiState.Error).message,
                Toast.LENGTH_SHORT
            ).show()
        }
        else -> Unit
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(20.dp)
    ) {
        Text(
            text = "Lupa Password",
            style = MaterialTheme.typography.titleLarge.copy(
                fontSize = 32.sp,
                lineHeight = 48.sp
            ),
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Untuk mengganti pasword akun Anda,\nmasukkan email anda",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Light,
                lineHeight = 24.sp
            ),
        )
        Spacer(Modifier.height(24.dp))
        TextFieldCustom(
            modifier = Modifier.fillMaxWidth(),
            values = email,
            label = "Email",
            onValueChange = {
                email = it
            },
            keyboardType = KeyboardType.Email,
        )
        Spacer(Modifier.height(32.dp))
        ButtonCustom(
            modifier = Modifier.fillMaxWidth(),
            label = "Kirim",
            onClick = {
                Log.d("ChangePassword", "Button clicked")
                authViewModel.forgotPassword(email)
            }
        )
    }

}


@Preview(showBackground = true)
@Composable
fun PreviewForgotPasswordScreen() {
    Innovillage6thTheme {
        ForgotPasswordScreen(
            authViewModel = viewModel()
        )
    }
}
