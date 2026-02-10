package id.co.mondo.ukhuwah.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import id.co.mondo.ukhuwah.data.model.Nutritions
import id.co.mondo.ukhuwah.ui.theme.Innovillage6thTheme
import id.co.mondo.ukhuwah.ui.theme.abu

@Composable
fun NutritionContent(
    nutritions: Nutritions
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 20.dp),
    ) {
        Text(
            text = "Kandungan Per Porsi",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold
            )
        )
        Spacer(Modifier.height(12.dp))
        NutritionItem(
            name = "Energi",
            value = if (nutritions.energi == 0.0) "-" else "${nutritions.energi} kkal",
            isGray = true
        )
        NutritionItem(
            name = "Karbohidrat",
            value = if (nutritions.karbo == 0.0) "-" else "${nutritions.karbo} g",
            isGray = false
        )
        NutritionItem(
            name = "Lemak",
            value = if (nutritions.lemak == 0.0) "-" else "${nutritions.lemak} g",
            isGray = true
        )
        NutritionItem(
            name = "Protein",
            value = if (nutritions.protein == 0.0) "-" else "${nutritions.protein} g",
            isGray = false
        )
        NutritionItem(
            name = "Zat besi",
            value = if (nutritions.zatBesi == 0.0) "-" else "${nutritions.zatBesi} mg",
            isGray = true
        )
        NutritionItem(
            name = "Zinc",
            value = if (nutritions.zinc == 0.0) "-" else "${nutritions.zinc} mg",
            isGray = false
        )
    }
}

@Composable
fun NutritionItem(
    name: String,
    value: String,
    isGray: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (isGray) abu else Color.Transparent)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Normal
            ),
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Normal
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewNutritionContent() {
    Innovillage6thTheme {
        Column {
            NutritionContent(
                nutritions = Nutritions(
                    energi = 100.0,
                    karbo = 20.0,
                    lemak = 30.0,
                    protein = 40.0,
                    zatBesi = 50.0,
                    zinc = 60.0
                )
            )
        }
    }
}