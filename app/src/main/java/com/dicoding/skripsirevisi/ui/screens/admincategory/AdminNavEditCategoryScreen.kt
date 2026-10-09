@file:OptIn(ExperimentalMaterial3Api::class)

package com.dicoding.skripsirevisi.ui.screens.admincategory

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.dicoding.skripsirevisi.R
import com.dicoding.skripsirevisi.data.viewmodel.CategoryAdminViewModel
import com.dicoding.skripsirevisi.navigation.provideNavController
import com.dicoding.skripsirevisi.ui.components.HeaderTwoIconActive
import com.dicoding.skripsirevisi.ui.components.Loading
import com.dicoding.skripsirevisi.ui.components.ModalCardError
import com.dicoding.skripsirevisi.ui.components.ModalCardQuest
import com.dicoding.skripsirevisi.ui.components.ModalCardSuccess
import com.dicoding.skripsirevisi.ui.theme.MainBlack
import com.dicoding.skripsirevisi.ui.theme.MainPink
import com.dicoding.skripsirevisi.ui.theme.MainWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminNavEditCategoryPage(navController: NavController) {
    val viewModel: CategoryAdminViewModel = viewModel()
    var isLoading by remember { mutableStateOf(false) }
    val categoryId = navController.currentBackStackEntry?.arguments?.getString("categoryId")
    val products by viewModel.products
    val cat by viewModel.cat.collectAsState()

    val isFormValid by remember {
        derivedStateOf {
            viewModel.categoryName.isNotBlank()
        }
    }

    LaunchedEffect(categoryId) {
        viewModel.selectCategory(categoryId.toString())
    }
    LaunchedEffect(cat) {
        if (cat.isNotEmpty()) {
            viewModel.selectCategoryProduct(cat)
        }
    }


    var showModalDelete by remember { mutableStateOf(false) }
    if (showModalDelete) {
        Dialog(onDismissRequest = { }) {
            ModalCardSuccess(
                painterResource(id = R.drawable.success_green),
                "Penghapusan kategori berhasil",
                "Kategori akan dihapus dari database",
                "Kembali",
                navController, {
                    navController.navigate("NavCategoryScreen") {
                        popUpTo("NavCategoryScreen") { inclusive = true }
                    }
                },
                Modifier
            )
        }
    }

    var showModalQuest by remember { mutableStateOf(false) }
    if (showModalQuest) {
        Dialog(onDismissRequest = { showModalQuest = false }) {
            ModalCardQuest(
                painterResource(id = R.drawable.icon_tanda_tanya_biru),
                "Apakah anda ingin menghapus data kategori?",
                "Data anda akan dihapus dari sistem",
                "Lanjutkan",
                navController,
                {
                    viewModel.deleteCategory(categoryId.toString()) { success, _ ->
                        if (success) {
                            showModalDelete = true
                        }
                    }
                    showModalQuest = false
                },
                Modifier
            )
        }
    }

    var showModalError by remember { mutableStateOf(false) }
    if (showModalError) {
        Dialog(onDismissRequest = { }) {
            ModalCardError(
                painterResource(id = R.drawable.failed_red),
                "Hapus Kategori Gagal",
                "Masih terdapat produk dengan kategori yang ingin dihapus",
                "Kembali",
                navController,
                { showModalError = false })
        }
    }

    var showModalEdit by remember { mutableStateOf(false) }
    if (showModalEdit) {
        Dialog(onDismissRequest = { showModalEdit = false }) {
            ModalCardSuccess(
                painterResource(id = R.drawable.success_green),
                "Data berhasil diubah",
                "Silahkan kembali ke halaman sebelumnya",
                "Kembali",
                navController, {
                    navController.navigate("NavCategoryScreen") {
                        popUpTo("NavCategoryScreen") { inclusive = true }
                    }
                },
                Modifier
            )
        }
    }

    Scaffold(
        bottomBar = { Button(
                onClick = { isLoading = true
                    viewModel.updateCategory(categoryId.toString()) { success -> isLoading = false
                        if (success) { showModalEdit = true } } },
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                enabled = isFormValid && !isLoading,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MainPink, contentColor = MainWhite),
            ) {
                Text("Edit Kategori")
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(top = 16.dp), contentAlignment = Alignment.TopCenter) {
            Column(modifier = Modifier.padding(innerPadding).verticalScroll(rememberScrollState()).fillMaxSize()) {
                HeaderTwoIconActive("Edit Kategori", navController, Modifier.clickable {
                    if (products.isEmpty()) { showModalQuest = true
                    } else { showModalError = true }
                }, MainPink)
                FormAddCategoryEdit("Nama Kategori", Modifier.padding(top = 32.dp))
            }
        }
    }
    Loading(isLoading)
}

@Composable
fun FormAddCategoryEdit(title: String, modifier: Modifier) {
    val viewModel: CategoryAdminViewModel = viewModel()
    val cat by viewModel.cat.collectAsState()

    Column(
        modifier = modifier
            .padding(start = 16.dp, end = 16.dp, top = 16.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif,
                color = MainBlack
            )
        )

        TextField(
            value = viewModel.categoryName,
            onValueChange = { viewModel.updateCategory(it) },
            maxLines = 1,
            textStyle = TextStyle(fontSize = 16.sp, color = Color(0xFF636363)),
            colors = TextFieldDefaults.textFieldColors(containerColor = Color.Transparent),
            placeholder = { Text(cat, fontSize = 16.sp, color = Color(0xFF636363)) },
            modifier = Modifier
                .fillMaxWidth()
        )
    }
}

@Preview(showBackground = true, device = Devices.PIXEL_4)
@Composable
fun AdminNavEditCategoryPagePreview() {
    com.dicoding.skripsirevisi.ui.theme.SkripsiRevisiTheme {
        Column {
            AdminNavEditCategoryPage(provideNavController())
        }
    }
}

