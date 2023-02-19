package com.example.blessingofshoes_1.adapter

import android.content.Context
import android.content.Intent
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.bumptech.glide.Glide
import com.example.blessingofshoes_1.EditProductActivity
import com.example.blessingofshoes_1.GenerateAccountingActivity
import com.example.blessingofshoes_1.R
import com.example.blessingofshoes_1.RestockActivity
import com.example.blessingofshoes_1.databinding.ItemFinancialAccountingBinding
import com.example.blessingofshoes_1.databinding.ItemProductSecondBinding
import com.example.blessingofshoes_1.db.Accounting
import com.example.blessingofshoes_1.db.Product
import com.example.blessingofshoes_1.viemodel.AppViewModel
import java.text.NumberFormat
import java.util.*

class AccountingAdapter (private val context: Context?, private var accountingItem: List<Accounting>) : RecyclerView.Adapter<AccountingAdapter.AccountingViewHolder>() {

    inner class AccountingViewHolder (val binding: ItemFinancialAccountingBinding): RecyclerView.ViewHolder(binding.root)
    private lateinit var onItemClickCallback: OnItemClickCallback
    private lateinit var viewModel: AppViewModel


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountingViewHolder {
        val binding = ItemFinancialAccountingBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return AccountingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AccountingViewHolder, position: Int) {
        val listAccounting = accountingItem[position]

        /*var productItemDeleted: Product
        productItemDeleted.idProduct = listProduct.idProduct
        productItemDeleted.nameProduct = listProduct.nameProduct
        productItemDeleted.priceProduct = listProduct.priceProduct
        productItemDeleted.stockProduct = listProduct.stockProduct*/

        //holder.binding.tvProductId.text = listProduct.idProduct.toString()
        val localeID =  Locale("in", "ID")
        val numberFormat = NumberFormat.getCurrencyInstance(localeID)
        holder.binding.tvIdFinancialAccounting.text = "Accounting ID : " + listAccounting.idAccounting.toString()
        holder.binding.AccountBalanceValue.text = numberFormat.format(listAccounting.initDigital!!.toDouble()).toString()
        holder.binding.CashBalanceValue.text = numberFormat.format(listAccounting.initCash!!.toDouble()).toString()
        holder.binding.TotalStockValue.text = listAccounting.initCash!!.toString()
        holder.binding.StockWorthValue.text = numberFormat.format(listAccounting.initStockWorth!!.toDouble()).toString()
        holder.binding.username.text = listAccounting.username!!
        holder.binding.status.text = listAccounting.status!!
        holder.binding.imageSwitch.setOnClickListener {
            if (holder.binding.accountingReport.visibility == View.GONE) {
                TransitionManager.beginDelayedTransition(holder.binding.root, AutoTransition())
                holder.binding.accountingReport.visibility = View.VISIBLE
                holder.binding.imageSwitch.setImageResource(R.drawable.ic_baseline_keyboard_arrow_up_24)
            } else {
                TransitionManager.beginDelayedTransition(holder.binding.root, AutoTransition())
                holder.binding.accountingReport.visibility = View.GONE
                holder.binding.imageSwitch.setImageResource(R.drawable.ic_baseline_keyboard_double_arrow_down_24)
            }
        }
        holder.binding.capitalInvestmentValue.text = numberFormat.format(listAccounting.capitalInvest!!.toDouble()).toString()
        holder.binding.transactionTotal.text = numberFormat.format(listAccounting.incomeTransaction!!.toDouble()).toString()
        holder.binding.transactionItemTotal.text = listAccounting.transactionItem!!.toString() + " Product"
        holder.binding.restockTotal.text = numberFormat.format(listAccounting.restockPurchases!!.toDouble()).toString()
        holder.binding.restockItemTotal.text = listAccounting.restockItem!!.toString() + " Product"
        holder.binding.returnTotal.text = numberFormat.format(listAccounting.returnTotal!!.toDouble()).toString()
        holder.binding.returnItemTotal.text = listAccounting.returnItem!!.toString() + " Product"
        holder.binding.balanceInValue.text = numberFormat.format(listAccounting.balanceIn!!.toDouble()).toString()
        holder.binding.balanceOutValue.text = numberFormat.format(listAccounting.balanceOut!!.toDouble()).toString()
        holder.binding.profitValue.text = numberFormat.format(listAccounting.profitEarned!!.toDouble()).toString()

        holder.binding.AccountBalanceValueFinal.text = numberFormat.format(listAccounting.finalDigital!!.toDouble()).toString()
        holder.binding.CashBalanceValueFinal.text = numberFormat.format(listAccounting.finalCash!!.toDouble()).toString()
        holder.binding.TotalStockValueFinal.text = listAccounting.finalStock.toString()  + " Product"
        holder.binding.StockWorthValueFinal.text = numberFormat.format(listAccounting.finalWorth!!.toDouble()).toString()
            //listener?.onAddClick(listAccounting, position)

        /*holder.binding.btnInfo.setOnClickListener{
            SweetAlertDialog(context, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                .setTitleText("Information "+ listProduct.nameProduct.toString())
                .setContentText("Stock available : "+listProduct.stockProduct.toString()+" and "+numberFormat.format(listProduct.totalPurchases!!.toDouble()).toString()+ " worth.")
                .setCustomImage(R.drawable.ic_baseline_info_24)
                .setConfirmText("Close")
                .setConfirmClickListener { sDialog ->
                    sDialog.dismissWithAnimation()
                }
                .show()
        }*/
    }

    var listener: AccountingClickListener? = null

    override fun getItemCount(): Int = accountingItem.size

    fun setProductData(postList: List<Accounting>)
    {

        this.accountingItem = postList
        notifyDataSetChanged()

    }
    interface OnItemClickCallback {
        fun onItemClicked(data: Accounting)
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    interface AccountingClickListener {
        //    fun onItemRemoveClick(position: Int)
        fun onAddClick(accounting: Accounting, position: Int)
    }
}