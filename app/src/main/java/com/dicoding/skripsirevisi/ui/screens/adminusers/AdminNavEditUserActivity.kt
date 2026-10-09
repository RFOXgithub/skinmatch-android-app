@file:OptIn(ExperimentalMaterial3Api::class)

package com.dicoding.skripsirevisi.ui.screens.adminusers

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
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
import com.dicoding.skripsirevisi.data.viewmodel.UserAdminViewModel
import com.dicoding.skripsirevisi.navigation.provideNavController
import com.dicoding.skripsirevisi.ui.components.HeaderTwoIconActive
import com.dicoding.skripsirevisi.ui.components.Loading
import com.dicoding.skripsirevisi.ui.components.ModalCardError
import com.dicoding.skripsirevisi.ui.components.ModalCardSuccess
import com.dicoding.skripsirevisi.ui.theme.MainBlack
import com.dicoding.skripsirevisi.ui.theme.MainPink
import com.dicoding.skripsirevisi.ui.theme.MainWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminEditUserPage(navController: NavController) {
    val viewModel: UserAdminViewModel = viewModel()
    val selectedSkinType by viewModel.selectedSkin.collectAsState()
    val userId = navController.currentBackStackEntry?.arguments?.getString("userId")
    val isFormValid by remember {
        derivedStateOf {
            viewModel.emailUser.isNotBlank() && viewModel.selectedSkin.value.isNotBlank()
        }
    }
    val selectedLevel by viewModel.selectedLevel.collectAsState()
    var isLoading by remember { mutableStateOf(false) }

    var showModalDelete by remember { mutableStateOf(false) }
    var showModalEdit by remember { mutableStateOf(false) }
    var showModalError by remember { mutableStateOf(false) }

    LaunchedEffect(userId) {
        viewModel.selectUser(userId.toString())
    }

    if (showModalDelete) {
        Dialog(onDismissRequest = { showModalDelete = false }) {
            ModalCardSuccess(
                painterResource(id = R.drawable.success_green),
                "Penghapusan pengguna berhasil",
                "Pengguna akan dihapus dari database",
                "Kembali",
                navController, {
                    navController.navigate("NavUsersScreen") {
                        popUpTo("NavUsersScreen") { inclusive = true }
                    }
                },
                Modifier
            )
        }
    }

    if (showModalEdit) {
        Dialog(onDismissRequest = { showModalEdit = false }) {
            ModalCardSuccess(
                painterResource(id = R.drawable.success_green),
                "Data berhasil diubah",
                "Silahkan kembali ke halaman sebelumnya",
                "Kembali",
                navController, {
                    navController.navigate("NavUsersScreen") {
                        popUpTo("NavUsersScreen") { inclusive = true }
                    }
                },
                Modifier
            )
        }
    }

    if (showModalError) {
        Dialog(onDismissRequest = { showModalError = false }) {
            ModalCardError(
                painterResource(id = R.drawable.failed_red),
                "Ubah Pengguna Gagal",
                "Format email tidak sesuai",
                "Kembali",
                navController, {
                    showModalError = false
                }
            )
        }
    }

    Scaffold(
        bottomBar = {
            Button(
                onClick = {
                    isLoading = true
                    viewModel.updateUser(userId.toString()) { success ->
                        isLoading = false
                        if (success) {
                            showModalEdit = true
                        } else {
                            showModalError = true
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
                Text("Ubah Pengguna")
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
                HeaderTwoIconActive("Edit Pengguna", navController, Modifier, Color.Transparent)
                FormAdminUserEdit("Email", modifier = Modifier.padding(top = 40.dp))
                // com.dicoding.skripsirevisi.ui.screens.adminproduct.SkinTypeDropdown("Tipe Kulit", selectedSkinType, { viewModel.setSkin(it) })
                // UserLevelDropdownEdit("Level", selectedLevel, { viewModel.setLevel(it) })
            }
        }
    }
    Loading(isLoading)
}

@Composable
fun FormAdminUserEdit(title: String, modifier: Modifier) {
    val viewModel: UserAdminViewModel = viewModel()
    val data by viewModel.userData

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
            value = viewModel.emailUser,
            onValueChange = { viewModel.updateEmail(it) },
            maxLines = 1,
            textStyle = TextStyle(fontSize = 16.sp, color = Color(0xFF636363)),
            colors = TextFieldDefaults.textFieldColors(containerColor = Color.Transparent),
            placeholder = {
                data?.let {
                    Text(
                        it.email,
                        fontSize = 16.sp,
                        color = Color(0xFF636363)
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserLevelDropdownEdit(
    title: String,
    selectedLevel: String,
    onLevelSelected: (String) -> Unit
) {
    val options = listOf("admin", "customer")
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp, top = 16.dp)
            .fillMaxWidth()
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

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it }
        ) {
            Box(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth()
                    .height(42.dp)
                    .border(
                        width = 1.dp,
                        shape = RoundedCornerShape(10.dp),
                        color = Color.Black.copy(alpha = 0.05f)
                    )
                    .menuAnchor(),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = selectedLevel,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight.Normal,
                            fontSize = 16.sp,
                            color = Color.Gray
                        ),
                        modifier = Modifier.weight(1f)
                    )

                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Dropdown Icon",
                        modifier = Modifier.size(24.dp),
                        tint = MainPink
                    )
                }
            }

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onLevelSelected(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, device = Devices.PIXEL_4)
@Composable
fun AdminNavEditPagePreview() {
    com.dicoding.skripsirevisi.ui.theme.SkripsiRevisiTheme {
        Column {
            AdminEditUserPage(provideNavController())
        }
    }
}