package id.co.mondo.ukhuwah.data.supabase

import android.util.Log
import id.co.mondo.ukhuwah.data.dto.AllUsersDto
import id.co.mondo.ukhuwah.data.dto.InsertUserDto
import id.co.mondo.ukhuwah.data.dto.ProfileUserDto
import id.co.mondo.ukhuwah.data.dto.SelectUsersDto
import id.co.mondo.ukhuwah.data.dto.UpdateUserDto
import id.co.mondo.ukhuwah.data.dto.UserDto
import id.co.mondo.ukhuwah.data.model.RoleUser
import id.co.mondo.ukhuwah.data.model.UpdateUser
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns

class UserService {

    private val supabase = SupabaseConfig.client


    suspend fun createUser(user: InsertUserDto): Result<String> {
        return try {
            val result = supabase.from("users").insert(user)
            Log.d("UserService", "Insert profile sukses: $result")
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
    suspend fun updateUser(user: UpdateUser): Result<UpdateUserDto> {

        return try {
            val user = supabase
                .from("users")
                .update(
                    {
//                        user.name?.let { set("name", it) }
                        user.fatherName?.let { set("father_name", it) }
//                        user.nik?.let { set("nik", it) }
                        user.phone?.let { set("phone", it) }
                        user.address?.let { set("address", it) }
//                        user.email?.let { set("email", it) }
                        user.isWhitoutNik.let { set("is_whitout_nik", it) }
                        user.isExampleEmail.let { set("is_example_email", it) }
                    }
                ) {
                    select()
                    filter {
                        eq("id_users", user.id)
                    }
                }
                .decodeSingle<UpdateUserDto>()
            Log.d("UserService", "Update user sukses: $user")
            Result.success(user)
        } catch (e: Exception) {
            Log.e("UserService", "Update user GAGAL", e)
            Result.failure(e)
        }
    }

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