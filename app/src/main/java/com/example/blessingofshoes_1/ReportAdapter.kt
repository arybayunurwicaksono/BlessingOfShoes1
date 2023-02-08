package com.example.blessingofshoes_1

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Transaction
import com.bumptech.glide.Glide
import com.example.blessingofshoes_1.databinding.ItemProductSecondBinding
import com.example.blessingofshoes_1.databinding.ItemReportBinding
import com.example.blessingofshoes_1.db.Product
import java.text.NumberFormat
import java.util.*

class ReportAdapter (private val context: Context?, private var transactionItem: List<com.example.blessingofshoes_1.db.Transaction>) : RecyclerView.Adapter<ReportAdapter.ReportViewHolder>() {

    inner class ReportViewHolder (val binding: ItemReportBinding): RecyclerView.ViewHolder(binding.root)
    private lateinit var onItemClickCallback: OnItemClickCallback
    private lateinit var viewModel: AppViewModel


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportViewHolder {
        val binding = ItemReportBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ReportViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReportViewHolder, position: Int) {
        val listTransaction = transactionItem[position]

        holder.binding.txtPaymentTitle.text = "Total Payment"
        holder.binding.txtProfitTitle.text = "Profit"
        val localeID =  Locale("in", "ID")
        val numberFormat = NumberFormat.getCurrencyInstance(localeID)
        holder.binding.txtItemTotalProfit.text = numberFormat.format(listTransaction.profitTransaction!!.toDouble()).toString()
        holder.binding.txtTotalBayar.text = numberFormat.format(listTransaction.totalTransaction!!.toDouble()).toString()
        holder.binding.btnView.setOnClickListener {
            val intent = Intent(holder.itemView.context, DetailReportActivity::class.java)
            intent.putExtra("DATA_ID", listTransaction.idTransaction)
            holder.itemView.context.startActivity(intent)
        }
        holder.binding.txtTglTransaksi.text = listTransaction.transactionDate
    }


    override fun getItemCount(): Int = transactionItem.size



    fun setTransactionData(postList: List<com.example.blessingofshoes_1.db.Transaction>)
    {

        this.transactionItem = postList
        notifyDataSetChanged()

    }
    interface OnItemClickCallback {
        fun onItemClicked(data: Product)
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

}