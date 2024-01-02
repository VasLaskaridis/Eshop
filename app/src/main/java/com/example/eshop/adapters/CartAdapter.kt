package com.example.eshop.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.eshop.R
import com.example.eshop.db.Product
import java.text.DecimalFormat

class CartAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>()
{
    private var productsList : List<Product>?=null

    private lateinit var purchasedProductsList : List<Product>

    private lateinit var cartProductListener: CartProductListener

    private lateinit var context: Context

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        context = recyclerView.getContext()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.getContext())
        var itemView: View = inflater.inflate(R.layout.cart_item_layout, parent, false)
        val viewHolder = CartViewHolder(itemView)
        return viewHolder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val cartItemHolder: CartViewHolder = holder as CartViewHolder
        val currentCartItem: Product = productsList!!.get(position)

        Glide.with(context).load(currentCartItem.image).into(cartItemHolder.cartProductImage_img)
        cartItemHolder.cartProductImage_img.transitionName=currentCartItem.image
        cartItemHolder.favProductName_tv.text=currentCartItem.name
        cartItemHolder.favProductQuantity_tv.text=currentCartItem.quantityType

        cartItemHolder.productQuantityMinus_img.setOnClickListener(View.OnClickListener {
            cartItemHolder.productQuantity_et.setText((currentCartItem.quantity-1).toString())
            onQuantityTextChanged(cartItemHolder.productQuantity_et.text, currentCartItem, cartItemHolder.cartProductPrice_tv)
        })
        cartItemHolder.productQuantityPlus_img.setOnClickListener(View.OnClickListener {
            cartItemHolder.productQuantity_et.setText((currentCartItem.quantity+1).toString())
            onQuantityTextChanged(cartItemHolder.productQuantity_et.text, currentCartItem, cartItemHolder.cartProductPrice_tv)
        })
        cartItemHolder.productQuantity_et.setText(currentCartItem.quantity.toString())

        cartItemHolder.cartProductPrice_tv.text=(currentCartItem.price* currentCartItem.quantity).toString()+"$"
    }


    override fun getItemCount(): Int {
        if(productsList!=null){
            return productsList!!.size
        }else{
            return 0
        }
    }

    fun setCartProductList(list: List<Product>){
        productsList=list
        purchasedProductsList=list
        notifyDataSetChanged()
    }

    fun getPurchasedProducts() = purchasedProductsList

    fun onQuantityTextChanged(
        text: CharSequence,
        product: Product,
        priceTextView: TextView
    ) {
        val quantity = text.toString()
        if (quantity.isNotEmpty()) {
            val quantityNumber = quantity.toDouble().toInt()
            if (quantityNumber > 0) {
               // if(product in purchasedProductsList) {
                    val product = productsList!!.single { it.id == product.id }
                    product.quantity = quantityNumber
                    val price = product.price * quantityNumber
                    priceTextView.text = "" + DecimalFormat("##.##").format(price) + "$"
                //}else{}
            }
       }
    }

    inner class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var cartProductImage_img: ImageView
        var productQuantityMinus_img: ImageView
        var productQuantityPlus_img: ImageView
        var deleteItemFromCart_img: ImageView
        var cartProductPrice_tv: TextView
        var favProductQuantity_tv: TextView
        var favProductName_tv: TextView
        var productQuantity_et: EditText

        init {
            cartProductImage_img=itemView.findViewById(R.id.cartProductImage_img)
            productQuantityMinus_img=itemView.findViewById(R.id.productQuantityMinus_img)
            productQuantityPlus_img=itemView.findViewById(R.id.productQuantityPlus_img)
            deleteItemFromCart_img=itemView.findViewById(R.id.deleteItemFromCart_img)
            favProductQuantity_tv=itemView.findViewById(R.id.favProductQuantity_tv)
            cartProductPrice_tv=itemView.findViewById(R.id.cartProductPrice_tv)
            favProductName_tv=itemView.findViewById(R.id.favProductName_tv)
            productQuantity_et=itemView.findViewById(R.id.productQuantity_et)
            deleteItemFromCart_img.setOnClickListener {
                val position = absoluteAdapterPosition
                if (cartProductListener != null && position != RecyclerView.NO_POSITION) {
                    cartProductListener.onProductDelete(productsList!!.get(position))
                }
            }

        }
    }

    interface CartProductListener {
        fun onProductDelete(product: Product)
    }

    fun setCartProductListenerDelete(listener: CartProductListener){
        cartProductListener=listener
    }
}