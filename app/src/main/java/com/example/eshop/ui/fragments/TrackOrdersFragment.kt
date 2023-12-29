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
import com.example.eshop.databinding.FragmentTrackOrdersBinding
import com.example.eshop.models.OrderEnums

class TrackOrdersFragment : Fragment() {

    private var _binding: FragmentTrackOrdersBinding? = null

    private val binding get() = _binding!!

    private val args by navArgs<TrackOrdersFragmentArgs>()
    private val order by lazy { args.order }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTrackOrdersBinding.inflate(inflater, container, false)
        val view = binding.root
        binding.backButton.setOnClickListener(View.OnClickListener {
            findNavController().popBackStack()
        })
        binding.orderIdTv.text=order.orderId

        if(order.orderStatus != OrderEnums.PLACED){
            binding.orderStateV1.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.ic_check))
            binding.orderStateView2.setBackground(ContextCompat.getDrawable(requireContext(), R.color.blue))
            binding.orderConfirmContainer.alpha=1.0f
        }else{
            binding.orderStateV1.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.current_status))
            binding.orderStateView2.setBackground(ContextCompat.getDrawable(requireContext(), R.color.lightBlack))
            binding.orderConfirmContainer.alpha=0.3f
        }
        if(order.orderStatus != OrderEnums.PLACED && order.orderStatus!= OrderEnums.CONFIRMED){
            binding.orderStateView3.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.ic_check))
            binding.orderStateView4.setBackground(ContextCompat.getDrawable(requireContext(), R.color.blue))
            binding.orderShippedContainer.alpha=1.0f
        }else{
            binding.orderStateView3.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.current_status))
            binding.orderStateView4.setBackground(ContextCompat.getDrawable(requireContext(), R.color.lightBlack))
            binding.orderShippedContainer.alpha=0.3f
        }
        if(order.orderStatus == OrderEnums.DELIVERED ){
            binding.orderStateView5.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.ic_check))
            binding.orderDeliverContainer.alpha=1.0f
        }else{
            binding.orderStateView5.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.current_status))
            binding.orderDeliverContainer.alpha=0.3f
        }
        binding.orderConfirmContainer.alpha
        return view
    }
}