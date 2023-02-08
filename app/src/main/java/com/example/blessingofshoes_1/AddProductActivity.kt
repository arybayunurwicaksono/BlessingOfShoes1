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
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.drawToBitmap
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import cn.pedant.SweetAlert.SweetAlertDialog
import com.bumptech.glide.load.resource.bitmap.TransformationUtils
import com.example.blessingofshoes_1.databinding.ActivityAddProductBinding
import com.example.blessingofshoes_1.db.AppDb
import com.example.blessingofshoes_1.db.BalanceReport
import com.example.blessingofshoes_1.db.Product
import com.example.blessingofshoes_1.db.Restock
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*


class AddProductActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddProductBinding
    private var getFile: File? = null
    private lateinit var appViewModel: AppViewModel
    private lateinit var converters: Converters
    private val viewModel by viewModels<AppViewModel>()
    lateinit var sharedPref: Preferences


    override fun onBackPressed() {
        super.onBackPressed()
        val i = Intent(this@AddProductActivity, MainActivity::class.java)
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(i)
        finish()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddProductBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPref = Preferences(this)
        setSupportActionBar(binding.toolbar)

        var itemPrice : Int = 0
        var itemRealPrice : Int = 0
        var itemProfit : Int
        var itemProfitValue : Int
        var itemStock : Int = 0
        var itemTotalPurchases : Int = 0

        appViewModel = obtainViewModel(this@AddProductActivity)
        val database = AppDb.getDatabase(applicationContext)
        val dao = database.dbDao()

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        binding.btnCamera.setOnClickListener {
            takePicture()
        }

        binding.btnInsertProduct.setOnClickListener{
            reduceFileImage(getFile!!)
        }

        binding.edtProductPrice.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                when {
                    s.isNullOrBlank() -> {
                        binding.edtProductPrice.error = "Fill Real Price"
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
                        binding.edtProductPrice.error = "Fill Price"
                    }
                    else -> {
                        itemPrice = s.toString().toInt()
                    }
                }


            }
        })
        binding.edtProductStock.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                when {
                    s.isNullOrBlank() -> {
                        binding.edtProductStock.error = "Fill Stock"
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
                        binding.edtProductStock.error = "Fill Stock"
                    }
                    else -> {
                        itemStock = s.toString().toInt()
                    }
                }

            }
        })
        binding.btnGallery.setOnClickListener{
            startGallery()
        }
        binding.edtTotalPurchases.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                when {
                    s.isNullOrBlank() -> {
                        binding.edtTotalPurchases.error = "Fill Total Purchases"
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
                        binding.edtTotalPurchases.error = "Fill Total Purchases"
                    }
                    else -> {
                        itemTotalPurchases = s.toString().toInt()

                        itemRealPrice = itemTotalPurchases/itemStock
                        itemProfitValue = itemPrice - itemRealPrice
                        val localeID =  Locale("in", "ID")
                        val numberFormat = NumberFormat.getCurrencyInstance(localeID)
                        binding.profitValue.text = itemProfitValue.toString()
                        binding.priceValue.text = itemRealPrice.toString()

                    }
                }

            }
        })


        binding.btnInsertProduct.setOnClickListener{
            val productName = binding.edtProductName.text.toString().trim()
            val productBrand = binding.edtProductBrand.text.toString().trim()
            val productPrice = binding.edtProductPrice.text.toString().trim()
            var productStock = binding.edtProductStock.text.toString().trim()
            var productStockFix : Int = 0
            var productPriceFix : Int = 0
            var productRealPriceFix : Int = 0
            var productTotalPurchasesFix : Int = 0
            val productSize = binding.edtProductSize.text.toString().trim()
            val productRealPrice = binding.priceValue.text.toString().trim()
            val productProfit = binding.profitValue.text.toString().toInt()
            var productTotalPurchases = binding.edtTotalPurchases.text.toString().trim()
            var productSupplier = binding.edtSupplier.text.toString().trim()
            val photoItem = binding.imageView.toString().trim()
            val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
            val currentDate = sdf.format(Date())
            when {
                productName.isEmpty() -> {
                    binding.edtProductName.error = "Fill Real Price"
                    SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Oops...")
                        .setContentText("Some data is empty!")
                        .show()
                }
                productSupplier.isEmpty() -> {
                    binding.edtSupplier.error = "Fill Supplier"
                    SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Oops...")
                        .setContentText("Some data is empty!")
                        .show()
                }
                productBrand.isEmpty() -> {
                    binding.edtProductBrand.error = "Fill Product Brand"
                    SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Oops...")
                        .setContentText("Some data is empty!")
                        .show()
                }
                productPrice.isEmpty() -> {
                    binding.edtProductPrice.error = "Fill Price"
                    SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Oops...")
                        .setContentText("Some data is empty!")
                        .show()
                }
                productStock.isEmpty() -> {
                    binding.edtProductStock.error = "Fill Stock"
                    SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Oops...")
                        .setContentText("Some data is empty!")
                        .show()
                }
                productSize.isEmpty() -> {
                    binding.edtProductSize.error = "Fill Size"
                    SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Oops...")
                        .setContentText("Some data is empty!")
                        .show()
                }
                productTotalPurchases.isEmpty() -> {
                    binding.edtTotalPurchases.error = "Fill Total Purchases"
                    SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
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
                    val username = viewModel.readUsername(sharedPref.getString(Constant.PREF_EMAIL))
                    SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("Payment Method")
                        .setConfirmText("Cash")
                        .setCancelText("Digital")
                        .setConfirmClickListener { sDialog ->
                            binding.edtTypeBalance.setText("Cash")
                            var typePayment = binding.edtTypeBalance.text.toString()
                            SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("Data is Correct")
                                .setContentText("Click Save to insert data")
                                .setConfirmText("Save")
                                .setConfirmClickListener { pDialog ->
                                    lifecycleScope.launch {

                                        val productPhoto = binding.imageView.drawToBitmap()
                                        appViewModel.insertProduct(Product(0,productName, productBrand,
                                            productPriceFix, productStockFix, productSize, productRealPriceFix, productTotalPurchasesFix, productProfit, productPhoto, username, currentDate))
                                        var lastProductAdded = appViewModel.readLastProduct()!!
                                        appViewModel.insertRestock(Restock(0, lastProductAdded, productName, productStockFix, productTotalPurchasesFix, productSupplier, username, currentDate))
                                        appViewModel.insertBalanceReport(BalanceReport(0, productTotalPurchasesFix, "Out", typePayment, "restock", username, currentDate))
                                        appViewModel.updateCashOutBalance(this@AddProductActivity,productTotalPurchasesFix) {
                                            finishTask()
                                        }
                                    }
//val intent = Intent(this, LoginActivity::class.java)

                                }
                                .show()
                        }
                        .setCancelText("Digital")
                        .setCancelClickListener { xDialog ->
                            binding.edtTypeBalance.setText("Digital")
                            var typePayment = binding.edtTypeBalance.text.toString()
                            SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("Data is Correct")
                                .setContentText("Click Save to insert data")
                                .setConfirmText("Save")
                                .setConfirmClickListener { pDialog ->
                                    lifecycleScope.launch {

                                        val productPhoto = binding.imageView.drawToBitmap()
                                        appViewModel.insertProduct(Product(0,productName, productBrand,
                                            productPriceFix, productStockFix, productSize, productRealPriceFix, productTotalPurchasesFix, productProfit, productPhoto, username, currentDate))
                                        var lastProductAdded = appViewModel.readLastProduct()!!
                                        appViewModel.insertRestock(Restock(0, lastProductAdded, productName, productStockFix, productTotalPurchasesFix, productSupplier, username, currentDate))
                                        appViewModel.insertBalanceReport(BalanceReport(0, productTotalPurchasesFix, "Out", typePayment, "restock", username, currentDate))
                                        appViewModel.updateDigitalOutBalance(this@AddProductActivity,productTotalPurchasesFix) {
                                            finishTask()
                                        }
                                    }
//val intent = Intent(this, LoginActivity::class.java)

                                }
                                .show()
                        }
                        .show()


                }

            }
            //val productPhoto = getBitmap()
            //var productStockFix: Int = Integer.parseInt(productStock)

        }

    }
    private fun finishTask() {
        textMassge("Your product has been created!")
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
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
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImg, this)

            getFile = myFile

            binding.imageView.setImageURI(selectedImg)
        }
    }

    private suspend fun getBitmap(): Bitmap {
        val result = binding.imageView
        return (result as BitmapDrawable).bitmap
    }
    fun takePicture(){
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)

        createCustomTempFile(application).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                this,
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
        if (result.resultCode == RESULT_OK) {
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
                binding.imageView.setImageBitmap(rotatedBitmap)
            } else {
                Snackbar.make(
                    binding.root,
                    "Please Insert Product Image",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }
    }
    private fun obtainViewModel(activity: AppCompatActivity): AppViewModel {
        val factory = ViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory).get(AppViewModel::class.java)
    }
    private fun textMassge(s: String) {
        Toast.makeText(this,s, Toast.LENGTH_SHORT).show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Snackbar.make(
                    binding.root,
                    getString(R.string.img_upload_error),
                    Snackbar.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }
    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
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

/*    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.getItemId()) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        this.finish()
    }*/
}