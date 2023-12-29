package com.example.eshop.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.eshop.R
import com.example.eshop.db.Product

class OrderProductsAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private  var orderProductList : List<Product>?=null

    lateinit var context: Context

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        context = recyclerView.getContext()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderProductViewHolder {
        val inflater = LayoutInflater.from(parent.getContext())
        var itemView: View = inflater.inflate(R.layout.order_product_item, parent, false)
        val viewHolder = OrderProductViewHolder(itemView)
        return viewHolder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val orderProductHolder: OrderProductViewHolder = holder as OrderProductViewHolder
        val currentOrderProduct: Product = orderProductList!!.get(position)
        Glide.with(context).load(currentOrderProduct.image).into(orderProductHolder.productImage_img)
        orderProductHolder.productName_tv.text=currentOrderProduct.name
        orderProductHolder.productQuantity_tv.text=currentOrderProduct.quantity.toString()
        orderProductHolder.productPrice_tv.text=currentOrderProduct.price.toString()
    }

    override fun getItemCount(): Int {
        if(orderProductList!=null){
            return orderProductList!!.size
        }else{
            return 0
        }
    }

    fun setOrderList(list:List<Product>){
        orderProductList=list
    }

    inner class OrderProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var productImage_img: ImageView
        var productName_tv: TextView
        var productQuantity_tv: TextView
        var productPrice_tv: TextView

        init {
            productImage_img=itemView.findViewById(R.id.productImage_img)
            productName_tv=itemView.findViewById(R.id.productName_tv)
            productQuantity_tv=itemView.findViewById(R.id.productQuantity_tv)
            productPrice_tv=itemView.findViewById(R.id.productPrice_tv)
        }

    }
}