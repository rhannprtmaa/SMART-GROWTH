package id.co.mondo.ukhuwah.data.retrofit

import id.co.mondo.ukhuwah.data.dto.InsertUserDto
import id.co.mondo.ukhuwah.data.dto.UpdateUserRequestDto
import id.co.mondo.ukhuwah.data.retrofit.response.ServiceResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    @POST("users/register")
    suspend fun Regist(
        @Body regiterUser: InsertUserDto
    )

    @PATCH("users/update-profile/{id}")
    suspend fun UpdateUsers(
        @Path("id") id: String,
        @Body request: UpdateUserRequestDto
    ): Response<ServiceResponse>
}