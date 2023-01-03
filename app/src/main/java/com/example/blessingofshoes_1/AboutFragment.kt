package com.example.blessingofshoes_1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.blessingofshoes_1.authentication.LoginActivity
import com.example.blessingofshoes_1.databinding.FragmentAboutBinding
import dagger.hilt.android.AndroidEntryPoint

class AboutFragment : Fragment() {

    private var binding: FragmentAboutBinding? = null
    lateinit var sharedPref: Preferences
    private lateinit var test: AppRepository
    private val viewModel by viewModels<AppViewModel>()

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
        val email = sharedPref.getString(Constant.PREF_EMAIL)

        binding?.apply {
            userEmail.text = email
        }


        Log.d("uName", "main ${email}")

    }
}