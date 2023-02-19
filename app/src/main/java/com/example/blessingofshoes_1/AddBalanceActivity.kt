package com.example.blessingofshoes_1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.blessingofshoes_1.databinding.ActivityAddBalanceBinding
import com.example.blessingofshoes_1.utils.Constant
import com.example.blessingofshoes_1.utils.Preferences
import com.example.blessingofshoes_1.viemodel.AppViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class AddBalanceActivity : AppCompatActivity() {

    private lateinit var _activityAddBalanceBinding: ActivityAddBalanceBinding
    private val binding get() = _activityAddBalanceBinding
    private val viewModel by viewModels<AppViewModel>()
    private var getFile: File? = null
    private lateinit var appViewModel: AppViewModel
    private var idProduct: Int? = 0
    lateinit var sharedPref: Preferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _activityAddBalanceBinding = ActivityAddBalanceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val eType = intent.getStringExtra("DATA_TYPE")
        binding.btnInsertBalance.setOnClickListener {
            var total = binding.edtTotalBalance!!.toString().trim()
            var totalFix = total.toInt()
            var status = "In"
            var typeBalance = eType
            val username = viewModel.readUsername(sharedPref.getString(Constant.PREF_EMAIL))
            val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
            val currentDate = sdf.format(Date())
            var note = "Capital"
            SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText(totalFix.toString()+status+typeBalance+username+currentDate+note)
                .setConfirmText("Ok")
                .setConfirmClickListener { sDialog->
                    sDialog.dismissWithAnimation()
                }
                .show()
        /*if (typeBalance == "Cash") {
                appViewModel.updateCashBalance(this, totalFix) {
                    SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("Balance Updated")
                        .setConfirmText("Ok")
                        .setConfirmClickListener { sDialog->
                            sDialog.dismissWithAnimation()
                            val intent = Intent(this, MainActivity::class.java)

                            startActivity(intent)
                            finish()
                        }

                        .show()
                }
            } else {
                appViewModel.updateDigitalBalance(this, totalFix) {
                    SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("Balance Updated")
                        .setConfirmText("Ok")
                        .setConfirmClickListener { sDialog->
                            sDialog.dismissWithAnimation()
                            val intent = Intent(this, MainActivity::class.java)

                            startActivity(intent)
                            finish()
                        }
                        .show()



                }
            }
            appViewModel.insertBalanceReport(
                BalanceReport(
                    0,
                    totalFix,
                    status,
                    typeBalance,
                    note,
                    username,
                    currentDate
                )
            )*/
        }
    }
}