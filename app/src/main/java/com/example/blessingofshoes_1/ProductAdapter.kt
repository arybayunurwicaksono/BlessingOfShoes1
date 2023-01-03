package com.example.blessingofshoes_1

import android.app.Activity
import android.app.Fragment
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.blessingofshoes_1.databinding.ItemProductBinding
import com.example.blessingofshoes_1.db.Product

class ProductAdapter (private val context: Context?, private var productItem: List<Product>) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    inner class ProductViewHolder (val binding: ItemProductBinding): RecyclerView.ViewHolder(binding.root)
    private lateinit var onItemClickCallback: OnItemClickCallback
    private lateinit var viewModel: AppViewModel

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val listProduct = productItem[position]
        holder.binding.tvProductId.text = listProduct.idProduct.toString()
        holder.binding.tvProductName.text = listProduct!!.nameProduct
        holder.binding.tvProductPrice.text = listProduct!!.priceProduct
        holder.binding.tvProductStock.text = listProduct!!.stockProduct
        Glide.with(holder.itemView.context)
            .load(listProduct!!.productPhoto)
            .fitCenter()
            .into(holder.binding.imageView)
        holder.binding.btnEdit.setOnClickListener {
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
        holder.binding.btnDelete.setOnClickListener{
            viewModel.deleteProduct()
            Toast.makeText(context, "Kamu menghapus " + listProduct!!.nameProduct, Toast.LENGTH_SHORT).show()
        }
        holder.itemView.setOnClickListener { onItemClickCallback.onItemClicked(productItem[holder.adapterPosition]) }
        holder.binding.itemProduct.setOnClickListener{
            Toast.makeText(context, "Kamu memilih " + listProduct!!.idProduct, Toast.LENGTH_SHORT).show()
        }

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