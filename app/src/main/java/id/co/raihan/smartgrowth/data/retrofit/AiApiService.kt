package id.co.raihan.smartgrowth.data.retrofit

import id.co.raihan.smartgrowth.data.dto.InsertMeasureAiDto
import id.co.raihan.smartgrowth.data.retrofit.response.RecomendasiAiResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AiApiService {

    @POST("rekomendasi")
    suspend fun RekomendasiAi(
        @Body request: InsertMeasureAiDto
    ): Response<RecomendasiAiResponse>

}