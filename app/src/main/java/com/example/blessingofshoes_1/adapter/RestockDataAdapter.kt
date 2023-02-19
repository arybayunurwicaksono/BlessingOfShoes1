package com.example.blessingofshoes_1.adapter

import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.blessingofshoes_1.R
import com.example.blessingofshoes_1.RecyclerClickListener
import com.example.blessingofshoes_1.databinding.ItemRestockBinding
import com.example.blessingofshoes_1.db.Restock
import java.text.NumberFormat
import java.util.*

class RestockDataAdapter : ListAdapter<Restock, RestockDataAdapter.RestockHolder>(DiffCallback()) {

    inner class RestockHolder (val binding: ItemRestockBinding): RecyclerView.ViewHolder(binding.root)

    private lateinit var listener: RecyclerClickListener
    fun setItemListener(listener: RecyclerClickListener) {
        this.listener = listener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestockHolder {
        val binding = ItemRestockBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return RestockHolder(binding)
    }

    override fun onBindViewHolder(holder: RestockHolder, position: Int) {
        val currentItem = getItem(position)
        val localeID =  Locale("in", "ID")
        val numberFormat = NumberFormat.getCurrencyInstance(localeID)
        holder.binding.username.text = "belum update table"
        holder.binding.usernameTitle.text = "Username"
        holder.binding.stockTitle.text = "Stock"
        holder.binding.supplierTitle.text = "Supplier"
        holder.binding.tvIdRestock.text = "#00"+ currentItem!!.idRestock.toString()
        holder.binding.tvProductName.text = currentItem!!.nameProduct
        holder.binding.tvSupplier.text = currentItem!!.supplier
        holder.binding.txtTglRestock.text = currentItem!!.restockDate
        holder.binding.txtItemTotalPurchases.text = numberFormat.format(currentItem.totalPurchases!!.toDouble()).toString()
        holder.binding.txtItemTotalRestock.text = currentItem!!.stockAdded.toString() + "Item"
        holder.binding.imageSwitch.setOnClickListener {
            if (holder.binding.txtItemTotalRestock.visibility == GONE) {
                TransitionManager.beginDelayedTransition(holder.binding.root, AutoTransition())
                holder.binding.username.visibility = VISIBLE
                holder.binding.usernameTitle.visibility = VISIBLE
                holder.binding.stockTitle.visibility = VISIBLE
                holder.binding.supplierTitle.visibility = VISIBLE
                holder.binding.txtItemTotalPurchasesTitle.visibility = VISIBLE
                holder.binding.txtItemTotalPurchases.visibility = VISIBLE
                holder.binding.txtItemTotalRestock.visibility = VISIBLE
                holder.binding.tvSupplier.visibility = VISIBLE
                holder.binding.divider.visibility = VISIBLE
                holder.binding.imageSwitch.setImageResource(R.drawable.ic_baseline_keyboard_arrow_up_24)
            } else {
                TransitionManager.beginDelayedTransition(holder.binding.root, AutoTransition())
                holder.binding.username.visibility = GONE
                holder.binding.usernameTitle.visibility = GONE
                holder.binding.stockTitle.visibility = GONE
                holder.binding.supplierTitle.visibility = GONE
                holder.binding.txtItemTotalPurchasesTitle.visibility = GONE
                holder.binding.txtItemTotalPurchases.visibility = GONE
                holder.binding.txtItemTotalRestock.visibility = GONE
                holder.binding.tvSupplier.visibility = GONE
                holder.binding.divider.visibility = GONE
                holder.binding.imageSwitch.setImageResource(R.drawable.ic_baseline_keyboard_double_arrow_down_24)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Restock>() {
        override fun areItemsTheSame(oldItem: Restock, newItem: Restock) =
            oldItem.idRestock == newItem.idRestock

        override fun areContentsTheSame(oldItem: Restock, newItem: Restock) =
            oldItem == newItem
    }
}