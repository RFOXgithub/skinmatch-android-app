package com.dicoding.skripsirevisi.ui.screens.admincategory

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
import com.dicoding.skripsirevisi.ui.components.Header
import com.dicoding.skripsirevisi.ui.components.Loading
import com.dicoding.skripsirevisi.ui.components.ModalCardSuccess
import com.dicoding.skripsirevisi.ui.theme.MainBlack
import com.dicoding.skripsirevisi.ui.theme.MainPink
import com.dicoding.skripsirevisi.ui.theme.MainWhite


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminNavAddCategoryPage(navController: NavController) {
    val viewModel: CategoryAdminViewModel = viewModel()
    var isLoading by remember { mutableStateOf(false) }
    var showModalSuccess by remember { mutableStateOf(false) }

    val isFormValid by remember {
        derivedStateOf {
            viewModel.categoryName.isNotBlank()
        }
    }

    if (showModalSuccess) {
        Dialog(onDismissRequest = { showModalSuccess = false }) {
            ModalCardSuccess(
                painterResource(id = R.drawable.success_green),
                "Tambah Kategori Berhasil",
                "Silahkan kembali jika ingin menambah kategori lainnya",
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
        bottomBar = {
            Button(
                onClick = {
                    isLoading = true
                    viewModel.addCategory { success, _ ->
                        isLoading = false
                        if (success) {
                            showModalSuccess = true
                        } else {
                            navController.popBackStack()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                enabled = isFormValid && !isLoading,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MainPink,
                    contentColor = MainWhite
                ),
            ) {
                Text("Tambah Kategori")
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 16.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .fillMaxSize()
            ) {
                Header(title = "Tambah Kategori", navController)
                FormAddCategory("Nama Kategori", Modifier.padding(top = 32.dp))
            }
        }
    }
    Loading(isLoading)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormAddCategory(title: String, modifier: Modifier) {
    val viewModel: CategoryAdminViewModel = viewModel()

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
            placeholder = { Text("Kategori", fontSize = 16.sp, color = Color(0xFF636363)) },
            modifier = Modifier
                .fillMaxWidth()
        )
    }
}

@Preview(showBackground = true, device = Devices.PIXEL_4)
@Composable
fun AdminNavAddCategoryPagePreview() {
    com.dicoding.skripsirevisi.ui.theme.SkripsiRevisiTheme {
        Column {
            AdminNavAddCategoryPage(provideNavController())
        }
    }
}