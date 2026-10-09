package com.dicoding.skripsirevisi.data.viewmodel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.skripsirevisi.data.dataclass.Category
import com.dicoding.skripsirevisi.data.dataclass.Ingredient
import com.dicoding.skripsirevisi.data.dataclass.Product
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class SearchViewModel : ViewModel() {
    private val database =
        FirebaseDatabase.getInstance("https://rskripsirevisi-default-rtdb.asia-southeast1.firebasedatabase.app").reference

    private val _prList = MutableStateFlow<List<Product>>(emptyList())
    val prList: StateFlow<List<Product>> = _prList

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _categoryList = mutableStateOf<List<Category>>(emptyList())
    val categoryList: State<List<Category>> = _categoryList

    private val _products = mutableStateOf<List<Product>>(emptyList())
    val products: State<List<Product>> = _products

    private val _selectedCategory = mutableStateOf<String?>(null)
    val selectedCategory: State<String?> = _selectedCategory

    init {
        fetchAllProduct()
        fetchAllCategory()
    }

    private fun loadProductsByCategory(category: String) {
        database.child("product")
            .orderByChild("category")
            .equalTo(category)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val productList = mutableListOf<Product>()

                        for (userSnapshot in snapshot.children) {
                            try {
                                val id = userSnapshot.key ?: ""
                                val category = userSnapshot.child("category").value as? String ?: ""
                                val desc = userSnapshot.child("desc").value as? String ?: ""
                                val product_name =
                                    userSnapshot.child("product_name").value as? String ?: ""
                                val typeskin = userSnapshot.child("skin").value as? String ?: ""
                                val brand = userSnapshot.child("brand").value as? String ?: ""
                                val url = userSnapshot.child("url").value as? String ?: ""
                                val ingredientCount =
                                    (userSnapshot.child("ingredientCount").value as? Long) ?: 0
                                val rating_avg =
                                    userSnapshot.child("rating_avg").value as? String ?: ""
                                val rating_count =
                                    userSnapshot.child("rating_count").value as? String ?: ""

                                val ingredients =
                                    userSnapshot.child("ingredients").children.mapNotNull { ingredientSnapshot ->
                                        val name = ingredientSnapshot.child("name")
                                            .getValue(String::class.java)
                                        val description = ingredientSnapshot.child("description")
                                            .getValue(String::class.java)

                                        if (!name.isNullOrEmpty() && !description.isNullOrEmpty()) {
                                            Ingredient(name, description)
                                        } else null
                                    }

                                val product =
                                    Product(
                                        id,
                                        category,
                                        desc,
                                        product_name,
                                        typeskin,
                                        brand,
                                        url,
                                        ingredientCount,
                                        rating_avg,
                                        rating_count,
                                        ingredients
                                    )

                                productList.add(product)
                            } catch (e: Exception) {
                                Log.e("Firebase", "Error parsing product: ${e.message}")
                            }
                        }

                        _products.value = productList
                    } else {
                        Log.d("Firebase", "Tidak ada produk dengan kategori: $category")
                        _products.value = emptyList()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("Firebase", "Error: ${error.message}")
                }
            })
    }


    fun selectCategory(category: String) {
        _selectedCategory.value = category
        loadProductsByCategory(category)
    }

    fun fetchAllCategory() {
        database.child("categories").get()
            .addOnSuccessListener { snapshot ->
                val category = mutableListOf<Category>()

                for (userSnapshot in snapshot.children) {
                    val id = userSnapshot.key ?: ""
                    val name = userSnapshot.child("name").value as? String ?: ""

                    category.add(Category(id, name))
                }

                _categoryList.value = category
            }
            .addOnFailureListener { e ->
                Log.e("FirebaseData", "Error: ${e.message}")
            }
    }

    fun fetchAllProduct() {
        database.child("product").get()
            .addOnSuccessListener { snapshot ->
                val productList = mutableListOf<Product>()

                for (userSnapshot in snapshot.children) {
                    val id = userSnapshot.key ?: ""
                    val category = userSnapshot.child("category").value as? String ?: ""
                    val desc = userSnapshot.child("desc").value as? String ?: ""
                    val product_name = userSnapshot.child("product_name").value as? String ?: ""
                    val typeskin = userSnapshot.child("skin").value as? String ?: ""
                    val brand = userSnapshot.child("brand").value as? String ?: ""
                    val url = userSnapshot.child("url").value as? String ?: ""
                    val ingredientCount = userSnapshot.child("ingredientCount").value as? Long ?: 0
                    val rating_avg = userSnapshot.child("rating_avg").value as? String ?: ""
                    val rating_count = userSnapshot.child("rating_count").value as? String ?: ""
                    val ingredients =
                        snapshot.child("ingredients").children.mapNotNull { ingredientSnapshot ->
                            val name = ingredientSnapshot.child("name").getValue(String::class.java)
                            val description =
                                ingredientSnapshot.child("description").getValue(String::class.java)

                            if (name != null && description != null) Ingredient(
                                name,
                                description
                            ) else null
                        }

                    productList.add(
                        Product(
                            id,
                            category,
                            desc,
                            product_name,
                            typeskin,
                            brand,
                            url,
                            ingredientCount,
                            rating_avg,
                            rating_count,
                            ingredients
                        )
                    )
                }

                _prList.value = productList
            }
            .addOnFailureListener { e ->
                Log.e("FirebaseData", "Error: ${e.message}")
            }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    val filteredProducts: StateFlow<List<Product>> =
        combine(_prList, _searchQuery) { products, query ->
            if (query.isEmpty()) products
            else products.filter { it.product_name.contains(query, ignoreCase = true) }
        }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
}