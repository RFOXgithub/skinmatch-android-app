@file:OptIn(ExperimentalLayoutApi::class)

package com.dicoding.skripsirevisi.ui.screens.general

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.dicoding.skripsirevisi.R
import com.dicoding.skripsirevisi.data.api.ProductRecommendation
import com.dicoding.skripsirevisi.data.dataclass.Product
import com.dicoding.skripsirevisi.data.viewmodel.RutinitasViewModel
import com.dicoding.skripsirevisi.navigation.provideNavController
import com.dicoding.skripsirevisi.ui.components.BottomBar
import com.dicoding.skripsirevisi.ui.components.Loading
import com.dicoding.skripsirevisi.ui.components.ProductComp
import com.dicoding.skripsirevisi.ui.components.TitleProductComp
import com.dicoding.skripsirevisi.ui.theme.MainPink
import com.dicoding.skripsirevisi.utils.CheckedItemsDataStore
import com.dicoding.skripsirevisi.utils.RutinitasViewModelFactory
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePage(navController: NavController) {
    val context = LocalContext.current.applicationContext
    val checkedItemsDataStore = remember { CheckedItemsDataStore(context) }
    val viewModel: RutinitasViewModel = viewModel(
        factory = RutinitasViewModelFactory(checkedItemsDataStore)
    )
    var isLoading by remember { mutableStateOf(false) }
    val rutinitasData by viewModel.selectedRoutines.collectAsState()
    val rutinitasDataNight by viewModel.selectedRoutinesNight.collectAsState()
    val productListSkin by viewModel.prListSkin.collectAsState()
    val userInteraction by viewModel.userInteraction.collectAsState()
    val recommendationsState = remember { mutableStateOf<List<ProductRecommendation>>(emptyList()) }
    val productRating by viewModel.recommendations.collectAsState()
    val combinedProducts = (productRating + recommendationsState.value)
        .distinctBy { it.id }


    LaunchedEffect(Unit) {
        viewModel.getRutinitasById()
        viewModel.fetchRecommendationsRating()
        viewModel.fetchRecommendations { recommendationsResponse ->
            recommendationsState.value = recommendationsResponse?.recommendations ?: emptyList()
        }
    }

    Scaffold(
        bottomBar = { BottomBar(navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            CalendarComponent()
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
            ) {
                TitleProductComp("Produk Rekomendasi")

                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalArrangement = Arrangement.Center,
                 ) {
                    if (userInteraction != null) {
                        isLoading = true
                        combinedProducts.take(6).forEach { product ->
                            isLoading = false
                            ProductComp(
                                modifier = Modifier.clickable {
                                    navController.navigate("DetailScreen/${product.id}")
                                },
                                navController = navController,
                                product.url,
                                productName = product.product_name,
                                product.rating_avg
                            )
                        }
                    } else {
                        isLoading = true
                        productListSkin.take(6).forEach { product ->
                            isLoading = false
                            ProductComp(
                                modifier = Modifier.clickable {
                                    navController.navigate("DetailScreen/${product.id}")
                                },
                                navController = navController,
                                product.url,
                                productName = product.product_name,
                                product.ratingAvg
                            )
                        }
                    }
                }

                TitleProductComp("Rutinitas Skin Care Pagi")
                SkinCareRoutinesCard(
                    time = rutinitasData?.waktuRutinitas ?: "PAGI",
                    produkList = rutinitasData?.id_produk_list ?: emptyList(),
                    viewModel = viewModel,
                    modifier = Modifier,
                    onClickAdd = {
                        navController.navigate("NavAddRutinitasScreen/${rutinitasData?.waktuRutinitas ?: "PAGI"}")
                    },
                    onClickEdit = {
                        navController.navigate("NavEditRutinitasScreen/${rutinitasData?.waktuRutinitas ?: "PAGI"}")
                    }
                )

                TitleProductComp("Rutinitas Skin Care Malam")
                SkinCareRoutinesCard2(
                    time = rutinitasDataNight?.waktuRutinitas ?: "MALAM",
                    produkList = rutinitasDataNight?.id_produk_list ?: emptyList(),
                    viewModel = viewModel,
                    modifier = Modifier,
                    onClickAdd = {
                        navController.navigate("NavAddRutinitasScreen/${rutinitasDataNight?.waktuRutinitas ?: "MALAM"}")
                    },
                    onClickEdit = {
                        navController.navigate("NavEditRutinitasScreen/${rutinitasDataNight?.waktuRutinitas ?: "MALAM"}")
                    }
                )
            }

        }
    }
    Loading(isLoading)
}

