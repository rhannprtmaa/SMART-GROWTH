package id.co.mondo.ukhuwah.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.co.mondo.ukhuwah.R
import id.co.mondo.ukhuwah.data.model.AgeResult
import id.co.mondo.ukhuwah.data.model.ListChild
import id.co.mondo.ukhuwah.ui.theme.Innovillage6thTheme
import id.co.mondo.ukhuwah.ui.theme.Pink4

@Composable
fun CardChild(
    child: ListChild,
    onClick: () -> Unit
) {

    val age = child.ageResult

    Card(
        modifier = Modifier.size(width = 215.dp, height = 110.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .background(
                        Pink4,
                        RoundedCornerShape(
                            topStart = 12.dp,
                            topEnd = 12.dp
                        )
                    )
                    .padding(horizontal = 8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .offset(
                            y = 18.dp
                        )
                        .background(
                            color = Color.LightGray,
                            shape = CircleShape
                        )
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp)
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = child.nameChild,
                        color = Color.Black,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 14.sp
                        ),
                    )
                    Text(
                        text = "${age.years} Tahun ${age.months} Bulan ${age.days} Hari",
                        color = Color.Black,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Light
                        ),
                    )
                }
                Icon(
                    painter = painterResource(R.drawable.chevronright),
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun EmptyChildCard() {
    Card(
        modifier = Modifier.size(width = 215.dp, height = 110.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Belum ada data anak",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }
    }
}



@Preview(showBackground = true)
@Composable
fun PreviewCardChild() {
    Innovillage6thTheme {
        Column(Modifier.fillMaxSize()) {
            CardChild(
                child = ListChild(
                    nameChild = "Bambang",
                    ageResult = AgeResult(
                        years = 1,
                        months = 2,
                        days = 3,
                        totalMonths = 4,
                        monthAndDays = "5 bulan 6 hari"
                    ),
                    id = 1,
                    childNo = 1
                ),
                onClick = {}

            )
        }
    }
}