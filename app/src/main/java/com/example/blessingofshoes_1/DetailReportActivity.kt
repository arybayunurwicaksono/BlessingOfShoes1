package com.example.blessingofshoes_1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.blessingofshoes_1.adapter.DetailReportAdapter
import com.example.blessingofshoes_1.databinding.ActivityDetailReportBinding
import com.example.blessingofshoes_1.db.AppDb
import com.example.blessingofshoes_1.utils.Preferences
import com.example.blessingofshoes_1.viemodel.AppViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File
import java.text.NumberFormat
import java.util.*


@AndroidEntryPoint
class DetailReportActivity : AppCompatActivity() {

    private lateinit var _activityDetailReportBinding: ActivityDetailReportBinding
    private val binding get() = _activityDetailReportBinding
    private val viewModel by viewModels<AppViewModel>()
    private var getFile: File? = null
    private lateinit var adapter: DetailReportAdapter
    private var idProduct : Int? = 0
    var total: Int = 0
    var totalItemPrice : Int = 0
    lateinit var sharedPref: Preferences
    private val appDatabase by lazy { AppDb.getDatabase(this).dbDao() }

    var profitItem: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _activityDetailReportBinding = ActivityDetailReportBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPref = Preferences(this)
        val localeID =  Locale("in", "ID")
        val numberFormat = NumberFormat.getCurrencyInstance(localeID)

        val eId = intent.getIntExtra("DATA_ID", 0)
        Log.i("extraData", "ID : $eId")

        viewModel.readTransactionById(eId).observe(this, Observer {
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
            binding.transactionDate.text = it.transactionDate.toString()

        })
        lifecycleScope.launch {
            appDatabase.readTransactionItem(eId).collect { cartList ->
                if (cartList.isNotEmpty()) {
                    adapter.submitList(cartList)
                }
            }
        }
        setRecyclerView()


    }

    private fun setRecyclerView() {
        val rvCart = findViewById<RecyclerView>(R.id.rv_product_cart)
        rvCart.layoutManager = LinearLayoutManager(this)
        rvCart.setHasFixedSize(true)
        adapter = DetailReportAdapter()
        rvCart.adapter = adapter
    }
}