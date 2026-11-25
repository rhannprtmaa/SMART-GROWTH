package id.co.raihan.smartgrowth.data.dto

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable


@Serializable
data class InsertMeasureAiDto(
    @SerializedName("sex")
    val sex : Int,
    @SerializedName("age")
    val age : Int,
    @SerializedName("weight")
    val weight : Double,
    @SerializedName("height")
    val height : Double
)

