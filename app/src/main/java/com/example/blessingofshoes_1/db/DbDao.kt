package com.example.blessingofshoes_1.db

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface DbDao {

    @Query("SELECT * FROM users WHERE email LIKE :email")
    fun getUserInfo(email: String) : Users

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun registerUser(users: Users)

    @Query("SELECT * FROM users WHERE email LIKE :email AND password LIKE :password")
    fun readDataUser(email: String, password: String): Users

    @Query("SELECT email FROM users WHERE email=:email")
    fun validateEmail(email: String) : String

    @Query("SELECT username FROM users WHERE username=:username")
    fun validateUsername(username: String) : String

    @Query("SELECT username FROM users WHERE email LIKE :email")
    fun readUsername(email: String) : String

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertProduct(product: Product)

    @Query("SELECT * FROM product")
    fun getAllProduct() : LiveData<List<Product>>

    @Query("UPDATE product SET nameProduct =:nameProduct, nameProduct =:nameProduct, priceProduct =:priceProduct, " +
            "stockProduct =:stockProduct, productPhoto =:productPhoto WHERE idProduct =:idProduct LIKE :idProduct")
    fun updateProduct(idProduct: Int?, nameProduct:String, priceProduct:String, stockProduct:String, productPhoto: Bitmap)

    @Query("SELECT * FROM product WHERE idProduct LIKE :idProduct")
    fun readProductItem(idProduct: Int?): LiveData<Product>

    //@Query("SELECT nameProduct FROM product WHERE idProduct LIKE :idProduct LIKE :idProduct")
    //fun readProductName(idProduct: Int): Product

    @Update
    fun updateProductItem(data: Product)

    @Delete
    fun deleteProductItem(data: Product)

    @Query("DELETE FROM product WHERE idProduct LIKE :idProduct")
    fun deleteProduct(idProduct: Int?)
}