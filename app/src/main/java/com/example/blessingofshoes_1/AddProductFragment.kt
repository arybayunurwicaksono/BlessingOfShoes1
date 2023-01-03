package com.example.blessingofshoes_1

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.blessingofshoes_1.databinding.FragmentAddProductBinding
import com.example.blessingofshoes_1.db.AppDb

class AddProductFragment : Fragment() {

    private var binding: FragmentAddProductBinding? = null
    private lateinit var appViewModel: AppViewModel

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

        //appViewModel = obtainViewModel(this@AddProductFragment)
        //val database = AppDb.getDatabase()
       // val dao = database.dbDao()

        val productName: EditText = requireView().findViewById(R.id.edt_product_name)
        val productPrice: EditText = requireView().findViewById(R.id.edt_product_price)
        val productStock: EditText = requireView().findViewById(R.id.edt_product_stock)

        val btnInsertProduct: Button = requireView().findViewById(R.id.btn_insert_product)



    }
    private fun obtainViewModel(activity: AppCompatActivity): AppViewModel {
        val factory = ViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory).get(AppViewModel::class.java)
    }
}