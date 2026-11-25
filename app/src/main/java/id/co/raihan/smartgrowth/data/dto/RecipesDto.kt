package id.co.raihan.smartgrowth.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class AllRecipesDto(
    @SerialName("id")
    val id: Int,

    @SerialName("image_url")
    val image: String?,

    @SerialName("title")
    val title: String,

    @SerialName("age")
    val age: String,
)


@Serializable
data class RecipesDto(
    @SerialName("id")
    val id: Int,

    @SerialName("image_url")
    val image: String?,

    @SerialName("title")
    val title: String,

    @SerialName("age")
    val age: String,

    @SerialName("recipe_steps")
    val recipeSteps: List<RecipeStepsDto>,

    @SerialName("recipe_ingredients")
    val recipeIngredients: List<RecipeIngredientsDto>,

    @SerialName("recipe_spice")
    val recipeSpices: List<RecipeSpicesDto>? = null,

    @SerialName("recipe_nutritions")
    val nutritionsDto: List<NutritionsDto>,

    @SerialName("fruits")
    val fruits: List<FruitsDto>
)


@Serializable
data class RecipeStepsDto(

    @SerialName("step")
    val step: String,

    @SerialName("order_no")
    val orderNo: Int
)

@Serializable
data class RecipeIngredientsDto(

    @SerialName("ingredient")
    val ingredients: String,

    @SerialName("order_no")
    val orderNo: Int
)

@Serializable
data class RecipeSpicesDto(

    @SerialName("spice")
    val spice: String,

    @SerialName("order_no")
    val orderNo: Int
)

@Serializable
data class NutritionsDto(

    @SerialName("energi_kkal")
    val energi: Double?,

    @SerialName("carbohydrate_g")
    val karbo: Double?,

    @SerialName("fat_g")
    val lemak: Double?,

    @SerialName("protein_g")
    val protein: Double?,

    @SerialName("iron_mg")
    val zatBesi: Double?,

    @SerialName("zinc_mg")
    val zinc: Double?,
)

@Serializable
data class FruitsDto(

    @SerialName("fruit")
    val fruit: String,

    @SerialName("order_no")
    val orderNo: Int
)