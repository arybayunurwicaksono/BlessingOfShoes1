package com.example.blessingofshoes_1

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import com.example.blessingofshoes_1.db.AppDb
import com.example.blessingofshoes_1.db.DbDao
import com.example.blessingofshoes_1.db.Product
import com.example.blessingofshoes_1.db.Users
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.inject.Inject

class AppRepository @Inject constructor(application: Application) {
    private val mDbDao: DbDao
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()

    init {
        val db = AppDb.getDatabase(application)
        mDbDao = db.dbDao()
    }

    // database
    fun registerUser(users: Users){
        executorService.execute { mDbDao.registerUser(users) }
    }

    fun insertProduct(product: Product) {

        mDbDao.insertProduct(product)
        //return mDbDao.insertProduct(product)

    }

    fun updateProduct(idProduct: Int?, nameProduct:String, priceProduct:String, stockProduct:String, productPhoto: Bitmap) {

        mDbDao.updateProduct(idProduct, nameProduct, priceProduct, stockProduct, productPhoto)
        //return mDbDao.insertProduct(product)

    }

    fun getUserInfo(email: String): Users = mDbDao.getUserInfo(email)
    fun getAllProduct(): LiveData<List<Product>> = mDbDao.getAllProduct()
    fun readUsername(email: String): String = mDbDao.readUsername(email)
    //fun readProductName(idProduct: Int): Product = mDbDao.readProductName(idProduct)
    fun readProductItem(idProduct: Int?): LiveData<Product> = mDbDao.readProductItem(idProduct)
    fun deleteProduct(idProduct: Int?) = mDbDao.deleteProduct(idProduct)
    fun updateProductItem(data:Product) {
        CoroutineScope(Dispatchers.Main).launch {
            mDbDao.updateProductItem(data)
        }
    }
/*    fun deleteProductItem(data:Product) {
        CoroutineScope(Dispatchers.Main).launch {
            mDbDao.deleteProductItem(data)
        }
    }*/
    fun delete(product: Product) = executeThread {
        mDbDao.deleteProductItem(product)
    }


}