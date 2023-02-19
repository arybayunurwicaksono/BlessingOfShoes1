package com.example.blessingofshoes_1.authentication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import com.example.blessingofshoes_1.utils.Constant
import com.example.blessingofshoes_1.MainActivity
import com.example.blessingofshoes_1.utils.Preferences
import com.example.blessingofshoes_1.R
import com.example.blessingofshoes_1.databinding.ActivityLoginBinding
import com.example.blessingofshoes_1.db.AppDb

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    lateinit var sharedPref: Preferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvSignUp.setOnClickListener {
            val intent = Intent(applicationContext, RegisterActivity::class.java)

            startActivity(intent)
        }

        getSupportActionBar()?.hide()

        sharedPref = Preferences(this)

        val database = AppDb.getDatabase(applicationContext)
        val dao = database.dbDao()


        binding.btnSignIn.setOnClickListener {

            val email = binding.edtEmail.text.toString().trim()
            val password = binding.edtPassword.text.toString().trim()

            when {
                email.isEmpty() -> {
                    binding.edtEmail.error = getString(R.string.er_empty_email)
                }
                password.isEmpty() -> {
                    binding.edtPassword.error = getString(R.string.er_empty_password)
                }
                else -> {
                    val entity = dao.readDataUser(email, password)
                    if (entity == null) {
                        textMassge("Incorrectly entered the username or password you entered")
                    } else {
                        textMassge("Login Success")
                        sharedPref.put(Constant.PREF_EMAIL, email)
                        sharedPref.put(Constant.PREF_PASSWORD, password)
                        sharedPref.put(Constant.PREF_IS_LOGIN, true)
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }

                }
            }


        }


        val signUp = findViewById<TextView>(R.id.tv_sign_up)
        signUp.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()
        if (sharedPref.getBoolean(Constant.PREF_IS_LOGIN)) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun textMassge(s: String) {
        Toast.makeText(this,s, Toast.LENGTH_SHORT).show()
    }


}