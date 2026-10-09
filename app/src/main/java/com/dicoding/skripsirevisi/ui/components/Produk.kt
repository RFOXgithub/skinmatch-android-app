package com.dicoding.skripsirevisi.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.dicoding.skripsirevisi.R
import com.dicoding.skripsirevisi.ui.theme.MainBlack

@Composable
fun TitleProductComp(title: String) {
    Row(
        modifier = Modifier
            .padding(top = 24.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(R.drawable.title_content),
            contentDescription = "Title",
            modifier = Modifier
                .padding(end = 8.dp)
                .size(20.dp)
        )

        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = FontFamily.SansSerif,
                color = Color.Black
            )
        )
    }
}

@Composable
fun ProductComp(
    modifier: Modifier,
    navController: NavController,
    imageUrl: String,
    productName: String,
    avgRating: String
) {
    Box(
        modifier = modifier
            .padding(top = 8.dp)
            .clip(RoundedCornerShape(10.dp))
            .width(105.dp)
            .height(155.dp)
            .border(
                width = 1.dp,
                color = Color.Black.copy(alpha = 0.05f),
                shape = RoundedCornerShape(10.dp)
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 4.dp)
        ) {
            Image(
                painter = rememberAsyncImagePainter(imageUrl),
                contentDescription = "Product",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .padding(start = 8.dp, end = 12.dp, top = 12.dp)
                    .clip(
                        RoundedCornerShape(23.dp)
                    )
            )

            Text(
                text = productName,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = FontFamily.SansSerif,
                    color = MainBlack,
                    lineHeight = 12.sp
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 12.dp, start = 4.dp)
            )

            Row(
                modifier = Modifier.padding(top = 3.dp, start = 4.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.icon_bintang),
                    contentDescription = "Rating",
                    modifier = Modifier.size(12.dp)
                )



                Text(
                    text = String.format("%.1f", avgRating.toFloat()),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Normal,
                        fontFamily = FontFamily.SansSerif,
                        color = MainBlack
                    ),
                    modifier = Modifier.padding(start = 2.dp)
                )
            }
        }
    }
}

