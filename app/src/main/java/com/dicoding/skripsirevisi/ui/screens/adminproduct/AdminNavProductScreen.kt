@file:OptIn(ExperimentalMaterial3Api::class)

package com.dicoding.skripsirevisi.ui.screens.adminproduct

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.dicoding.skripsirevisi.R
import com.dicoding.skripsirevisi.data.dataclass.Category
import com.dicoding.skripsirevisi.data.viewmodel.ProductViewModel
import com.dicoding.skripsirevisi.navigation.provideNavController
import com.dicoding.skripsirevisi.ui.components.AuthGreeting
import com.dicoding.skripsirevisi.ui.components.BottomBarAdmin
import com.dicoding.skripsirevisi.ui.screens.general.SearchBar
import com.dicoding.skripsirevisi.ui.theme.MainBlack
import com.dicoding.skripsirevisi.ui.theme.MainPink
import com.dicoding.skripsirevisi.ui.theme.SkripsiRevisiTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavProductPage(navController: NavController) {
    val viewModel: ProductViewModel = viewModel()
    val product by viewModel.prList.collectAsState()
    val products by viewModel.products
    val categories by viewModel.categoryList
    val searchQuery by viewModel.searchQuery.collectAsState()
    val productList by viewModel.filteredProducts.collectAsState()

    Scaffold(
        bottomBar = { BottomBarAdmin(navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AuthGreeting("Selamat Datang, Admin!", "Semoga anda tetap sehat dan bugar")
            SearchBar(
                searchQuery = searchQuery,
                onSearch = { viewModel.updateSearchQuery(it) }
            )
            ProductCategory(
                categories = categories,
                selectedCategory = viewModel.selectedCategory.value,
                onCategorySelected = { selectedCategory ->
                    viewModel.selectCategory(selectedCategory.name)
                }
            )

            AddProductList(
                "Daftar Produk",
                "+ Tambah Produk",
                Modifier.padding(bottom = 12.dp),
                navController,
                Modifier.clickable {
                    navController.navigate("NavAddProductScreen")
                })
            LazyColumn {
                val filteredProducts = productList.filter { product ->
                    val matchesCategory =
                        viewModel.selectedCategory.value.isNullOrEmpty() || product.category == viewModel.selectedCategory.value
                    val matchesSearchQuery = searchQuery.isEmpty() || product.product_name.contains(
                        searchQuery,
                        ignoreCase = true
                    )

                    matchesCategory && matchesSearchQuery
                }

                items(filteredProducts) { data ->
                    AdminProductCard(
                        navController,
                        Modifier,
                        productName = data.product_name,
                        cat = data.category,
                        desc = data.desc,
                        skinType = data.skin,
                        imageUrl = data.url,
                        onClick = {
                            navController.navigate("NavEditProductScreen/${data.id}")
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ProductCategory(
    categories: List<Category>,
    selectedCategory: String?,
    onCategorySelected: (Category) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .height(42.dp)
            .border(
                width = 1.dp,
                shape = RoundedCornerShape(10.dp),
                color = Color.Black.copy(alpha = 0.05f)
            )
            .clickable { expanded = true },
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = selectedCategory ?: "Pilih Kategori",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp,
                    color = MainBlack
                ),
                modifier = Modifier.weight(1f)
            )

            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.icon_below),
                contentDescription = "Action",
                modifier = Modifier.size(24.dp),
                tint = MainPink
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            categories.forEach { category ->
                DropdownMenuItem(
                    text = { Text(category.name) },
                    onClick = {
                        onCategorySelected(category)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun AddProductListTemp(
    title: String,
    addText: String,
    modifier: Modifier,
    navController: NavController,
    modifierText: Modifier,
    modifierTemp: Modifier
) {
    Row(
        modifier = modifier
            .padding(start = 16.dp, end = 16.dp, top = 8.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif,
                color = MainBlack
            ),
            modifier = modifierTemp
        )

        Text(
            text = addText,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif,
                color = MainPink
            ),
            modifier = modifierText
        )
    }
}

@Composable
fun AddProductList(
    title: String,
    addText: String,
    modifier: Modifier,
    navController: NavController,
    modifierText: Modifier,
) {
    Row(
        modifier = modifier
            .padding(start = 16.dp, end = 16.dp, top = 8.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif,
                color = MainBlack
            )
        )

        Text(
            text = addText,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif,
                color = MainPink
            ),
            modifier = modifierText
        )
    }
}

@Composable
fun AdminProductCard(
    navController: NavController,
    modifier: Modifier,
    productName: String,
    cat: String,
    desc: String,
    skinType: String,
    imageUrl: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .wrapContentHeight()
            .border(
                width = 1.dp,
                shape = RoundedCornerShape(15.dp),
                color = Color.Black.copy(alpha = 0.05f)
            ),
        contentAlignment = Alignment.CenterStart
    ) {
        Column(
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            Row(
                modifier = Modifier.padding(8.dp)
            ) {
                Image(
                    painter = rememberAsyncImagePainter(imageUrl),
                    contentDescription = "Produk",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(10.dp)),
                    contentScale = ContentScale.Crop
                )

                Column(
                    modifier = Modifier.padding(start = 4.dp)
                ) {
                    Text(
                        text = productName,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.SansSerif,
                            color = MainBlack
                        ),
                        modifier = Modifier.padding(bottom = 6.dp)
                    )

                    Text(
                        text = cat,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal,
                            fontFamily = FontFamily.SansSerif,
                            color = Color.Gray
                        ),
                        modifier = Modifier.padding(bottom = 4.dp)
                    )

                    Divider(
                        color = Color.Black.copy(alpha = 0.05f),
                        thickness = 1.dp,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(
                        text = skinType,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal,
                            fontFamily = FontFamily.SansSerif,
                            color = Color.Gray
                        ),
                        modifier = Modifier.padding(bottom = 4.dp)
                    )

                    Divider(
                        color = Color.Black.copy(alpha = 0.05f),
                        thickness = 1.dp,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(
                        text = desc,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal,
                            fontFamily = FontFamily.SansSerif,
                            color = Color.Gray,
                            lineHeight = 12.sp
                        ),
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            OutlinedButton(
                onClick = { onClick() },
                modifier = modifier
                    .fillMaxWidth()
                    .height(30.dp)
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(15.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color.Black
                ),
                border = BorderStroke(1.dp, Color.Gray)
            ) {
                Text("Edit Produk")
            }
        }
    }
}

@Preview(showBackground = true, device = Devices.PIXEL_4)
@Composable
fun NavProductPagePreview() {
    SkripsiRevisiTheme {
        Column {
            NavProductPage(provideNavController())
        }
    }
}