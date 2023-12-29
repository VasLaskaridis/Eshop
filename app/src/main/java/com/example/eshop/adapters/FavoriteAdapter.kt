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
import javax.inject.Inject

class FavoriteAdapter
@Inject
constructor() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var favoriteProductClickListener: FavoriteProductClickListener? = null

    var productList: List<Product>?=null

    lateinit var context: Context

    fun setAdapterProductList(list: List<Product>) {
        productList = list
        notifyDataSetChanged()
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        context = recyclerView.getContext()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.getContext())
        val itemView = inflater.inflate(R.layout.favorite_item_rv_layout, parent, false)
        val viewHolder = FavoriteViewHolder(itemView)
        return viewHolder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val favoriteHolder: FavoriteViewHolder = holder as FavoriteViewHolder
        val currentProduct: Product = productList!!.get(position)

        Glide.with(context).load(currentProduct.image).into(favoriteHolder.favProductImage)
        favoriteHolder.favProductImage.transitionName=currentProduct.image
        favoriteHolder.favProductName.text = currentProduct.name
        favoriteHolder.favProductQuantity.text = currentProduct.quantityType
        favoriteHolder.favProductPrice.text = currentProduct.price.toString()
        if (getItemCount()>0){
            favoriteHolder.favoriteProductLayout.visibility=View.VISIBLE
        }else{
            favoriteHolder.favoriteProductLayout.visibility=View.GONE
        }
    }

    override fun getItemCount(): Int {
        if(productList!=null){
            return productList!!.size
        }else{
            return 0
        }
    }

    inner class FavoriteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)  {
        var favProductImage: ImageView
        var favProductName: TextView
        var favProductQuantity: TextView
        var favProductPrice: TextView
        var favoriteProductLayout: androidx.constraintlayout.widget.ConstraintLayout

        init{
            favProductImage=itemView.findViewById(R.id.favProductImage_img)
            favProductName=itemView.findViewById(R.id.favProductName_tv)
            favProductQuantity=itemView.findViewById(R.id.favProductQuantity_tv)
            favProductPrice=itemView.findViewById(R.id.favProductPrice_tv)
            favoriteProductLayout=itemView.findViewById(R.id.favoriteProduct_cl)
            favoriteProductLayout.setOnClickListener {
                val position = absoluteAdapterPosition
                if (favoriteProductClickListener != null && position != RecyclerView.NO_POSITION) {
                    favoriteProductClickListener!!.onFavProductClick(productList!!.get(position),favProductImage)
                }
            }
        }

    }

    interface FavoriteProductClickListener {
        fun onFavProductClick(product: Product, favProductImage: ImageView)
    }

    fun setFavoriteProductClickListener(listener: FavoriteProductClickListener) {
        this.favoriteProductClickListener = listener
    }

}