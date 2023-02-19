package com.example.blessingofshoes_1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.blessingofshoes_1.adapter.FinancialAdapter
import com.example.blessingofshoes_1.databinding.ActivityRestockDataBinding
import com.example.blessingofshoes_1.db.AppDb
import com.example.blessingofshoes_1.db.BalanceReport
import com.example.blessingofshoes_1.utils.Preferences
import com.example.blessingofshoes_1.viemodel.AppViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.ArrayList

@AndroidEntryPoint
class FinancialActivity : AppCompatActivity() {
    private lateinit var _activityRestockDataBinding: ActivityRestockDataBinding
    private val binding get() = _activityRestockDataBinding
    private lateinit var adapter: FinancialAdapter
    private val viewModel by viewModels<AppViewModel>()
    lateinit var sharedPref: Preferences
    lateinit var balanceList: ArrayList<BalanceReport>
    lateinit var balanceListData: List<BalanceReport>
    lateinit var listBalance : ArrayList<BalanceReport>
    private val appDatabase by lazy { AppDb.getDatabase(this).dbDao() }
    private lateinit var rvBalance: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_financial)

        setRecyclerView()
        observeNotes()
    }

    private fun setRecyclerView() {
        rvBalance = findViewById<RecyclerView>(R.id.rv_balance)
        rvBalance.layoutManager = LinearLayoutManager(this)
        rvBalance.setHasFixedSize(true)
        adapter = FinancialAdapter()
        rvBalance.adapter = adapter
    }
    private fun observeNotes() {
        lifecycleScope.launch {
            appDatabase.getBalanceReportData().collect { balanceList ->
                if (balanceList.isNotEmpty()) {
                    balanceListData = balanceList
                    adapter.submitList(balanceList)
                }
            }
        }
    }
}