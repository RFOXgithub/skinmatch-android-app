@file:OptIn(ExperimentalMaterial3Api::class)

package com.dicoding.skripsirevisi.ui.screens.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
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
import com.dicoding.skripsirevisi.data.viewmodel.HistoryViewModel
import com.dicoding.skripsirevisi.data.viewmodel.LoginViewModel
import com.dicoding.skripsirevisi.navigation.provideNavController
import com.dicoding.skripsirevisi.ui.components.AuthGreeting
import com.dicoding.skripsirevisi.ui.components.Loading
import com.dicoding.skripsirevisi.ui.components.ModalCardError
import com.dicoding.skripsirevisi.ui.components.ModalCardSuccess
import com.dicoding.skripsirevisi.ui.components.OnlyHeader
import com.dicoding.skripsirevisi.ui.theme.MainBlack
import com.dicoding.skripsirevisi.ui.theme.MainPink
import com.dicoding.skripsirevisi.ui.theme.MainWhite
import com.dicoding.skripsirevisi.utils.LoginViewModelFactory
import kotlinx.coroutines.delay

@Composable
fun LoginPage(navController: NavController) {
    val context = LocalContext.current.applicationContext
    val viewModel: LoginViewModel = viewModel(factory = LoginViewModelFactory(context))
    val viewModelHis: HistoryViewModel = viewModel()
    val isFormValid by remember {
        derivedStateOf {
            viewModel.username.isNotBlank() && viewModel.password.isNotBlank()
        }
    }

    var isLoading by remember { mutableStateOf(false) }
    var showModalSuccessCust by remember { mutableStateOf(false) }
    var showModalSuccessAdmin by remember { mutableStateOf(false) }
    var showModalError by remember { mutableStateOf(false) }

    val idUser by viewModel.userId.collectAsState(initial = null)

    LaunchedEffect(idUser) {
        viewModelHis.onUserChanged(idUser.toString())
    }

    Scaffold(
        bottomBar = {
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = {
                        isLoading = true
                        viewModel.loginUser { success, level, _ ->
                            isLoading = false
                            if (success) {
                                when (level) {
                                    "admin" -> {
                                        showModalSuccessAdmin = true
                                    }

                                    "customer" -> {
                                        showModalSuccessCust = true
                                    }
                                }
                            } else {
                                showModalError = true
                            }
                        }
                    },
                    enabled = isFormValid && !isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isFormValid) MainPink else Color.Gray,
                        contentColor = MainWhite
                    )
                ) {
                    Text(
                        text = "Masuk",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Belum punya akun? Daftar",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp,
                        color = MainBlack
                    ),
                    modifier = Modifier
                        .clickable { navController.navigate("RegisterScreen") }
                        .padding(8.dp)
                )
            }

            if (showModalSuccessCust) {
                Dialog(onDismissRequest = { }) {
                    ModalCardSuccess(
                        painterResource(id = R.drawable.success_green),
                        "Masuk Berhasil",
                        "Anda akan dialihkan ke halaman utama",
                        "",
                        navController, { },
                        Modifier.alpha(0f)

                    )

                    LaunchedEffect(Unit) {
                        delay(1500)
                        navController.navigate("HomeScreen")
                    }
                }
            }

            if (showModalSuccessAdmin) {
                Dialog(onDismissRequest = { }) {
                    ModalCardSuccess(
                        painterResource(id = R.drawable.success_green),
                        "Masuk Berhasil",
                        "Anda akan dialihkan ke halaman utama",
                        "",
                        navController, { },
                        Modifier.alpha(0f)

                    )

                    LaunchedEffect(Unit) {
                        delay(1500)
                        navController.navigate("NavProductScreen")
                    }
                }
            }

            if (showModalError) {
                Dialog(onDismissRequest = { }) {
                    ModalCardError(
                        painterResource(id = R.drawable.failed_red),
                        "Masuk Gagal",
                        "Pastikan Email atau Password Anda Benar",
                        "Kembali",
                        navController,
                        { showModalError = false })
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
                OnlyHeader(title = "Masuk")
                AuthGreeting(
                    "Selamat Datang di Rfox Store!",
                    "Masukkan data akun anda untuk lanjut", Modifier.padding(top = 4.dp)
                )
                FormEmailLogin()
                FormPasswordLogin()
                LupaSandi(navController)
            }
        }
    }
    Loading(isLoading)
}

@Composable
fun FormEmailLogin(loginViewModel: LoginViewModel = viewModel()) {
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
            value = loginViewModel.username,
            onValueChange = { loginViewModel.updateUsername(it) },
            maxLines = 1,
            textStyle = TextStyle(fontSize = 16.sp, color = Color(0xFF636363)),
            colors = TextFieldDefaults.textFieldColors(containerColor = Color.Transparent),
            placeholder = { Text("Email", fontSize = 16.sp, color = Color(0xFF636363)) },
            modifier = Modifier
                .padding(start = 3.dp)
                .fillMaxWidth()
        )
    }
}

@Composable
fun FormPasswordLogin(loginViewModel: LoginViewModel = viewModel()) {
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
            modifier = Modifier.size(24.dp)
        )

        TextField(
            value = loginViewModel.password,
            onValueChange = { loginViewModel.updatePassword(it) },
            maxLines = 1,
            textStyle = TextStyle(fontSize = 16.sp, color = Color(0xFF636363)),
            colors = TextFieldDefaults.textFieldColors(containerColor = Color.Transparent),
            placeholder = { Text("Kata Sandi", fontSize = 16.sp, color = Color(0xFF636363)) },
            visualTransformation = if (loginViewModel.passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password),
            trailingIcon = {
                IconButton(onClick = { loginViewModel.togglePasswordVisibility() }) {
                    val iconRes =
                        if (loginViewModel.passwordVisible) R.drawable.gambar_eye_open else R.drawable.gambar_eye_closed
                    Image(
                        painter = painterResource(id = iconRes),
                        contentDescription = "Toggle Password Visibility",
                        modifier = Modifier.size(24.dp)
                    )
                }
            },
            modifier = Modifier
                .padding(start = 3.dp)
                .fillMaxWidth()
        )
    }
}

@Composable
fun LupaSandi(navController: NavController) {
    Column(
        modifier = Modifier
            .padding(top = 12.dp)
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .wrapContentHeight(),
        horizontalAlignment = Alignment.End
    ) {
        Text(
            text = "Lupa Sandi?",
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                fontFamily = FontFamily.SansSerif,
                color = Color(0xFF636363)
            ),
            modifier = Modifier
                .padding(top = 16.dp)
                .clickable {
                    navController.navigate("ResetScreen")
                }
        )
    }
}

@Preview(showBackground = true, device = Devices.PIXEL_4)
@Composable
fun LoginPagePreview() {
    com.dicoding.skripsirevisi.ui.theme.SkripsiRevisiTheme {
        Column {
            LoginPage(provideNavController())
        }
    }
}

