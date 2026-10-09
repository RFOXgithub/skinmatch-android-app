@file:OptIn(ExperimentalMaterial3Api::class)

package com.dicoding.skripsirevisi.ui.screens.adminproduct

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
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
import androidx.compose.ui.draw.clip
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
import coil.compose.rememberAsyncImagePainter
import com.dicoding.skripsirevisi.R
import com.dicoding.skripsirevisi.data.dataclass.Ingredient
import com.dicoding.skripsirevisi.data.viewmodel.ProductViewModel
import com.dicoding.skripsirevisi.navigation.provideNavController
import com.dicoding.skripsirevisi.ui.components.HeaderTwoIconActive
import com.dicoding.skripsirevisi.ui.components.Loading
import com.dicoding.skripsirevisi.ui.components.ModalCardQuest
import com.dicoding.skripsirevisi.ui.components.ModalCardSuccess
import com.dicoding.skripsirevisi.ui.theme.MainBlack
import com.dicoding.skripsirevisi.ui.theme.MainPink
import com.dicoding.skripsirevisi.ui.theme.MainWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminNavEditProductPage(navController: NavController) {
    val viewModel: ProductViewModel = viewModel()
    val selectedCategory by viewModel.selectedCat.collectAsState()
    val selectedSkinType by viewModel.selectedSkin.collectAsState()
    val productId = navController.currentBackStackEntry?.arguments?.getString("productId")
    var isLoading by remember { mutableStateOf(false) }
    val selectedProduct by viewModel.selectedProduct.collectAsState()

    val isFormValid by remember {
        derivedStateOf {
            viewModel.productName.isNotBlank() && viewModel.deskripsi.isNotBlank() && viewModel.selectedSkin.value.isNotBlank() && viewModel.selectedCat.value.isNotBlank()
        }
    }

    LaunchedEffect(productId) {
        viewModel.getProductById(productId.toString())
    }

    var showModalDelete by remember { mutableStateOf(false) }
    if (showModalDelete) {
        Dialog(onDismissRequest = { }) {
            ModalCardSuccess(
                painterResource(id = R.drawable.success_green),
                "Penghapusan produk berhasil",
                "Produk akan dihapus dari database",
                "Kembali",
                navController, {
                    navController.navigate("NavProductScreen") {
                        popUpTo("NavProductScreen") { inclusive = true }
                    }
                },
                Modifier
            )
        }
    }

    var showModalEdit by remember { mutableStateOf(false) }
    if (showModalEdit) {
        Dialog(onDismissRequest = { showModalEdit = false }) {
            ModalCardSuccess(
                painterResource(id = R.drawable.success_green),
                "Data berhasil diubah",
                "Silahkan kembali ke halaman sebelumnya",
                "Kembali",
                navController, {
                    navController.navigate("NavProductScreen") {
                        popUpTo("NavProductScreen") { inclusive = true }
                    }
                },
                Modifier
            )
        }
    }

    var showModalQuest by remember { mutableStateOf(false) }
    if (showModalQuest) {
        Dialog(onDismissRequest = { showModalQuest = false }) {
            ModalCardQuest(
                painterResource(id = R.drawable.icon_tanda_tanya_biru),
                "Apakah anda ingin menghapus data produk?",
                "Data anda akan dihapus dari sistem",
                "Lanjutkan",
                navController,
                {
                    viewModel.deleteProduct(productId.toString()) { success, _ ->
                        if (success) {
                            showModalDelete = true
                        }
                    }
                    showModalQuest = false
                },
                Modifier
            )
        }
    }

    Scaffold(
        bottomBar = {
            Button(
                onClick = {
                    isLoading = true
                    viewModel.updateProduct(productId.toString()) { success ->
                        isLoading = false
                        if (success) {
                            showModalEdit = true
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                enabled = isFormValid && !isLoading,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MainPink,
                    contentColor = MainWhite
                ),
            ) {
                Text("Ubah Produk")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 16.dp)
        ) {
            HeaderTwoIconActive("Edit Produk", navController, Modifier.clickable {
                showModalQuest = true
            }, MainPink)
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .fillMaxSize()
            ) {
                FormProductNameEdit("Nama Produk", modifier = Modifier.padding(top = 40.dp))
                FormDeskripsiEdit("Deskripsi", modifier = Modifier)
                FormBrandEdit("Brand", Modifier)
                selectedProduct?.let {
                    CategoryDropdown("Kategori", if (selectedCategory == "Kategori") {
                        it.category
                    } else {
                        selectedCategory
                    }, { viewModel.setCat(it) })
                    SkinTypeDropdown("Tipe Kulit", if (selectedSkinType == "Tipe Kulit") {
                        it.skin
                    } else {
                        selectedSkinType
                    }, { viewModel.setSkin(it) })
                    ImageCardEdit(viewModel)
                    BahanAktifMainEdit("Kandungan", Modifier)
                }
            }
        }
    }
    Loading(isLoading)
}

@Composable
fun BahanAktifMainEdit(title: String, modifier: Modifier) {
    val viewModel: ProductViewModel = viewModel()
    var namaBahan by remember { mutableStateOf("") }
    var deskripsiBahan by remember { mutableStateOf("") }
    var isDeskripsiMode by remember { mutableStateOf(false) }

    val mutableIngredients = viewModel.ingredients

    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif,
                color = MainBlack
            )
        )

        TextField(
            value = if (isDeskripsiMode) deskripsiBahan else namaBahan,
            onValueChange = { if (isDeskripsiMode) deskripsiBahan = it else namaBahan = it },
            maxLines = Int.MAX_VALUE,
            textStyle = TextStyle(fontSize = 16.sp, color = Color(0xFF636363)),
            colors = TextFieldDefaults.textFieldColors(containerColor = Color.Transparent),
            placeholder = {
                Text(
                    if (isDeskripsiMode) "Masukkan deskripsi bahan aktif" else "Masukkan nama bahan aktif",
                    fontSize = 16.sp,
                    color = Color(0xFF636363)
                )
            },
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                IconButton(
                    onClick = {
                        if (!isDeskripsiMode) {
                            isDeskripsiMode = true
                        } else {
                            viewModel.addIngredient(Ingredient(namaBahan, deskripsiBahan))
                            namaBahan = ""
                            deskripsiBahan = ""
                            isDeskripsiMode = false
                        }
                    }
                ) {
                    Icon(
                        imageVector = if (isDeskripsiMode) Icons.Default.Done else Icons.Default.ArrowForward,
                        contentDescription = if (isDeskripsiMode) "Simpan" else "Lanjut",
                        tint = MainPink
                    )
                }
            }
        )
    }

    Box(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .wrapContentHeight()
            .border(
                width = 1.dp,
                color = Color.Black.copy(alpha = 0.05f),
                shape = RoundedCornerShape(20.dp)
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "${mutableIngredients.size} INGREDIENTS",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = FontFamily.SansSerif,
                    color = MainPink,
                    letterSpacing = 6.sp
                ),
                modifier = Modifier.padding(top = 16.dp)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 48.dp, bottom = 16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            mutableIngredients.forEachIndexed { index, (ingredient, description) ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = if (index == 0) 0.dp else 8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .background(color = MainPink, shape = RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${index + 1}",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                fontFamily = FontFamily.SansSerif,
                                color = MainWhite
                            )
                        )
                    }

                    Column(modifier = Modifier.padding(start = 12.dp, end = 12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = ingredient,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    fontFamily = FontFamily.SansSerif,
                                    color = MainBlack
                                )
                            )

                            IconButton(
                                onClick = {
                                    viewModel.removeIngredient(Ingredient(ingredient, description))
                                },
                                modifier = Modifier.size(16.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Hapus Ingredient",
                                    tint = MainPink
                                )
                            }
                        }
                        Text(
                            text = description,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Normal,
                                fontFamily = FontFamily.SansSerif,
                                color = Color.Gray
                            ),
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun ImageCardEdit(viewModel: ProductViewModel = viewModel()) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.selectImage(it) }
    }

    Column(
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp, top = 16.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Gambar",
            style = MaterialTheme.typography.bodyLarge.copy(
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color.Black
            )
        )

        Box(
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth()
                .height(42.dp)
                .border(
                    width = 1.dp,
                    color = Color.Black.copy(alpha = 0.05f),
                    shape = RoundedCornerShape(10.dp)
                )
                .clickable { launcher.launch("image/*") },
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (viewModel.imageUri != null) "Gambar dipilih" else "Ubah Gambar",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Normal,
                        fontSize = 16.sp,
                        color = Color.Gray
                    ),
                    modifier = Modifier.weight(1f)
                )

                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.icon_right),
                    contentDescription = "Upload Gambar",
                    tint = MainPink,
                    modifier = Modifier
                        .size(24.dp)
                )
            }
        }

        if (viewModel.imageUri != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Image(
                painter = rememberAsyncImagePainter(viewModel.imageUri),
                contentDescription = "Gambar yang dipilih",
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .border(1.dp, Color.Gray, RoundedCornerShape(10.dp))
            )
        }
    }
}

