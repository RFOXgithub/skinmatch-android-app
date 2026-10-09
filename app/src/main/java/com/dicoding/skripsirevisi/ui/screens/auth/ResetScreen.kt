@file:OptIn(ExperimentalMaterial3Api::class)

package com.dicoding.skripsirevisi.ui.screens.auth

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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
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
import com.dicoding.skripsirevisi.data.viewmodel.ResetViewModel
import com.dicoding.skripsirevisi.navigation.provideNavController
import com.dicoding.skripsirevisi.ui.components.AuthGreeting
import com.dicoding.skripsirevisi.ui.components.Header
import com.dicoding.skripsirevisi.ui.components.Loading
import com.dicoding.skripsirevisi.ui.components.ModalCardError
import com.dicoding.skripsirevisi.ui.components.ModalCardSuccess
import com.dicoding.skripsirevisi.ui.theme.MainPink
import com.dicoding.skripsirevisi.ui.theme.MainWhite

@Composable
fun ResetPage(navController: NavController, viewModel: ResetViewModel = viewModel()) {
    val isFormValid by remember {
        derivedStateOf {
            viewModel.email.isNotBlank()
        }
    }

    var isLoading by remember { mutableStateOf(false) }
    var showModalSuccess by remember { mutableStateOf(false) }
    var showModalError by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = {
            Button(
                onClick = {
                    isLoading = true
                    viewModel.resetPassword { success, msg ->
                        isLoading = false
                        if (success) {
                            showModalSuccess = true
                        } else {
                            showModalError = true
                        }
                    }
                },
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .height(48.dp),
                enabled = isFormValid && !isLoading,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MainPink,
                    contentColor = MainWhite
                ),
            ) {
                Text("Lanjutkan")
            }

            if (showModalSuccess) {
                Dialog(onDismissRequest = { }) {
                    ModalCardSuccess(
                        painterResource(id = R.drawable.success_green),
                        "Link Ganti Sandi Telah Di Kirim",
                        "Silahkan Cek Email untuk Melakukan Proses Ganti Sandi",
                        "Kembali",
                        navController,
                        { showModalSuccess = false },
                        Modifier
                    )
                }
            }

            if (showModalError) {
                Dialog(onDismissRequest = { }) {
                    ModalCardError(
                        painterResource(id = R.drawable.failed_red),
                        "Gagal Mengirim Link",
                        "Pastikan Email Anda Telah Terdaftar pada Sistem",
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
                Header(title = "Ganti Sandi", navController)
                AuthGreeting(
                    "Lupa Kata Sandi",
                    "Jangan panik, akan kami bantu! masukkan email anda"
                )
                FormEmailReset()
            }
        }
    }
    Loading(isLoading)
}

@Composable
fun FormEmailReset() {
    val viewModel: ResetViewModel = viewModel()
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
            value = viewModel.email,
            onValueChange = { email -> viewModel.updateEmail(email) },
            maxLines = 2,
            textStyle = TextStyle(
                fontSize = 16.sp,
                color = Color(0xFF636363),
                fontWeight = FontWeight.Normal,
                fontFamily = FontFamily.SansSerif
            ),
            colors = TextFieldDefaults.textFieldColors(containerColor = Color.Transparent),
            placeholder = {
                if (viewModel.email.isEmpty()) {
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
                .padding(start = 3.dp)
                .fillMaxWidth()
        )
    }
}

@Preview(showBackground = true, device = Devices.PIXEL_4)
@Composable
fun ResetPagePreview() {
    com.dicoding.skripsirevisi.ui.theme.SkripsiRevisiTheme {
        Column {
            ResetPage(provideNavController())
        }
    }
}