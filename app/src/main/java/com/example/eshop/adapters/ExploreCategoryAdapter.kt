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
import com.example.eshop.models.CategoryItem
import javax.inject.Inject

class ExploreCategoryAdapter
@Inject
constructor() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    private var categoryList:List<CategoryItem>?=null
    private lateinit var categoryClickListener: CategoryClickListener
    private lateinit var context: Context

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        context = recyclerView.getContext()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchProductViewHolder {
        val inflater = LayoutInflater.from(parent.getContext())
        val itemView = inflater.inflate(R.layout.search_product, parent, false)
        val viewHolder = SearchProductViewHolder(itemView)
        return viewHolder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val searchProductHolder: SearchProductViewHolder = holder as SearchProductViewHolder
        val currentCategoryItem: CategoryItem = categoryList!!.get(position)
        Glide.with(context).load(currentCategoryItem.image).into(searchProductHolder.searchProductImage)
        searchProductHolder.searchProductName.text=currentCategoryItem.name
    }

    override fun getItemCount(): Int {
        if(categoryList!=null){
           return categoryList!!.size
        }else{
            return 0
        }
    }

    fun setCategoryItemList(list: List<CategoryItem>) {
        categoryList = list
        notifyDataSetChanged()
    }

    inner class SearchProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)  {
        var searchProductImage: ImageView
        var searchProductName: TextView
        var searchProductView: CardView


        init{
            searchProductImage=itemView.findViewById(R.id.productImage_img)
            searchProductName=itemView.findViewById(R.id.productName_tv)
            searchProductView=itemView.findViewById(R.id.category_cd)
            searchProductView.setOnClickListener {
                val position = absoluteAdapterPosition
                if (categoryClickListener != null && position != RecyclerView.NO_POSITION) {
                    categoryClickListener!!.onCategoryClick(categoryList!!.get(position))
                }
            }
        }
    }

    interface CategoryClickListener {
        fun onCategoryClick(categoryItem: CategoryItem)
    }

    fun setCategoryClickListener(listener: CategoryClickListener) {
        this.categoryClickListener = listener
    }

}