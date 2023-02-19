package com.example.blessingofshoes_1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.drawToBitmap
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import cn.pedant.SweetAlert.SweetAlertDialog
import com.bumptech.glide.Glide
import com.example.blessingofshoes_1.databinding.ActivityDetailProductBinding
import com.example.blessingofshoes_1.db.Cart
import com.example.blessingofshoes_1.utils.Constant
import com.example.blessingofshoes_1.utils.Preferences
import com.example.blessingofshoes_1.viemodel.AppViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File
import java.text.NumberFormat
import java.util.*

@AndroidEntryPoint
class DetailProductActivity : AppCompatActivity() {

    private lateinit var _activityDetailProductBinding: ActivityDetailProductBinding
    private val binding get() = _activityDetailProductBinding
    private val viewModel by viewModels<AppViewModel>()
    private var getFile: File? = null
    private var idProduct : Int? = 0
    var total: Int = 0
    var totalItemPrice : Int = 0
    lateinit var sharedPref: Preferences

    var profitItem: Int = 0
    /*override fun onBackPressed() {
        super.onBackPressed()
        val i = Intent(this@DetailProductActivity, MainActivity::class.java)
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(i)
        finish()

    }*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _activityDetailProductBinding = ActivityDetailProductBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPref = Preferences(this)
        val localeID =  Locale("in", "ID")
        val numberFormat = NumberFormat.getCurrencyInstance(localeID)

        val eId = intent.getIntExtra("DATA_ID", 0)
        val eName = intent.getStringExtra("DATA_NAME")
        val ePrice = intent.getIntExtra("DATA_PRICE", 0)
        val eTotal = intent.getIntExtra("DATA_TOTAL", 0)
        val eProfit = intent.getIntExtra("DATA_PROFIT", 0)
        val ePayment = intent.getIntExtra("DATA_PAYMENT", 0)
        Log.i("extraData", "ID : $eId")
        Log.i("extraData", "nane : $eName")
        Log.i("extraData", "total : $eTotal")
        Log.i("extraData", "profit : $eProfit")
        Log.i("extraData", "payment : $ePayment")

        viewModel.readProductItem(eId).observe(this, Observer {
            binding.tvProductNameTitle.text = "Name"
            binding.tvProductBrandTitle.text = "Brand"
            binding.tvProductSizeTitle.text = "Size"
            binding.tvProductStockTitle.text = "Stock Left"
            binding.tvProductPriceTitle.text = "Price Per Item"
            binding.tvProductProfitTitle.text = "Profit Per Item"
            binding.tvItemTotalTitle.text = "Total Item"
            binding.tvItemTotalProfitTitle.text = "Total Profit"
            binding.tvItemTotalPriceTitle.text = "Total Price"

            binding.tvProductName.setText(it.nameProduct)
            binding.tvProductBrand.setText(it.brandProduct)
            binding.tvProductPrice.setText(numberFormat.format(it.priceProduct!!.toDouble()).toString())
            binding.tvProductSize.setText(it.sizeProduct)
            binding.tvProductProfit.setText(numberFormat.format(it.profitProduct!!.toDouble()).toString())
            binding.tvTotalPrice.setText(numberFormat.format(ePayment.toDouble()).toString())
            binding.tvProductStock.setText(it.stockProduct.toString())
            Glide.with(this@DetailProductActivity)
                .load(it.productPhoto)
                .fitCenter()
                .into(binding.imageView)


            // di comment


            var extraPrice = (it.priceProduct!!).toInt()
            var extraRealPrice = (it.realPriceProduct!!).toInt()
            var totalPurchasesFinal = eTotal * extraRealPrice
            var extraProfit = (it.profitProduct!!).toInt()
            //val email = sharedPref.getString(Constant.PREF_EMAIL)
            binding.tvItemTotal.setText(eTotal.toString())
            binding.tvItemTotalProfit.setText(eProfit.toString())
            binding.tvItemTotalPrice.setText(ePayment.toString())
            binding.btnAddToCart.setOnClickListener {
                Toast.makeText(this,totalItemPrice.toString(),Toast.LENGTH_SHORT)
                lifecycleScope.launch {
                    SweetAlertDialog(this@DetailProductActivity, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("Attention")
                        .setContentText("You sure to add this item to Cart?")
                        .setConfirmText("Yes")
                        .setConfirmClickListener { sDialog ->
                            val username = viewModel.readUsername(sharedPref.getString(Constant.PREF_EMAIL)) //username belum muncul!
                            val productPhoto = binding.imageView.drawToBitmap()
                            viewModel.insertCart(Cart(0, eId, eName, extraPrice, eTotal, extraProfit, eProfit, ePayment, username, "onProgress", 0, productPhoto))
                            viewModel.sumTotalPurchasesItem(eId, totalPurchasesFinal)
                            viewModel.sumStockItem(eId, eTotal)
                            sDialog.dismissWithAnimation()
                            val navController = findNavController(R.id.bottom_nav)
                            navController.navigate(R.id.action_global_test)
                        }
                        .show()


                }


            }


        })
    }
}

/*idProduct = it.idProduct
            var stockExist = it.stockProduct
            var priceDefault = it.priceProduct
            if (it.stockProduct!!.toInt() > 0){
                binding.tvProductStock.text = "Stock: " + it.stockProduct
            } else{
                binding.tvStockTitle.text = "Stock: 0"
                binding.tvStockTitle.setTextColor(Color.RED)
                binding.tvProductStock.setTextColor(Color.RED)
            }
            binding.tvTotalTitle.text = "Rp."
            Glide.with(this@DetailProductActivity)
                .load(it.productPhoto)
                .fitCenter()
                .into(binding.imageView)
            binding.btnPlus.setOnClickListener {
                val old_value = binding.txtQty.text.toString().toInt()
                val new_value = old_value+1

                // Tidak bisa tambah lebih dari stok yg ada
                if(new_value > stockExist!!.toInt()){
                    Toast.makeText(this, "Stock Empty", Toast.LENGTH_LONG).show()
                }else{
                    binding.txtQty.setText(new_value.toString())
                    val subtotal = priceDefault!!.toInt() * new_value
                    binding.tvTotalPrice.setText(subtotal.toString())

                    total = total + priceDefault.toString().toInt()
                }

            }

            binding.btnMinus.setOnClickListener {
                val old_value = binding.txtQty.text.toString().toInt()
                val new_value = old_value-1

                // Biarkan data terhapus apabila jumlah nol
                if (new_value>=0){
                    binding.txtQty.setText(new_value.toString())
                    val subtotal = priceDefault!!.toInt() * new_value
                    binding.tvTotalPrice.setText(subtotal.toString())
                    totalItemPrice = subtotal.toInt()
                    total = total - priceDefault.toString().toInt()
                }
            }*/