package com.example.blessingofshoes_1.db

import android.graphics.Bitmap
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "restock")
data class  Restock(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "idRestock")
    var idRestock: Int = 0,

    @ColumnInfo(name = "idProduct")
    var idProduct: Int = 0,

    @ColumnInfo(name = "nameProduct")
    var nameProduct: String? = null,

    @ColumnInfo(name = "stockAdded")
    var stockAdded: Int? = null,

    @ColumnInfo(name = "totalPurchases")
    var totalPurchases: Int? = null,

    @ColumnInfo(name = "supplier")
    var supplier: String? = null,

    @ColumnInfo(name = "username")
    var username: String? = null,

    @ColumnInfo(name = "restockDate")
    var restockDate: String? = null

) : Parcelable