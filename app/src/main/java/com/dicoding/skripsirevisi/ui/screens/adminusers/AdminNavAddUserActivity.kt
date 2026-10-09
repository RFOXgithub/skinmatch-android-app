@file:OptIn(ExperimentalMaterial3Api::class)

package com.dicoding.skripsirevisi.ui.screens.adminusers

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
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
import com.dicoding.skripsirevisi.ui.components.Header
import com.dicoding.skripsirevisi.ui.components.Loading
import com.dicoding.skripsirevisi.ui.components.ModalCardError
import com.dicoding.skripsirevisi.ui.components.ModalCardSuccess
import com.dicoding.skripsirevisi.ui.theme.MainBlack
import com.dicoding.skripsirevisi.ui.theme.MainPink
import com.dicoding.skripsirevisi.ui.theme.MainWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminAddUserPage(navController: NavController) {
    val viewModel: UserAdminViewModel = viewModel()
    val selectedSkinType by viewModel.selectedSkin.collectAsState()

    val isFormValid by remember {
        derivedStateOf {
            viewModel.emailUser.isNotBlank() && viewModel.password.isNotBlank() && viewModel.selectedSkin.value.isNotBlank()
        }
    }

    val selectedLevel by viewModel.selectedLevel.collectAsState()

    var isLoading by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null) }
    var showModalSuccess by remember { mutableStateOf(false) }
    var showModalError by remember { mutableStateOf(false) }

    if (showModalSuccess) {
        Dialog(onDismissRequest = { showModalSuccess = false }) {
            ModalCardSuccess(
                painterResource(id = R.drawable.success_green),
                "Tambah Pengguna Berhasil",
                "Silahkan lakukan verifikasi melalui email terkait",
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
                "Tambah Pengguna Gagal",
                "Format email atau sandi tidak sesuai",
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
                    viewModel.addUser { success, msg ->
                        isLoading = false
                        message = msg
                        if (success) {
                            showModalSuccess = true
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
                Text("Tambah Pengguna")
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
                Header(title = "Tambah Pengguna", navController)
                FormAdminUserAdd("Email", modifier = Modifier.padding(top = 40.dp))
                FormAdminUserAddPass("Kata Sandi", Modifier)
                //com.dicoding.skripsirevisi.ui.screens.adminproduct.SkinTypeDropdown("Tipe Kulit", selectedSkinType, { viewModel.setSkin(it) })
                //UserLevelDropdown("Level", selectedLevel, { viewModel.setLevel(it) })
            }
        }
    }
    Loading(isLoading)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormAdminUserAdd(title: String, modifier: Modifier) {
    val viewModel: UserAdminViewModel = viewModel()

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
            placeholder = { Text("Email", fontSize = 16.sp, color = Color(0xFF636363)) },
            modifier = Modifier
                .fillMaxWidth()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormAdminUserAddPass(title: String, modifier: Modifier) {
    val viewModel: UserAdminViewModel = viewModel()

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
            value = viewModel.password,
            onValueChange = { viewModel.updatePassword(it) },
            maxLines = 1,
            textStyle = TextStyle(fontSize = 16.sp, color = Color(0xFF636363)),
            colors = TextFieldDefaults.textFieldColors(containerColor = Color.Transparent),
            placeholder = { Text("Kata Sandi", fontSize = 16.sp, color = Color(0xFF636363)) },
            visualTransformation = if (viewModel.passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password),
            trailingIcon = {
                IconButton(onClick = { viewModel.togglePasswordVisibility() }) {
                    val iconRes =
                        if (viewModel.passwordVisible) R.drawable.gambar_eye_open else R.drawable.gambar_eye_closed
                    Image(
                        painter = painterResource(id = iconRes),
                        contentDescription = "Toggle Password Visibility",
                        modifier = Modifier.size(24.dp)
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
fun UserLevelDropdown(
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

@Composable
fun SkinTypeDropdown(
    title: String,
    selectedLevel: String,
    onLevelSelected: (String) -> Unit
) {
    val viewModel: UserAdminViewModel = viewModel()
    val options = viewModel.skinList.value
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
                        text = { Text(option.name) },
                        onClick = {
                            onLevelSelected(option.name)
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
fun AdminNavAddPagePreview() {
    com.dicoding.skripsirevisi.ui.theme.SkripsiRevisiTheme {
        Column {
            AdminAddUserPage(provideNavController())
        }
    }
}
