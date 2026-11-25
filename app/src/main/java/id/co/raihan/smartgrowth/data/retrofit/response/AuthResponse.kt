package id.co.raihan.smartgrowth.data.retrofit.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ServiceResponse(
    @SerialName("message")
    val msg: String
)

