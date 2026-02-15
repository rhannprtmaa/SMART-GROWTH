package id.co.mondo.ukhuwah.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.co.mondo.ukhuwah.data.dto.UpdateUserRequestDto
import id.co.mondo.ukhuwah.data.model.AllUsers
import id.co.mondo.ukhuwah.data.model.InsertUpdateUser
import id.co.mondo.ukhuwah.data.model.ListChild
import id.co.mondo.ukhuwah.data.model.SelectUser
import id.co.mondo.ukhuwah.data.model.User
import id.co.mondo.ukhuwah.data.model.UserProfile
import id.co.mondo.ukhuwah.data.supabase.AuthService
import id.co.mondo.ukhuwah.data.supabase.UserService
import id.co.mondo.ukhuwah.data.utils.calculateAgeFromBirthToNow
import id.co.mondo.ukhuwah.ui.common.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UserViewModel(
    private val userService: UserService = UserService(),
    private val authService: AuthService = AuthService(),
) : ViewModel() {

    private val _userState = MutableStateFlow<UiState<List<AllUsers>>>(UiState.Empty)
    val userState: StateFlow<UiState<List<AllUsers>>> = _userState

    private val _userProfileState = MutableStateFlow<UiState<UserProfile>>(UiState.Empty)
    val userProfileState: StateFlow<UiState<UserProfile>> = _userProfileState

    private val _userId = MutableStateFlow<UiState<User>>(UiState.Empty)
    val userId: StateFlow<UiState<User>> = _userId

    private val _selectedUser = MutableStateFlow<UiState<List<SelectUser>>>(UiState.Empty)
    val selectedUser: StateFlow<UiState<List<SelectUser>>> = _selectedUser

    private val _updateState = MutableStateFlow<UiState<String>>(UiState.Empty)
    val updateState = _updateState.asStateFlow()

    var isRefreshing by mutableStateOf(false)
        private set

    fun getAllUser(isRefresh: Boolean = false) {
        viewModelScope.launch {
            if (isRefreshing) {
                isRefreshing = true
            } else {
                _userState.value = UiState.Loading
            }
            val response = userService.getAllUsers()
            response.onSuccess { data ->
                val user = data.sortedByDescending { it.createdAt }.map { user ->
                    AllUsers(
                        id = user.id,
                        name = user.name.lowercase().replaceFirstChar { it.uppercase() },
                        email = user.email,
                        nik = user.nik ?: "-",
                        phone = user.phone ?: "-",
                        children = user.children?.size ?: 0
                    )
                }
                Log.d("UserViewModel", "Get all users sukses: $user")
                _userState.value = UiState.Success(user)
            }.onFailure {
                Log.d("UserViewModel", "Get all users GAGAL: $it")
                _userState.value = UiState.Error("Gagal mendapatkan data")
            }
            isRefreshing = false

        }
    }

    fun getProfileUser() {
        viewModelScope.launch {
            _userProfileState.value = UiState.Loading
            val response = userService.getProfileUser()
            response.onSuccess { user ->
                val user = UserProfile(
                    id = user.id,
                    name = user.name.lowercase().replaceFirstChar { it.uppercase() },
                    isExampleEmail = user.isExampleEmail,
                    isWhitoutNik = user.isWhitoutNik,
                    children = user.children
                        ?.sortedBy { it.childNo }
                        ?.map {
                            ListChild(
                                id = it.id,
                                nameChild = it.name.lowercase()
                                    .replaceFirstChar { it.uppercase() },
                                ageResult = calculateAgeFromBirthToNow(
                                    birth = it.birth
                                ),
                                childNo = it.childNo
                            )
                        }
                )
                Log.d("UserViewModel", "SUKSES mendapatkan Profile User: $user")
                _userProfileState.value = UiState.Success(user)
            }.onFailure {
                Log.d("UserViewModel", "GAGAL mendapatkan Profile User: $it")
                _userProfileState.value = UiState.Error("Gagal mendapatkan Profile User")
            }
        }

    }

    fun getUserById(id: String) {
        Log.d("UserViewModel", "Get user by id Loading...")
        _userId.value = UiState.Loading
        viewModelScope.launch {
            val response = userService.getUserById(id)
            response.onSuccess { user ->

                if (user.id != id) {
                    _userId.value = UiState.Error("User tidak ditemukan")
                }
                val mapUser = User(
                    id = user.id,
                    name = user.name.lowercase().replaceFirstChar { it.uppercase() },
                    fatherName = user.fatherName?.lowercase()?.replaceFirstChar { it.uppercase() },
                    nik = user.nik,
                    kk = user.kk,
                    email = user.email,
                    phone = user.phone,
                    address = user.address.lowercase().replaceFirstChar { it.uppercase() },
                    isExampleEmail = user.isExampleEmail,
                    isWhitoutNik = user.isWhitoutNik,
                    children = user.children
                        ?.sortedBy { it.childNo }
                        ?.map { children ->
                            ListChild(
                                id = children.id,
                                nameChild = children.name.lowercase()
                                    .replaceFirstChar { it.uppercase() },
                                ageResult = calculateAgeFromBirthToNow(
                                    birth = children.birth
                                ),
                                childNo = children.childNo
                            )
                        }
                )
                Log.d("UserViewModel", "SUKSES Ambil data user by id: $mapUser")
                _userId.value = UiState.Success(mapUser)
            }.onFailure {
                Log.d("UserViewModel", "GAGAL Ambil data user by id: $it")
                _userId.value = UiState.Error("Gagal mendapatkan data")
            }

        }
    }

    fun selectUser() {
        _selectedUser.value = UiState.Loading
        viewModelScope.launch {
            val response = userService.selectAllUsers()
            response.onSuccess { data ->
                val user = data.map {
                    SelectUser(
                        id = it.id,
                        name = it.name.lowercase().replaceFirstChar { it.uppercase() },
                        nik = it.nik ?: ""
                    )
                }
                Log.d("UserViewModel", "SUKSES Ambil data user: $user")
                _selectedUser.value = UiState.Success(user)
            }.onFailure {
                Log.d("UserViewModel", "GAGAL Ambil data user: $it")
                _selectedUser.value = UiState.Error("Gagal mendapatkan data")
            }
        }
    }

    fun updateUser(user: InsertUpdateUser) {

        val currentUser = _userId.value

        if (currentUser !is UiState.Success) {
            _updateState.value = UiState.Error("Data user belum siap")
            return
        }

        val oldUser = currentUser.data

        val oldIsWhitoutNik = oldUser.isWhitoutNik == true
        val oldIsExampleEmail = oldUser.isExampleEmail == true

        val isNikChanged = user.nik != oldUser.nik
        val isEmailChanged = user.email != oldUser.email

        when {
            oldIsWhitoutNik && oldIsExampleEmail -> {
                if (!isNikChanged || !isEmailChanged) {
                    _updateState.value = UiState.Error("Wajib mengisi NIK dan mengganti Email")
                    return
                }
            }

            oldIsExampleEmail -> {
                if (!isEmailChanged) {
                    _updateState.value = UiState.Error("Wajib mengganti Email")
                    return
                }
            }
        }

        val updates = UpdateUserRequestDto(
            name = user.name.lowercase().replaceFirstChar { it.uppercase() }.takeIf { it != oldUser.name },
            fatherName = user.fatherName?.lowercase()?.replaceFirstChar { it.uppercase() }.takeIf { it != oldUser.fatherName },
            nik = user.nik.takeIf { it != oldUser.nik },
            email = user.email.takeIf { it != oldUser.email },
            phone = user.phone.takeIf { it != oldUser.phone },
            address = user.address.lowercase().replaceFirstChar { it.uppercase() }.takeIf { it != oldUser.address },
            kk = user.kk?.takeIf { it.isNotBlank() && it != oldUser.kk },
            isWhitoutNik = if (oldIsWhitoutNik && isNikChanged) false else null,
            isExampleEmail = if (oldIsExampleEmail && isEmailChanged) false else null
        )
        Log.d("UPDATE_PAYLOAD", updates.toString())
        if (updates == UpdateUserRequestDto()) {
            _updateState.value = UiState.Error("Tidak ada perubahan")
            return
        }

        _updateState.value = UiState.Loading
        viewModelScope.launch {
            val response = userService.updateUserById(user.id, updates)
            response.onSuccess {
                Log.d("UserViewModel", "SUKSES Update profil User, refresh user : ${updates.name}")
                _updateState.value = UiState.Success("Update profil sukses")
                _updateState.value = UiState.Empty
                val id = user.id
                getUserById(id)
            }.onFailure {
                Log.d("UserViewModel", "Update profil GAGAL: $it")
                _updateState.value = UiState.Empty
                _updateState.value = UiState.Error("Gagal update profil")
            }
        }
    }

}

