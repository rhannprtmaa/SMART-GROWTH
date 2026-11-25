package id.co.raihan.smartgrowth.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp

@Composable
fun AiRecommendationText(text: String) {

    val formattedText = buildAnnotatedString {

        val lines = text.split("\n")

        lines.forEach { line ->

            when {

                // 🔹 Heading ###
                line.startsWith("###") -> {
                    append("\n")
                    withStyle(
                        SpanStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    ) {
                        append(line.removePrefix("###").trim())
                    }
                    append("\n")
                }

                // 🔹 Bullet
                line.startsWith("- ") -> {
                    append("• ")
                    append(parseBold(line.removePrefix("- ")))
                    append("\n")
                }

                // 🔹 Bold line (contoh: **Usia**: 58 bulan)
                line.contains("**") -> {
                    append(parseBold(line))
                    append("\n")
                }

                // 🔹 Kosong
                line.isBlank() -> {
                    append("\n")
                }

                else -> {
                    append(line)
                    append("\n")
                }
            }
        }
    }

    Text(
        text = formattedText,
        style = MaterialTheme.typography.bodySmall.copy(
            fontSize = 10.sp
        ),
        lineHeight = 20.sp
    )
}

fun parseBold(text: String): AnnotatedString {
    return buildAnnotatedString {

        val parts = text.split("**")

        parts.forEachIndexed { index, part ->
            if (index % 2 == 1) {
                withStyle(
                    SpanStyle(fontWeight = FontWeight.Bold)
                ) {
                    append(part)
                }
            } else {
                append(part)
            }
        }
    }
}