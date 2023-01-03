package com.example.blessingofshoes_1.authentication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.blessingofshoes_1.AppViewModel
import com.example.blessingofshoes_1.R
import com.example.blessingofshoes_1.ViewModelFactory
import com.example.blessingofshoes_1.databinding.ActivityRegisterBinding
import com.example.blessingofshoes_1.db.AppDb
import com.example.blessingofshoes_1.db.Users
import com.google.android.material.snackbar.Snackbar

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    private lateinit var appViewModel: AppViewModel

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



        binding.imgBackRegister.setOnClickListener {
            onBackPressed()
            finish()
        }

       binding.btnCreateAccount.setOnClickListener {
           val fullname = binding.edtFullName.text.toString().trim()
           val username = binding.edtUsername.text.toString().trim()
           val email = binding.edtEmail.text.toString().trim()
           val password = binding.edtPassword.text.toString().trim()
           val entityUsername = dao.validateUsername(username)
           val entity = dao.validateEmail(email)
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
                   appViewModel.registerUser(Users(0,fullname, username,email,password))
                   //val intent = Intent(this, LoginActivity::class.java)
                   textMassge("Your account has been created!")
                   //startActivity(intent)
                   finish()
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
}