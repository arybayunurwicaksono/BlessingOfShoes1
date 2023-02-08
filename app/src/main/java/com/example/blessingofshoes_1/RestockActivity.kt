package com.example.blessingofshoes_1

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.drawToBitmap
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.Observer
import cn.pedant.SweetAlert.SweetAlertDialog
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.TransformationUtils
import com.example.blessingofshoes_1.databinding.ActivityRestockBinding
import com.example.blessingofshoes_1.db.BalanceReport
import com.example.blessingofshoes_1.db.Product
import com.example.blessingofshoes_1.db.Restock
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class RestockActivity : AppCompatActivity() {
    private lateinit var _activityRestockBinding: ActivityRestockBinding
    private val binding get() = _activityRestockBinding
    private val viewModel by viewModels<AppViewModel>()
    private var getFile: File? = null
    private var idProduct : Int? = 0
    lateinit var sharedPref: Preferences

    override fun onBackPressed() {
        super.onBackPressed()
        val i = Intent(this@RestockActivity, MainActivity::class.java)
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(i)
        finish()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _activityRestockBinding = ActivityRestockBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        sharedPref = Preferences(this)
        val eName = intent.getStringExtra("DATA_NAME")
        val eId = intent.getIntExtra("DATA_ID", 0)
        var itemPrice : Int = 0
        var itemRealPrice : Int = 0
        var totalStockPrice : Int = 0
        var itemProfit : Int
        var newStock : Int = 0
        var newProfit : Int = 0
        var newVar : Int = 0
        var itemProfitValue : String
        var oldVar : Int = 0
        var oldStock : Int = 0
        var overallStock : Int = 0
        var newRealPricePerItem : Int = 0
        var ePrice : Int = 0
        var eProfit : Int = 0
        val localeID =  Locale("in", "ID")
        val numberFormat = NumberFormat.getCurrencyInstance(localeID)
        var productBrand : String = ""
        var productPrice : String = ""
        var productSize : String = ""
        var productStock : Int = 0
        var productName : String

        Log.i("extraId", "ID : $eId")
        viewModel.readProductItem(eId).observe(this, Observer {
            productName = it.nameProduct!!
            binding.collapsingToolbar.title = "Restock "+ productName
            binding.profitValue.setText(it.profitProduct.toString())
            binding.priceValue.setText(it.realPriceProduct.toString())
            idProduct = it.idProduct
            oldStock = it.stockProduct!!.toString().toInt()
            oldVar = it.totalPurchases!!
            eProfit = it.profitProduct!!
            ePrice = it.priceProduct!!.toInt()
            productBrand = it.brandProduct!!.toString()
            productPrice = it.priceProduct!!.toString()
            productSize = it.sizeProduct!!.toString()
            productStock = it.stockProduct!!
            Glide.with(this@RestockActivity)
                .load(it.productPhoto)
                .fitCenter()
                .into(binding.imageView)
            binding.edtProductStock.addTextChangedListener(object : TextWatcher {

                override fun afterTextChanged(s: Editable) {
                    when {
                        s.isNullOrBlank() -> {
                            binding.edtProductStock.error = "Fill New Stock"
                        }
                    }
                }

                override fun beforeTextChanged(s: CharSequence, start: Int,
                                               count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence, start: Int,
                                           before: Int, count: Int) {
                    when {
                        s.isNullOrBlank() -> {
                            binding.edtProductStock.error = "Fill New Stock"
                        }
                        else -> {
                            newStock = s.toString().toInt()
                        }
                    }

                }
            })
            binding.edtTotalStockPurchases.addTextChangedListener(object : TextWatcher {

                override fun afterTextChanged(s: Editable) {
                    when {
                        s.isNullOrBlank() -> {
                            binding.edtTotalStockPurchases.error = "Fill Total Stock Purchases"
                        }
                    }
                }

                override fun beforeTextChanged(s: CharSequence, start: Int,
                                               count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence, start: Int,
                                           before: Int, count: Int) {
                    when {
                        s.isNullOrBlank() -> {
                            binding.edtTotalStockPurchases.error = "Fill Total Stock Purchases"
                        }
                        else -> {

                            totalStockPrice = s.toString().toInt()
                            newVar = totalStockPrice
                            overallStock = oldStock + newStock


                            newRealPricePerItem = (oldVar + newVar)/overallStock
                            newProfit = ePrice - newRealPricePerItem
                            binding.priceValue.text = newRealPricePerItem.toString()
                            binding.profitValue.text = newProfit.toString()

                        }
                    }

                }
            })
            binding.btnInsertProduct.setOnClickListener {
                var pStock = binding.edtProductStock.text.toString().trim()


                var productRealPrice = binding.priceValue.text.toString().trim()
                val totalStockPurchases = binding.edtTotalStockPurchases.text.toString().trim()
                val supplier = binding.edtSupplier.text.toString().trim()
                val productProfit = binding.profitValue.text.toString().toInt()
                when {
                    pStock.isEmpty() -> {
                        binding.edtProductStock.error = "Fill New Stock"
                        SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Oops...")
                            .setContentText("Some data is empty!")
                            .show()
                    }
                    totalStockPurchases.isEmpty() -> {
                        binding.edtTotalStockPurchases.error = "Fill Total Stock Purchases"
                        SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Oops...")
                            .setContentText("Some data is empty!")
                            .show()
                    }
                    supplier.isEmpty() -> {
                        binding.edtSupplier.error = "Fill Supplier"
                        SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Oops...")
                            .setContentText("Some data is empty!")
                            .show()
                    }
                    else -> {

                        var productStockFix : Int = productStock + pStock.toInt()
                        var eIdProduct = eId.toString()
                        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
                        val currentDate = sdf.format(Date())
                        var productPriceFix = productPrice.toInt()
                        var productRealPriceFix = productRealPrice.toInt()
                        var totalPurchasesFix = oldVar + totalStockPurchases.toInt()
                        var pStockFix = pStock.toInt()
                        var pTotalPurchases = totalStockPurchases.toInt()
                        val username = viewModel.readUsername(sharedPref.getString(Constant.PREF_EMAIL))
                        SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText("Payment Method")
                            .setConfirmText("Cash")
                            .setCancelText("Digital")
                            .setConfirmClickListener { sDialog ->
                                binding.edtType.setText("Cash")
                                var typePayment = binding.edtType.text.toString()

                                    sDialog.dismissWithAnimation()
                                SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                                    .setTitleText("Data is Correct")
                                    .setContentText("Click Save to insert data")
                                    .setConfirmText("Save")
                                    .setConfirmClickListener { pDialog ->
                                        lifecycleScope.launch {
                                            val productPhoto = binding.imageView.drawToBitmap()
                                            var reportPurchases = binding.edtTotalStockPurchases.text.toString().toInt()
                                            viewModel.updateProductItem(applicationContext, eId, productName, productBrand,
                                                productPriceFix, productStockFix, productSize, productRealPriceFix, totalPurchasesFix, productProfit, productPhoto, username, currentDate) {
                                                viewModel.insertRestock(Restock(0, eId, productName, pStockFix, pTotalPurchases, supplier, currentDate))
                                                viewModel.insertBalanceReport(BalanceReport(0, reportPurchases, "Out", typePayment, "restock", username, currentDate))
                                                viewModel.updateCashOutBalance(this@RestockActivity,reportPurchases) {
                                                    finishUpdate()
                                                }

                                            }
                                        }

                                    }
                                    .show()
                            }
                            .setCancelText("Digital")
                            .setCancelClickListener { xDialog ->
                                binding.edtType.setText("Digital")
                                var typePayment = binding.edtType.text.toString()

                                SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                                    .setTitleText("Data is Correct")
                                    .setContentText("Click Save to insert data")
                                    .setConfirmText("Save")
                                    .setConfirmClickListener { pDialog ->
                                        pDialog.dismissWithAnimation()
                                        lifecycleScope.launch {
                                            var reportPurchases = binding.edtTotalStockPurchases.text.toString().toInt()
                                            val productPhoto = binding.imageView.drawToBitmap()
                                            viewModel.updateProductItem(applicationContext, eId, productName, productBrand,
                                                productPriceFix, productStockFix, productSize, productRealPriceFix, totalPurchasesFix, productProfit, productPhoto, username, currentDate) {
                                                viewModel.insertRestock(Restock(0, eId, productName, pStockFix, pTotalPurchases, supplier, currentDate))
                                                viewModel.insertBalanceReport(BalanceReport(0, reportPurchases, "Out", typePayment, "restock", username, currentDate))
                                                viewModel.updateDigitalOutBalance(this@RestockActivity,reportPurchases) {
                                                    finishUpdate()
                                                }

                                            }
                                        }

                                    }.show()
                            }
                            .show()

                    }
                }
            }

        })

    }


    fun finishUpdate(){
        Toast.makeText(this, "Restock Complete", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

}