package com.example.blessingofshoes_1

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.blessingofshoes_1.adapter.ReportAdapter
import com.example.blessingofshoes_1.databinding.FragmentReportBinding
import com.example.blessingofshoes_1.db.Product
import com.example.blessingofshoes_1.db.Transaction
import com.example.blessingofshoes_1.viemodel.AppViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class ReportFragment : Fragment() {

    private var binding: FragmentReportBinding? = null
    private lateinit var reportAdapter: ReportAdapter
    private val viewModel by viewModels<AppViewModel>()
    lateinit var transactionList: ArrayList<Transaction>
    lateinit var transactionListData: List<Transaction>
    private lateinit var rvTransaction: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_report, container, false)

        val localeID =  Locale("in", "ID")
        val numberFormat = NumberFormat.getCurrencyInstance(localeID)
        var profitValue = view.findViewById<TextView>(R.id.profitValue)
        var moneyStored = view.findViewById<TextView>(R.id.money_stored)
        var validateTransaction = viewModel.checkTransaction()
        when {
            validateTransaction == 0 -> {
                profitValue.text = "Rp.000.00"
                moneyStored.text = "Rp.000.00"
            }
            else ->{
                profitValue.text = (numberFormat.format(viewModel.sumTotalCompleteProfit()!!.toDouble()).toString())
                moneyStored.text = (numberFormat.format(viewModel.sumTotalTransaction()!!.toDouble()).toString())
            }
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getAllTransaction().observe(viewLifecycleOwner) { itemList ->
            if (itemList != null) {
                transactionListData = itemList
                reportAdapter.setTransactionData(itemList)
            }
        }
        transactionList = ArrayList()
        rvTransaction()
        initAction()

    }

    private fun initAction() {
        val callback = Callback()
        val itemTouchHelper = ItemTouchHelper(callback)

        itemTouchHelper.attachToRecyclerView(rvTransaction)
    }

    private fun rvTransaction() {
        rvTransaction = requireView().findViewById(R.id.rv_transaction)
        reportAdapter = ReportAdapter(context, transactionList)
        rvTransaction.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = reportAdapter
        }
        reportAdapter.setOnItemClickCallback(object : ReportAdapter.OnItemClickCallback {
            override fun onItemClicked(data: Product) {
                showSelectedItem(data)
                /*val intentToDetail = Intent(context, EditProductActivity::class.java)
                intentToDetail.putExtra("DATA", data)
                intentToDetail.putExtra("DATA_ID", data.idProduct)
                intentToDetail.putExtra("DATA_NAME", data.nameProduct)
//                intentToDetail.putExtra("DATA_PRICE", data.priceProduct)
//                intentToDetail.putExtra("DATA_STOCK", data.stockProduct)
                //intentToDetail.putExtra("DATA_PHOTO", data.productPhoto)
                startActivity(intentToDetail)*/
            }
        })

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
            val data = transactionListData[position]
            val data2 = transactionListData[position]
            //Toast.makeText(context, "Berhasil Menghapus : " + data.nameProduct, Toast.LENGTH_LONG).show()
            Snackbar.make(requireView(), "Deleted " + data.idTransaction, Snackbar.LENGTH_LONG)
                .setAction(
                    "Undo",
                    View.OnClickListener {
                        // adding on click listener to our action of snack bar.
                        // below line is to add our item to array list with a position.

                        // below line is to notify item is
                        // added to our adapter class.
                        reportAdapter.notifyItemInserted(position)

                    }).show()



            SweetAlertDialog(context, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                .setTitleText("Delete this "+ data.transactionDate.toString() + " Transaction?")
                .setContentText("You cannot undo this event!")
                .setCustomImage(R.drawable.logo_round)
                .setConfirmText("Ok")
                .setConfirmClickListener { sDialog ->
                    viewModel.deleteTransaction(data.idTransaction)
                    val localeID =  Locale("in", "ID")
                    val numberFormat = NumberFormat.getCurrencyInstance(localeID)
                    var profitValue = view!!.findViewById<TextView>(R.id.profitValue)
                    var moneyStored = view!!.findViewById<TextView>(R.id.money_stored)
                    var validateTransaction = viewModel.checkTransaction()
                    when {
                        validateTransaction == 0 -> {
                            profitValue.text = "Rp.000.00"
                            moneyStored.text = "Rp.000.00"
                        }
                        else ->{
                            profitValue.text = (numberFormat.format(viewModel.sumTotalCompleteProfit()!!.toDouble()).toString())
                            moneyStored.text = (numberFormat.format(viewModel.sumTotalTransaction()!!.toDouble()).toString())
                        }
                    }
                    sDialog.dismissWithAnimation()
                }
                .setCancelText("Cancel")
                .setCancelClickListener { pDialog ->
                    viewModel.insertTransaction(data2)
                    viewModel.getAllTransaction().observe(viewLifecycleOwner) { itemList ->
                        if (itemList != null) {
                            transactionListData = itemList
                            reportAdapter.setTransactionData(itemList)
                        }
                    }
                    pDialog.dismissWithAnimation()
                }
                .show()
        }



    }
}