package com.example.eshop.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.eshop.ui.ViewModels.CartViewModel
import com.example.eshop.R
import com.example.eshop.adapters.CartAdapter
import com.example.eshop.databinding.FragmentCartBinding
import com.example.eshop.db.Product
import com.example.eshop.utils.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CartFragment : Fragment() {

    private var _binding: FragmentCartBinding? = null

    private val binding get() = _binding!!

    private val cartViewModel by activityViewModels<CartViewModel>()

    private val cartProductsList = mutableListOf<Product>()

    lateinit var cartAdapter: CartAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        val view = binding.root
        binding.checkOutProductsBtn.setOnClickListener(View.OnClickListener {
            openCheckOutDialog(getTotalPrice().toFloat())
        })
        cartAdapter=CartAdapter()
        cartAdapter.setCartProductListenerDelete(object: CartAdapter.CartProductListener{
            override fun onProductDelete(product: Product) {
                cartViewModel.deleteProductFromCart(product)
                cartViewModel.getAllCartProducts()
            }
        })
        binding.cartRv.adapter=cartAdapter
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cartViewModel.getAllCartProducts()
        observeListener()
    }

    private fun observeListener() {
        // retrieve user cart products to show in recycler view.
        cartViewModel.cartProductsLiveData.observe(viewLifecycleOwner) { cartProducts ->
            when (cartProducts) {
                is Resource.Success -> {
                    if (cartProducts.data == null || cartProducts.data.isEmpty()) {
                        binding.emptyProductsImg.visibility=View.VISIBLE
                        binding.cartContainer.visibility=View.GONE
                    } else {
                        cartAdapter.setCartProductList(cartProducts.data)
                        binding.emptyProductsImg.visibility=View.GONE
                        binding.cartContainer.visibility=View.VISIBLE

                    }
                    binding.progressBar.visibility=View.GONE
                }
                is Resource.Loading -> {
                    binding.progressBar.visibility=View.VISIBLE
                }
                is Resource.Error -> {
                    binding.progressBar.visibility=View.GONE
                    Toast.makeText(requireContext(), cartProducts.msg, Toast.LENGTH_SHORT).show()
                }
                else -> {}
            }
        }
    }

    private fun getTotalPrice(): Double {
        // get all total price from all products in user cart to show in checkout dialog.
        var totalPrice = 0.0
        cartAdapter.getPurchasedProducts().forEach {
            totalPrice += it.run { quantity * price }
        }
        return totalPrice
    }

    // pass total price and all products to checkout dialog to complete payment process and upload user order.
    private fun openCheckOutDialog(totalPrice: Float) {
        cartProductsList.clear()
        cartProductsList.addAll(cartAdapter.getPurchasedProducts())
        if (cartProductsList.isEmpty()) {
            Toast.makeText(requireContext(), "The cart is empty", Toast.LENGTH_SHORT).show()
            return
        }
        val action = CartFragmentDirections.actionCartFragmentToCheckoutFragment(
            totalPrice,
            cartProductsList.toTypedArray()
        )
        findNavController().navigate(action)
    }


}