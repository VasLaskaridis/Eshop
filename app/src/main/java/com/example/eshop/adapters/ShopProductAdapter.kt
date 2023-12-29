package com.example.eshop.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.eshop.R
import com.example.eshop.models.MainShopItem

class ShopProductAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private  var shopList: List<MainShopItem>? = null

    private var seeAllClickedListener: onSeeAllClickedListener? = null

    private var productClickListener: ProductItemsAdapter.onProductClickListener? = null

    fun setShopList(list: List<MainShopItem>) {
        shopList = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.getContext())
        var itemView: View = inflater.inflate(R.layout.product_type_rv_layout, parent, false)
        val viewHolder = MainShopItemHolder(itemView)
        return viewHolder
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val shopItemHolder: MainShopItemHolder = holder as MainShopItemHolder
        val currentShopItem: MainShopItem = shopList!!.get(position)

        shopItemHolder.productName.text = currentShopItem.name
        val productAdapter=ProductItemsAdapter(true, true)
        productAdapter.setProductList(currentShopItem.list)
        productAdapter.setProductClickListener(productClickListener!!)
        shopItemHolder.shopItems.adapter=productAdapter
    }

    override fun getItemCount(): Int {
        if(shopList!=null)
            return shopList!!.size
        else
            return 0
    }


    inner class MainShopItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var productName: TextView
        var seeAllProducts: TextView
        var shopItems: RecyclerView

        init {
            seeAllProducts = itemView.findViewById(R.id.seeAllProducts_tv)
            productName = itemView.findViewById(R.id.productTypeName_tv)
            shopItems = itemView.findViewById(R.id.shopItems_rv)
            seeAllProducts.setOnClickListener {
                val position = absoluteAdapterPosition
                if (seeAllClickedListener != null && position != RecyclerView.NO_POSITION) {
                    seeAllClickedListener!!.onSeeAllClicked(shopList!!.get(position))
                }
            }
        }

    }

    interface onSeeAllClickedListener {
        fun onSeeAllClicked(mainShopItem: MainShopItem)
    }

    fun setSeeAllClickedListener(listener: onSeeAllClickedListener) {
        this.seeAllClickedListener = listener
    }

    fun setProductClickListener(listener: ProductItemsAdapter.onProductClickListener) {
        this.productClickListener = listener
    }
}