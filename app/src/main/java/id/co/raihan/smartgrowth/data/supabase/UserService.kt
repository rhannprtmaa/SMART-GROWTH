package id.co.raihan.smartgrowth.data.supabase

import android.util.Log
import id.co.raihan.smartgrowth.data.dto.AllUsersDto
import id.co.raihan.smartgrowth.data.dto.InsertUserDto
import id.co.raihan.smartgrowth.data.dto.ProfileUserDto
import id.co.raihan.smartgrowth.data.dto.SelectUsersDto
import id.co.raihan.smartgrowth.data.dto.UpdateUserRequestDto
import id.co.raihan.smartgrowth.data.dto.UserDto
import id.co.raihan.smartgrowth.data.model.RoleUser
import id.co.raihan.smartgrowth.data.retrofit.ApiConfig
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns

class UserService {

    private val supabase = SupabaseConfig.client
    private val apiService = ApiConfig.getApiService()

    suspend fun createNewUser(user: InsertUserDto): Result<String> {
        return try {

            val response = apiService.Regist(user)
            Log.d("UserService", "Insert profile sukses: $response")

            Result.success("Berhasil membuat User")
        } catch (e: Exception) {
            Log.e("UserService", "Insert profile GAGAL", e)
            Result.failure(e)
        }
    }

    suspend fun getUserRole(): Result<RoleUser> {
        return try {
            val userId = supabase.auth.currentUserOrNull()?.id
                ?: return Result.failure(Exception("User belum login"))
            val select = Columns.raw(
                """
                    id_users,
                    name,
                    role
                """.trimIndent()
            )

            val roleUser = supabase
                .from("users")
                .select(select) {
                    filter {
                        eq("id_users", userId)
                    }
                }
                .decodeSingle<RoleUser>()

            Log.d("AuthService", "Role user = $roleUser")

            Result.success(roleUser)

        } catch (e: Exception) {
            Log.e("AuthService", "Gagal ambil role", e)
            Result.failure(e)
        }
    }

    suspend fun getProfileUser(): Result<ProfileUserDto> {
        return try {
            val userId = supabase.auth.currentUserOrNull()?.id
                ?: return Result.failure(Exception("User belum login"))
            val select = Columns.raw(
                """
                    id_users,
                    name,
                    is_example_email,
                    is_whitout_nik,
                    children(
                        id,
                        name,
                        birth,
                        child_no
                    )
                """.trimIndent()
            )
            val result = supabase
                .from("users")
                .select(select) {
                    filter {
                        eq("id_users", userId)
                    }
                }
                .decodeSingle<ProfileUserDto>()
            Log.d("UserService", "Get profile user sukses: $result")
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAllUsers(): Result<List<AllUsersDto>> {
        return try {
            val select = Columns.raw(
                """
                id_users,
                name,
                nik,
                phone,
                email,
                created_at,
                children(
                    id,
                    name
                )
                """.trimIndent()
            )
            val users = supabase
                .from("users")
                .select(select) {
                    filter {
                        eq("role", "Parent")
                    }
                }
                .decodeList<AllUsersDto>()
            Log.d("UserService", "SUKSESS Ambil List Users: $users")
            Result.success(users)
        } catch (e: Exception) {
            Log.e("UserService", "GAGAL Ambil List users", e)
            Result.failure(e)
        }
    }

    suspend fun getUserById(id: String): Result<UserDto> {
        return try {
            val select = Columns.raw(
                """
                id_users,
                name,
                father_name,
                nik,
                kk,
                email,
                phone,
                address,
                is_example_email,
                is_whitout_nik,
                children(
                    id,
                    name,
                    birth,
                    child_no
                )
                """.trimIndent()
            )
            val users = supabase
                .from("users")
                .select(select) {
                    filter {
                        eq("id_users", id)
                    }
                }
                .decodeSingle<UserDto>()
            Log.d("UserService", "Get user By Id sukses: $users")
            Result.success(users)
        } catch (e: Exception) {
            Log.e("UserService", "Get user By Id GAGAL", e)
            Result.failure(e)
        }
    }

    //    ini blum fix
    suspend fun updateUserById(userId: String, request: UpdateUserRequestDto): Result<String> {

        return try {
            val response = apiService.UpdateUsers(userId, request)

            if (response.isSuccessful) {
                Result.success(response.body()?.msg ?: "Success")
            } else {
                Result.failure(Exception("Update gagal"))
            }
        } catch (e: Exception) {
            Log.e("UserService", "Update user GAGAL", e)
            Result.failure(e)
        }
    }

//    suspend fun updateUserbyId(user: UpdateUser): Result<String> {
//
//        return try {
//            val
//            val user = apiService.UpdateUsers(
//                id =user.id,
//                updateUser = UpdateUserbyIdDto(
//
//                )
//                )
//            Log.d("UserService", "Update user sukses: $user")
//            Result.success("Update Profile Berhasil")
//        } catch (e: Exception) {
//            Log.e("UserService", "Update user GAGAL", e)
//            Result.failure(e)
//        }
//    }

    suspend fun selectAllUsers(): Result<List<SelectUsersDto>> {
        return try {
            val select = Columns.raw(
                """
                id_users,
                name,
                nik
                """.trimIndent()
            )
            val users = supabase
                .from("users")
                .select(select) {
                    filter {
                        eq("role", "Parent")
                    }
                }
                .decodeList<SelectUsersDto>()
            Log.d("UserService", "Get all users sukses: $users")
            Result.success(users)
        } catch (e: Exception) {
            Log.e("UserService", "Get all users with child GAGAL", e)
            Result.failure(e)
        }
    }


}