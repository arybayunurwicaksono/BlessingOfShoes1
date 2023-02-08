package com.example.blessingofshoes_1.db

import android.graphics.Bitmap
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "product")
data class  Product(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "idProduct")
    var idProduct: Int = 0,

    @ColumnInfo(name = "nameProduct")
    var nameProduct: String? = null,

    @ColumnInfo(name = "brandProduct")
    var brandProduct: String? = null,

    @ColumnInfo(name = "priceProduct")
    var priceProduct: Int? = null,

    @ColumnInfo(name = "stockProduct")
    var stockProduct: Int? = null,

    @ColumnInfo(name = "sizeProduct")
    var sizeProduct: String? = null,

    @ColumnInfo(name = "realPriceProduct")
    var realPriceProduct: Int? = null,

    @ColumnInfo(name = "totalPurchases")
    var totalPurchases: Int? = null,

    @ColumnInfo(name = "profitProduct")
    var profitProduct: Int? = 0,

    @ColumnInfo(name = "productPhoto")
    val productPhoto: Bitmap,

    @ColumnInfo(name = "username")
    var username: String? = null,

    @ColumnInfo(name = "timeAdded")
    var timeAdded: String? = null

    ) : Parcelable