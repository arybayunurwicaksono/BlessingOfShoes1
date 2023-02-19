package com.example.blessingofshoes_1

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.drawToBitmap
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import cn.pedant.SweetAlert.SweetAlertDialog
import com.bumptech.glide.load.resource.bitmap.TransformationUtils
import com.example.blessingofshoes_1.databinding.FragmentAddProductBinding
import com.example.blessingofshoes_1.db.AppDb
import com.example.blessingofshoes_1.db.BalanceReport
import com.example.blessingofshoes_1.db.Product
import com.example.blessingofshoes_1.db.Restock
import com.example.blessingofshoes_1.utils.Constant
import com.example.blessingofshoes_1.utils.Preferences
import com.example.blessingofshoes_1.utils.createCustomTempFile
import com.example.blessingofshoes_1.utils.uriToFile
import com.example.blessingofshoes_1.viemodel.AppViewModel
import com.example.blessingofshoes_1.viemodel.ViewModelFactory
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class AddProductFragment : Fragment() {

    private var binding: FragmentAddProductBinding? = null
    private lateinit var appViewModel: AppViewModel
    private val appDatabase by lazy { AppDb.getDatabase(context!!).dbDao() }
    lateinit var sharedPref: Preferences
    private var getFile: File? = null
    val Fragment.packageManager get() = activity?.packageManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAddProductBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPref = Preferences(requireContext())

        var itemPrice : Int = 0
        var itemRealPrice : Int = 0
        var itemProfit : Int
        var itemProfitValue : Int
        var itemStock : Int = 0
        var itemTotalPurchases : Int = 0

        binding?.apply {
            btnCamera.setOnClickListener {
                takePicture()
            }

            btnInsertProduct.setOnClickListener {
                reduceFileImage(getFile!!)
            }
            btnGallery.setOnClickListener {
                startGallery()
            }
            edtProductPrice.addTextChangedListener(object : TextWatcher {

                override fun afterTextChanged(s: Editable) {
                    when {
                        s.isNullOrBlank() -> {
                            edtProductPrice.error = "Fill Real Price"
                        }
                    }

                }

                override fun beforeTextChanged(s: CharSequence, start: Int,
                                               count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence, start: Int,
                                           before: Int, count: Int) {
                    when {
                        s.isNullOrBlank() -> {
                            edtProductPrice.error = "Fill Price"
                        }
                        else -> {
                            itemPrice = s.toString().toInt()
                        }
                    }


                }
            })
            edtProductStock.addTextChangedListener(object : TextWatcher {

                override fun afterTextChanged(s: Editable) {
                    when {
                        s.isNullOrBlank() -> {
                            edtProductStock!!.error = "Fill Stock"
                        }
                    }
                }

                override fun beforeTextChanged(
                    s: CharSequence, start: Int,
                    count: Int, after: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence, start: Int,
                    before: Int, count: Int
                ) {
                    when {
                        s.isNullOrBlank() -> {
                            edtProductStock!!.error = "Fill Stock"
                        }
                        else -> {
                            itemStock = s.toString().toInt()
                        }
                    }

                }
            })
            edtProductStock.addTextChangedListener(object : TextWatcher {

                override fun afterTextChanged(s: Editable) {
                    when {
                        s.isNullOrBlank() -> {
                            edtProductStock!!.error = "Fill Stock"
                        }
                    }
                }

                override fun beforeTextChanged(
                    s: CharSequence, start: Int,
                    count: Int, after: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence, start: Int,
                    before: Int, count: Int
                ) {
                    when {
                        s.isNullOrBlank() -> {
                            edtProductStock!!.error = "Fill Stock"
                        }
                        else -> {
                            itemStock = s.toString().toInt()
                        }
                    }

                }
            })
            edtTotalPurchases.addTextChangedListener(object : TextWatcher {

                override fun afterTextChanged(s: Editable) {
                    when {
                        s.isNullOrBlank() -> {
                            edtTotalPurchases.error = "Fill Total Purchases"
                        }
                    }
                }

                override fun beforeTextChanged(
                    s: CharSequence, start: Int,
                    count: Int, after: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence, start: Int,
                    before: Int, count: Int
                ) {
                    when {
                        s.isNullOrBlank() -> {
                            edtTotalPurchases.error = "Fill Total Purchases"
                        }
                        else -> {
                            itemTotalPurchases = s.toString().toInt()

                            itemRealPrice = itemTotalPurchases / itemStock
                            itemProfitValue = itemPrice - itemRealPrice
                            val localeID = Locale("in", "ID")
                            val numberFormat = NumberFormat.getCurrencyInstance(localeID)
                            profitValue.text = itemProfitValue.toString()
                            priceValue.text = itemRealPrice.toString()

                        }
                    }

                }
            })
            btnInsertProduct.setOnClickListener {
                val productName = edtProductName.text.toString().trim()
                val productBrand = edtProductBrand.text.toString().trim()
                val productPrice = edtProductPrice.text.toString().trim()
                var productStock = edtProductStock.text.toString().trim()
                var productStockFix: Int = 0
                var productPriceFix: Int = 0
                var productRealPriceFix: Int = 0
                var productTotalPurchasesFix: Int = 0
                val productSize = edtProductSize.text.toString().trim()
                val productRealPrice = priceValue.text.toString().trim()
                val productProfit = profitValue.text.toString().toInt()
                var productTotalPurchases = edtTotalPurchases.text.toString().trim()
                var productSupplier = edtSupplier.text.toString().trim()
                val photoItem = imageView.toString().trim()
                val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
                val currentDate = sdf.format(Date())
                when {
                    productName.isEmpty() -> {
                        edtProductName.error = "Fill Real Price"
                        SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Oops...")
                            .setContentText("Some data is empty!")
                            .show()
                    }
                    productSupplier.isEmpty() -> {
                        edtSupplier.error = "Fill Supplier"
                        SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Oops...")
                            .setContentText("Some data is empty!")
                            .show()
                    }
                    productBrand.isEmpty() -> {
                        edtProductBrand.error = "Fill Product Brand"
                        SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Oops...")
                            .setContentText("Some data is empty!")
                            .show()
                    }
                    productPrice.isEmpty() -> {
                        edtProductPrice.error = "Fill Price"
                        SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Oops...")
                            .setContentText("Some data is empty!")
                            .show()
                    }
                    productStock.isEmpty() -> {
                        edtProductStock.error = "Fill Stock"
                        SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Oops...")
                            .setContentText("Some data is empty!")
                            .show()
                    }
                    productSize.isEmpty() -> {
                        edtProductSize.error = "Fill Size"
                        SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Oops...")
                            .setContentText("Some data is empty!")
                            .show()
                    }
                    productTotalPurchases.isEmpty() -> {
                        edtTotalPurchases.error = "Fill Total Purchases"
                        SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Oops...")
                            .setContentText("Some data is empty!")
                            .show()
                    }
/*                photoItem.isEmpty() -> {
                    Toast.makeText(applicationContext, "Please Insert Product Image ", Snackbar.LENGTH_LONG)
                }*/
                    else -> {
                        productStockFix = productStock.toInt()
                        productPriceFix = productPrice.toInt()
                        productRealPriceFix = productRealPrice.toInt()
                        productTotalPurchasesFix = productTotalPurchases.toInt()
                        val username =
                            appDatabase.readUsername(sharedPref.getString(Constant.PREF_EMAIL))
                        SweetAlertDialog(requireContext(), SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText("Payment Method")
                            .setConfirmText("Cash")
                            .setCancelText("Digital")
                            .setConfirmClickListener { sDialog ->
                                edtTypeBalance.setText("Cash")
                                var typePayment = edtTypeBalance.text.toString()
                                SweetAlertDialog(requireContext(), SweetAlertDialog.SUCCESS_TYPE)
                                    .setTitleText("Data is Correct")
                                    .setContentText("Click Save to insert data")
                                    .setConfirmText("Save")
                                    .setConfirmClickListener { pDialog ->
                                        lifecycleScope.launch {
                                            val productPhoto = imageView.drawToBitmap()
                                            appDatabase.insertProduct(
                                                Product(
                                                    0,
                                                    productName,
                                                    productBrand,
                                                    productPriceFix,
                                                    productStockFix,
                                                    productSize,
                                                    productRealPriceFix,
                                                    productTotalPurchasesFix,
                                                    productProfit,
                                                    productPhoto,
                                                    username,
                                                    currentDate
                                                )
                                            )
                                            var lastProductAdded = appDatabase.readLastProduct()!!
                                            appDatabase.insertRestock(
                                                Restock(
                                                    0,
                                                    lastProductAdded,
                                                    productName,
                                                    productStockFix,
                                                    productTotalPurchasesFix,
                                                    productSupplier,
                                                    username,
                                                    currentDate
                                                )
                                            )
                                            appDatabase.insertBalanceReport(
                                                BalanceReport(
                                                    0,
                                                    productTotalPurchasesFix,
                                                    "Out",
                                                    typePayment,
                                                    "restock",
                                                    username,
                                                    currentDate
                                                )
                                            )
                                            appDatabase.updateCashOutBalance(
                                                productTotalPurchasesFix
                                            )

                                        }
//val intent = Intent(this, LoginActivity::class.java)
                                        pDialog.dismissWithAnimation()
                                        sDialog.dismissWithAnimation()
                                        finishTask()
                                    }
                                    .show()
                            }
                            .setCancelText("Digital")
                            .setCancelClickListener { xDialog ->
                                edtTypeBalance.setText("Digital")
                                var typePayment = edtTypeBalance.text.toString()
                                SweetAlertDialog(requireContext(), SweetAlertDialog.SUCCESS_TYPE)
                                    .setTitleText("Data is Correct")
                                    .setContentText("Click Save to insert data")
                                    .setConfirmText("Save")
                                    .setConfirmClickListener { pDialog ->
                                        lifecycleScope.launch {

                                            val productPhoto = imageView.drawToBitmap()
                                            appDatabase.insertProduct(
                                                Product(
                                                    0,
                                                    productName,
                                                    productBrand,
                                                    productPriceFix,
                                                    productStockFix,
                                                    productSize,
                                                    productRealPriceFix,
                                                    productTotalPurchasesFix,
                                                    productProfit,
                                                    productPhoto,
                                                    username,
                                                    currentDate
                                                )
                                            )
                                            var lastProductAdded = appDatabase.readLastProduct()!!
                                            appDatabase.insertRestock(
                                                Restock(
                                                    0,
                                                    lastProductAdded,
                                                    productName,
                                                    productStockFix,
                                                    productTotalPurchasesFix,
                                                    productSupplier,
                                                    username,
                                                    currentDate
                                                )
                                            )
                                            appDatabase.insertBalanceReport(
                                                BalanceReport(
                                                    0,
                                                    productTotalPurchasesFix,
                                                    "Out",
                                                    typePayment,
                                                    "restock",
                                                    username,
                                                    currentDate
                                                )
                                            )
                                            appDatabase.updateDigitalOutBalance(
                                                productTotalPurchasesFix
                                            )

                                        }
                                        pDialog.dismissWithAnimation()
                                            xDialog.dismissWithAnimation()
                                        finishTask()
//val intent = Intent(this, LoginActivity::class.java)

                                    }
                                    .show()
                            }
                            .show()


                    }

                }

            }
        }



    }

    private fun finishTask() {
        textMassge("Your product has been created!")
        findNavController().navigate(R.id.action_add_product_fragment_to_productFragment)
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }
    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImg, requireContext())

            getFile = myFile

            binding?.apply {
                imageView.setImageURI(selectedImg)
            }
        }
    }

    private suspend fun getBitmap(): Bitmap {
        val result = binding?.imageView
        return (result as BitmapDrawable).bitmap
    }
    fun takePicture(){

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        packageManager?.resolveActivity(intent, 0)

        createCustomTempFile(requireContext()).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                requireContext(),
                "com.example.blessingofshoes_1",
                it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherIntentCamera.launch(intent)

        }
    }
    private lateinit var currentPhotoPath: String
    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            val file = File(currentPhotoPath).also { getFile = it }
            val os: OutputStream
            val bitmap = BitmapFactory.decodeFile(getFile?.path)
            val exif = ExifInterface(currentPhotoPath)
            if (getFile != null) {
                val orientation: Int = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED
                )
                val rotatedBitmap: Bitmap = when (orientation) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> TransformationUtils.rotateImage(bitmap, 90)
                    ExifInterface.ORIENTATION_ROTATE_180 -> TransformationUtils.rotateImage(bitmap, 180)
                    ExifInterface.ORIENTATION_ROTATE_270 -> TransformationUtils.rotateImage(bitmap, 270)
                    ExifInterface.ORIENTATION_NORMAL -> bitmap
                    else -> bitmap
                }
                var compressQuality = 100
                var streamLength: Int
                do {
                    val bmpStream = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
                    val bmpPicByteArray = bmpStream.toByteArray()
                    streamLength = bmpPicByteArray.size
                    compressQuality -= 5
                } while (streamLength > 1000000)
                bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(file))
                try {
                    os = FileOutputStream(file)
                    rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 50, os)
                    os.flush()
                    os.close()
                    getFile = file
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                binding?.apply {
                    imageView.setImageBitmap(rotatedBitmap)
                }
            } else {
                SweetAlertDialog(requireContext(), SweetAlertDialog.SUCCESS_TYPE)
                    .setTitleText("Some data is empty")
                    .setContentText("Please Insert Product Image")
                    .setConfirmText("ok")
                    .show()
            }
        }
    }

    private fun obtainViewModel(activity: AppCompatActivity): AppViewModel {
        val factory = ViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory).get(AppViewModel::class.java)
    }
    private fun textMassge(s: String) {
        Toast.makeText(requireContext(),s, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }
    private fun reduceFileImage(file: File): File {
        val bitmap = BitmapFactory.decodeFile(file.path)
        var compressQuality = 100
        var streamLength: Int
        do {
            val bmpStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
            val bmpPicByteArray = bmpStream.toByteArray()
            streamLength = bmpPicByteArray.size
            compressQuality -= 5
        } while (streamLength > 1000000)
        bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(file))
        return file
    }
}