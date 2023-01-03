package com.example.blessingofshoes_1

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.blessingofshoes_1.authentication.RegisterActivity
import com.example.blessingofshoes_1.databinding.FragmentProductBinding
import com.example.blessingofshoes_1.db.Product
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductFragment : Fragment() {

    private var binding: FragmentProductBinding? = null
    private lateinit var productAdapter: ProductAdapter
    private val viewModel by viewModels<AppViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_product, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getAllProduct().observe(viewLifecycleOwner) { itemList ->
            if (itemList != null) {
                productAdapter.setProductData(itemList)
            }
        }

       // val btnAdd: FloatingActionButton = requireView().findViewById(R.id.btn_add)
        //btnAdd.setOnClickListener{
        //    val intent = Intent(context, AddProductActivity::class.java)
        //    startActivity(intent)
        //}
        rvProduct()
    }

    private fun rvProduct() {
        val recyclerViewCart: RecyclerView = requireView().findViewById(R.id.rv_product)
        productAdapter = ProductAdapter(context, ArrayList())
        recyclerViewCart.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = productAdapter
        }
        productAdapter.setOnItemClickCallback(object : ProductAdapter.OnItemClickCallback {
            override fun onItemClicked(data: Product) {
                showSelectedItem(data)
                //val intentToDetail = Intent(context, EditProductActivity::class.java)
                //intentToDetail.putExtra("DATA", data)
                //intentToDetail.putExtra("DATA_NAME", data.nameProduct)
//                intentToDetail.putExtra("DATA_PRICE", data.priceProduct)
//                intentToDetail.putExtra("DATA_STOCK", data.stockProduct)
                //intentToDetail.putExtra("DATA_PHOTO", data.productPhoto)
                //startActivity(intentToDetail)
            }
        })

    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
    private fun showSelectedItem(item: Product) {
        //Toast.makeText(context, "Kamu memilih " + item.nameProduct, Toast.LENGTH_SHORT).show()
    }
}