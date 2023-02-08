package com.example.blessingofshoes_1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.blessingofshoes_1.databinding.ActivityDetailReportBinding
import com.example.blessingofshoes_1.databinding.ActivityReturnBinding
import com.example.blessingofshoes_1.db.AppDb
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File
import java.text.NumberFormat
import java.util.*

@AndroidEntryPoint
class ReturnActivity : AppCompatActivity() {
    private lateinit var _activityReturnBinding: ActivityReturnBinding
    private val binding get() = _activityReturnBinding
    private val viewModel by viewModels<AppViewModel>()
    private var getFile: File? = null
    private lateinit var adapter: ReturnAdapter
    private var idProduct : Int? = 0
    var total: Int = 0
    var totalItemPrice : Int = 0
    lateinit var sharedPref: Preferences
    private val appDatabase by lazy { AppDb.getDatabase(this).dbDao() }

    var profitItem: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _activityReturnBinding = ActivityReturnBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPref = Preferences(this)
        val localeID =  Locale("in", "ID")
        val numberFormat = NumberFormat.getCurrencyInstance(localeID)

        val eId = intent.getIntExtra("DATA_ID", 0)
        Log.i("extraData", "ID : $eId")


        binding.edtIdTransaction.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                when {
                    s.isNullOrBlank() -> {
                        binding.edtIdTransaction.error
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
                when {
                    s.isNullOrBlank() -> {
                        binding.edtIdTransaction.error
                        binding.rvTransaction.setVisibility(View.GONE)
                    }
                }
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
                when {
                    s.isNullOrBlank() -> {
                        binding.edtIdTransaction.error
                        binding.rvTransaction.setVisibility(View.GONE)
                    }
                    else -> {
                        var readId = s.toString().toInt()
                        lifecycleScope.launch {
                            appDatabase.readTransactionItem(readId).collect { cartList ->
                                if (cartList.isNotEmpty()) {
                                    adapter.submitList(cartList)
                                }
                            }
                        }
                        setRecyclerView()
                        binding.rvTransaction.setVisibility(View.VISIBLE)
                    }
                }

            }
        })

        binding.btnReadId.setOnClickListener{
            if (eId == 0) {
                var readId = binding.edtIdTransaction.text.toString()
                when {
                    readId.toString().isNullOrEmpty() -> {
                        SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("ID IS EMPTY")
                            .setContentText("PLEASE INSERT AN ID CORRECTLY")
                            .setConfirmText("OK")
                            .show()
                        binding.rvTransaction.setVisibility(View.GONE)
                    }
                    else -> {
                        lifecycleScope.launch {
                            appDatabase.readTransactionItem(readId.toInt()).collect { cartList ->
                                if (cartList.isNotEmpty()) {
                                    adapter.submitList(cartList)
                                }
                            }
                        }

                        setRecyclerView()
                        binding.rvTransaction.setVisibility(View.VISIBLE)
                    }
                }

            }
            else {
                lifecycleScope.launch {
                    appDatabase.readTransactionItem(eId).collect { cartList ->
                        if (cartList.isNotEmpty()) {
                            adapter.submitList(cartList)
                        }
                    }
                }
                setRecyclerView()
            }
        }
        /*viewModel.readTransactionById(eId).observe(this, Observer {
            binding.tvItemTotalProfitTitle.text = "Total Profit"
            binding.tvItemTotalPriceTitle.text = "Total Price"
            binding.usernameTitle.text = "Cashier Username"
            binding.transactionIdTitle.text = "Transaction ID"

            binding.transactionId.setText("#00"+it.idTransaction!!.toString())
            binding.username.setText(it.username!!.toString())
            var total = it.totalTransaction!!.toString()
            var profit = it.profitTransaction!!.toString()

            Log.i("extraData", "ID : $total")
            Log.i("extraData", "ID : $profit")
            binding.tvItemTotalPrice.text = (numberFormat.format(it.totalTransaction!!.toDouble()).toString())
            binding.tvItemTotalProfit.text = (numberFormat.format(it.profitTransaction!!.toDouble()).toString())

        })*/



    }

    private fun setRecyclerView() {
        val rvTransaction = findViewById<RecyclerView>(R.id.rv_transaction)
        rvTransaction.layoutManager = LinearLayoutManager(this)
        rvTransaction.setHasFixedSize(true)
        adapter = ReturnAdapter()
        rvTransaction.adapter = adapter
    }
}