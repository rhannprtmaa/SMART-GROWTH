package id.co.mondo.ukhuwah.data.model

import kotlinx.serialization.Serializable

@Serializable
data class AllChild(
    val id: Int,
    val name: String,
    val nik: String?
)

@Serializable
data class ProfileChild(
    val id: Int,
    val name: String,
    val nik: String?,
    val gender: String,
    val birth: String,
    val heightCm: Double?,
    val weightKg: Double?,
    val headCm: Double?
)

@Serializable
data class UpdateProfileChild(
    val id: Int,
    val name: String,
    val nik: String?,
    val gender: String,
    val birth: String,
    val heightCm: Double?,
    val weightKg: Double?,
    val headCm: Double?
)


@Serializable
data class Children(
    val id: Int? = null,
    val users_id: String? = null,
    val name: String? = null,
    val nik: String? = null,
    val gender: String? =  null,
    val birth: String? = null,
    val heightCm: Double? = null,
    val weightKg: Double? = null,
    val armCm: Double? = null,
    val headCm: Double? = null,
    val created_at: String? = null,
    val measurements: List<Measurements>? = null,
    val ageResult: AgeResult? = null
)

@Serializable
data class InsertNewChildren(
    val userId: String?,
    val name: String,
    val childNo: Int,
    val nik: String?,
    val gender: String,
    val birth: String,
    val heightCm: Double?,
    val weightKg: Double?,
    val headCm: Double?
)


@Serializable
data class CountChildMeasure(
    val countChild: Int? = null,
    val countMeasure: Int? = null
)




@Serializable
data class ListMeasure(
    val childrenId: Int,
    val name: String,
    val measureId: Int,
    val measuredAt: String,
    val heightCm: Double?,
    val weightKg: Double?,
    val armCm: Double?,
    val headCm: Double?,
    val ageResult: AgeResult,
)

@Serializable
data class ChildWithMeasured(
    val id: Int,
    val name: String,
    val gender: String,
    val ageResult: AgeResult,
    val measurement: ChildMeasure?
)

@Serializable
data class ChildMeasure(
    val id: Int,
    val measuredAt: String,
    val heightCm: Double,
    val weightKg: Double,
    val armCm: Double,
    val headCm: Double
)

