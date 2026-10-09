@file:OptIn(ExperimentalMaterial3Api::class)

package com.dicoding.skripsirevisi.ui.screens.general

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.dicoding.skripsirevisi.R
import com.dicoding.skripsirevisi.data.viewmodel.ProductViewModel
import com.dicoding.skripsirevisi.data.viewmodel.SearchViewModel
import com.dicoding.skripsirevisi.navigation.provideNavController
import com.dicoding.skripsirevisi.ui.components.BottomBar
import com.dicoding.skripsirevisi.ui.components.OnlyHeader
import com.dicoding.skripsirevisi.ui.components.TitleProductComp
import com.dicoding.skripsirevisi.ui.theme.MainBlack
import com.dicoding.skripsirevisi.ui.theme.MainPink
import com.dicoding.skripsirevisi.ui.theme.MainWhite

@Composable
fun SearchPage(navController: NavController) {
    val viewModel: SearchViewModel = viewModel()
    val viewModelPr: ProductViewModel = viewModel()
    val productList by viewModel.filteredProducts.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val categories by viewModel.categoryList
    var selectedCategory by remember { mutableStateOf("Semua Produk") }
    val allCategories = listOf("Semua Produk") + categories.map { it.name }

    val products by viewModel.products
    val product by viewModel.prList.collectAsState()

    Scaffold(
        bottomBar = { BottomBar(navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            OnlyHeader(title = "Pencarian")
            SearchBar(
                searchQuery = searchQuery,
                onSearch = { viewModel.updateSearchQuery(it) }
            )

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(allCategories) { categoryName ->
                    val isSelected = categoryName == selectedCategory
                    val bgColor = if (isSelected) MainPink else Color(0xFFE565C1)

                    CategoryCard(
                        modifier = Modifier.clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) {
                            selectedCategory = categoryName
                            if (categoryName == "Semua Produk") {
                                viewModel.updateSearchQuery("")
                                viewModel.selectCategory("")
                            } else {
                                viewModel.selectCategory(categoryName)
                            }
                        },
                        category = categoryName,
                        backgroundColor = bgColor
                    )
                }
            }

            viewModel.selectedCategory.value
                ?.takeIf { it.isNotBlank() }
                ?.let { TitleProductComp(it) }
                ?: TitleProductComp("Semua Produk")

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.padding(16.dp),
                contentPadding = PaddingValues(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val filteredProducts = productList.filter { product ->
                    val matchesQuery = searchQuery.isEmpty() || product.product_name.contains(
                        searchQuery,
                        ignoreCase = true
                    )
                    val matchesCategory =
                        viewModel.selectedCategory.value.isNullOrEmpty() || product.category == viewModel.selectedCategory.value
                    matchesQuery && matchesCategory
                }

                if (filteredProducts.isEmpty()) {
                    item(span = { GridItemSpan(3) }) {
                        EmptyText()
                    }
                } else {
                    items(filteredProducts) { data ->
                        ProductCard(
                            modifier = Modifier.clickable {
                                viewModelPr.addUserInteraction(data.id) { suc, _ ->
                                    if (suc) {
                                        navController.navigate("DetailScreen/${data.id}")
                                    }
                                }
                            },
                            navController,
                            imageUrl = data.url,
                            productName = data.product_name,
                            rating = data.ratingAvg
                        )
                    }
                }
            }

        }
    }
}

@Composable
fun EmptyText() {
    Text(
        text = "Produk tidak ditemukan",
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        style = MaterialTheme.typography.bodyMedium,
        textAlign = TextAlign.Center
    )
}

@Composable
fun CategoryCard(modifier: Modifier = Modifier, category: String, backgroundColor: Color) {
    Box(
        modifier = modifier
            .padding(top = 16.dp)
            .wrapContentWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(backgroundColor)
            .height(30.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = category,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif,
                color = MainWhite
            ),
            modifier = Modifier
                .padding(horizontal = 10.dp)
        )
    }
}


@Composable
fun ProductCard(
    modifier: Modifier,
    navController: NavController,
    imageUrl: String,
    productName: String,
    rating: String
) {
    val viewModel: SearchViewModel = viewModel()
    val productList by viewModel.filteredProducts.collectAsState()

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .width(105.dp)
            .height(170.dp)
            .border(
                width = 1.dp,
                color = Color.Black.copy(alpha = 0.05f),
                shape = RoundedCornerShape(10.dp)
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Image(
                painter = rememberAsyncImagePainter(imageUrl),
                contentDescription = "Product",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .padding(start = 8.dp, end = 8.dp, top = 6.dp)
                    .clip(RoundedCornerShape(10.dp)),
                contentScale = ContentScale.Crop
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
                modifier = Modifier.padding(top = 4.dp, start = 4.dp, bottom = 4.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.icon_bintang),
                    contentDescription = "Rating",
                    modifier = Modifier.size(12.dp)
                )

                Text(
                    text = String.format("%.1f", rating.toFloat()),
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

@Composable
fun SearchBar(searchQuery: String, onSearch: (String) -> Unit) {
    Box(
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp, top = 16.dp)
            .fillMaxWidth()
            .height(45.dp)
            .border(
                color = Color.Black.copy(alpha = 0.05f),
                shape = RoundedCornerShape(8.dp),
                width = 1.dp
            ),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.padding(start = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.icon_search_dua),
                contentDescription = "Search",
                modifier = Modifier
                    .size(24.dp)
                    .padding(end = 6.dp),
                tint = Color.Gray,
            )

            TextField(
                value = searchQuery,
                onValueChange = { onSearch(it) },
                placeholder = {
                    Text(
                        text = "Cari Produk Pilihanmu!",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight.Normal,
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(fontSize = 14.sp),
                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Red,
                    containerColor = Color.Transparent
                )
            )
        }
    }
}

@Preview(showBackground = true, device = Devices.PIXEL_4)
@Composable
fun SearchPagePreview() {
    com.dicoding.skripsirevisi.ui.theme.SkripsiRevisiTheme {
        Column {
            SearchPage(provideNavController())
        }
    }
}
