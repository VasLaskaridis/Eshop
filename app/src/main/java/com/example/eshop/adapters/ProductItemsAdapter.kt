package com.example.eshop.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.eshop.R
import com.example.eshop.db.Product
import com.example.eshop.utils.Constants.ADVANCED_SHOP_LAYOUT
import com.example.eshop.utils.Constants.SIMPLE_SHOP_LAYOUT

class ProductItemsAdapter(
    private val showInSimpleStyle: Boolean,
    private val showInGridView: Boolean = false,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var productClickListener: onProductClickListener? = null

    lateinit var context:Context

    private var productsList: List<Product>?=null

    override fun getItemViewType(position: Int): Int {
        return if (showInSimpleStyle)
            SIMPLE_SHOP_LAYOUT
        else
            ADVANCED_SHOP_LAYOUT
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        context = recyclerView.getContext()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.getContext())
        var itemView: View? = null
        var viewHolder: SimpleProductHolder? = null
        if (showInGridView){
            itemView = inflater.inflate(R.layout.product_items_grid_rv_layout, parent, false)
            viewHolder=SimpleProductHolder(itemView)
            val viewHolder = SimpleProductHolder(itemView)
            return viewHolder
        }
        else if (viewType == ADVANCED_SHOP_LAYOUT){
            itemView = inflater.inflate(R.layout.advanced_product_items_rv_layout, parent, false)
            val viewHolder = AdvanceProductHolder(itemView)
            return viewHolder
        }
        else{
            itemView = inflater.inflate(R.layout.product_items_rv_layout, parent, false)
            viewHolder=SimpleProductHolder(itemView)
            val viewHolder = SimpleProductHolder(itemView)
            return viewHolder
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentProduct: Product = productsList!!.get(position)
        if (showInSimpleStyle){
            val productHolder: SimpleProductHolder = holder as SimpleProductHolder
            productHolder.productType.text = currentProduct.quantityType
            productHolder.productPrice.text = currentProduct.price.toString()
            Glide.with(context).load(currentProduct.image).into(productHolder.productImage)
            productHolder.productImage.transitionName=currentProduct.image
            productHolder.productName.text = currentProduct.name
        }else{
            val productHolder: AdvanceProductHolder = holder as AdvanceProductHolder
            Glide.with(context).load(currentProduct.image).into(productHolder.productImage)
            productHolder.productImage.transitionName=currentProduct.image
            productHolder.productName.text = currentProduct.name
        }
    }

    fun setProductList(list: List<Product>) {
        productsList = list
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        if(productsList!=null){
            return productsList!!.size
        }else{
            return 0
        }
    }


    inner class AdvanceProductHolder(itemView: View) : RecyclerView.ViewHolder(itemView)  {
        var productImage: ImageView
        var productName: TextView
        var productContainer: CardView

        init {
            productImage = itemView.findViewById(R.id.productImage_img)
            productName = itemView.findViewById(R.id.productName_tv)
            productContainer = itemView.findViewById(R.id.productContainer)
            productContainer.setOnClickListener{
                val position = absoluteAdapterPosition
                if (productClickListener != null && position != RecyclerView.NO_POSITION) {
                    productClickListener!!.onProductClick(productsList!!.get(position), productImage)
                }
            }
        }
    }

    inner class SimpleProductHolder(itemView: View) : RecyclerView.ViewHolder(itemView)  {
        var productImage: ImageView
        var productName: TextView
        var productType: TextView
        var productPrice: TextView
        var cardView: CardView
        var productContainer: CardView

        init {
            productImage = itemView.findViewById(R.id.productImage_img)
            productName = itemView.findViewById(R.id.productName_tv)
            productContainer = itemView.findViewById(R.id.productContainer)
            productContainer.setOnClickListener{
                val position = absoluteAdapterPosition
                if (productClickListener != null && position != RecyclerView.NO_POSITION) {
                    productClickListener!!.onProductClick(productsList!!.get(position), productImage)
                }
            }

            productPrice=itemView.findViewById(R.id.productPrice_tv)
            productType = itemView.findViewById(R.id.productType_tv)
            cardView = itemView.findViewById(R.id.cardView)
            cardView.setOnClickListener {
                val position = absoluteAdapterPosition
                if (productClickListener != null && position != RecyclerView.NO_POSITION) {
                    productClickListener!!.addProductToCart(productsList!!.get(position))
                }
            }
        }
    }

    interface onProductClickListener {
        fun onProductClick(productModel: Product, transitionImageView: ImageView)
        fun addProductToCart(productModel: Product)
    }

    fun setProductClickListener(listener: onProductClickListener) {
        this.productClickListener = listener
    }


}