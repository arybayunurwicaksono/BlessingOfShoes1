package com.example.blessingofshoes_1.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.blessingofshoes_1.RecyclerClickListener
import com.example.blessingofshoes_1.databinding.ItemCartBinding
import com.example.blessingofshoes_1.db.Cart
import java.text.NumberFormat
import java.util.*

class PaymentAdapter : ListAdapter<Cart, PaymentAdapter.CartHolder>(DiffCallback()) {

    inner class CartHolder (val binding: ItemCartBinding): RecyclerView.ViewHolder(binding.root)

    private lateinit var listener: RecyclerClickListener
    fun setItemListener(listener: RecyclerClickListener) {
        this.listener = listener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartHolder {
        val binding = ItemCartBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return CartHolder(binding)
    }

    override fun onBindViewHolder(holder: CartHolder, position: Int) {
        val currentItem = getItem(position)
        val localeID =  Locale("in", "ID")
        val numberFormat = NumberFormat.getCurrencyInstance(localeID)
        holder.binding.tvTotalPayment.text = numberFormat.format(currentItem.totalpayment!!.toDouble()).toString()
        holder.binding.tvItemName.text = currentItem!!.nameItem
        holder.binding.tvItemTotal.text = "(" + currentItem!!.totalItem.toString() + " x"
        holder.binding.tvProductPrice.text = numberFormat.format(currentItem.priceItem!!.toDouble()).toString() + ")"
        Glide.with(holder.itemView.context)
            .load(currentItem!!.productPhoto)
            .fitCenter()
            .into(holder.binding.imageView)
    }

    class DiffCallback : DiffUtil.ItemCallback<Cart>() {
        override fun areItemsTheSame(oldItem: Cart, newItem: Cart) =
            oldItem.idItem == newItem.idItem

        override fun areContentsTheSame(oldItem: Cart, newItem: Cart) =
            oldItem == newItem
    }
}