package id.co.raihan.smartgrowth.data.supabase

import android.util.Log
import id.co.raihan.smartgrowth.data.dto.AllRecipesDto
import id.co.raihan.smartgrowth.data.dto.RecipesDto
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns


class HealthService {
    private val supabase = SupabaseConfig.client


    suspend fun getAllRecipes(): Result<List<AllRecipesDto>> {
        return try {
            val select = Columns.raw(
                """
                    id,
                    image_url,
                    title,
                    age
                """.trimIndent()
            )
            val result = supabase
                .from("recipes")
                .select(select)
                .decodeList<AllRecipesDto>()
            Log.d("HealthService", "Get all recipes success: $result")
            Result.success(result)
        } catch (e: Exception) {
            Log.d("HealthService", "Get all recipes FAILED: $e")
            Result.failure(e)
        }
    }

    suspend fun getRecipeById(id: Int): Result<RecipesDto> {
        return try {
            val select = Columns.raw(
                """
                    id,
                    image_url,
                    title,
                    age,
                    recipe_steps(
                        step,
                        order_no
                    ),
                    recipe_ingredients(
                        ingredient,
                        order_no
                    ),
                    recipe_spice(
                        spice,
                        order_no
                    ),
                    recipe_nutritions(
                        energi_kkal,
                        carbohydrate_g,
                        fat_g,
                        protein_g,
                        iron_mg,
                        zinc_mg
                    ),
                    fruits(
                        fruit,
                        order_no
                    )
                """.trimIndent()
            )

            val recipe = supabase
                .from("recipes")
                .select(select) {
                    filter {
                        eq("id", id)
                    }
                }
                .decodeSingle<RecipesDto>()

            Log.d("HealthService", "Get recipes by id ($id) success: $recipe")
            Result.success(recipe)
        } catch (e: Exception) {
            Log.d("HealthService", "Get recipes by id ($id) FAILED: $e")
            Result.failure(e)
        }
    }
}