@Composable
fun FormBrandEdit(title: String, modifier: Modifier) {
    val viewModel: ProductViewModel = viewModel()

    Column(
        modifier = modifier
            .padding(start = 16.dp, end = 16.dp, top = 16.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif,
                color = MainBlack
            )
        )

        TextField(
            value = viewModel.brand,
            onValueChange = { viewModel.updateBrand(it) },
            maxLines = 1,
            textStyle = TextStyle(fontSize = 16.sp, color = Color(0xFF636363)),
            colors = TextFieldDefaults.textFieldColors(containerColor = Color.Transparent),
            placeholder = { Text("Brand", fontSize = 16.sp, color = Color(0xFF636363)) },
            modifier = Modifier
                .fillMaxWidth()
        )
    }
}

@Composable
fun FormDeskripsiEdit(title: String, modifier: Modifier) {
    val viewModel: ProductViewModel = viewModel()

    Column(
        modifier = modifier
            .padding(start = 16.dp, end = 16.dp, top = 16.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif,
                color = MainBlack
            )
        )

        TextField(
            value = viewModel.deskripsi,
            onValueChange = { viewModel.updateDeskripsi(it) },
            maxLines = Int.MAX_VALUE,
            textStyle = TextStyle(fontSize = 16.sp, color = Color(0xFF636363)),
            colors = TextFieldDefaults.textFieldColors(containerColor = Color.Transparent),
            placeholder = { Text("Deskripsi", fontSize = 16.sp, color = Color(0xFF636363)) },
            modifier = Modifier
                .fillMaxWidth()
        )
    }
}

