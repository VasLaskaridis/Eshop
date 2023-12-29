package com.example.eshop.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.eshop.adapters.OrderProductsAdapter
import com.example.eshop.databinding.FragmentSpecificOrderBinding

class SpecificOrderFragment : Fragment() {

    private var _binding: FragmentSpecificOrderBinding? = null

    private val binding get() = _binding!!

    private val args by navArgs<SpecificOrderFragmentArgs>()
    private val order by lazy { args.order }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSpecificOrderBinding.inflate(inflater, container, false)
        val view = binding.root

        val orderProductsAdapter =OrderProductsAdapter()
        orderProductsAdapter.setOrderList(order.productsList)
        binding.specificOrderRv.adapter=orderProductsAdapter

        binding.backButton.setOnClickListener(View.OnClickListener {
            findNavController().popBackStack()
        })
        binding.orderIdTv.text=order.orderId
        binding.trackOrderButton.setOnClickListener(View.OnClickListener {
            val action = SpecificOrderFragmentDirections.actionSpecificOrderFragmentToTrackOrdersFragment(order)
            findNavController().navigate(action)
        })
        return view
    }

}