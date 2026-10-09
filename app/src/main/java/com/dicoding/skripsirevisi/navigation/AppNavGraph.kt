package com.dicoding.skripsirevisi.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.dicoding.skripsirevisi.ui.screens.admincategory.AdminNavAddCategoryPage
import com.dicoding.skripsirevisi.ui.screens.admincategory.AdminNavEditCategoryPage
import com.dicoding.skripsirevisi.ui.screens.admincategory.NavCategoryPage
import com.dicoding.skripsirevisi.ui.screens.adminproduct.AdminNavEditProductPage
import com.dicoding.skripsirevisi.ui.screens.adminproduct.AdminNavProductActivityPage
import com.dicoding.skripsirevisi.ui.screens.adminproduct.NavProductPage
import com.dicoding.skripsirevisi.ui.screens.adminprofile.AdminProfilePage
import com.dicoding.skripsirevisi.ui.screens.adminusers.AdminAddUserPage
import com.dicoding.skripsirevisi.ui.screens.adminusers.AdminEditUserPage
import com.dicoding.skripsirevisi.ui.screens.adminusers.AdminNavUserPage
import com.dicoding.skripsirevisi.ui.screens.auth.LoginPage
import com.dicoding.skripsirevisi.ui.screens.auth.RegisterPage
import com.dicoding.skripsirevisi.ui.screens.auth.ResetPage
import com.dicoding.skripsirevisi.ui.screens.general.AddRutinitasPage
import com.dicoding.skripsirevisi.ui.screens.general.DetailPage
import com.dicoding.skripsirevisi.ui.screens.general.EditRutinitasPage
import com.dicoding.skripsirevisi.ui.screens.general.HistoryPage
import com.dicoding.skripsirevisi.ui.screens.general.HomePage
import com.dicoding.skripsirevisi.ui.screens.general.ProfilePage
import com.dicoding.skripsirevisi.ui.screens.general.SearchPage
import com.dicoding.skripsirevisi.ui.screens.splash.SplashPage
import com.dicoding.skripsirevisi.utils.SessionManager

@Composable
fun AppNavGraph() {
    val navController = provideNavController()
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val isLoggedIn by sessionManager.isLoggedIn.collectAsState(initial = false)
    val userLevel by sessionManager.userLevel.collectAsState(initial = "customer")
    Log.d("SessionManager", "User Level: $userLevel")
    LaunchedEffect(isLoggedIn, userLevel) {
        if (isLoggedIn) {
            if (userLevel == "admin") {
                navController.navigate(Routes.NavProductScreen.route) {
                    popUpTo(Routes.LoginScreen.route) { inclusive = true }
                }
            } else {
                navController.navigate(Routes.HomeScreen.route) {
                    popUpTo(Routes.LoginScreen.route) { inclusive = true }
                }
            }
        } else {
            navController.navigate(Routes.LoginScreen.route) {
                popUpTo(Routes.SplashScreen.route) { inclusive = true }
            }
        }
    }


    NavHost(navController = navController, startDestination = Routes.SplashScreen.route) {
        composable(Routes.SplashScreen.route) { SplashPage(navController) }
        composable(Routes.LoginScreen.route) { LoginPage(navController) }
        composable(Routes.ResetScreen.route) { ResetPage(navController) }
        composable(Routes.RegisterScreen.route) { RegisterPage(navController) }
        composable(Routes.HomeScreen.route) { HomePage(navController) }
        composable(Routes.HistoryScreen.route) { HistoryPage(navController) }
        composable(Routes.SearchScreen.route) { SearchPage(navController) }
        composable(Routes.ProfileScreen.route) { ProfilePage(navController) }
        composable(Routes.DetailScreen.route) { DetailPage(navController) }
        composable(Routes.NavProductScreen.route) { NavProductPage(navController) }
        composable(Routes.NavEditProductScreen.route) { AdminNavEditProductPage(navController) }
        composable(Routes.NavAddProductScreen.route) { AdminNavProductActivityPage(navController) }
        composable(Routes.NavCategoryScreen.route) { NavCategoryPage(navController) }
        composable(Routes.NavAddCategoryScreen.route) { AdminNavAddCategoryPage(navController) }
        composable(Routes.NavEditCategoryScreen.route) { AdminNavEditCategoryPage(navController) }
        composable(Routes.NavUsersScreen.route) { AdminNavUserPage(navController) }
        composable(Routes.NavAddUsersScreen.route) { AdminAddUserPage(navController) }
        composable(Routes.NavEditUsersScreen.route) { AdminEditUserPage(navController) }
        composable(Routes.NavAdminProfileScreen.route) { AdminProfilePage(navController) }
        composable(Routes.NavAddRutinitasScreen.route) { AddRutinitasPage(navController) }
        composable(Routes.NavEditRutinitasScreen.route) { EditRutinitasPage(navController) }
    }
}