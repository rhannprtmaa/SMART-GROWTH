package id.co.mondo.ukhuwah.data.model

import kotlinx.serialization.Serializable


@Serializable
data class Recipes(
    val id: Int? = null,
    val image: String?,
    val title: String,
    val age: String,
    val recipeSteps: List<String>,
    val recipeIngredients: List<String>,
    val recipeSpices: List<String>,
    val nutritions: Nutritions,
    val fruits: List<String>
)

@Serializable
data class Nutritions(
    val energi: Double?,
    val karbo: Double?,
    val lemak: Double?,
    val protein: Double?,
    val zatBesi: Double?,
    val zinc: Double?,
)

@Serializable
data class AllRecipes(
    val id: Int,
    val image: String?,
    val title: String,
    val age: String,
)

