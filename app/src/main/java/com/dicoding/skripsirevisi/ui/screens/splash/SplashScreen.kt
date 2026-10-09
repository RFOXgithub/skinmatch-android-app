package com.dicoding.skripsirevisi.ui.screens.splash

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.work.WorkManager
import com.dicoding.skripsirevisi.R
import com.dicoding.skripsirevisi.navigation.AppNavGraph
import com.dicoding.skripsirevisi.navigation.Routes
import com.dicoding.skripsirevisi.ui.screens.ui.theme.SkripsiRevisiTheme
import com.dicoding.skripsirevisi.ui.theme.MainBlack
import com.dicoding.skripsirevisi.utils.checkAndResetIfNeeded
import com.dicoding.skripsirevisi.utils.scheduleDailyReset
import kotlinx.coroutines.delay

@SuppressLint("CustomSplashScreen")
class SplashScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SkripsiRevisiTheme {
                AppNavGraph()
            }
        }
    }
}

fun forceReset(context: Context) {
    Log.d("ForceReset", "Cancelling existing WorkManager job...")
    WorkManager.getInstance(context).cancelUniqueWork("resetCheckedItems")

    Log.d("ForceReset", "Checking and forcing reset...")
    checkAndResetIfNeeded(context)

    Log.d("ForceReset", "Scheduling new WorkManager job...")
    scheduleDailyReset(context)
}


@Composable
fun SplashPage(navController: NavController) {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        forceReset(context)
        delay(5000)
        navController.navigate(Routes.LoginScreen.route) {
            popUpTo(Routes.SplashScreen.route) { inclusive = true }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(R.drawable.splash_image_png),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(200.dp)
            )

            Text(
                text = "Tingkatkan rutinitas\n" +
                        " merawat kulit anda",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Normal,
                    fontSize = 24.sp,
                    color = MainBlack
                )
            )
        }
    }
}
