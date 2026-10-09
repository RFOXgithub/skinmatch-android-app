package com.dicoding.skripsirevisi.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

@Composable
fun provideNavController(): NavHostController {
    return rememberNavController()
}