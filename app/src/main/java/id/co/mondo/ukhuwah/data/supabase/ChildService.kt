package id.co.mondo.ukhuwah.data.supabase

import android.util.Log
import id.co.mondo.ukhuwah.data.dto.AllChildDto
import id.co.mondo.ukhuwah.data.dto.ChildDto
import id.co.mondo.ukhuwah.data.dto.ChildWithMeasureDto
import id.co.mondo.ukhuwah.data.dto.CountChildWithMeasureDto
import id.co.mondo.ukhuwah.data.dto.InsertMeasureAiDto
import id.co.mondo.ukhuwah.data.dto.InsertNewChildDto
import id.co.mondo.ukhuwah.data.dto.UpdateChildDto
import id.co.mondo.ukhuwah.data.model.NewMeasure
import id.co.mondo.ukhuwah.data.model.UpdateProfileChild
import id.co.mondo.ukhuwah.data.retrofit.ApiConfig
import id.co.mondo.ukhuwah.data.retrofit.response.RecomendasiAiResponse
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns

class ChildService {

    private val supabase = SupabaseConfig.client
    private val AiApiService = ApiConfig.getAiApiService()

    suspend fun getAllChild(): Result<List<AllChildDto>> {
        return try {
            val selector = Columns.raw(
                """
                id,
                name,
                nik
            """.trimIndent()
            )
            val result = supabase
                .from("children")
                .select(selector)
                .decodeList<AllChildDto>()
            Log.d("ChildService", "Get all child success: $result")
            Result.success(result)
        } catch (e: Exception) {
            Log.d("ChildService", "Get all child failed: $e")
            Result.failure(e)
        }
    }

    suspend fun getChildWithMeasureById(id: Int): Result<ChildWithMeasureDto> {
        return try {
            val select = Columns.raw(
                """
                id,
                name,
                gender,
                birth,
                measurements(
                    id,
                    measured_at,
                    heightCm,
                    weightKg,
                    armCm,
                    headCm
                )
                """.trimIndent()
            )
            val child = supabase
                .from("children")
                .select(select) {
                    filter {
                        eq("id", id)
                    }
                }
                .decodeSingle<ChildWithMeasureDto>()
            Log.d("ChildService", "Get Child with measured By id sukses: $child")
            Result.success(child)
        } catch (e: Exception) {
            Log.e("ChildService", "Get Child with measured  By id GAGAL", e)
            Result.failure(e)
        }

    }

    suspend fun getCountChildWithMeasure(): Result<List<CountChildWithMeasureDto>>{
        return try {
            val select = Columns.raw(
                """
                id,
                name,
                measurements(
                    id,
                    measured_at
                )
                """.trimIndent()
            )
            val child = supabase
                .from("children")
                .select(select)
                .decodeList<CountChildWithMeasureDto>()
            Result.success(child)
        }catch (e: Exception){
            Log.e("UserService", "Get count child & measure GAGAL", e)
            Result.failure(e)
        }
    }

    suspend fun getChildById(id: Int): Result<ChildDto> {
        return try {
            val select = Columns.raw(
                """
                id,
                users_id,
                name,
                nik,
                gender,
                birth,
                heightCm,
                weightKg,
                headCm,
                created_at
                """.trimIndent()
            )
            val child = supabase
                .from("children")
                .select(select) {
                    filter {
                        eq("id", id)
                    }
                }
                .decodeSingle<ChildDto>()
            Log.d("UserService", "Get Child By Id sukses: $child")
            Result.success(child)
        } catch (e: Exception) {
            Log.e("UserService", "Get Child  By Id GAGAL", e)
            Result.failure(e)
        }

    }

    suspend fun getListMeasure(): Result<List<ChildWithMeasureDto>> {
        return try {
            val select = Columns.raw(
                """
                id,
                name,
                gender,
                birth,
                measurements(
                    id,
                    measured_at,
                    heightCm,
                    weightKg,
                    armCm,
                    headCm
                )
                """.trimIndent()
            )
            val child = supabase
                .from("children")
                .select(select)
                .decodeList<ChildWithMeasureDto>()
            Log.d("UserService", "Get all children & Measure sukses: $child")
            Result.success(child)
        } catch (e: Exception) {
            Log.e("UserService", "Get all children & Measure GAGAL", e)
            Result.failure(e)
        }
    }

    suspend fun insertNewMeasureChild(measure: NewMeasure): Result<Unit> {
        return try {
            val result = supabase
                .from("measurements")
                .insert(measure)
            Log.d("UserService", "Insert new Measure sukses: $result")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("UserService", "Insert new Measure GAGAL", e)
            Result.failure(e)
        }
    }

    suspend fun insertNewChildUser(child: InsertNewChildDto): Result<Unit> {
        return try {
            val result = supabase
                .from("children")
                .insert(child)
            Log.d("UserService", "Insert new child sukses: $result")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("UserService", "Insert new child GAGAL", e)
            Result.failure(e)
        }
    }

    suspend fun updateChild(child: UpdateProfileChild): Result<UpdateChildDto> {

        return try {
            val setChild = supabase
                .from("children")
                .update(
                    {
                        child.name?.let { set("name", it) }
                        child.nik?.let { set("nik", it) }
                        child.gender?.let { set("gender", it) }
                        child.birth?.let { set("birth", it) }
                        child.heightCm?.let { set("heightCm", it) }
                        child.weightKg?.let { set("weightKg", it) }
                        child.headCm?.let { set("headCm", it) }
                    }
                ) {
                    select(Columns.raw("id, name"))
                    filter {
                        eq("id", child.id)
                    }
                }
                .decodeSingle<UpdateChildDto>()
            Log.d("UserService", "Update child sukses: $child")
            Result.success(setChild)
        } catch (e: Exception) {
            Log.e("UserService", "Update child GAGAL", e)
            Result.failure(e)
        }
    }


    suspend fun recommendationAi(insert: InsertMeasureAiDto): Result<RecomendasiAiResponse>{
        return try {
            val response = AiApiService.RekomendasiAi(insert)

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Log.d("ChildService", "Rekomendasi AI sukses: $body")
                    Result.success(body)
                } else {
                    Result.failure(Exception("Response body is null"))
                }
            } else {
                Result.failure(Exception("Error: ${response.code()} ${response.message()}"))
            }

        } catch (e: Exception) {
            Log.e("ChildService", "Rekomendasi AI GAGAL", e)
            Result.failure(e)
        }
    }
}