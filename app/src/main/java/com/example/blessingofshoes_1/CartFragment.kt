package com.example.blessingofshoes_1

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.drawToBitmap
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.blessingofshoes_1.databinding.FragmentCartBinding
import com.example.blessingofshoes_1.databinding.FragmentProductBinding
import com.example.blessingofshoes_1.db.AppDb
import com.example.blessingofshoes_1.db.Cart
import com.example.blessingofshoes_1.db.Product
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class CartFragment : Fragment() {

    private var binding: FragmentCartBinding? = null
    private lateinit var cartAdapter: CartAdapter
    private val viewModel by viewModels<AppViewModel>()
    lateinit var productList: ArrayList<Product>
    private val appDatabase by lazy { AppDb.getDatabase(context!!).dbDao() }
    private lateinit var recyclerViewProduct: RecyclerView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_cart, container, false)
        val localeID =  Locale("in", "ID")
        val numberFormat = NumberFormat.getCurrencyInstance(localeID)
        var tvTotalPayment = view.findViewById<CollapsingToolbarLayout>(R.id.collapsing_toolbar)
        //var readTotalPayment : String? = appDatabase.sumTotalPayment()!! as? String
/*        var cartTest = appDatabase.testCart("onProgress").toString()
        Log.d("TEST_NULL", cartTest)
        if (cartTest == "onProgress") {
            tvTotalPayment.title = (numberFormat.format(appDatabase.sumTotalPayment()!!.toDouble()).toString())
        } else {
            tvTotalPayment.title = "Rp.00"
        }*/
      /*  when {
            readTotalPayment == null -> binding?.txtTotalBayar!!.text = null
            readTotalPayment != null -> binding?.txtTotalBayar!!.setText(readTotalPayment.toString())
        }*/
        /*if ((viewModel.sumTotalPayment()!!.toString().toInt()) == null) {
            tvTotalPayment.text = "0"
        } else {
            tvTotalPayment.text = (numberFormat.format(viewModel.sumTotalPayment()!!.toDouble()).toString())
        }*/
        /*when {
            cartTest.isNullOrEmpty()-> {
                tvTotalPayment.text = "0"
            }
            else ->{
                tvTotalPayment.text = (numberFormat.format(viewModel.sumTotalPayment()!!.toDouble()).toString())
            }
        }*/

        var btnBayar = view.findViewById<Button>(R.id.btnBayar)
        btnBayar.setOnClickListener{

            var cartTest = appDatabase.testCart("onProgress").toString()
            Log.d("TEST_NULL", cartTest)
            if (cartTest == "onProgress") {
                val intent = Intent(requireContext(), PaymentActivity::class.java)
                startActivity(intent)
            } else {
                SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("CART IS EMPTY")
                    .setContentText("PLEASE INSERT AN ITEM FIRST")
                    .setConfirmText("OK")
                    .show()
            }

        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getAllProduct().observe(viewLifecycleOwner) { itemList ->
            if (itemList != null) {
                cartAdapter.setProductData(itemList)
            }
        }

        productList = ArrayList()
        rvProduct()
        view.findViewById<TextView>(R.id.btn_add_to_cart)

    }

    private fun rvProduct() {
        recyclerViewProduct = requireView().findViewById(R.id.rv_product_cart)
        cartAdapter = CartAdapter(context, productList)
        recyclerViewProduct.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            /*cartAdapter.setItemListener(object : RecyclerClickListener {

                // Tap the 'X' to delete the note.
                override fun onItemRemoveClick(position: Int) {
                }

                // Tap the note to edit.
                override fun onItemClick(position: Int) {
                    val pList = productList
                    val pId = pList[position].idProduct
                    val pName = pList[position].nameProduct
                    val pTotal = pList[position].stockProduct
                    val pProfit = pList[position].profitProduct
                    val pPayment = pList[position].profitProduct
                    val addCart = Cart(0, pId, pName, pTotal, pProfit, pPayment)
                    lifecycleScope.launch {
                        appDatabase.insertCart(addCart)
                    }
                    SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("Data is Correct?")
                        .setContentText(pId.toString() + ", " + pName + ", " + pTotal + ", " + pProfit + ", " +pPayment)
                        .setConfirmText("Add to Cart")
                        .setCancelText("Cancel")
                        .setConfirmClickListener { sDialog ->
                            lifecycleScope.launch {
                                appDatabase.insertCart(addCart)
                            }
                            sDialog.dismissWithAnimation()
                        }.show()
                }
            })*/
            adapter = cartAdapter
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}