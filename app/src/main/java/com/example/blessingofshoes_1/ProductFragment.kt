package com.example.blessingofshoes_1

import android.app.Application
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.blessingofshoes_1.authentication.RegisterActivity
import com.example.blessingofshoes_1.databinding.FragmentProductBinding
import com.example.blessingofshoes_1.db.AppDb
import com.example.blessingofshoes_1.db.Product
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProductFragment : Fragment() {

    private var binding: FragmentProductBinding? = null
    private lateinit var productAdapter: ProductAdapter
    private val viewModel by viewModels<AppViewModel>()
    lateinit var productList: ArrayList<Product>
    lateinit var productItem: Product
    private lateinit var ProductListItem: RecyclerView
    private val appDatabase by lazy { AppDb.getDatabase(requireContext()).dbDao() }
    private lateinit var recyclerViewProduct: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_product, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        viewModel.getAllProduct().observe(viewLifecycleOwner) { itemList ->
            if (itemList != null) {
                productAdapter.setProductData(itemList)
            }
        }
        productList = ArrayList()
       // val btnAdd: FloatingActionButton = requireView().findViewById(R.id.btn_add)
        //btnAdd.setOnClickListener{
        //    val intent = Intent(context, AddProductActivity::class.java)
        //    startActivity(intent)
        //}
        rvProduct()
        initAction()

    }

    private fun initAction() {
        val callback = Callback()
        val itemTouchHelper = ItemTouchHelper(callback)

        itemTouchHelper.attachToRecyclerView(recyclerViewProduct)
    }

    private fun rvProduct() {
        recyclerViewProduct = requireView().findViewById(R.id.rv_product)
        productAdapter = ProductAdapter(context, productList)
        recyclerViewProduct.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = productAdapter
        }
        /*productAdapter.setOnItemClickCallback(object : ProductAdapter.OnItemClickCallback {
            override fun onItemClicked(data: Product) {
                showSelectedItem(data)
                //val intentToDetail = Intent(context, EditProductActivity::class.java)
                //intentToDetail.putExtra("DATA", data)
                //intentToDetail.putExtra("DATA_NAME", data.nameProduct)
//                intentToDetail.putExtra("DATA_PRICE", data.priceProduct)
//                intentToDetail.putExtra("DATA_STOCK", data.stockProduct)
                //intentToDetail.putExtra("DATA_PHOTO", data.productPhoto)
                //startActivity(intentToDetail)
            }
        })*/

    }

    fun finishDelete () {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
    private fun showSelectedItem(item: Product) {
        //Toast.makeText(context, "Kamu memilih " + item.nameProduct, Toast.LENGTH_SHORT).show()
    }

    inner class Callback : ItemTouchHelper.Callback() {

        override fun getMovementFlags(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder
        ): Int {
            return makeMovementFlags(0, ItemTouchHelper.RIGHT)
        }

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val data = productList.toMutableList().get(direction)

            Toast.makeText(context, "Nama Produk : " + data.nameProduct, Toast.LENGTH_LONG).show()
            viewModel.delete(Product(data.idProduct, data.nameProduct, data.priceProduct, data.stockProduct, data.productPhoto))
        }
    }
}