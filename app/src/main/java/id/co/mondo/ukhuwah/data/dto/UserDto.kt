package id.co.mondo.ukhuwah.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    @SerialName("id_users")
    val id: String,

    @SerialName("name")
    val name: String,

    @SerialName("father_name")
    val fatherName: String?,

    @SerialName("nik")
    val nik: String?,

    @SerialName("email")
    val email: String,

    @SerialName("phone")
    val phone: String?,

    @SerialName("address")
    val address: String,

    @SerialName("is_example_email")
    val isExampleEmail: Boolean,

    @SerialName("is_whitout_nik")
    val isWhitoutNik: Boolean,

    @SerialName("children")
    val children: List<ListChildDto>?
)

@Serializable
data class UpdateUserDto(
    @SerialName("id_users")
    val id: String,

    @SerialName("name")
    val name: String
)

@Serializable
data class AllUsersDto(
    @SerialName("id_users")
    val id: String,

    @SerialName("name")
    val name: String,

    @SerialName("nik")
    val nik: String?,

    @SerialName("email")
    val email: String,

    @SerialName("phone")
    val phone: String?,

    @SerialName("created_at")
    val createdAt: String,

    @SerialName("children")
    val children: List<CountChildDto>?
)

@Serializable
data class SelectUsersDto(
    @SerialName("id_users")
    val id: String,

    @SerialName("name")
    val name: String,

    @SerialName("nik")
    val nik: String?,
)

@Serializable
data class ProfileUserDto(
    @SerialName("id_users")
    val id: String,

    @SerialName("name")
    val name: String,

    @SerialName("is_example_email")
    val isExampleEmail: Boolean,

    @SerialName("is_whitout_nik")
    val isWhitoutNik: Boolean,

    @SerialName("children")
    val children: List<ListChildDto>?
)

@Serializable
data class CountChildDto(
    @SerialName("id")
    val id: Int,
    @SerialName("name")
    val name: String,
)

@Serializable
data class ListChildDto(
    @SerialName("id")
    val id: Int,
    @SerialName("name")
    val name: String,
    @SerialName("birth")
    val birth: String,
    @SerialName("child_no")
    val childNo: Int,
)

@Serializable
data class InsertUserDto(
    @SerialName("id_users")
    val id: String,

    @SerialName("name")
    val name: String,

    @SerialName("father_name")
    val fatherName: String?,

    @SerialName("nik")
    val nik: String,

    @SerialName("kk")
    val kk: String?,

    @SerialName("email")
    val email: String,

    @SerialName("phone")
    val phone: String,

    @SerialName("address")
    val address: String,

    @SerialName("is_example_email")
    val isExampleEmail: Boolean,

    @SerialName("is_whitout_nik")
    val isWhitoutNik: Boolean
)

data class AccountDto(
    val id: String,
    val email: String,
)






