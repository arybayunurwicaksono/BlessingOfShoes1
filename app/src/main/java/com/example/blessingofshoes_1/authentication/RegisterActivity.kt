package com.example.blessingofshoes_1.authentication

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.drawToBitmap
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.ViewModelProvider
import cn.pedant.SweetAlert.SweetAlertDialog
import com.bumptech.glide.load.resource.bitmap.TransformationUtils
import com.example.blessingofshoes_1.*
import com.example.blessingofshoes_1.databinding.ActivityRegisterBinding
import com.example.blessingofshoes_1.db.AppDb
import com.example.blessingofshoes_1.db.Balance
import com.example.blessingofshoes_1.db.Users
import com.google.android.material.snackbar.Snackbar
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    private lateinit var appViewModel: AppViewModel
    private var getFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        appViewModel = obtainViewModel(this@RegisterActivity)
        val database = AppDb.getDatabase(applicationContext)
        val dao = database.dbDao()

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                RegisterActivity.REQUIRED_PERMISSIONS,
                RegisterActivity.REQUEST_CODE_PERMISSIONS
            )
        }

        binding.imgBackRegister.setOnClickListener {
            onBackPressed()
            finish()
        }

        binding.imageView.setOnClickListener {
            SweetAlertDialog(this, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                .setTitleText("Profile Photo")
                .setContentText("Choose method!")
                .setCustomImage(R.drawable.logo_round)
                .setConfirmText("Camera")
                .setConfirmClickListener { sDialog ->
                    takePicture()
                    sDialog.dismissWithAnimation()
                }
                .setCancelText("Gallery")
                .setCancelButtonBackgroundColor(R.color.blue_600)
                .setCancelClickListener { pDialog ->
                    startGallery()
                    pDialog.dismissWithAnimation()
                }
                .show()
        }

       binding.btnCreateAccount.setOnClickListener {
           val fullname = binding.edtFullName.text.toString().trim()
           val username = binding.edtUsername.text.toString().trim()
           val email = binding.edtEmail.text.toString().trim()
           val password = binding.edtPassword.text.toString().trim()
           val entityUsername = dao.validateUsername(username)
           val entity = dao.validateEmail(email)
           var photoUser = binding.imageView.drawToBitmap()
           when {
               fullname.isEmpty() -> {
                   binding.edtFullName.error = getString(R.string.er_empty_username)
               }
               username.isEmpty() -> {
                   binding.edtUsername.error = getString(R.string.er_empty_username)
               }
               username == entityUsername -> {
                   binding.edtUsername.error = getString(R.string.er_taken_username)
               }
               email.isEmpty() -> {
                   binding.edtEmail.error = getString(R.string.er_empty_email)
               }
               !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                   binding.edtEmail.error = getString(R.string.er_wrong_email_format)
               }
               email == entity -> {
                   binding.edtEmail.error = getString(R.string.er_taken_email)
               }
               password.isEmpty() -> {
                   binding.edtPassword.error = getString(R.string.er_empty_password)
               }
               password.length < 6 -> {
                   binding.edtPassword.error = getString(R.string.er_password_to_short)
               }
               else -> {
                   var validateOwner = dao.validateOwner()
                   if(validateOwner == 0) {
                       var role = "Admin"
                       appViewModel.registerUser(Users(0,fullname, username,email,password,role, photoUser))
                       //val intent = Intent(this, LoginActivity::class.java)
                       textMassge("Your account has been created!")
                       appViewModel.insertBalance(Balance(0,0,0))
                       //startActivity(intent)
                       finish()
                   } else {
                       var role = "Cashier"
                       appViewModel.registerUser(Users(0,fullname, username,email,password,role, photoUser))
                       //val intent = Intent(this, LoginActivity::class.java)
                       textMassge("Your account has been created!")
                       //startActivity(intent)
                       finish()
                   }
               }
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