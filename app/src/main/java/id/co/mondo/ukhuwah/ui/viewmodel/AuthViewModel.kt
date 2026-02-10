package id.co.mondo.ukhuwah.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.co.mondo.ukhuwah.data.dto.InsertUserDto
import id.co.mondo.ukhuwah.data.model.InsertUser
import id.co.mondo.ukhuwah.data.supabase.AuthService
import id.co.mondo.ukhuwah.data.supabase.UserService
import id.co.mondo.ukhuwah.ui.common.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authService: AuthService = AuthService(),
    private val userService: UserService = UserService()
) : ViewModel() {

    private val _loginState = MutableStateFlow<UiState<String>>(UiState.Empty)
    val loginState: StateFlow<UiState<String>> = _loginState

    private val _registerState = MutableStateFlow<UiState<String>>(UiState.Empty)
    val registerState: StateFlow<UiState<String>> = _registerState

    private val _logoutState = MutableStateFlow<UiState<String>>(UiState.Empty)
    val logoutState: StateFlow<UiState<String>> = _logoutState

    private val _isCheckingSession = MutableStateFlow(true)
    val isCheckingSession = _isCheckingSession.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn = _isLoggedIn.asStateFlow()

    private val _userRole = MutableStateFlow<String?>(null)
    val userRole = _userRole.asStateFlow()

    init {
        viewModelScope.launch {
            val loggedIn = authService.restoreSession()
            if (loggedIn) {
                loadUserRole()
            } else {
                _isLoggedIn.value = false
                _isCheckingSession.value = false
            }
        }
    }

    private suspend fun loadUserRole() {
        val result = userService.getUserRole()
        result.onSuccess { user ->
            Log.d("AuthViewModel", "Role user = $user")
            _userRole.value = user.role
            _isLoggedIn.value = true
        }.onFailure {
            Log.e("AuthViewModel", "Gagal ambil role", it)
            _isLoggedIn.value = false
            _userRole.value = null
            authService.logout()
        }
        _isCheckingSession.value = false
    }

    private fun resolveEmail(input: String): String {
        val value = input.trim()

        return if (value.contains("@")) {
            value
        } else {
            "$value@bontoulu.com"
        }
    }


    fun login(inputEmailOrUsername: String, password: String) {
        Log.d("Login", "inputEmailOrUsername: $inputEmailOrUsername")
        _loginState.value = UiState.Loading
        viewModelScope.launch {
            if (inputEmailOrUsername.isBlank()) {
                _loginState.value = UiState.Error("Username atau Email wajib diisi")
                return@launch
            }

            if (password.isBlank()) {
                _loginState.value = UiState.Error("Password wajib diisi")
                return@launch
            }

            val emailOrUsername = resolveEmail(inputEmailOrUsername)
            Log.d("Login", "Email atau Username: $emailOrUsername")

            val response = authService.login(emailOrUsername, password)
            response.onSuccess {
                Log.d("Login", "Login berhasil $emailOrUsername ")
                loadUserRole()
                _isLoggedIn.value = true
                _loginState.value = UiState.Success("Login Berhasil")
            }.onFailure {
                val errorMessage =
                    if (it.message?.contains("invalid", true) == true) {
                        "Email atau kata sandi salah"
                    } else {
                        "Login gagal, silakan coba lagi"
                    }
                Log.d("Login", "Login gagal $emailOrUsername : $it")
                _isLoggedIn.value = false
                _loginState.value = UiState.Error(errorMessage)

            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authService.logout()
            _userRole.value = null
            _isLoggedIn.value = false
            Log.d("Logout", "Logout berhasil : ${_isLoggedIn.value}")
        }
    }

    fun createAccount(
        user: InsertUser
    ) {
        _registerState.value = UiState.Loading

        viewModelScope.launch {

            if (user.email.isBlank()) {
                _registerState.value = UiState.Error("Email wajib diisi")
                return@launch
            }

            if (user.password.length < 6) {
                _registerState.value = UiState.Error("Password minimal 6 karakter")
                return@launch
            }
            if (user.password != user.confirmPassword) {
                _registerState.value = UiState.Error("Password tidak sama")
                return@launch
            }

            if (
                user.name.isNullOrBlank() ||
                user.nik.isNullOrBlank() ||
                user.phone.isNullOrBlank() ||
                user.address.isNullOrBlank()
            ) {
                _registerState.value =
                    UiState.Error("Semua data user wajib diisi")
                return@launch
            }

            val responseAuth = authService.register(user.email, user.password)
            responseAuth.onSuccess { account ->
                val setUser = InsertUserDto(
                    id = account.id,
                    name = user.name,
                    fatherName = user.fatherName.orEmpty(),
                    nik = user.nik,
                    kk = user.kk.orEmpty(),
                    email = account.email,
                    phone = user.phone,
                    address = user.address
                        .lowercase()
                        .replaceFirstChar { it.uppercase() },
                    isExampleEmail = false,
                    isWhitoutNik = false
                )

                val responseUser = userService.createUser(setUser)
                responseUser.onSuccess {
                    _registerState.value = UiState.Success("Buat Akun User Berhasil")
                    Log.d("CreateAccount", "Buat Akun user berhasil $setUser")
                }.onFailure {
                    Log.d("CreateAccount", "Gagal Menyimpan Profile $setUser")
                    val deleteAuth = authService.deleteUser(account.id)
                    Log.d("CreateAccount", "Hapus Akun di Auth berhasil $deleteAuth")
                    _registerState.value = UiState.Error("Gagal Membuat Akun User")
                }
            }.onFailure {
                Log.d("CreateAccount", "Error: ${it.message}")
                _registerState.value = UiState.Error(it.message ?: "Gagal Membuat Akun")
                return@launch
            }

        }
    }

    fun resetRegisterState() {
        _registerState.value = UiState.Empty
    }


}

