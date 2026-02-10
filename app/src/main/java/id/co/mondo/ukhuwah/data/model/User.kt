package id.co.mondo.ukhuwah.data.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String,
    val name: String,
    val fatherName: String?,
    val email: String,
    val nik: String?,
    val phone: String?,
    val address: String,
    val children: List<ListChild>?
)

@Serializable
data class InsertUpdateUser(
    val id: String,
//    val name: String,
    val fatherName: String,
//    val email: String,
//    val nik: String,
    val kk: String?,
    val phone: String,
    val address: String
)

@Serializable
data class UpdateUser(
    val id: String,
//    val name: String?,
    val fatherName: String?,
//    val email: String?,
//    val nik: String?,
//    val kk: String?,
    val phone: String?,
    val address: String?,
    val isWhitoutNik: Boolean,
    val isExampleEmail: Boolean
)

@Serializable
data class SelectUser(
    val id: String,
    val name: String,
    val nik: String?
)

@Serializable
data class ListChild(
    val id: Int,
    val nameChild: String,
    val ageResult: AgeResult,
    val childNo: Int,
)

@Serializable
data class InsertUser(
    val name: String,
    val fatherName: String?,
    val email: String,
    val password: String,
    val confirmPassword: String,
    val nik: String,
    val kk: String?,
    val phone: String,
    val address: String,
)

@Serializable
data class RoleUser(
    val id_users: String? = null,
    val name: String? = null,
    val role: String,
)


@Serializable
data class UserProfile(
    val id: String,
    val name: String,
    val isExampleEmail: Boolean,
    val isWhitoutNik: Boolean,
    val children: List<ListChild>?
)



@Serializable
data class AllUsers(
    val id: String,
    val name: String,
    val email: String,
    val nik: String?,
    val phone: String?,
    val children: Int
)