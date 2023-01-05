package com.example.blessingofshoes_1

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.blessingofshoes_1.db.Product
import com.example.blessingofshoes_1.db.Users
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AppViewModel@Inject constructor(application: Application): ViewModel() {

    private lateinit var product: Product
    private val appRepository: AppRepository = AppRepository(application)
    val _productItem = MutableLiveData<List<Product>>()
    val productItem: LiveData<List<Product>> = _productItem

    val _userResponse = MutableLiveData<Users>()
    val userResponse: LiveData<Users> = _userResponse

    // database
    fun registerUser(users: Users) {
        appRepository.registerUser(users)
    }

    fun getUserInfo(email: String): Users = appRepository.getUserInfo(email)

    fun readUsername(email: String): String = appRepository.readUsername(email)

    fun getAllProduct(): LiveData<kotlin.collections.List<Product>> = appRepository.getAllProduct()

    fun insertProduct(product: Product) {
        appRepository.insertProduct(product)
    }

    fun updateProduct(idProduct: Int?, nameProduct:String, priceProduct:String, stockProduct:String, productPhoto: Bitmap) {
        appRepository.updateProduct(idProduct, nameProduct, priceProduct, stockProduct, productPhoto)
    }

    //fun readProductName(idProduct: Int): Product = appRepository.readProductName(idProduct)

    fun readProductItem(idProduct: Int?): LiveData<Product> = appRepository.readProductItem(idProduct)
    fun deleteProduct(idProduct: Int?) = appRepository.deleteProduct(idProduct)
    fun updateProductItem(context: Context, idProduct:Int, nameProduct:String, priceProduct:String,stockProduct:String, productPhoto: Bitmap, onSuccess: (Boolean) -> Unit) {
        appRepository.updateProductItem(Product(idProduct, nameProduct, priceProduct, stockProduct, productPhoto))
        onSuccess(true)
    }
    /*fun deleteProductItem(context: Context?, data:Product, onSuccess: (Boolean) -> Unit) {
        appRepository.deleteProductItem(data)
        onSuccess(true)
    }*/
    fun delete(product: Product) {
        appRepository.delete(product)
    }
    fun getData(): Product = product
}
