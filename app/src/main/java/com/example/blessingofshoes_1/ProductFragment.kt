package com.example.blessingofshoes_1

import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
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
    lateinit var productListData: List<Product>
    private lateinit var ProductListItem: RecyclerView
    private val appDatabase by lazy { AppDb.getDatabase(requireContext()).dbDao() }
    private lateinit var recyclerViewProduct: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_product, container, false)

        var btnAdd = view.findViewById<FloatingActionButton>(R.id.btn_add)
        btnAdd.setOnClickListener{
            val intent = Intent(requireContext(), AddProductActivity::class.java)
            startActivity(intent)
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        observeNotes()
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

    private fun observeNotes() {
        lifecycleScope.launch {
            viewModel.getAllProduct().observe(viewLifecycleOwner) { itemList ->
                if (itemList != null) {
                    productListData = itemList
                    productAdapter.setProductData(itemList)
                }
            }
        }
    }

    private fun rvProduct() {
        recyclerViewProduct = requireView().findViewById(R.id.rv_product)
        productAdapter = ProductAdapter(context, productList)
        recyclerViewProduct.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = productAdapter
        }
        productAdapter.setOnItemClickCallback(object : ProductAdapter.OnItemClickCallback {
            override fun onItemClicked(data: Product) {
                showSelectedItem(data)
                val intentToDetail = Intent(context, EditProductActivity::class.java)
                intentToDetail.putExtra("DATA", data)
                intentToDetail.putExtra("DATA_ID", data.idProduct)
                intentToDetail.putExtra("DATA_NAME", data.nameProduct)
//                intentToDetail.putExtra("DATA_PRICE", data.priceProduct)
//                intentToDetail.putExtra("DATA_STOCK", data.stockProduct)
                //intentToDetail.putExtra("DATA_PHOTO", data.productPhoto)
                startActivity(intentToDetail)
            }
        })

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
            val position = viewHolder.adapterPosition
            val data = productListData[position]
            val data2 = productListData[position]
            //Toast.makeText(context, "Berhasil Menghapus : " + data.nameProduct, Toast.LENGTH_LONG).show()
            /*Snackbar.make(requireView(), "Deleted " + data.nameProduct, Snackbar.LENGTH_LONG)
                .setAction(
                    "Undo",
                    View.OnClickListener {
                        // adding on click listener to our action of snack bar.
                        // below line is to add our item to array list with a position.
                        viewModel.insertProduct(data2)
                        viewModel.getAllProduct().observe(viewLifecycleOwner) { itemList ->
                            if (itemList != null) {
                                productListData = itemList
                                productAdapter.setProductData(itemList)
                            }
                        }
                        // below line is to notify item is
                        // added to our adapter class.
                        productAdapter.notifyItemInserted(position)

                    }).show()*/


            SweetAlertDialog(context, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                .setTitleText("Delete this "+ data.nameProduct.toString() + "?")
                .setContentText("You cannot undo this event!")
                .setCustomImage(R.drawable.logo_round)
                .setConfirmText("Ok")
                .setConfirmClickListener { sDialog ->
                    viewModel.deleteProduct(data.idProduct)
                    sDialog.dismissWithAnimation()
                }
                .setCancelText("Cancel")
                .setCancelClickListener { pDialog ->
                    viewModel.insertProduct(data2)
                    observeNotes()
                    pDialog.dismissWithAnimation()
                }
                .show()
        }



    }
}