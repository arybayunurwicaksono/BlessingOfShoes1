package com.example.blessingofshoes_1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.core.view.drawToBitmap
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.blessingofshoes_1.databinding.ActivityDetailProductBinding
import com.example.blessingofshoes_1.databinding.ActivityPaymentBinding
import com.example.blessingofshoes_1.db.AppDb
import com.example.blessingofshoes_1.db.Cart
import com.example.blessingofshoes_1.db.Product
import com.example.blessingofshoes_1.db.Transaction
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.lang.Boolean.TRUE
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
@AndroidEntryPoint
class PaymentActivity : AppCompatActivity() {

    private lateinit var _activityPaymentBinding: ActivityPaymentBinding
    private val binding get() = _activityPaymentBinding
    private lateinit var adapter: PaymentAdapter
    private val viewModel by viewModels<AppViewModel>()
    lateinit var sharedPref: Preferences
    lateinit var cartList: ArrayList<Cart>
    lateinit var cartListData: List<Cart>
    lateinit var listCart : ArrayList<Cart>
    private val appDatabase by lazy { AppDb.getDatabase(this).dbDao() }
    private lateinit var rvCart: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _activityPaymentBinding = ActivityPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPref = Preferences(this)
        val localeID =  Locale("in", "ID")
        val numberFormat = NumberFormat.getCurrencyInstance(localeID)
        var tvTotalPayment = binding.txtTotalBayar
        var validateCart = viewModel.checkCart()
        when {
            validateCart == 0 -> {
                tvTotalPayment.text = "Rp.000.00"
            }
            else ->{
                tvTotalPayment.text = (numberFormat.format(viewModel.sumTotalPayment()!!.toDouble()).toString())
            }
        }
        var itemTotalPayment : Int = viewModel.sumTotalPayment()!!
        var itemPayment : Int = 0
        var itemPaymentReturn : Int = 0

