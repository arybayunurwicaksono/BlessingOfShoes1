package com.example.blessingofshoes_1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.blessingofshoes_1.databinding.ActivityRestockDataBinding
import com.example.blessingofshoes_1.db.AppDb
import com.example.blessingofshoes_1.db.Cart
import com.example.blessingofshoes_1.db.Restock
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.ArrayList

@AndroidEntryPoint
class RestockDataActivity : AppCompatActivity() {

    private lateinit var _activityRestockDataBinding: ActivityRestockDataBinding
    private val binding get() = _activityRestockDataBinding
    private lateinit var adapter: RestockDataAdapter
    private val viewModel by viewModels<AppViewModel>()
    lateinit var sharedPref: Preferences
    lateinit var restockList: ArrayList<Restock>
    lateinit var restockListData: List<Restock>
    lateinit var listRestock : ArrayList<Restock>
    private val appDatabase by lazy { AppDb.getDatabase(this).dbDao() }
    private lateinit var rvRestock: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restock_data)

        setRecyclerView()
        observeNotes()
    }

    private fun setRecyclerView() {
        rvRestock = findViewById<RecyclerView>(R.id.rv_product_cart)
        rvRestock.layoutManager = LinearLayoutManager(this)
        rvRestock.setHasFixedSize(true)
        adapter = RestockDataAdapter()
        rvRestock.adapter = adapter
    }
    private fun observeNotes() {
        lifecycleScope.launch {
            appDatabase.getRestockData().collect { restockList ->
                if (restockList.isNotEmpty()) {
                    restockListData = restockList
                    adapter.submitList(restockList)
                }
            }
        }
    }
}