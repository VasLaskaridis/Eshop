package com.example.eshop.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.eshop.R
import com.example.eshop.ui.ViewModels.AllOrdersViewModel
import com.example.eshop.adapters.AllOrdersAdapter
import com.example.eshop.databinding.FragmentAllOrdersBinding
import com.example.eshop.models.Order
import com.example.eshop.utils.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AllOrdersFragment : Fragment() {

    private var _binding: FragmentAllOrdersBinding? = null

    private val binding get() = _binding!!

    private val ordersViewModel by activityViewModels<AllOrdersViewModel>()

    lateinit var allOrdersAdapter: AllOrdersAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAllOrdersBinding.inflate(inflater, container, false)
        val view = binding.root
        allOrdersAdapter=AllOrdersAdapter()
        allOrdersAdapter.setOrderClickListener(object: AllOrdersAdapter.OrderClickListener{
            override fun onOrderClicked(order: Order) {
                val action = AllOrdersFragmentDirections.actionAllOrdersFragmentToSpecificOrderFragment(order)
                findNavController().navigate(action)
            }
        })
        binding.allOrdersRv.adapter=allOrdersAdapter
        binding.backButton.setOnClickListener(View.OnClickListener {
            findNavController().popBackStack()
        })
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeListener()
    }

    private fun observeListener() {
        ordersViewModel.userOrdersLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    if(it.data!=null && it.data.isNotEmpty()){
                        allOrdersAdapter.setOrderList(it.data.sortedBy { it.orderSubmittedTime })
                        binding.allOrdersRv.visibility=View.VISIBLE
                        binding.emptyProductsImg.visibility=View.GONE
                    }else{
                        binding.allOrdersRv.visibility=View.GONE
                        binding.emptyProductsImg.visibility=View.VISIBLE
                    }
                    binding.progressBar.visibility=View.GONE
                }
                is Resource.Error -> {
                    Toast.makeText(requireContext(), it.msg, Toast.LENGTH_SHORT).show()
                    binding.allOrdersRv.visibility=View.GONE
                    binding.emptyProductsImg.visibility=View.VISIBLE
                    binding.progressBar.visibility=View.GONE
                }
                is Resource.Loading -> binding.progressBar.visibility=View.VISIBLE
                else -> {}
            }
        }
    }

}