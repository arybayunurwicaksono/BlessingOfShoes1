package com.example.blessingofshoes_1.db

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "return")
data class  Return(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "idReturn")
    var idReturn: Int = 0,

    @ColumnInfo(name = "idCart")
    var idCart: Int? = null,

    @ColumnInfo(name = "totalItem")
    var totalItem: Int? = null,

    @ColumnInfo(name = "totalRefund")
    var totalRefund: Int? = null,

    @ColumnInfo(name = "note")
    var note: String? = null,

    @ColumnInfo(name = "username")
    var username: String? = null,

    @ColumnInfo(name = "returnDate")
    var returnDate: String? = null

) : Parcelable