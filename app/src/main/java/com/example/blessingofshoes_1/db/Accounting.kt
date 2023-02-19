package com.example.blessingofshoes_1.db

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "accounting")
data class Accounting(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "idAccounting")
    var idAccounting: Int = 0,

    @ColumnInfo(name = "dateAccounting")
    var dateAccounting: String? = null,

    @ColumnInfo(name = "initDigital")
    var initDigital: Int? = null,

    @ColumnInfo(name = "initCash")
    var initCash: Int? = null,

    @ColumnInfo(name = "initStock")
    var initStock: Int? = null,

    @ColumnInfo(name = "initStockWorth")
    var initStockWorth: Int? = null,

    @ColumnInfo(name = "capitalInvest")
    var capitalInvest: Int? = null,

    @ColumnInfo(name = "incomeTransaction")
    var incomeTransaction: Int? = null,

    @ColumnInfo(name = "transactionItem")
    var transactionItem: Int? = null,

    @ColumnInfo(name = "restockPurchases")
    var restockPurchases: Int? = null,

    @ColumnInfo(name = "restockItem")
    var restockItem: Int? = null,

    @ColumnInfo(name = "returnTotal")
    var returnTotal: Int? = null,

    @ColumnInfo(name = "returnItem")
    var returnItem: Int? = null,


    @ColumnInfo(name = "finalDigital")
    var finalDigital: Int? = null,

    @ColumnInfo(name = "finalCash")
    var finalCash: Int? = null,

    @ColumnInfo(name = "finalStock")
    var finalStock: Int? = null,

    @ColumnInfo(name = "finalWorth")
    var finalWorth: Int? = null,

    @ColumnInfo(name = "otherNeeds")
    var otherNeeds: Int? = null,

    @ColumnInfo(name = "username")
    var username: String? = null,

    @ColumnInfo(name = "status")
    var status: String? = null,

    @ColumnInfo(name = "balanceIn")
    var balanceIn: Int? = null,

    @ColumnInfo(name = "balanceOut")
    var balanceOut: Int? = null,

    @ColumnInfo(name = "profitEarned")
    var profitEarned: Int? = null,

) : Parcelable