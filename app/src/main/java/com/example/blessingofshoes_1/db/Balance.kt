package com.example.blessingofshoes_1.db

import android.graphics.Bitmap
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "balance")
data class  Balance(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "idBalance")
    var idBalance: Int = 0,

    @ColumnInfo(name = "digitalBalance")
    var digitalBalance: Int? = null,

    @ColumnInfo(name = "CashBalance")
    var CashBalance: Int? = 0

) : Parcelable