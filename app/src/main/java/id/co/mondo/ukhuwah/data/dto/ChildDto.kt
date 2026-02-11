package id.co.mondo.ukhuwah.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class InsertNewChildDto(
    @SerialName("users_id")
    val userId: String?,
    @SerialName("name")
    val name: String,
    @SerialName("child_no")
    val childNo: Int,
    @SerialName("nik")
    val nik: String?,
    @SerialName("gender")
    val gender: String,
    @SerialName("birth")
    val birth: String,
    @SerialName("heightCm")
    val heightCm: Double?,
    @SerialName("weightKg")
    val weightKg: Double?,
    @SerialName("headCm")
    val headCm: Double?
)


@Serializable
data class AllChildDto(
    @SerialName("id")
    val id: Int,
    @SerialName("name")
    val name: String,
    @SerialName("nik")
    val nik: String?
)

@Serializable
data class ChildWithMeasureDto(
    @SerialName("id")
    val id: Int,
    @SerialName("name")
    val name: String,
    @SerialName("birth")
    val birth: String,
    @SerialName("measurements")
    val measurements: List<MeasureDto>?
)

@Serializable
data class MeasureDto(
    @SerialName("id")
    val id: Int,

    @SerialName("measured_at")
    val measuredAt: String,

    @SerialName("heightCm")
    val heightCm: Double?,

    @SerialName("weightKg")
    val weightKg: Double?,

    @SerialName("armCm")
    val armCm: Double?,

    @SerialName("headCm")
    val headCm: Double?,
)

@Serializable
data class ChildDto(
    @SerialName("id")
    val id: Int,

    @SerialName("name")
    val name: String,

    @SerialName("nik")
    val nik: String?,

    @SerialName("gender")
    val gender: String,

    @SerialName("birth")
    val birth: String,

    @SerialName("heightCm")
    val heightCm: Double?,

    @SerialName("weightKg")
    val weightKg: Double?,

    @SerialName("headCm")
    val headCm: Double?,
)

@Serializable
data class UpdateChildDto(
    @SerialName("id")
    val id: Int,

    @SerialName("name")
    val name: String
)

@Serializable
data class CountChildWithMeasureDto(
    @SerialName("id")
    val id: Int,

    @SerialName("name")
    val name: String,

    @SerialName("measurements")
    val measurements: List<CountMeasureDto>?
)

@Serializable
data class CountMeasureDto(
    @SerialName("id")
    val id: Int,

    @SerialName("measured_at")
    val measuredAt: String
)

