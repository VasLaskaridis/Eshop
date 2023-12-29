package com.example.eshop.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.eshop.R
import com.example.eshop.databinding.FragmentOrderStatusBinding

class OrderStatusFragment : Fragment() {

    private var _binding: FragmentOrderStatusBinding? = null

    private val binding get() = _binding!!

    private val args by navArgs<OrderStatusFragmentArgs>()
    private val orderSubmitted by lazy { args.isOrderSubmitted }
    private val order by lazy { args.order }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOrderStatusBinding.inflate(inflater, container, false)
        val view = binding.root
        if(orderSubmitted){
            binding.orderStatusImage.setBackground(ContextCompat.getDrawable(requireContext(),
                R.drawable.success_order
            ))
            binding.orderStatusTitleTv.text="Your Order has been accepted"
            binding.orderStatusMessageTv.text="Your items has been placed and is on its way to begin processed."
            binding.trackOrderButton.text="Track_order"
        }else{
            binding.orderStatusImage.setBackground(ContextCompat.getDrawable(requireContext(),
                R.drawable.fail_order
            ))
            binding.orderStatusTitleTv.text="Oops! Order Failed"
            binding.orderStatusMessageTv.text="Something went wrong. Please try another payment card."
            binding.trackOrderButton.text="TryAgain"
        }
        binding.trackOrderButton.setOnClickListener(View.OnClickListener {
            if(orderSubmitted){
                val action = OrderStatusFragmentDirections.actionOrderStatusFragmentToTrackOrdersFragment(order)
                findNavController().navigate(action)
            }else{
                findNavController().popBackStack()
            }
        })
        binding.backToHomeButton.setOnClickListener(View.OnClickListener {
            val action = OrderStatusFragmentDirections.actionOrderStatusFragmentToShopFragment()
            findNavController().navigate(action)
        })
        return view
    }
}