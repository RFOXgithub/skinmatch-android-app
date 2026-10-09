@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package com.dicoding.skripsirevisi.ui.screens.general

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.dicoding.skripsirevisi.R
import com.dicoding.skripsirevisi.data.dataclass.Ingredient
import com.dicoding.skripsirevisi.data.viewmodel.LoginViewModel
import com.dicoding.skripsirevisi.data.viewmodel.ProductViewModel
import com.dicoding.skripsirevisi.navigation.provideNavController
import com.dicoding.skripsirevisi.ui.components.Header
import com.dicoding.skripsirevisi.ui.components.ModalCardSuccess
import com.dicoding.skripsirevisi.ui.theme.MainBlack
import com.dicoding.skripsirevisi.ui.theme.MainPink
import com.dicoding.skripsirevisi.ui.theme.MainWhite
import com.dicoding.skripsirevisi.utils.LoginViewModelFactory

@Composable
fun DetailPage(navController: NavController) {
    val context = LocalContext.current.applicationContext
    val viewModelLogin: LoginViewModel = viewModel(factory = LoginViewModelFactory(context))
    val viewModel: ProductViewModel = viewModel()
    val detailId = navController.currentBackStackEntry?.arguments?.getString("detailId")
    val selectedProduct by viewModel.selectedProduct.collectAsState()
    val selectedRating by viewModel.selectedRating
    val idUser by viewModelLogin.userId.collectAsState(initial = null)
    val rating by viewModel.selectedRatingUser.collectAsState(initial = null)

    val isFormValid by remember {
        derivedStateOf {
            selectedRating > 0
        }
    }

    var showModalSuccess by remember { mutableStateOf(false) }
    if (showModalSuccess) {
        Dialog(onDismissRequest = { }) {
            ModalCardSuccess(
                painterResource(id = R.drawable.success_green),
                "Ubah Rating Berhasil",
                "Data rating anda akan masuk pada menu riwayat",
                "Selesai",
                navController, {
                    navController.navigate("HistoryScreen") {
                        popUpTo("HistoryScreen") { inclusive = true }
                    }
                },
                Modifier
            )
        }
    }

    var showModalSuccess2 by remember { mutableStateOf(false) }
    if (showModalSuccess2) {
        Dialog(onDismissRequest = { }) {
            ModalCardSuccess(
                painterResource(id = R.drawable.success_green),
                "Beri Rating Berhasil",
                "Data rating anda akan masuk pada menu riwayat",
                "Selesai",
                navController, {
                    navController.navigate("HistoryScreen") {
                        popUpTo("HistoryScreen") { inclusive = true }
                    }
                },
                Modifier
            )
        }
    }

    LaunchedEffect(detailId, idUser, rating) {
        viewModel.getProductById(detailId.toString())
        viewModel.getRatingById(detailId.toString(), idUser.toString())
        rating?.let {
            viewModel.updateRating(it.rating_value)
        }
    }

    Scaffold(
        bottomBar = {
            Button(
                onClick = {
                    if (rating != null) {
                        viewModel.updateRating(detailId.toString(), rating!!.id_rating) { success ->
                            if (success) {
                                showModalSuccess = true
                            }
                        }
                    } else {
                        viewModel.addRating(detailId.toString(), idUser.toString()) { success, _ ->
                            if (success) {
                                showModalSuccess2 = true
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                enabled = isFormValid,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MainPink,
                    contentColor = MainWhite
                )
            ) {
                Text(if (rating != null) "Ubah Rating" else "Beri Rating")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 16.dp)
        ) {
            Header("Detail Produk", navController)
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .fillMaxSize()
            ) {
                selectedProduct?.let { product ->
                    DetailImage(product.url)
                    ProductName(
                        product.product_name,
                        product.category,
                        product.ratingAvg,
                        product.ratingCount,
                        product.skin
                    )
                    Rating(
                        selectedRating = selectedRating,
                        onRatingChange = viewModel::updateRating,
                        title = if (rating != null) "Kamu sudah memberikan rating untuk produk ini!" else "Bagaimana Hasilnya di Kulitmu?",
                        if (rating != null) "Apakah kamu ingin memperbarui rating atau membagikan pengalaman lebih lanjut tentang perubahan yang kamu rasakan?" else "Apakah produk ini membuat kulitmu lebih sehat dan glowing? Beri rating dan bagikan perubahan yang kamu rasakan!"
                    )
                    Description(product.desc)
                    IngredientCard(product.ingredients)
                }
            }
        }
    }
}

@Composable
fun DetailImage(imageUrl: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .padding(start = 16.dp, end = 16.dp, top = 24.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Image(
            painter = rememberAsyncImagePainter(imageUrl),
            contentDescription = "Produk",
            modifier = Modifier
                .fillMaxHeight()
                .clip(RoundedCornerShape(60.dp)),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun ProductName(
    productName: String,
    cat: String,
    rating: String,
    ratingCount: String,
    skin: String
) {
    Column(
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 24.dp)
    ) {
        SelectionContainer {
            Text(
                text = buildAnnotatedString {
                    append("$productName ")
                    withStyle(
                        style = SpanStyle(color = MainBlack.copy(alpha = 0.5f))
                    ) {
                        append("($skin)")
                    }
                },
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = FontFamily.SansSerif,
                    color = MainBlack
                )
            )
        }

        Row(
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.title_content),
                contentDescription = "Produk",
                modifier = Modifier.size(18.dp)
            )

            Text(
                text = cat,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    fontFamily = FontFamily.SansSerif,
                    color = MainBlack
                ),
                modifier = Modifier
                    .padding(start = 4.dp)
                    .weight(1f)
            )

            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(R.drawable.icon_bintang_shade),
                        contentDescription = "Rating",
                        modifier = Modifier.size(24.dp)
                    )

                    Text(
                        text = String.format("%.1f", rating.toFloat()),
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal,
                            fontFamily = FontFamily.SansSerif,
                            color = MainBlack
                        ),
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }

                Text(
                    text = ("($ratingCount Penilaian)"),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        fontFamily = FontFamily.SansSerif,
                        color = Color.Gray
                    ),
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

@Composable
fun Description(desc: String) {
    Column(
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 12.dp)
    ) {
        Text(
            text = "Deskripsi",
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = FontFamily.SansSerif,
                color = MainBlack
            )
        )

        Text(
            text = desc,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = FontFamily.SansSerif,
                color = Color.Gray
            ),
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Composable
fun IngredientCard(ingredients: List<Ingredient>) {
    Box(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .wrapContentHeight()
            .border(
                width = 1.dp,
                color = Color.Black.copy(alpha = 0.05f),
                shape = RoundedCornerShape(20.dp)
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "${ingredients.size} INGREDIENTS",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = FontFamily.SansSerif,
                    color = MainPink,
                    letterSpacing = 6.sp
                ),
                modifier = Modifier.padding(top = 16.dp)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 48.dp, bottom = 16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            ingredients.forEachIndexed { index, (ingredient, description) ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = if (index == 0) 0.dp else 8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .background(color = MainPink, shape = RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${index + 1}",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                fontFamily = FontFamily.SansSerif,
                                color = MainWhite
                            )
                        )
                    }

                    Column(modifier = Modifier.padding(start = 12.dp)) {
                        Text(
                            text = ingredient,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                fontFamily = FontFamily.SansSerif,
                                color = MainBlack
                            )
                        )
                        Text(
                            text = description,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Normal,
                                fontFamily = FontFamily.SansSerif,
                                color = Color.Gray
                            ),
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Rating(
    selectedRating: Long,
    onRatingChange: (Long) -> Unit,
    title: String,
    subTitle: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = FontFamily.SansSerif,
                    color = MainBlack
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = subTitle,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(5) { index ->
                    Icon(
                        imageVector = ImageVector.vectorResource(
                            id = if (index < selectedRating) R.drawable.icon_bintang_filled
                            else R.drawable.icon_bintang_empty
                        ),
                        contentDescription = "Rating",
                        modifier = Modifier
                            .size(40.dp)
                            .padding(end = 9.dp)
                            .clickable { onRatingChange((index + 1).toLong()) },
                        tint = Color(0xFFFEC84B)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, device = Devices.PIXEL_4)
@Composable
fun DetailPagePreview() {
    com.dicoding.skripsirevisi.ui.theme.SkripsiRevisiTheme {
        Column {
            DetailPage(provideNavController())
        }
    }
}
