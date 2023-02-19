package com.example.blessingofshoes_1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.blessingofshoes_1.databinding.ActivityEditProductBinding
import com.example.blessingofshoes_1.databinding.ActivityGenerateAccountingBinding
import com.example.blessingofshoes_1.db.AppDb
import com.example.blessingofshoes_1.utils.Constant
import com.example.blessingofshoes_1.utils.Preferences
import com.example.blessingofshoes_1.viemodel.AppViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class GenerateAccountingActivity : AppCompatActivity() {

    private lateinit var _activityGenerateAccountingBinding: ActivityGenerateAccountingBinding
    private val binding get() = _activityGenerateAccountingBinding
    private val viewModel by viewModels<AppViewModel>()
    lateinit var sharedPref: Preferences
    private val appDatabase by lazy { AppDb.getDatabase(this).dbDao() }

    override fun onBackPressed() {
        super.onBackPressed()
        val i = Intent(this@GenerateAccountingActivity, MainActivity::class.java)
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(i)
        finish()

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _activityGenerateAccountingBinding = ActivityGenerateAccountingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPref = Preferences(this)
        val sdf = SimpleDateFormat("MMMM/yyyy")
        val currentDate = sdf.format(Date())
        val localeID =  Locale("in", "ID")
        val numberFormat = NumberFormat.getCurrencyInstance(localeID)
        viewModel.readDetailMonthlyAccounting(currentDate).observe(this, Observer {
            binding.tvIdFinancialAccounting.text = "Accounting ID : " + it.idAccounting.toString()
            binding.AccountBalanceValue.text = numberFormat.format(it.initDigital!!.toDouble()).toString()
            binding.CashBalanceValue.text = numberFormat.format(it.initCash!!.toDouble()).toString()
            binding.TotalStockValue.text = it.initCash!!.toString()
            binding.StockWorthValue.text = numberFormat.format(it.initStockWorth!!.toDouble()).toString()
            binding.username.text = it.username!!
            binding.status.text = it.status!!
            binding.capitalInvestmentValue.text = numberFormat.format(it.capitalInvest!!.toDouble()).toString()
            binding.transactionTotal.text = numberFormat.format(it.incomeTransaction!!.toDouble()).toString()
            binding.transactionItemTotal.text = it.transactionItem!!.toString() + " Product"
            binding.restockTotal.text = numberFormat.format(it.restockPurchases!!.toDouble()).toString()
            binding.restockItemTotal.text = it.restockItem!!.toString() + " Product"
            binding.returnTotal.text = numberFormat.format(it.returnTotal!!.toDouble()).toString()
            binding.returnItemTotal.text = it.returnItem!!.toString() + " Product"
            binding.balanceInValue.text = numberFormat.format(it.balanceIn!!.toDouble()).toString()
            binding.balanceOutValue.text = numberFormat.format(it.balanceOut!!.toDouble()).toString()
            binding.profitValue.text = numberFormat.format(it.profitEarned!!.toDouble()).toString()

            binding.AccountBalanceValueFinal.text = numberFormat.format(it.finalDigital!!.toDouble()).toString()
            binding.CashBalanceValueFinal.text = numberFormat.format(it.finalCash!!.toDouble()).toString()
            binding.TotalStockValueFinal.text = it.finalStock.toString()  + " Product"
            binding.StockWorthValueFinal.text = numberFormat.format(it.finalWorth!!.toDouble()).toString()
            val sdf = SimpleDateFormat("M/yyyy")
            val currentDate = sdf.format(Date())
            var dateSearch = "%"+currentDate+"%"
            var eId = it.idAccounting
            var eDate = it.dateAccounting
            var eInitDigital = it.initDigital
            var eInitCash = it.initCash
            var eInitStock = it.initStock
            var eInitWorth = it.initStockWorth
            var eCapital = appDatabase.sumTotalInvest(dateSearch)
            var eTransaction : Int? = 0
            var eItemTransaction : Int? = 0
            val username = viewModel.readUsername(sharedPref.getString(Constant.PREF_EMAIL))
            var validateTransaction = appDatabase.validateTransaction(dateSearch)
            if (validateTransaction!=0) {
                eTransaction = appDatabase.sumTotalTransactionAcc(dateSearch)!!
                eItemTransaction = appDatabase.readTotalTransactionItem(dateSearch)!!
            }
            var eRestockPurchases : Int? = 0
            var eRestockItem : Int? = 0
            var validateRestock = appDatabase.validateRestock(dateSearch)
            if (validateRestock!=0){
                eRestockPurchases = appDatabase.sumTotalPurchases(dateSearch)
                eRestockItem = appDatabase.sumTotalStockAdded(dateSearch)
            }
/*        var eRestockPurchases = appDatabase.sumTotalPurchases(currentDate)
        var eRestockItem = appDatabase.sumTotalStockAdded(currentDate)*/
            var eReturn : Int? = 0
            var eReturnItem : Int? = 0
            var validateReturn = appDatabase.validateReturn(dateSearch)
            if (validateReturn!=0){
                eReturn = appDatabase.sumTotalRefund(dateSearch)
                eReturnItem = appDatabase.sumTotalRefundItem(dateSearch)
            }
            var eStatus = "complete"
            var eFinalDigital = appDatabase.readDigitalBalance()
            var eFinalCash = appDatabase.readCashBalance()
            var eStock = appDatabase.readTotalStock()
            var eWorth = appDatabase.readTotalStockWorth()
            var eOtherNeeds = 0
            var balanceIn = eFinalCash + eFinalDigital
            var balanceOut = 0
            var validateBalance = appDatabase.validateCountBalance()
            if (validateBalance != 0) {
                balanceOut = appDatabase.sumTotalBalanceOut(dateSearch)!!
            }
            var eProfit = appDatabase.sumTotalProfitAcc(dateSearch)
            var profitFix = 0
            if (eReturn == 0) {
                if (eProfit != null) {
                    profitFix = eProfit
                }
            }
                //eReturn!! + eOtherNeeds



            binding.btnGenerate.setOnClickListener{
                SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                    .setTitleText("Update Monthly Accounting?")
                    .setConfirmText("Yes")
                    .setCancelText("No")
                    .setConfirmClickListener { sDialog->
                        val fsdf = SimpleDateFormat("MMMM/yyyy")
                        val fFurrentDate = fsdf.format(Date())
                        sDialog.dismissWithAnimation()
                        lifecycleScope.launch {
                            //viewModel.updateProduct(idProduct, productName, productPrice,productStock, productPhoto)
                            viewModel.updateMonthlyAccounting(
                                applicationContext, eId!!, fFurrentDate, eInitDigital, eInitCash, eInitStock, eInitWorth,
                                eCapital, eTransaction, eItemTransaction, eRestockPurchases, eRestockItem,
                                eReturn, eReturnItem, eFinalDigital, eFinalCash, eStock, eWorth, 0,
                                username, eStatus, balanceIn, balanceOut, profitFix) {
/*                            val intent = Intent(this@GenerateAccountingActivity, MainActivity::class.java)
                            intent.putExtra("DATA_STATUS", "accounting")
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            startActivity(intent)*/
                                binding.tvIdFinancialAccounting.text = eId.toString()
                                binding.dateFinancialAccounting.text = fFurrentDate
                                binding.AccountBalance.text = numberFormat.format(eInitDigital!!.toDouble()).toString()
                                binding.CashBalance.text = numberFormat.format(eInitCash!!.toDouble()).toString()
                                binding.TotalStockValue.text = eInitStock.toString()  + " Product"
                                binding.StockWorthValue.text = numberFormat.format(eInitWorth!!.toDouble()).toString()
                                binding.capitalInvestmentValue.text = numberFormat.format(eCapital!!.toDouble()).toString()
                                binding.transactionTotal.text = numberFormat.format(eTransaction!!.toDouble()).toString()
                                binding.transactionItemTotal.text = eItemTransaction!!.toString() + " Product"
                                binding.restockTotal.text = numberFormat.format(eRestockPurchases!!.toDouble()).toString()
                                binding.restockItemTotal.text = eRestockItem!!.toString() + " Product"
                                binding.returnTotal.text = numberFormat.format(eReturn!!.toDouble()).toString()
                                binding.returnItemTotal.text = eReturnItem!!.toString() + " Product"
                                binding.balanceInValue.text = numberFormat.format(balanceIn!!.toDouble()).toString()
                                binding.balanceOutValue.text = numberFormat.format(balanceOut!!.toDouble()).toString()
                                binding.profitValue.text = numberFormat.format(profitFix!!.toDouble()).toString()

                                binding.AccountBalanceValueFinal.text = numberFormat.format(eFinalDigital!!.toDouble()).toString()
                                binding.CashBalanceValueFinal.text = numberFormat.format(eFinalCash!!.toDouble()).toString()
                                binding.TotalStockValueFinal.text = eStock.toString()  + " Product"
                                binding.StockWorthValueFinal.text = numberFormat.format(eWorth!!.toDouble()).toString()

                            }

                        }


                    }
                    .show()
            }

        })
    }
}