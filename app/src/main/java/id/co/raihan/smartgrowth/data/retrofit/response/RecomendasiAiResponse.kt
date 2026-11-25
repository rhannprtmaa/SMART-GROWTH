package id.co.raihan.smartgrowth.data.retrofit.response

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class RecomendasiAiResponse(
    @SerializedName("diagnosis")
    val diagnosis: DiagnosisResponese,
    @SerializedName("recommendation_markdown")
    val desc: String,
    @SerializedName("model_used")
    val moodelUsed: String,
    @SerializedName("used_fallback")
    val usedFallback: Boolean,
)

@Serializable
data class DiagnosisResponese(
    @SerializedName("Height per Age")
    val heightPerAge: String,
    @SerializedName("Weight per Age")
    val weightPerAge: String,
    @SerializedName("Weight per Height")
    val heightPerWeight: String,
)