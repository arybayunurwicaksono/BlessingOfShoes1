package com.example.blessingofshoes_1.db

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "balanceReport")
data class  BalanceReport(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "idBalanceReport")
    var idBalanceReport: Int = 0,

    @ColumnInfo(name = "totalBalance")
    var totalBalance: Int? = null,

    @ColumnInfo(name = "status")
    var status: String? = null,

    @ColumnInfo(name = "typePayment")
    var typePayment: String? = null,

    @ColumnInfo(name = "reportTag")
    var reportTag: String? = null,

    @ColumnInfo(name = "username")
    var username: String? = null,

    @ColumnInfo(name = "timeAdded")
    var timeAdded: String? = null

) : Parcelable