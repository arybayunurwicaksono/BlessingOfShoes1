package com.example.blessingofshoes_1

import android.app.Activity
import android.app.Fragment
import android.content.Context
import android.content.Intent
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.core.util.Pair
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.bumptech.glide.Glide
import com.example.blessingofshoes_1.databinding.ItemProductBinding
import com.example.blessingofshoes_1.databinding.ItemProductSecondBinding
import com.example.blessingofshoes_1.db.Product
import com.google.android.material.snackbar.Snackbar
import java.text.NumberFormat
import java.util.*

class ProductAdapter (private val context: Context?, private var productItem: List<Product>) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    inner class ProductViewHolder (val binding: ItemProductSecondBinding): RecyclerView.ViewHolder(binding.root)
    private lateinit var onItemClickCallback: OnItemClickCallback
    private lateinit var viewModel: AppViewModel


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductSecondBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val listProduct = productItem[position]

        /*var productItemDeleted: Product
        productItemDeleted.idProduct = listProduct.idProduct
        productItemDeleted.nameProduct = listProduct.nameProduct
        productItemDeleted.priceProduct = listProduct.priceProduct
        productItemDeleted.stockProduct = listProduct.stockProduct*/

        //holder.binding.tvProductId.text = listProduct.idProduct.toString()
        holder.binding.tvProfitTitle.text = "+"
        holder.binding.tvStockTitle.text = "Stock: "
        val localeID =  Locale("in", "ID")
        val numberFormat = NumberFormat.getCurrencyInstance(localeID)
        holder.binding.tvProductProfit.text = numberFormat.format(listProduct.profitProduct!!.toDouble()).toString()
        holder.binding.tvProductPrice.text = numberFormat.format(listProduct.priceProduct!!.toDouble()).toString()
        holder.binding.tvProductName.text = listProduct!!.nameProduct
        holder.binding.tvProductBrand.text = listProduct!!.brandProduct
        holder.binding.tvProductSize.text = listProduct!!.sizeProduct
        holder.binding.tvProductStock.text = listProduct!!.stockProduct.toString()
        holder.binding.tvTimeAdded.text = listProduct!!.timeAdded
        Glide.with(holder.itemView.context)
            .load(listProduct!!.productPhoto)
            .fitCenter()
            .into(holder.binding.imageView)
        holder.binding.btnInfo.setOnClickListener{
            SweetAlertDialog(context, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                .setTitleText("Information "+ listProduct.nameProduct.toString())
                .setContentText("Stock available : "+listProduct.stockProduct.toString()+" and "+numberFormat.format(listProduct.totalPurchases!!.toDouble()).toString()+ " worth.")
                .setCustomImage(R.drawable.ic_baseline_info_24)
                .setConfirmText("Close")
                .setConfirmClickListener { sDialog ->
                    sDialog.dismissWithAnimation()
                }
                .show()
        }
        holder.binding.imageSwitch.setOnClickListener {
            if (holder.binding.imageView.visibility == View.GONE) {
                TransitionManager.beginDelayedTransition(holder.binding.root, AutoTransition())
                holder.binding.cardViewStock.visibility = View.VISIBLE
                holder.binding.btnEdit.visibility = View.VISIBLE
                holder.binding.imageView.visibility = View.VISIBLE
                holder.binding.imageSwitch.setImageResource(R.drawable.ic_baseline_keyboard_arrow_up_24)
            } else {
                TransitionManager.beginDelayedTransition(holder.binding.root, AutoTransition())
                holder.binding.cardViewStock.visibility = View.GONE
                holder.binding.btnEdit.visibility = View.GONE
                holder.binding.imageView.visibility = View.GONE
                holder.binding.imageSwitch.setImageResource(R.drawable.ic_baseline_keyboard_double_arrow_down_24)
            }
        }
        /*holder.binding.btnEdit.setOnClickListener {
            val intentToDetail = Intent(context, EditProductActivity::class.java)
            //intentToDetail.putExtra("DATA", data)
            intentToDetail.putExtra("DATA_NAME", listProduct!!.nameProduct)
            intentToDetail.putExtra("DATA_ID", listProduct!!.idProduct)
            //val intent = Intent(holder.itemView.context, EditProductActivity::class.java)
            //intent.putExtra(EditProductActivity.EXTRA_ID, listProduct.idProduct)
            //intent.putExtra(EditProductActivity.EXTRA_NAME, listProduct!!.nameProduct)
            //intent.putExtra(EditProductActivity.EXTRA_PRICE, listProduct!!.priceProduct)
            //intent.putExtra(EditProductActivity.EXTRA_STOCK, listProduct!!.stockProduct)

            //intent.putExtra(EditProductActivity.EXTRA_PHOTO, listProduct.productPhoto)
            holder.itemView.context.startActivity(intentToDetail)

        }
        holder.binding.btnDelete.setOnClickListener(){ v->
            //viewModel.deleteProduct()
            //Toast.makeText(context, "Kamu menghapus " + listProduct!!.nameProduct, Toast.LENGTH_SHORT).show()
            val productDeleted = listProduct!!.nameProduct
            viewModel.deleteProduct(listProduct!!.idProduct)
            *//*viewModel.deleteProductItem(context, Product(listProduct!!.idProduct,
                listProduct!!.nameProduct, listProduct!!.priceProduct, listProduct!!.stockProduct,
                listProduct!!.productPhoto)) {*//*
                Snackbar.make(v, "Deleted " + productDeleted, Snackbar.LENGTH_LONG)
                    .setAction(
                        "Next",
                        View.OnClickListener {
                            // adding on click listener to our action of snack bar.
                            // below line is to add our item to array list with a position.
                            *//*viewModel.insertProduct(deletedItem)

                            // below line is to notify item is
                            // added to our adapter class.
                            productAdapter.notifyItemInserted(position)*//*

                            val intentToRefresh = Intent(context, MainActivity::class.java)
                            context?.startActivity(intentToRefresh)
                        }).show()
            //}

        }*/
        holder.binding.btnEdit.setOnClickListener {

            SweetAlertDialog(context, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                .setTitleText(listProduct!!.nameProduct.toString())
                .setContentText("Please select edit option!")
                .setConfirmText("Detail")
                .setCustomImage(R.drawable.logo_round)
                .setCancelText("Restock")
                .setCancelButtonBackgroundColor(R.color.light_green)
                .setConfirmClickListener { sDialog ->
                    val intent = Intent(holder.itemView.context, EditProductActivity::class.java)
                    intent.putExtra("DATA_ID", listProduct.idProduct)
                    intent.putExtra("DATA_NAME", listProduct!!.nameProduct)
                    holder.itemView.context.startActivity(intent)
                }
                .setCancelClickListener { pDialog ->
                    val intent = Intent(holder.itemView.context, RestockActivity::class.java)
                    intent.putExtra("DATA_ID", listProduct.idProduct)
                    intent.putExtra("DATA_NAME", listProduct!!.nameProduct)
                    holder.itemView.context.startActivity(intent)
                }
                .show()
        }
        /*holder.binding.itemProduct.setOnClickListener{
            Toast.makeText(context, "Kamu memilih " + listProduct!!.idProduct, Toast.LENGTH_SHORT).show()
        }*/

    }


    override fun getItemCount(): Int = productItem.size



    fun setProductData(postList: List<Product>)
    {

        this.productItem = postList
        notifyDataSetChanged()

    }
    interface OnItemClickCallback {
        fun onItemClicked(data: Product)
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

}