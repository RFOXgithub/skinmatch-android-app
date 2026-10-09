package com.dicoding.skripsirevisi.data.viewmodel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.dicoding.skripsirevisi.data.dataclass.SkinType
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class RegisterViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database: DatabaseReference = FirebaseDatabase.getInstance(
        "https://rskripsirevisi-default-rtdb.asia-southeast1.firebasedatabase.app"
    ).reference

    private val _skinList = mutableStateOf<List<SkinType>>(emptyList())
    val skinList: State<List<SkinType>> = _skinList

    private val _selectedSkin = MutableStateFlow("Tipe Kulit")
    val selectedSkin: StateFlow<String> = _selectedSkin

    var username by mutableStateOf("")
        private set
    var email by mutableStateOf("")
        private set
    var password by mutableStateOf("")
        private set
    var passwordVisible by mutableStateOf(false)
        private set

    init {
        fetchAllSkin()
    }

    fun updateUsername(input: String) {
        username = input
    }

    fun updateEmail(input: String) {
        email = input
    }

    fun setSkin(skin: String) {
        _selectedSkin.value = skin
    }

    fun updatePassword(input: String) {
        password = input
    }

    fun togglePasswordVisibility() {
        passwordVisible = !passwordVisible
    }

    fun registerUser(onResult: (Boolean, String) -> Unit) {
        val skin = _selectedSkin.value
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user?.sendEmailVerification()?.addOnCompleteListener { emailTask ->
                        if (emailTask.isSuccessful) {
                            val userId = user.uid
                            val userData = mapOf(
                                "email" to email,
                                "skin" to skin,
                                "level" to "customer"
                            )

                            database.child("akun").child(userId).setValue(userData)
                                .addOnSuccessListener {
                                    onResult(
                                        true,
                                        "Registrasi berhasil! Cek email untuk verifikasi."
                                    )
                                }
                                .addOnFailureListener { e ->
                                    onResult(false, "Gagal menyimpan data: ${e.message}")
                                }
                        } else {
                            onResult(
                                false,
                                "Gagal mengirim email verifikasi: ${emailTask.exception?.message}"
                            )
                        }
                    }
                } else {
                    onResult(false, "Registrasi gagal: ${task.exception?.message}")
                }
            }
    }

    fun fetchAllSkin() {
        database.child("skintype").get()
            .addOnSuccessListener { snapshot ->
                val skin = mutableListOf<SkinType>()

                for (userSnapshot in snapshot.children) {
                    val id = userSnapshot.key ?: ""
                    val name = userSnapshot.child("jenis_kulit").value as? String ?: ""

                    skin.add(SkinType(id, name))
                }

                _skinList.value = skin
            }
            .addOnFailureListener { e ->
                Log.e("FirebaseData", "Error: ${e.message}")
            }
    }
}
