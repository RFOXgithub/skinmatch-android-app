package com.dicoding.skripsirevisi.ui.screens.admincategory

import android.util.Log
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.dicoding.skripsirevisi.R
import com.dicoding.skripsirevisi.data.viewmodel.CategoryAdminViewModel
import com.dicoding.skripsirevisi.navigation.provideNavController
import com.dicoding.skripsirevisi.ui.components.AuthGreeting
import com.dicoding.skripsirevisi.ui.components.BottomBarAdmin
import com.dicoding.skripsirevisi.ui.screens.adminproduct.AddProductList
import com.dicoding.skripsirevisi.ui.theme.MainPink

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavCategoryPage(navController: NavController) {
    val viewModel: CategoryAdminViewModel = viewModel()
    val categories by viewModel.categoryList

    Scaffold(
        bottomBar = { BottomBarAdmin(navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AuthGreeting("Selamat Datang, Admin!", "Semoga anda tetap sehat dan bugar")
            AddProductList(
                "Daftar Kategori",
                "+ Tambah Kategori",
                Modifier.padding(top = 24.dp), navController, Modifier.clickable {
                    navController.navigate("NavAddCategoryScreen")
                }
            )
            LazyColumn {
                items(categories) { cat ->
                    ListCategoryCard(navController, Modifier.clickable {
                        Log.d("RFOX", cat.id)
                        navController.navigate("NavEditCategoryScreen/${cat.id}")
                    }, cat.name)
                }
            }
        }
    }
}

@Composable
fun ListCategoryCard(navController: NavController, modifier: Modifier, name: String) {
    Column(
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp, top = 16.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(42.dp)
                .border(
                    width = 1.dp,
                    color = Color.Black.copy(alpha = 0.05f),
                    shape = RoundedCornerShape(10.dp)
                ),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Normal,
                        fontSize = 16.sp,
                        color = Color.Gray
                    ),
                    modifier = Modifier.weight(1f)
                )

                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.icon_right),
                    contentDescription = "Daftar Pengguna",
                    tint = MainPink,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true, device = Devices.PIXEL_4)
@Composable
fun NavCategoryPagePreview() {
    com.dicoding.skripsirevisi.ui.theme.SkripsiRevisiTheme {
        Column {
            NavCategoryPage(provideNavController())
        }
    }
}