@SuppressLint("SimpleDateFormat")
@Composable
fun CalendarComponent() {
    val calendar = Calendar.getInstance()
    val sdf = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault())
    val today = sdf.format(calendar.time)

    val days = listOf("S", "M", "T", "W", "T", "F", "S")

    val dates = (0..6).map {
        val tempCalendar = Calendar.getInstance()
        tempCalendar.add(Calendar.DAY_OF_MONTH, it - calendar.get(Calendar.DAY_OF_WEEK) + 1)
        SimpleDateFormat("dd", Locale.getDefault()).format(tempCalendar.time)
    }

    val todayDate = SimpleDateFormat("dd", Locale.getDefault()).format(calendar.time)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(124.dp)
            .clip(
                RoundedCornerShape(bottomStart = 25.dp, bottomEnd = 25.dp)
            )
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFFBF0089), Color(0xFFCBCBCB))
                )
            )
    ) {
        Image(
            painter = painterResource(id = R.drawable.calendar_image),
            contentDescription = "Background Image",
            modifier = Modifier
                .size(132.dp)
                .align(Alignment.CenterStart)
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = today,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif,
                    color = Color.Black
                ),
                modifier = Modifier.padding(top = 20.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                days.zip(dates).forEach { (day, date) ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = day,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.SansSerif,
                                color = Color.Black
                            ),
                            modifier = Modifier.padding(top = 8.dp)
                        )

                        if (date == todayDate) {
                            Box(
                                modifier = Modifier
                                    .padding(top = 6.dp)
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(MainPink),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = date,
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.SansSerif,
                                        color = Color.White
                                    )
                                )
                            }
                        } else {
                            Text(
                                text = date,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.SansSerif,
                                    color = Color.Black
                                ),
                                modifier = Modifier.padding(top = 12.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SkinCareRoutinesCard(
    time: String,
    produkList: List<Product>,
    viewModel: RutinitasViewModel,
    modifier: Modifier = Modifier,
    onClickAdd: () -> Unit,
    onClickEdit: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val checkedItems = viewModel.checkedItems

    Box(
        modifier = Modifier
            .padding(top = 12.dp, start = 16.dp, end = 16.dp)
            .fillMaxWidth()
            .wrapContentHeight()
            .border(
                width = 1.dp,
                color = Color.Black.copy(alpha = 0.05f),
                shape = RoundedCornerShape(10.dp)
            )
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, start = 16.dp, end = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.icon_triple_dot),
                    contentDescription = "Menu",
                    tint = Color.Transparent
                )

                Text(
                    text = time,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 8.sp,
                        color = Color(0xFFBF0089)
                    )
                )

                Box(
                    modifier = Modifier.wrapContentSize(Alignment.TopEnd)
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.icon_triple_dot),
                        contentDescription = "Menu",
                        modifier = Modifier.clickable { expanded = true }
                    )

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        if (produkList.isNotEmpty()) {
                            DropdownMenuItem(
                                text = { Text("Ubah Rutinitas") },
                                onClick = {
                                    expanded = false
                                    onClickEdit()
                                }
                            )
                        } else {
                            DropdownMenuItem(
                                text = { Text("Tambah Rutinitas") },
                                onClick = {
                                    expanded = false
                                    onClickAdd()
                                }
                            )
                        }
                    }
                }
            }

            if (produkList.isEmpty()) {
                Text(
                    text = "Rutinitas kosong",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Gray
                    ),
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            } else {
                produkList.forEachIndexed { index, product ->
                    val isChecked = checkedItems[product.id] ?: false

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp, start = 16.dp, end = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(product.url),
                            contentDescription = "Produk",
                            modifier = Modifier
                                .size(28.dp)
                                .clip(RoundedCornerShape(3.dp))
                        )

                        Text(
                            text = "${index + 1}. ${product.category}: ${product.product_name}",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.Black,
                                textDecoration = if (isChecked) TextDecoration.LineThrough else TextDecoration.None
                            ),
                            modifier = Modifier
                                .padding(start = 6.dp)
                                .weight(1f)
                        )

                        Icon(
                            imageVector = if (isChecked) ImageVector.vectorResource(id = R.drawable.icon_reverse_tick)
                            else ImageVector.vectorResource(id = R.drawable.icon_tick),
                            contentDescription = "Checklist",
                            modifier = Modifier
                                .size(24.dp)
                                .clickable {
                                    viewModel.toggleCheckedItem(
                                        product.id,
                                        isSecondList = false
                                    )
                                },
                            tint = Color(0xFFBF0089)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun SkinCareRoutinesCard2(
    time: String,
    produkList: List<Product>,
    viewModel: RutinitasViewModel,
    modifier: Modifier = Modifier,
    onClickAdd: () -> Unit,
    onClickEdit: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val checkedItems = viewModel.checkedItems2

    Box(
        modifier = Modifier
            .padding(top = 12.dp, start = 16.dp, end = 16.dp)
            .fillMaxWidth()
            .wrapContentHeight()
            .border(
                width = 1.dp,
                color = Color.Black.copy(alpha = 0.05f),
                shape = RoundedCornerShape(10.dp)
            )
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, start = 16.dp, end = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.icon_triple_dot),
                    contentDescription = "Menu",
                    tint = Color.Transparent
                )

                Text(
                    text = time,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 8.sp,
                        color = Color(0xFFBF0089)
                    )
                )

                Box(
                    modifier = Modifier.wrapContentSize(Alignment.TopEnd)
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.icon_triple_dot),
                        contentDescription = "Menu",
                        modifier = Modifier.clickable { expanded = true }
                    )

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        if (produkList.isNotEmpty()) {
                            DropdownMenuItem(
                                text = { Text("Ubah Rutinitas") },
                                onClick = {
                                    expanded = false
                                    onClickEdit()
                                }
                            )
                        } else {
                            DropdownMenuItem(
                                text = { Text("Tambah Rutinitas") },
                                onClick = {
                                    expanded = false
                                    onClickAdd()
                                }
                            )
                        }
                    }
                }
            }

            if (produkList.isEmpty()) {
                Text(
                    text = "Rutinitas kosong",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Gray
                    ),
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            } else {
                produkList.forEachIndexed { index, product ->
                    val isChecked = checkedItems[product.id] ?: false

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp, start = 16.dp, end = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(product.url),
                            contentDescription = "Produk",
                            modifier = Modifier
                                .size(28.dp)
                                .clip(RoundedCornerShape(3.dp))
                        )

                        Text(
                            text = "${index + 1}. ${product.category}: ${product.product_name}",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.Black,
                                textDecoration = if (isChecked) TextDecoration.LineThrough else TextDecoration.None
                            ),
                            modifier = Modifier
                                .padding(start = 6.dp)
                                .weight(1f)
                        )

                        Icon(
                            imageVector = if (isChecked) ImageVector.vectorResource(id = R.drawable.icon_reverse_tick)
                            else ImageVector.vectorResource(id = R.drawable.icon_tick),
                            contentDescription = "Checklist",
                            modifier = Modifier
                                .size(24.dp)
                                .clickable {
                                    viewModel.toggleCheckedItem(
                                        product.id,
                                        isSecondList = true
                                    )
                                },
                            tint = Color(0xFFBF0089)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}


@Preview(showBackground = true, device = Devices.PIXEL_4)
@Composable
fun HomePagePreview() {
    com.dicoding.skripsirevisi.ui.theme.SkripsiRevisiTheme {
        Column {
            HomePage(provideNavController())
        }
    }
}