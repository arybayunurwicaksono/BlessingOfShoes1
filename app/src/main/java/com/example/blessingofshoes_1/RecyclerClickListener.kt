package com.example.blessingofshoes_1

import com.example.blessingofshoes_1.db.Product

interface RecyclerClickListener {
//    fun onItemRemoveClick(position: Int)
    fun onItemClick(product: Product, position: Int, qty: Int)
}