package com.example.blessingofshoes_1

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.blessingofshoes_1.databinding.ItemDetailReportBinding
import com.example.blessingofshoes_1.databinding.ItemReturnBinding
import com.example.blessingofshoes_1.db.Cart
import java.text.NumberFormat
import java.util.*

class ReturnAdapter : ListAdapter<Cart, ReturnAdapter.TransactionHolder>(DiffCallback()) {

    inner class TransactionHolder (val binding: ItemReturnBinding): RecyclerView.ViewHolder(binding.root)

    private lateinit var listener: RecyclerClickListener
    fun setItemListener(listener: RecyclerClickListener) {
        this.listener = listener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionHolder {
        val binding = ItemReturnBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return TransactionHolder(binding)
    }

    override fun onBindViewHolder(holder: TransactionHolder, position: Int) {
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
        holder.binding.txtQty.text = currentItem!!.totalItem.toString()
        holder.binding.btnPlus.setOnClickListener {
            val old_value = holder.binding.txtQty.text.toString().toInt()
            val new_value = old_value+1

            // Tidak bisa tambah lebih dari stok yg ada
            if(new_value > currentItem!!.totalItem!!.toInt()){
                Toast.makeText(holder.itemView.context, "Cannot return item more than QTY transaction", Toast.LENGTH_LONG).show()
            }else{
                holder.binding.txtQty.setText(new_value.toString())
            }

        }

        holder.binding.btnMinus.setOnClickListener {
            val old_value = holder.binding.txtQty.text.toString().toInt()
            val new_value = old_value-1

            // Biarkan data terhapus apabila jumlah nol
            if (new_value>=0){
                holder.binding.txtQty.setText(new_value.toString())
            }
        }

    }

    class DiffCallback : DiffUtil.ItemCallback<Cart>() {
        override fun areItemsTheSame(oldItem: Cart, newItem: Cart) =
            oldItem.idItem == newItem.idItem

        override fun areContentsTheSame(oldItem: Cart, newItem: Cart) =
            oldItem == newItem
    }
}