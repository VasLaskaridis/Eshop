package com.example.eshop.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.eshop.R
import com.example.eshop.models.Order
import com.example.eshop.models.OrderEnums
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AllOrdersAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private var orderList : List<Order>?=null

    private lateinit var orderClickListener: OrderClickListener


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllOrdersViewHolder {
        val inflater = LayoutInflater.from(parent.getContext())
        var itemView: View = inflater.inflate(R.layout.order_item, parent, false)
        val viewHolder = AllOrdersViewHolder(itemView)
        return viewHolder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val orderItemHolder: AllOrdersViewHolder = holder as AllOrdersViewHolder
        val currentOrderItem: Order = orderList!!.get(position)

        orderItemHolder.orderTotalCost_tv.text=currentOrderItem.totalCost.toString()
        orderItemHolder.orderId_tv.text=currentOrderItem.orderId
        val df = SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.getDefault())
        df.format(Date(currentOrderItem.orderSubmittedTime)).let {
            orderItemHolder.orderSubmittedTime_tv.text= it
        }
        orderItemHolder.orderLocation_tv.text=currentOrderItem.orderLocation
        orderItemHolder.orderStatus_tv.text=currentOrderItem.orderStatus.toString()
        if(currentOrderItem.orderStatus == OrderEnums.DELIVERED){
            orderItemHolder.orderStatus_tv.setTextColor(Color.parseColor("#53B175"))
        }else{
            orderItemHolder.orderStatus_tv.setTextColor(Color.parseColor("#D36A0F"))
        }
    }

    override fun getItemCount(): Int {
        if(orderList!=null){
            return orderList!!.size
        }else{
            return 0
        }
    }

    fun setOrderList(list:List<Order>){
        orderList=list
        notifyDataSetChanged()
    }

    inner class AllOrdersViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var orderStatus_tv: TextView
        var orderTotalCost_tv: TextView
        var orderId_tv: TextView
        var orderSubmittedTime_tv: TextView
        var orderLocation_tv: TextView
        var orderContainer_cv: androidx.cardview.widget.CardView

        init {
            orderStatus_tv=itemView.findViewById(R.id.orderStatus_tv)
            orderTotalCost_tv=itemView.findViewById(R.id.orderTotalCost_tv)
            orderId_tv=itemView.findViewById(R.id.orderId_tv)
            orderSubmittedTime_tv=itemView.findViewById(R.id.orderSubmittedTime_tv)
            orderLocation_tv=itemView.findViewById(R.id.orderLocation_tv)
            orderContainer_cv=itemView.findViewById(R.id.orderContainer_cv)
            orderContainer_cv.setOnClickListener {
                val position = absoluteAdapterPosition
                if (orderClickListener != null && position != RecyclerView.NO_POSITION) {
                    orderClickListener.onOrderClicked(orderList!!.get(position))
                }
            }

        }
    }

    interface OrderClickListener{
        fun onOrderClicked(order: Order)
    }

    fun setOrderClickListener(listener: OrderClickListener){
        orderClickListener=listener
    }
}