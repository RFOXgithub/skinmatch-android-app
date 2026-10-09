@file:OptIn(ExperimentalMaterial3Api::class)

package com.dicoding.skripsirevisi.ui.screens.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
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
import com.dicoding.skripsirevisi.data.viewmodel.RegisterViewModel
import com.dicoding.skripsirevisi.navigation.provideNavController
import com.dicoding.skripsirevisi.ui.components.AuthGreeting
import com.dicoding.skripsirevisi.ui.components.Header
import com.dicoding.skripsirevisi.ui.components.Loading
import com.dicoding.skripsirevisi.ui.components.ModalCardError
import com.dicoding.skripsirevisi.ui.components.ModalCardSuccess
import com.dicoding.skripsirevisi.ui.theme.MainPink
import com.dicoding.skripsirevisi.ui.theme.MainWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterPage(navController: NavController, viewModel: RegisterViewModel = viewModel()) {
    val isFormValid by remember {
        derivedStateOf {
            viewModel.email.isNotBlank() && viewModel.password.isNotBlank() && viewModel.selectedSkin.value.isNotBlank()
        }
    }
    var isButtonEnabled by remember { mutableStateOf(true) }
    var showModalError by remember { mutableStateOf(false) }
    var showModalSuccess by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val selectedSkinType by viewModel.selectedSkin.collectAsState()

    Scaffold(
        bottomBar = {
            Button(
                onClick = {
                    isLoading = true
                    viewModel.registerUser { success, msg ->
                        isLoading = false

                        if (!success) {
                            message = msg
                            showModalError = true
                        } else {
                            message = msg
                            showModalSuccess = true
                        }
                    }
                },
                enabled = isFormValid && isButtonEnabled && !isLoading,
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MainPink,
                    contentColor = MainWhite
                ),
            ) {
                Text("Daftar")
            }

            if (showModalError) {
                Dialog(onDismissRequest = { }) {
                    ModalCardError(
                        painterResource(id = R.drawable.failed_red),
                        "Gagal Mendaftar",
                        "Silahkan Cek Email Verifikasi atau Coba Lagi Nanti",
                        "Kembali",
                        navController,
                        { showModalError = false })
                }
            }

            if (showModalSuccess) {
                Dialog(onDismissRequest = { }) {
                    ModalCardSuccess(
                        painterResource(id = R.drawable.success_green),
                        "Pendaftaran Berhasil",
                        "Silahkan Cek Email Verifikasi untuk Melakukan Proses Verifikasi",
                        "Kembali",
                        navController,
                        { showModalSuccess = false },
                        Modifier
                    )
                }
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
                Header(title = "Daftar", navController)
                AuthGreeting(
                    "Selamat Datang di Rfox Store!",
                    "Masukkan data akun anda untuk lanjut"
                )
                FormEmailRegister()
                SkinTypeDropdown(
                    selectedSkinType,
                    { viewModel.setSkin(it) })
                FormPasswordRegister()
            }
        }
    }
    Loading(isLoading)
}

@Composable
fun FormEmailRegister() {
    val registerViewModel: RegisterViewModel = viewModel()
    Row(
        modifier = Modifier
            .padding(top = 40.dp)
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .wrapContentHeight(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(id = R.drawable.icon_email),
            contentDescription = "Email",
            tint = MainPink,
            modifier = Modifier.size(24.dp)
        )

        TextField(
            value = registerViewModel.email,
            onValueChange = { email -> registerViewModel.updateEmail(email) },
            maxLines = 2,
            textStyle = TextStyle(
                fontSize = 16.sp,
                color = Color(0xFF636363),
                fontWeight = FontWeight.Normal,
                fontFamily = FontFamily.SansSerif
            ),
            colors = TextFieldDefaults.textFieldColors(containerColor = Color.Transparent),
            placeholder = {
                if (registerViewModel.email.isEmpty()) {
                    Text(
                        "Email",
                        style = TextStyle(
                            fontSize = 16.sp,
                            color = Color(0xFF636363),
                            fontWeight = FontWeight.Normal,
                            fontFamily = FontFamily.SansSerif
                        )
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
        )
    }
}

@Composable
fun SkinTypeDropdown(
    selectedLevel: String,
    onLevelSelected: (String) -> Unit
) {
    val viewModel: RegisterViewModel = viewModel()
    val options = viewModel.skinList.value
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp, top = 40.dp)
            .fillMaxWidth()
    ) {

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it }
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(R.drawable.skin),
                    contentDescription = "Skin",
                    modifier = Modifier
                        .size(19.dp)
                )
                Box(
                    modifier = Modifier
                        .padding(top = 8.dp, start = 16.dp)
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

@Composable
fun FormPasswordRegister() {
    val registerViewModel: RegisterViewModel = viewModel()
    Row(
        modifier = Modifier
            .padding(top = 40.dp)
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .wrapContentHeight(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(R.drawable.gambar_lock),
            contentDescription = "Password",
            modifier = Modifier
                .size(24.dp)
        )

        TextField(
            value = registerViewModel.password,
            onValueChange = { password -> registerViewModel.updatePassword(password) },
            maxLines = 2,
            textStyle = TextStyle(
                fontSize = 16.sp,
                color = Color(0xFF636363),
                fontWeight = FontWeight.Normal,
                fontFamily = FontFamily.SansSerif
            ),
            colors = TextFieldDefaults.textFieldColors(containerColor = Color.Transparent),
            placeholder = {
                if (registerViewModel.password.isEmpty()) {
                    Text(
                        "Kata Sandi",
                        style = TextStyle(
                            fontSize = 16.sp,
                            color = Color(0xFF636363),
                            fontWeight = FontWeight.Normal,
                            fontFamily = FontFamily.SansSerif
                        )
                    )
                }
            },
            visualTransformation = if (registerViewModel.passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password),
            trailingIcon = {
                IconButton(onClick = { registerViewModel.togglePasswordVisibility() }) {
                    val iconRes =
                        if (registerViewModel.passwordVisible) R.drawable.gambar_eye_open else R.drawable.gambar_eye_closed
                    Image(
                        painter = painterResource(id = iconRes),
                        contentDescription = if (registerViewModel.passwordVisible) "Hide Password" else "Show Password",
                        modifier = Modifier
                            .size(24.dp),
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
        )
    }
}

@Preview(showBackground = true, device = Devices.PIXEL_4)
@Composable
fun RegisterPagePreview() {
    com.dicoding.skripsirevisi.ui.theme.SkripsiRevisiTheme {
        Column {
            RegisterPage(provideNavController())
        }
    }
}