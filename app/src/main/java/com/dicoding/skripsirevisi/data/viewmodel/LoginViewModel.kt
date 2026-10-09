package com.dicoding.skripsirevisi.data.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.skripsirevisi.utils.SessionManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class LoginViewModel(context: Context) : ViewModel() {

    private val sessionManager = SessionManager(context)

    var username by mutableStateOf("")
        private set
    var password by mutableStateOf("")
        private set
    var passwordVisible by mutableStateOf(false)
        private set
    val userId: Flow<String?> = sessionManager.userIdFlow
    val userEmail: Flow<String?> = sessionManager.userEmail
    val userLevel: Flow<String?> = sessionManager.userLevel
    val userSkin: Flow<String?> = sessionManager.userSkin

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database: DatabaseReference = FirebaseDatabase.getInstance(
        "https://rskripsirevisi-default-rtdb.asia-southeast1.firebasedatabase.app"
    ).reference.child("akun")

    fun updateUsername(newUsername: String) {
        username = newUsername
    }

    fun updatePassword(newPassword: String) {
        password = newPassword
    }

    fun togglePasswordVisibility() {
        passwordVisible = !passwordVisible
    }

    fun loginUser(onResult: (Boolean, String?, String?) -> Unit) {
        auth.signInWithEmailAndPassword(username, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser

                    if (user != null) {
                        if (user.isEmailVerified) {
                            val userId = user.uid
                            database.child(userId).get()
                                .addOnSuccessListener { akunSnapshot ->
                                    val email = akunSnapshot.child("email").value as? String ?: ""
                                    val skin = akunSnapshot.child("skin").value as? String ?: ""
                                    val level =
                                        akunSnapshot.child("level").value as? String ?: "customer"

                                    Log.d("FirebaseData", "Email: $email, Level: $level")

                                    viewModelScope.launch {
                                        sessionManager.saveUserSession(userId, email, level, skin)
                                    }

                                    onResult(true, level, email)
                                }
                                .addOnFailureListener {
                                    onResult(false, null, "Gagal mendapatkan data user.")
                                }
                        } else {
                            auth.signOut()
                            onResult(
                                false,
                                null,
                                "Email belum diverifikasi. Silakan cek email Anda."
                            )
                        }
                    } else {
                        onResult(false, null, "User tidak ditemukan.")
                    }
                } else {
                    onResult(false, null, task.exception?.localizedMessage ?: "Login gagal!")
                }
            }
    }


    fun logoutUser() {
        auth.signOut()
        viewModelScope.launch {
            sessionManager.clearUserSession()
        }
    }

}
