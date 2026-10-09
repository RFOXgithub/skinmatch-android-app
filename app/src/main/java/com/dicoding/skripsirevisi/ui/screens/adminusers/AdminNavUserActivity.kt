@file:OptIn(ExperimentalMaterial3Api::class)

package com.dicoding.skripsirevisi.ui.screens.adminusers

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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.dicoding.skripsirevisi.data.dataclass.Level
import com.dicoding.skripsirevisi.data.viewmodel.UserAdminViewModel
import com.dicoding.skripsirevisi.navigation.provideNavController
import com.dicoding.skripsirevisi.ui.components.AuthGreeting
import com.dicoding.skripsirevisi.ui.components.BottomBarAdmin
import com.dicoding.skripsirevisi.ui.screens.adminproduct.AddProductList
import com.dicoding.skripsirevisi.ui.theme.MainBlack
import com.dicoding.skripsirevisi.ui.theme.MainPink

@Composable
fun AdminNavUserPage(navController: NavController) {
    val viewModel: UserAdminViewModel = viewModel()
    val user by viewModel.emailList
    val categories by viewModel.categoryList
    val selectedUser by viewModel.selectedUser
    val localCategoryList = listOf(
        Level("customer"),
        Level("admin")
    )

    Scaffold(
        bottomBar = { BottomBarAdmin(navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AuthGreeting("Selamat Datang, Admin!", "Semoga anda tetap sehat dan bugar")
            CategoryDropDown(
                categories = localCategoryList,
                selectedCategory = viewModel.selectedCategory.value,
                onCategorySelected = { selectedCategory ->
                    viewModel.selectCategory(selectedCategory.level)
                }
            )
            AddProductList(
                "Daftar Pengguna",
                "+ Tambah Pengguna",
                Modifier.padding(top = 24.dp), navController, Modifier.clickable {
                    navController.navigate("NavAddUsersScreen")
                }
            )

            LazyColumn {
                val filteredUser = if (viewModel.selectedCategory.value.isNullOrEmpty()) {
                    user
                } else {
                    categories.filter { it.level == viewModel.selectedCategory.value }
                }
                items(filteredUser) { user ->
                    ListUserCard(navController, Modifier.clickable {
                        navController.navigate("NavEditUsersScreen/${user.id}")
                    }, user.email)
                }
            }
        }
    }
}

@Composable
fun CategoryDropDown(
    categories: List<Level>,
    selectedCategory: String?,
    onCategorySelected: (Level) -> Unit
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
                text = selectedCategory ?: "Pilih Jenis Pengguna",
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
                    text = { Text(category.level) },
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
fun ListUserCard(navController: NavController, modifierCard: Modifier, mail: String) {
    Column(
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp, top = 16.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = modifierCard
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
                    text = mail,
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
fun AdminNavPagePreview() {
    com.dicoding.skripsirevisi.ui.theme.SkripsiRevisiTheme {
        Column {
            AdminNavUserPage(provideNavController())
        }
    }
}
