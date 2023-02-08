package com.example.blessingofshoes_1.db

import android.graphics.Bitmap
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "cart")
data class Cart(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "idItem")
    var idItem: Int = 0,

    @ColumnInfo(name = "idProduct")
    var idProduct: Int? = null,

    @ColumnInfo(name = "nameItem")
    var nameItem: String? = null,

    @ColumnInfo(name = "priceItem")
    var priceItem: Int? = 0,

    @ColumnInfo(name = "totalItem")
    var totalItem: Int? = null,

    @ColumnInfo(name = "profitItem")
    var itemProfit: Int? = 0,

    @ColumnInfo(name = "totalProfit ")
    var totalProfit: Int? = 0,

    @ColumnInfo(name = "totalpayment")
    var totalpayment: Int? = 0,

    @ColumnInfo(name = "username")
    var username: String? = null,

    @ColumnInfo(name = "status")
    var status: String? = null,

    @ColumnInfo(name = "idTransaction")
    var idTransaction: Int? = 0,

    @ColumnInfo(name = "productPhoto")
    val productPhoto: Bitmap

) : Parcelable