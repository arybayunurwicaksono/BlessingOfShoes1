package com.example.blessingofshoes_1

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.blessingofshoes_1.databinding.ActivityDetailProductBinding
import com.example.blessingofshoes_1.databinding.ActivityMainBinding
import com.example.blessingofshoes_1.db.Accounting
import com.example.blessingofshoes_1.db.AppDb
import com.example.blessingofshoes_1.utils.Constant
import com.example.blessingofshoes_1.utils.Preferences
import com.example.blessingofshoes_1.viemodel.AppViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mazenrashed.printooth.Printooth
import com.mazenrashed.printooth.data.printable.Printable
import com.mazenrashed.printooth.data.printable.RawPrintable
import com.mazenrashed.printooth.data.printable.TextPrintable
import com.mazenrashed.printooth.data.printer.DefaultPrinter
import com.mazenrashed.printooth.ui.ScanningActivity
import com.mazenrashed.printooth.utilities.Printing
import com.mazenrashed.printooth.utilities.PrintingCallback
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity(){

    private lateinit var _activityMainBinding: ActivityMainBinding
    private val binding get() = _activityMainBinding
    private val viewModel by viewModels<AppViewModel>()
    private var printing : Printing? = null
    lateinit var sharedPref: Preferences
    private val appDatabase by lazy { AppDb.getDatabase(this).dbDao() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (Printooth.hasPairedPrinter())
            printing = Printooth.printer()
        else resultLauncher.launch(
                Intent(
                    this@MainActivity,
                    ScanningActivity::class.java
                )
            )
        val sdf = SimpleDateFormat("MMMM/yyyy")
        val currentDate = sdf.format(Date())
        val monthPicker = currentDate.toString()
        val validateByMonth = appDatabase.validateAccounting(monthPicker)!!
        var validateCountBalance = appDatabase.validateCountBalance()!!
        var validateCountProduct = appDatabase.validateCountProduct()!!

        sharedPref = Preferences(this)
        val username = viewModel.readUsername(sharedPref.getString(Constant.PREF_EMAIL))
        //var sumInvest = appDatabase.sumTotalInvest()!!

        if (validateCountBalance == 0) {
            if (validateCountProduct == 0) {
                SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                    .setTitleText("Let's get started")
                    .setConfirmText("Ok")
                    .setConfirmClickListener { sDialog ->
                        if (validateByMonth == 0) {
                            viewModel.insertAccounting(
                                Accounting(
                                    0,
                                    currentDate,
                                    0,
                                    0,
                                    0,
                                    0,
                                    0,
                                    0,0,0,0,0,0,0,0,0,0, 0, username,"onProgress", 0,0,0))
                        }
                        sDialog.dismissWithAnimation()
                    }
                    .show()
            }
        } else {
            if (validateByMonth == 0) {
                var digitalBalance = appDatabase.readDigitalBalance()!!
                var cashBalance = appDatabase.readCashBalance()!!
                var stockValue = appDatabase.readTotalStock()!!
                var StockWorthValue = appDatabase.readTotalStockWorth()!!
                viewModel.insertAccounting(
                    Accounting(
                        0,
                        currentDate,
                        digitalBalance,
                        cashBalance,
                        stockValue,
                        StockWorthValue,
                        0,
                        0,0,0,0,0,0,0,0,0,0, 0, username,"onProgress", 0,0,0))
            }
        }


        getSupportActionBar()?.hide()
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        val navController = findNavController(R.id.nav_fragment)
        bottomNavigationView.setupWithNavController(navController)
        val eStatus = intent.getStringExtra("DATA_STATUS")
/*        var idTransaction = viewModel.readLastTransaction()!!
        //var totalRecord = viewModel.readTotalTransactionRecord(eId)
        Log.d("TestPRINT", eStatus+" "+idTransaction)*/
        if (eStatus == "print") {

            SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("Transaction Success")
                .setContentText("Print Receipt")
                .setConfirmText("Yes")
                .setConfirmClickListener { sDialog ->

                    initListeners()
                    sDialog.dismissWithAnimation()
                }.show()
        } else {
            Toast.makeText(this, "Ready to run", Toast.LENGTH_LONG)
        }


    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when(item.itemId){
            R.id.action_add ->{
                startActivity(Intent(this, AddProductActivity::class.java))
                finish()
                true
            }
            else -> false

        }
    }

    private fun initListeners() {
        if (!Printooth.hasPairedPrinter())
            resultLauncher.launch(
                Intent(
                    this@MainActivity,
                    ScanningActivity::class.java
                ),
            )
        else printDetails()

        /* callback from printooth to get printer process */
        printing?.printingCallback = object : PrintingCallback {
            override fun connectingWithPrinter() {
                Toast.makeText(this@MainActivity, "Connecting with printer", Toast.LENGTH_SHORT).show()
            }

            override fun printingOrderSentSuccessfully() {
                Toast.makeText(this@MainActivity, "Order sent to printer", Toast.LENGTH_SHORT).show()
            }

            override fun connectionFailed(error: String) {
                Toast.makeText(this@MainActivity, "Failed to connect printer", Toast.LENGTH_SHORT).show()
            }

            override fun onError(error: String) {
                Toast.makeText(this@MainActivity, error, Toast.LENGTH_SHORT).show()
            }

            override fun onMessage(message: String) {
                Toast.makeText(this@MainActivity, "Message: $message", Toast.LENGTH_SHORT).show()
            }

            override fun disconnected() {
                Toast.makeText(this@MainActivity, "Disconnected Printer", Toast.LENGTH_SHORT).show()
            }

        }
    }
    private fun printDetails() {
        val printables = getSomePrintables()
        printing?.print(printables)
    }

    /* Customize your printer here with text, logo and QR code */
    private fun getSomePrintables() = ArrayList<Printable>().apply {

        add(RawPrintable.Builder(byteArrayOf(27, 20, 4)).build()) // feed lines example in raw mode


        //logo
//            add(ImagePrintable.Builder(R.drawable.bold, resources)
//                    .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
//                    .build())
        val localeID =  Locale("in", "ID")
        val numberFormat = NumberFormat.getCurrencyInstance(localeID)

        add(
            TextPrintable.Builder()
                .setText("Blessing Of Shoes")
                .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
                .setFontSize(DefaultPrinter.FONT_SIZE_NORMAL)
                .setEmphasizedMode(DefaultPrinter.EMPHASIZED_MODE_BOLD)
                .setUnderlined(DefaultPrinter.UNDERLINED_MODE_OFF)
                .setNewLinesAfter(1)
                .build())
        add(
            TextPrintable.Builder()
                .setText("Jl. Raya Piyungan - Prambanan \nNo.KM. 1, Gatak, Bokoharjo, \nKec. Prambanan, Kabupaten Sleman,\nDaerah Istimewa Yogyakarta 55572")
                .setCharacterCode(DefaultPrinter.CHARCODE_PC1252)
                .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
                .setNewLinesAfter(1)
                .build())
        add(
            TextPrintable.Builder()
                .setText("0882006058160")
                .setCharacterCode(DefaultPrinter.CHARCODE_PC1252)
                .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
                .setNewLinesAfter(1)
                .build())
        add(
            TextPrintable.Builder()
                .setText("-------------------------------")
                .setCharacterCode(DefaultPrinter.CHARCODE_PC1252)
                .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
                .setNewLinesAfter(1)
                .build())

        val eStatus = intent.getStringExtra("DATA_STATUS")

        var idTransaction = viewModel.readLastTransaction()!!
        var totalPayment = (numberFormat.format(appDatabase.readTotalTransactionPayment(idTransaction)!!.toDouble()).toString())
        var totalReceived = (numberFormat.format(appDatabase.readTotalReceivedPayment(idTransaction)!!.toDouble()).toString())
        var totalChange = (numberFormat.format(appDatabase.readTotalChangePayment(idTransaction)!!.toDouble()).toString())
        var totalRecord = viewModel.readTotalTransactionRecord(idTransaction)
        if (idTransaction == idTransaction) {
            for (x in 0..totalRecord!!-1) {

                var nameItem : String = appDatabase.readNameItemReceipt(idTransaction,x)!!.toString()
                var priceItem : String = (numberFormat.format(appDatabase.readPriceItemReceipt(idTransaction,x)!!.toDouble()).toString())
                var totalItem : String = appDatabase.readTotalItemReceipt(idTransaction,x).toString()
                var totalPayment : String = (numberFormat.format(appDatabase.readTotalPaymentReceipt(idTransaction,x)!!.toDouble()).toString())
                add(
                    TextPrintable.Builder()
                        .setText(nameItem!!)
                        .setCharacterCode(DefaultPrinter.CHARCODE_PC1252)
                        .setNewLinesAfter(1)
                        .build())
                add(
                    TextPrintable.Builder()
                        .setText("("+priceItem!! + "x" + totalItem!!+")   "+totalPayment!!)
                        .setAlignment(DefaultPrinter.ALIGNMENT_RIGHT)
                        .setCharacterCode(DefaultPrinter.CHARCODE_PC1252)
                        .setNewLinesAfter(1)
                        .build())

            }
        } else {
            Toast.makeText(this@MainActivity, "Something Error", Toast.LENGTH_LONG)
        }
        add(
            TextPrintable.Builder()
                .setText("-------------------------------")
                .setCharacterCode(DefaultPrinter.CHARCODE_PC1252)
                .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
                .setNewLinesAfter(1)
                .build())
        add(
            TextPrintable.Builder()
                .setText("Subtotal "+totalPayment)
                .setCharacterCode(DefaultPrinter.CHARCODE_PC1252)
                .setAlignment(DefaultPrinter.ALIGNMENT_RIGHT)
                .setNewLinesAfter(1)
                .build())
        add(
            TextPrintable.Builder()
                .setText("-------------------------------")
                .setCharacterCode(DefaultPrinter.CHARCODE_PC1252)
                .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
                .setNewLinesAfter(1)
                .build())
        add(
            TextPrintable.Builder()
                .setText("Received "+totalReceived)
                .setCharacterCode(DefaultPrinter.CHARCODE_PC1252)
                .setAlignment(DefaultPrinter.ALIGNMENT_RIGHT)
                .setNewLinesAfter(1)
                .build())
        add(
            TextPrintable.Builder()
                .setText("Change "+totalChange)
                .setCharacterCode(DefaultPrinter.CHARCODE_PC1252)
                .setAlignment(DefaultPrinter.ALIGNMENT_RIGHT)
                .setNewLinesAfter(3)
                .build())

        add(
            TextPrintable.Builder()
                .setText("Thank you very much for buying\nfrom Blessing of Shoes")
                .setCharacterCode(DefaultPrinter.CHARCODE_PC1252)
                .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
                .build())

        add(RawPrintable.Builder(byteArrayOf(27, 100, 4)).build())

    }

    /* Inbuilt activity to pair device with printer or select from list of pair bluetooth devices */
    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == ScanningActivity.SCANNING_FOR_PRINTER &&  result.resultCode == Activity.RESULT_OK) {
            // There are no request codes
//            val intent = result.data
            printDetails()
        }
    }

}