        binding.diterima.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                when {
                    s.isNullOrBlank() -> {
                        binding.diterima.error = "Fill Payment"
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
                        binding.diterima.error = "Fill Payment"
                    }
                    else -> {
                        itemPayment = s.toString().toInt()
                        itemPaymentReturn = itemPayment - itemTotalPayment
                        if(itemPaymentReturn<0){
                            binding.diterima.error = "payment received less"
                        } else {
                            binding.txtReturn.text = itemPaymentReturn.toString()
                        }
                    }
                }

            }
        })

        setRecyclerView()
        observeNotes()
        initAction()
        binding.btnSimpanBayar.setOnClickListener {
            val paymentReceive = binding.diterima.text.toString().trim()
            when {
                validateCart == 0 -> {
                    SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Cart is Empty")
                        .setContentText("Please return to cashier menu!")
                        .show()
                }
                paymentReceive.isEmpty() -> {
                    binding.diterima.error = "Fill Payment"
                    SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Oops...")
                        .setContentText("Some data is not correct!")
                        .show()
                }
                paymentReceive.toInt()<0 -> {
                    binding.diterima.error = "payment received less"
                    SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Oops...")
                        .setContentText("Payment received less then Total Payment")
                        .show()
                }
                else -> {

                    var validateCart = viewModel.checkCart()
                    when {
                        validateCart == 0 -> {
                            tvTotalPayment.text = "Rp.000.00"
                            SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Cart is Empty")
                                .setContentText("Please return to cashier menu!")
                                .show()
                        }
                        else ->{
                            tvTotalPayment.text = (numberFormat.format(viewModel.sumTotalPayment()!!.toDouble()).toString())
                            var totalProfit = (numberFormat.format(viewModel.sumTotalProfit()!!.toDouble()).toString())
                            var totalCartProfit = viewModel.sumTotalProfit()!!.toInt()
                            var status = "complete"
                            var cartTotal = viewModel.sumTotalPayment()!!.toInt()
                            var moneyReceived = binding.diterima.text.toString().toInt()
                            var moneyChange = moneyReceived - cartTotal
                            val username = viewModel.readUsername(sharedPref.getString(Constant.PREF_EMAIL))
                            var onProcess : Boolean
                            val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
                            val currentDate = sdf.format(Date())
                            SweetAlertDialog(this, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                                .setTitleText("Payment")
                                .setContentText("Choose payment method")
                                .setConfirmText("Cash")
                                .setCustomImage(R.drawable.ic_baseline_balance_for_payment)
                                .setConfirmClickListener { sDialog ->
                                    lifecycleScope.launch {
                                        var typePayment = "Cash"
                                        viewModel.insertTransaction(
                                            Transaction(0, cartTotal, totalCartProfit, moneyReceived,
                                                moneyChange, username, typePayment, currentDate))
                                        onProcess = TRUE
                                        if(onProcess == TRUE) {
                                            var idTransaction = viewModel.readLastTransaction()!!.toInt()
                                            viewModel.updateCartIdTransaction(applicationContext, idTransaction) {
                                                viewModel.updateCartStatus(applicationContext, status){
                                                    /*setRecyclerView()
                                                    observeNotes()*/

                                                }
                                            }
                                        }
                                    }
                                    sDialog.dismissWithAnimation()
                                    SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                                        .setTitleText("Transaction Succeed :)")
                                        .setConfirmText("Ok")
                                        .setConfirmClickListener { fDialog ->
                                            val intent = Intent(this@PaymentActivity, MainActivity::class.java)
                                            startActivity(intent)
                                        }

                                }
                                .setCancelText("Digital")
                                .setCancelButtonBackgroundColor(R.color.blue_600)
                                .setCancelClickListener { pDialog ->
                                    var typePayment = "Digital"
                                    viewModel.insertTransaction(
                                        Transaction(0, cartTotal, totalCartProfit, moneyReceived,
                                            moneyChange, username, typePayment, currentDate))
                                    onProcess = TRUE
                                    if(onProcess == TRUE) {
                                        var idTransaction = viewModel.readLastTransaction()!!.toInt()
                                        viewModel.updateCartIdTransaction(applicationContext, idTransaction) {
                                            viewModel.updateCartStatus(applicationContext, status){
                                                /*setRecyclerView()
                                                observeNotes()*/
                                                val intent = Intent(this@PaymentActivity, MainActivity::class.java)
                                                startActivity(intent)
                                            }
                                        }
                                    }
                                    pDialog.dismissWithAnimation()
                                    SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                                        .setTitleText("Transaction Succeed :)")
                                        .setConfirmText("Ok")
                                        .setConfirmClickListener { fDialog ->
                                            val intent = Intent(this@PaymentActivity, MainActivity::class.java)
                                            startActivity(intent)
                                        }
                                }
                                .show()
                        }
                    }




                }
            }

        }
    }

    private fun initAction() {
        val callback = Callback()
        val itemTouchHelper = ItemTouchHelper(callback)

        itemTouchHelper.attachToRecyclerView(rvCart)
    }

    private fun setRecyclerView() {
        rvCart = findViewById<RecyclerView>(R.id.rv_product_cart)
        rvCart.layoutManager = LinearLayoutManager(this)
        rvCart.setHasFixedSize(true)
        adapter = PaymentAdapter()
        rvCart.adapter = adapter
    }
    private fun observeNotes() {
        lifecycleScope.launch {
            /*appDatabase.getAllCartItem().collect { cartList ->
                if (cartList.isNotEmpty()) {
                    adapter.submitList(cartList)
                }
            }*/
            var status : String = "onProgress"
            appDatabase.getCartByStatus(status).collect { cartList ->
                if (cartList.isNotEmpty()) {
                    cartListData = cartList
                    adapter.submitList(cartList)
                }
            }
        }
    }
    inner class Callback : ItemTouchHelper.Callback() {

        override fun getMovementFlags(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder
        ): Int {
            return makeMovementFlags(0, ItemTouchHelper.RIGHT)
        }

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val position = viewHolder.adapterPosition
            val data = cartListData[position]
            val data2 = cartListData[position]
            //Toast.makeText(context, "Berhasil Menghapus : " + data.nameProduct, Toast.LENGTH_LONG).show()
            SweetAlertDialog(this@PaymentActivity, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                .setTitleText("Delete this "+ data.nameItem.toString() + "?")
                .setContentText("You cannot undo this event!")
                .setCustomImage(R.drawable.logo_round)
                .setConfirmText("Ok")
                .setConfirmClickListener { sDialog ->
                    viewModel.sumCancelableStockItem(data2.idProduct, data2.totalItem)
                    var tPayment = data2.totalpayment.toString().toInt()
                    var tProfit = data2.totalProfit!!.toString().toInt()
                    var priceFix = tPayment - tProfit
                    viewModel.sumCancelableTotalPurchasesItem(data2.idProduct, priceFix)
                    viewModel.deleteCart(data.idItem)
                    val localeID =  Locale("in", "ID")
                    val numberFormat = NumberFormat.getCurrencyInstance(localeID)
                    var tvTotalPayment = binding.txtTotalBayar
                    var validateCart = viewModel.checkCart()
                    when {
                        validateCart == 0 -> {
                            tvTotalPayment.text = "Rp.000.00"
                        }
                        else ->{
                            tvTotalPayment.text = (numberFormat.format(viewModel.sumTotalPayment()!!.toDouble()).toString())
                        }
                    }
                    sDialog.dismissWithAnimation()
                }
                .setCancelText("Cancel")
                .setCancelClickListener { pDialog ->
                    viewModel.insertCart(data2)
                    observeNotes()
                    pDialog.dismissWithAnimation()
                }
                .show()

            /*Snackbar.make(binding.root, "Deleted " + data.nameItem, Snackbar.LENGTH_LONG)
                .setAction(
                    "Undo",
                    View.OnClickListener {
                        // adding on click listener to our action of snack bar.
                        // below line is to add our item to array list with a position.

                        // below line is to notify item is
                        // added to our adapter class.


                    }).show()*/



        }



    }



}