package com.example.blessingofshoes_1

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ExifInterface
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.drawToBitmap
import com.example.blessingofshoes_1.databinding.ActivityEditProductBinding
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import cn.pedant.SweetAlert.SweetAlertDialog
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.TransformationUtils
import com.example.blessingofshoes_1.utils.Preferences
import com.example.blessingofshoes_1.utils.createCustomTempFile
import com.example.blessingofshoes_1.utils.uriToFile
import com.example.blessingofshoes_1.viemodel.AppViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.NumberFormat
import java.util.*


@AndroidEntryPoint
class EditProductActivity : AppCompatActivity() {
    private lateinit var _activityEditProductBinding: ActivityEditProductBinding
    private val binding get() = _activityEditProductBinding
    private val viewModel by viewModels<AppViewModel>()
    private var getFile: File? = null
    private var idProduct : Int? = 0
    lateinit var sharedPref: Preferences

    override fun onBackPressed() {
        super.onBackPressed()
        val i = Intent(this@EditProductActivity, MainActivity::class.java)
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(i)
        finish()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _activityEditProductBinding = ActivityEditProductBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        var itemPrice : Int = 0
        var itemRealPrice : Int = 0
        var itemProfit : Int
        var itemProfitValue : String
        var iRealPrice : Int = 0
        var iStock : Int
        var iTotalPurchases : Int
        var iTime : String
        val eName = intent.getStringExtra("DATA_NAME")
        val eId = intent.getIntExtra("DATA_ID", 0)
        var username : String
        Log.i("extraId", "ID : $eId")
        viewModel.readProductItem(eId).observe(this, Observer {
            binding.edtProductName.setText(it.nameProduct)
            binding.edtProductBrand.setText(it.brandProduct)
            binding.edtProductPrice.setText(it.priceProduct.toString())
            binding.edtProductSize.setText(it.sizeProduct)
            binding.profitValue.setText(it.profitProduct.toString())
            idProduct = it.idProduct
            iRealPrice = it.realPriceProduct!!.toString().toInt()
            iStock = it.stockProduct!!
            var totalPurchasesFix = it.totalPurchases!!
            iTotalPurchases = it.totalPurchases!!
            iTime = it.timeAdded!!
            username = it.username!!
            binding.priceValue.setText(it.realPriceProduct.toString())
            Glide.with(this@EditProductActivity)
                .load(it.productPhoto)
                .fitCenter()
                .into(binding.imageView)
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
                            itemProfit = itemPrice - iRealPrice
                            itemProfitValue = itemProfit.toString()
                            val localeID =  Locale("in", "ID")
                            val numberFormat = NumberFormat.getCurrencyInstance(localeID)
                            binding.profitValue.text = itemProfitValue.toString()
                        }
                    }


                }
            })
            binding.btnInsertProduct.setOnClickListener {
                val productName = binding.edtProductName.text.toString().trim()
                val productBrand = binding.edtProductBrand.text.toString().trim()
                val productPrice = binding.edtProductPrice.text.toString().trim()
                val productStock = iStock
                var productPriceFix = productPrice.toInt()
                val productSize = binding.edtProductSize.text.toString().trim()
                val productRealPrice = iRealPrice
                var productTotalPurchases = iTotalPurchases
                var productTimeAdded = iTime
                val productProfit = binding.profitValue.text.toString().toInt()
                when {
                    productName.isEmpty() -> {
                        binding.edtProductName.error = "Fill Real Price"
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
                    productSize.isEmpty() -> {
                        binding.edtProductSize.error = "Fill Size"
                        SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Oops...")
                            .setContentText("Some data is empty!")
                            .show()
                    }

                    else -> {

                        SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText("Data is Correct")
                            .setContentText("Click Save to Update data")
/*                            .setContentText(idProduct!!.toString() + productName + productBrand +
                                productPriceFix+productStock.toString() +productSize.toString()+productRealPrice.toString() +
                                    totalPurchasesFix.toString() + productProfit.toString() + "photo"+productSupplier+productTimeAdded)*/
                            .setConfirmText("Save")
                            .setConfirmClickListener { sDialog ->

                                lifecycleScope.launch {
                                    val productPhoto = binding.imageView.drawToBitmap()
                                    //viewModel.updateProduct(idProduct, productName, productPrice,productStock, productPhoto)
                                    viewModel.updateProductItem(applicationContext, idProduct!!, productName, productBrand,
                                        productPriceFix, productStock, productSize, productRealPrice, totalPurchasesFix, productProfit, productPhoto, username, productTimeAdded) {
                                        finishUpdate()
                                    }
                                }

                            }.show()

                    }
                }
            }

        })

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                EditProductActivity.REQUIRED_PERMISSIONS,
                EditProductActivity.REQUEST_CODE_PERMISSIONS
            )
        }

        binding.btnCamera.setOnClickListener {
            takePicture()
        }
        binding.btnGallery.setOnClickListener {
            startGallery()
        }



        //getproductData(productData)


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

    fun finishUpdate(){
        Toast.makeText(this, "Your Product has been updated", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
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
        }
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

}