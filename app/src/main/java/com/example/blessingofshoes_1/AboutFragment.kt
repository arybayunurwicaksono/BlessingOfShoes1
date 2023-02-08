package com.example.blessingofshoes_1

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.blessingofshoes_1.authentication.LoginActivity
import com.example.blessingofshoes_1.databinding.FragmentAboutBinding
import com.example.blessingofshoes_1.db.AppDb
import com.example.blessingofshoes_1.db.Balance
import com.example.blessingofshoes_1.db.BalanceReport
import com.example.blessingofshoes_1.db.Product
import dagger.hilt.android.AndroidEntryPoint
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class AboutFragment : Fragment() {

    private var binding: FragmentAboutBinding? = null
    lateinit var sharedPref: Preferences
    private lateinit var appViewModel: AppViewModel
    private lateinit var test: AppRepository
    private val viewModel by viewModels<AppViewModel>()
    lateinit var balanceData: LiveData<Balance>
    private val appDatabase by lazy { AppDb.getDatabase(context!!).dbDao() }

    private val TAG = "account"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAboutBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPref = Preferences(requireContext())

        val btnLogout: Button = requireView().findViewById(R.id.btn_sign_out)
        btnLogout.setOnClickListener {
            sharedPref.clear()
            val intent = Intent(context, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK or
                    Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }

        val localeID =  Locale("in", "ID")
        val numberFormat = NumberFormat.getCurrencyInstance(localeID)
        val btnReturn: Button = requireView().findViewById(R.id.btn_return)
        val btnRestock: LinearLayout = requireView().findViewById(R.id.btn_restock)
        btnRestock.setOnClickListener {
            val intent = Intent(context, RestockDataActivity::class.java)
            startActivity(intent)

        }
        btnReturn.setOnClickListener {

            val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
            val currentDate = sdf.format(Date())
            SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                .setTitleText(" C DATE is  " + currentDate)
                .setConfirmText("OK")
                .show()
            /*val calenderDialog = DatePickerDialog(requireContext(),
                DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->

                    cDay = dayOfMonth
                    cMonth = month
                    cYear = year
                    val dateEnd : String = ("$cDay-${cMonth+1}-$cYear")

                }, cYear, cMonth, cDay)
            calenderDialog.show()*/
        }
        val email = sharedPref.getString(Constant.PREF_EMAIL)

        binding?.apply {
            AccountBalanceValue.text = (numberFormat.format(appDatabase.readDigitalBalance()!!.toDouble()).toString())
            CashBalanceValue.text = (numberFormat.format(appDatabase.readCashBalance()!!.toDouble()).toString())

            btnFinancial.setOnClickListener{
                val intent = Intent(context, FinancialActivity::class.java)
                startActivity(intent)
            }


            btnAddBalance.setOnClickListener {
                var typeBalance: String
                SweetAlertDialog(context, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                    .setTitleText("Balance Method")
                    .setConfirmText("Cash")
                    .setCancelText("Digital")
                    .setCustomImage(R.drawable.logo_round)
                    .setCancelButtonBackgroundColor(R.color.blue_600)
                    .setConfirmClickListener { sDialog ->
                        typeBalance = "Cash"
                        TransitionManager.beginDelayedTransition(root, AutoTransition())
                        edtTypeBalance.setText(typeBalance)
                        layoutAddBalance.visibility = VISIBLE
                        sDialog.dismissWithAnimation()
                    }
                    .setCancelClickListener { pDialog ->
                        typeBalance = "Digital"
                        TransitionManager.beginDelayedTransition(root, AutoTransition())
                        edtTypeBalance.setText(typeBalance)
                        layoutAddBalance.visibility = VISIBLE
                        pDialog.dismissWithAnimation()
                    }
                    .show()
            }
            btnInsertBalance.setOnClickListener {
                var total = edtTotalBalance.text.toString().toInt()
                var status = "In"
                var typeBalance = edtTypeBalance.text.toString()
                val username = viewModel.readUsername(sharedPref.getString(Constant.PREF_EMAIL))
                val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
                val currentDate = sdf.format(Date())
                var note = "Capital"
                appDatabase.insertBalanceReport(
                    BalanceReport(
                        0,
                        total,
                        status,
                        typeBalance,
                        note,
                        username,
                        currentDate
                    )
                )
                if (typeBalance == "Cash") {

                        SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText("Balance Updated")
                            .setConfirmText("Ok")
                            .setConfirmClickListener { sDialog ->
                                appDatabase.updateCashBalance(total)
                                TransitionManager.beginDelayedTransition(root, AutoTransition())
                                AccountBalanceValue.text = (numberFormat.format(appDatabase.readDigitalBalance()!!.toDouble()).toString())
                                CashBalanceValue.text = (numberFormat.format(appDatabase.readCashBalance()!!.toDouble()).toString())
                                layoutAddBalance.visibility = GONE
                            }
                            .show()

                } else {

                        SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText("Balance Updated")
                            .setConfirmText("Ok")
                            .setConfirmClickListener { sDialog ->
                                appDatabase.updateDigitalBalance(total)
                                TransitionManager.beginDelayedTransition(root, AutoTransition())
                                AccountBalanceValue.text = (numberFormat.format(appDatabase.readDigitalBalance()!!.toDouble()).toString())
                                CashBalanceValue.text = (numberFormat.format(appDatabase.readCashBalance()!!.toDouble()).toString())
                                layoutAddBalance.visibility = GONE
                            }
                            .show()



                }
            }


            Log.d("uName", "main ${email}")

        }
    }
}