@file:OptIn(ExperimentalMaterial3Api::class)

package com.dicoding.skripsirevisi.ui.screens.general

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
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
import com.dicoding.skripsirevisi.data.viewmodel.LoginViewModel
import com.dicoding.skripsirevisi.navigation.provideNavController
import com.dicoding.skripsirevisi.ui.components.BottomBar
import com.dicoding.skripsirevisi.ui.components.ModalCardQuest
import com.dicoding.skripsirevisi.ui.theme.MainBlack
import com.dicoding.skripsirevisi.ui.theme.MainPink
import com.dicoding.skripsirevisi.utils.LoginViewModelFactory

@Composable
fun ProfilePage(navController: NavController) {
    val context = LocalContext.current.applicationContext
    val viewModel: LoginViewModel = viewModel(factory = LoginViewModelFactory(context))
    val email by viewModel.userEmail.collectAsState(initial = null)
    val skin by viewModel.userSkin.collectAsState(initial = null)

    var showModalLogout by remember { mutableStateOf(false) }
    if (showModalLogout) {
        Dialog(onDismissRequest = { showModalLogout = false }) {
            ModalCardQuest(
                painterResource(id = R.drawable.icon_tanda_tanya_biru),
                "Apakah anda ingin keluar?",
                "Anda akan dialihkan ke halaman login",
                "Keluar",
                navController,
                {
                    viewModel.logoutUser()
                    navController.navigate("LoginScreen") {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                Modifier
            )
        }
    }

    var showModalReset by remember { mutableStateOf(false) }
    if (showModalReset) {
        Dialog(onDismissRequest = { showModalReset = false }) {
            ModalCardQuest(
                painterResource(id = R.drawable.icon_tanda_tanya_biru),
                "Apakah anda ingin mengganti sandi?",
                "Anda akan dialihkan ke halaman ganti sandi",
                "Lanjutkan",
                navController, { navController.navigate("ResetScreen") },
                Modifier
            )
        }
    }

    Scaffold(
        bottomBar = { BottomBar(navController) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .fillMaxSize()
            ) {
                HeaderProfile("Profile", navController, Modifier.clickable {
                    showModalLogout = true
                })
                FormEmailProfile(email ?: "Email tidak tersedia")
                FormPasswordDetail(navController, Modifier.clickable { showModalReset = true })
                FormTipeKulit(skin ?: "Skin tidak tersedia")
            }
        }
    }
}

@Composable
fun HeaderProfile(title: String, navController: NavController, modifier: Modifier) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp, start = 16.dp, end = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(id = R.drawable.icon_back),
            contentDescription = "Back",
            tint = Color.Transparent,
            modifier = Modifier.size(24.dp)
        )

        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif,
                color = MainBlack
            ),
        )

        Icon(
            imageVector = ImageVector.vectorResource(id = R.drawable.icon_logout_new),
            contentDescription = "Back",
            tint = MainPink,
            modifier = modifier
                .size(28.dp)
        )
    }
}

@Composable
fun FormEmailProfile(email: String) {
    Column(
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp, top = 44.dp)
            .fillMaxWidth()
            .wrapContentHeight(),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Email",
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif,
                color = MainBlack
            )
        )

        Text(
            text = email,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                fontFamily = FontFamily.SansSerif,
                color = Color.Gray
            ),
            modifier = Modifier.padding(top = 12.dp)
        )

        Divider(
            color = Color.Black.copy(alpha = 0.05f),
            thickness = 1.dp,
            modifier = Modifier
                .padding(top = 12.dp, start = 16.dp, end = 16.dp)
                .fillMaxWidth()
        )
    }
}

@Composable
fun FormPasswordDetail(navController: NavController, modifier: Modifier) {

    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 16.dp)
            .fillMaxWidth()
            .wrapContentHeight(),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Sandi",
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif,
                color = MainBlack
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "*********",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    fontFamily = FontFamily.SansSerif,
                    color = Color.Gray
                )
            )

            Row(
                modifier = modifier,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Ganti Sandi",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        fontFamily = FontFamily.SansSerif,
                        color = Color.Gray
                    )
                )

                Spacer(modifier = Modifier.width(4.dp))

                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.icon_right),
                    contentDescription = "Ganti Password",
                    tint = Color.Gray,
                    modifier = Modifier
                        .size(28.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Divider(
            color = Color.Black.copy(alpha = 0.05f),
            thickness = 1.dp,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun FormTipeKulit(skin: String) {
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 16.dp)
            .fillMaxWidth()
            .wrapContentHeight(),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Tipe Kulit",
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif,
                color = MainBlack
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = skin,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    fontFamily = FontFamily.SansSerif,
                    color = Color.Gray
                )
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Divider(
            color = Color.Black.copy(alpha = 0.05f),
            thickness = 1.dp,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(showBackground = true, device = Devices.PIXEL_4)
@Composable
fun ProfilePagePreview() {
    com.dicoding.skripsirevisi.ui.theme.SkripsiRevisiTheme {
        Column {
            ProfilePage(provideNavController())
        }
    }
}