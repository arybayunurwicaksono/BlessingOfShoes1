package com.example.blessingofshoes_1

import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.blessingofshoes_1.databinding.ItemFinancialBinding
import com.example.blessingofshoes_1.databinding.ItemRestockBinding
import com.example.blessingofshoes_1.db.BalanceReport
import com.example.blessingofshoes_1.db.Restock
import java.text.NumberFormat
import java.util.*

class FinancialAdapter : ListAdapter<BalanceReport, FinancialAdapter.FinancialHolder>(DiffCallback()) {

    inner class FinancialHolder (val binding: ItemFinancialBinding): RecyclerView.ViewHolder(binding.root)

    private lateinit var listener: RecyclerClickListener
    fun setItemListener(listener: RecyclerClickListener) {
        this.listener = listener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FinancialHolder {
        val binding = ItemFinancialBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return FinancialHolder(binding)
    }

    override fun onBindViewHolder(holder: FinancialHolder, position: Int) {
        val currentItem = getItem(position)
        val localeID =  Locale("in", "ID")
        val numberFormat = NumberFormat.getCurrencyInstance(localeID)
        holder.binding.BalanceTitle.text = currentItem!!.reportTag + " with " + currentItem!!.typePayment + " " + currentItem!!.status
        holder.binding.BalanceValue.text = numberFormat.format(currentItem.totalBalance!!.toDouble()).toString()
        if (currentItem!!.status == "Out") {
            holder.binding.balanceLayout.setBackgroundResource(R.color.light_red)
        } else {
            if (currentItem!!.typePayment == "Cash") {
                holder.binding.balanceLayout.setBackgroundResource(R.color.deep_green)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<BalanceReport>() {
        override fun areItemsTheSame(oldItem: BalanceReport, newItem: BalanceReport) =
            oldItem.idBalanceReport == newItem.idBalanceReport

        override fun areContentsTheSame(oldItem: BalanceReport, newItem: BalanceReport) =
            oldItem == newItem
    }
}