@Composable
fun FormProductNameEdit(title: String, modifier: Modifier) {
    val viewModel: ProductViewModel = viewModel()

    Column(
        modifier = modifier
            .padding(start = 16.dp, end = 16.dp, top = 16.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif,
                color = MainBlack
            )
        )

        TextField(
            value = viewModel.productName,
            onValueChange = { viewModel.updateProduct(it) },
            maxLines = Int.MAX_VALUE,
            textStyle = TextStyle(fontSize = 16.sp, color = Color(0xFF636363)),
            colors = TextFieldDefaults.textFieldColors(containerColor = Color.Transparent),
            placeholder = { Text("Nama Produk", fontSize = 16.sp, color = Color(0xFF636363)) },
            modifier = Modifier
                .fillMaxWidth()
        )
    }
}

@Composable
fun ImageCardInput() {
    Column(
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp, top = 16.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {

        Text(
            text = "Gambar",
            style = MaterialTheme.typography.bodyLarge.copy(
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color.Black
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        Image(
            painter = painterResource(R.drawable.product_image_example),
            contentDescription = "Produk",
            modifier = Modifier
                .size(100.dp)
                .clip(RoundedCornerShape(10.dp))
        )
    }
}

@Preview(showBackground = true, device = Devices.PIXEL_4)
@Composable
fun AdminNavEditProductPagePreview() {
    com.dicoding.skripsirevisi.ui.theme.SkripsiRevisiTheme {
        Column {
            AdminNavEditProductPage(provideNavController())
        }
    }
}
