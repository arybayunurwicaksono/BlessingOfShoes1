package com.example.blessingofshoes_1

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Button
import androidx.core.view.drawToBitmap
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.blessingofshoes_1.adapter.AccountingAdapter
import com.example.blessingofshoes_1.adapter.ProductAdapter
import com.example.blessingofshoes_1.databinding.FragmentAboutBinding
import com.example.blessingofshoes_1.databinding.FragmentFinancialAccountingBinding
import com.example.blessingofshoes_1.databinding.FragmentProductBinding
import com.example.blessingofshoes_1.db.*
import com.example.blessingofshoes_1.utils.Constant
import com.example.blessingofshoes_1.utils.Preferences
import com.example.blessingofshoes_1.viemodel.AppViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class FinancialAccountingFragment : Fragment(), AccountingAdapter.AccountingClickListener {

    private var binding: FragmentFinancialAccountingBinding? = null
    private lateinit var accountingAdapter: AccountingAdapter
    private val viewModel by viewModels<AppViewModel>()
    lateinit var accountingList: ArrayList<Accounting>
    lateinit var accountingListData: List<Accounting>
    lateinit var sharedPref: Preferences
    private lateinit var accountingListItem: RecyclerView
    private val appDatabase by lazy { AppDb.getDatabase(context!!).dbDao() }
    private lateinit var rvAccounting: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_financial_accounting, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeNotes()
        sharedPref = Preferences(requireContext())

        accountingList = ArrayList()
        // val btnAdd: FloatingActionButton = requireView().findViewById(R.id.btn_add)
        //btnAdd.setOnClickListener{
        //    val intent = Intent(context, AddProductActivity::class.java)
        //    startActivity(intent)
        //}
        val sdf = SimpleDateFormat("MMMM/yyyy")
        val currentDate = sdf.format(Date())
        var btnGenerate : Button = requireView().findViewById(R.id.btn_generate)
        btnGenerate.setOnClickListener {
            SweetAlertDialog(context, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                .setTitleText("Generating Data")
                .setContentText("Do you wan't to generate financial accounting for "+currentDate+"?")
                .setConfirmText("Yes")
                .setCustomImage(R.drawable.logo_round)
                .setCancelText("No")
                .setConfirmClickListener { sDialog ->
                    val intent = Intent(context, GenerateAccountingActivity::class.java)
                    startActivity(intent)
                    sDialog.dismissWithAnimation()
                }
                .show()
        }
        binding?.apply {

        }
        rvAccounting()
        initAction()

    }
    override fun onAddClick(accounting: Accounting, position: Int) {
        val sdf = SimpleDateFormat("M/yyyy")
        val currentDate = sdf.format(Date())
        var eId = accounting.idAccounting
        var eDate = accounting.dateAccounting
        var eInitDigital = accounting.initDigital
        var eInitCash = accounting.initCash
        var eInitStock = accounting.initStock
        var eInitWorth = accounting.initStockWorth
        var eCapital = accounting.capitalInvest
        var eTransaction : Int?
        var eItemTransaction : Int?
        sharedPref = Preferences(requireContext())
        val username = viewModel.readUsername(sharedPref.getString(Constant.PREF_EMAIL))
        var validateTransaction = appDatabase.validateTransaction(currentDate)
        if (validateTransaction==0){
            eTransaction = 0
            eItemTransaction = 0
        } else {
            eTransaction = appDatabase.sumTotalTransactionAcc(currentDate)!!
            eItemTransaction = appDatabase.readTotalTransactionItem(currentDate)!!
        }

        var eRestockPurchases : Int?
        var eRestockItem : Int?
        var validateRestock = appDatabase.validateRestock(currentDate)
        if (validateRestock==0){
            eRestockPurchases = 0
            eRestockItem = 0
        } else {
            eRestockPurchases = appDatabase.sumTotalPurchases(currentDate)
            eRestockItem = appDatabase.sumTotalStockAdded(currentDate)
        }
/*        var eRestockPurchases = appDatabase.sumTotalPurchases(currentDate)
        var eRestockItem = appDatabase.sumTotalStockAdded(currentDate)*/
        var eReturn : Int?
        var eReturnItem : Int?
        var validateReturn = appDatabase.validateReturn(currentDate)
        if (validateReturn==0){
            eReturn = 0
            eReturnItem = 0
        } else {
            eReturn = appDatabase.sumTotalRefund(currentDate)
            eReturnItem = appDatabase.sumTotalRefundItem(currentDate)
        }
        var eStatus = "complete"
        var eFinalDigital = appDatabase.readDigitalBalance()
        var eFinalCash = appDatabase.readCashBalance()
        var eStock = appDatabase.readTotalStock()
        var eWorth = appDatabase.readTotalStockWorth()
        var eOtherNeeds = 0
        var balanceIn = eFinalCash + eFinalDigital
        var balanceOut = eReturn!! + eOtherNeeds
        var eProfit = appDatabase.sumTotalProfitAcc(currentDate)
        var profitFix = balanceOut - eProfit!!

        SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
            .setTitleText("Update Monthly Accounting?")
            .setConfirmText("Yes")
            .setCancelText("No")
            .setConfirmClickListener { sDialog->
                val fsdf = SimpleDateFormat("MMMM/yyyy")
                val fFurrentDate = fsdf.format(Date())

                lifecycleScope.launch {
                    //viewModel.updateProduct(idProduct, productName, productPrice,productStock, productPhoto)
                    viewModel.updateMonthlyAccounting(
                        requireContext(), eId!!, fFurrentDate, eInitDigital, eInitCash, eInitStock, eInitWorth, eCapital, eTransaction, eItemTransaction, eRestockPurchases, eRestockItem,
                        eReturn, eReturnItem, eFinalDigital, eFinalCash, eStock, eWorth, 0, username, eStatus, balanceIn, balanceOut, profitFix) {
                        observeNotes()
                    }
                }
                sDialog.dismissWithAnimation()
            }
            .show()






    /*Log.d(TAG, "onItemClick: $product")

        var profitItem = product.profitProduct!!.toInt() * qty
        var totalPayment = (qty * product.priceProduct!!)
        sharedPref = Preferences(requireContext())
        val username = viewModel.readUsername(sharedPref.getString(Constant.PREF_EMAIL))
        var extraRealPrice = (product.realPriceProduct)?.toInt()
        var totalPurchasesFinal = qty * extraRealPrice!!

        viewModel.insertCart(
            Cart(
            0,
            product.idProduct,
            product.nameProduct,
            product.priceProduct,
            qty,
            product.profitProduct,
            profitItem,
            totalPayment,
            username,
            "onProgress",
            0,
            product.productPhoto
        )
        )

        viewModel.sumTotalPurchasesItem(product.idProduct, totalPurchasesFinal)
        viewModel.sumStockItem( product.idProduct, qty)
        SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
            .setTitleText("Item Added")
            .setConfirmText("Ok")
            .show()*/
    }
    private fun initAction() {
        val callback = Callback()
        val itemTouchHelper = ItemTouchHelper(callback)

        itemTouchHelper.attachToRecyclerView(rvAccounting)
    }

    private fun observeNotes() {
        lifecycleScope.launch {
            viewModel.getAllAccounting().observe(viewLifecycleOwner) { itemList ->
                if (itemList != null) {
                    accountingListData = itemList
                    accountingAdapter.setProductData(itemList)
                }
            }
        }
    }

    private fun rvAccounting() {
        rvAccounting = requireView().findViewById(R.id.rv_accounting)
        accountingAdapter = AccountingAdapter(context, accountingList)
        rvAccounting.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = accountingAdapter
        }
        /*accountingAdapter.setOnItemClickCallback(object : ProductAdapter.OnItemClickCallback {
            override fun onItemClicked(data: Accounting) {
                showSelectedItem(data)
*//*                val intentToDetail = Intent(context, EditProductActivity::class.java)
                intentToDetail.putExtra("DATA", data)
                intentToDetail.putExtra("DATA_ID", data.idAccounting)
//                intentToDetail.putExtra("DATA_PRICE", data.priceProduct)
//                intentToDetail.putExtra("DATA_STOCK", data.stockProduct)
                //intentToDetail.putExtra("DATA_PHOTO", data.productPhoto)
                startActivity(intentToDetail)*//*
            }
        })*/

    }

    fun finishDelete () {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
    private fun showSelectedItem(item: Accounting) {
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
            val data = accountingListData[position]
            val data2 = accountingListData[position]
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
                .setTitleText("Delete this "+ data.idAccounting.toString() + "?")
                .setContentText("You cannot undo this event!")
                .setCustomImage(R.drawable.logo_round)
                .setConfirmText("Ok")
                .setConfirmClickListener { sDialog ->
                    viewModel.deleteAccounting(data.idAccounting)
                    sDialog.dismissWithAnimation()
                }
                .setCancelText("Cancel")
                .setCancelClickListener { pDialog ->
                    viewModel.insertAccounting(data2)
                    observeNotes()
                    pDialog.dismissWithAnimation()
                }
                .show()
        }



    }
}