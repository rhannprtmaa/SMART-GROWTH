package id.co.mondo.ukhuwah.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.co.mondo.ukhuwah.data.model.AllRecipes
import id.co.mondo.ukhuwah.data.model.Nutritions
import id.co.mondo.ukhuwah.data.model.Recipes
import id.co.mondo.ukhuwah.data.supabase.HealthService
import id.co.mondo.ukhuwah.ui.common.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HealthViewModel : ViewModel() {

    private val _recipeByIdState =
        MutableStateFlow<UiState<Recipes>>(UiState.Empty)
    val recipeByIdState: StateFlow<UiState<Recipes>> = _recipeByIdState

    private val _recipesState = MutableStateFlow<UiState<List<AllRecipes>>>(UiState.Empty)
    val recipesState: StateFlow<UiState<List<AllRecipes>>> = _recipesState

    init {
        getALlRecipes()
    }

    fun getALlRecipes() {
        _recipesState.value = UiState.Loading

        viewModelScope.launch {
            val response = HealthService().getAllRecipes()
            response.onSuccess { list ->
                val response = list.map { dto ->
                    AllRecipes(
                        id = dto.id,
                        image = dto.image,
                        title = dto.title,
                        age = dto.age
                    )

                }
                Log.d("HealthViewModel", "getALlRecipes: $response")
                _recipesState.value = UiState.Success(response)
            }
                .onFailure {
                    Log.d("HealthViewModel", "getALlRecipes: $it")
                    _recipesState.value = UiState.Error("Gagal mendapatkan data")
                }
        }
    }


    fun getRecipeById(id: Int) {
        _recipeByIdState.value = UiState.Loading
        viewModelScope.launch {
            val response = HealthService().getRecipeById(id)

            response.onSuccess { recipe ->

                val nutrition = recipe.nutritionsDto.firstOrNull()

                val response = Recipes(
                    id = recipe.id,
                    image = recipe.image,
                    title = recipe.title,
                    age = recipe.age,
                    recipeSteps = recipe.recipeSteps.sortedBy { it.orderNo }.map { it.step },
                    recipeIngredients = recipe.recipeIngredients.sortedBy { it.orderNo }.map { it.ingredients },
                    recipeSpices = recipe.recipeSpices?.sortedBy { it.orderNo }?.map { it.spice } ?: emptyList(),
                    nutritions = Nutritions(
                        energi = nutrition?.energi ?: 0.0,
                        karbo = nutrition?.karbo ?: 0.0,
                        lemak = nutrition?.lemak ?: 0.0,
                        protein = nutrition?.protein ?: 0.0,
                        zatBesi = nutrition?.zatBesi ?: 0.0,
                        zinc = nutrition?.zinc ?: 0.0
                    ),
                    fruits = recipe.fruits.sortedBy { it.orderNo }.map { it.fruit }
                )

                _recipeByIdState.value = UiState.Success(response)
            }.onFailure {
                _recipeByIdState.value =
                    UiState.Error("Gagal mendapatkan data")
            }
        }
    }


}