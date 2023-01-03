package com.example.blessingofshoes_1.db

import android.graphics.Bitmap
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "product")
data class Product(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "idProduct")
    var idProduct: Int = 0,

    @ColumnInfo(name = "nameProduct")
    var nameProduct: String? = null,

    @ColumnInfo(name = "priceProduct")
    var priceProduct: String? = null,

    @ColumnInfo(name = "stockProduct")
    var stockProduct: String? = null,

    @ColumnInfo(name = "productPhoto")
    val productPhoto: Bitmap

    ) : Parcelable