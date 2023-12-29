package com.example.eshop.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.eshop.R
import com.example.eshop.adapters.ProductItemsAdapter
import com.example.eshop.databinding.FragmentAllProductsBinding
import com.example.eshop.db.Product
import com.example.eshop.ui.ViewModels.AllProductsViewModel
import com.example.eshop.utils.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AllProductsFragment : Fragment() {

    private var _binding: FragmentAllProductsBinding? = null

    private val binding get() = _binding!!

    private val args by navArgs<AllProductsFragmentArgs>()
    private val mainShopItem by lazy { args.mainShopItem }
    private val viewModel by activityViewModels<AllProductsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAllProductsBinding.inflate(inflater, container, false)
        val view = binding.root
        binding.backButtonImg.setOnClickListener(View.OnClickListener {
            findNavController().popBackStack()
        })
        binding.categoryNameTv.text=mainShopItem.name
        if(mainShopItem.list.size>0)
        {
            binding.emptyProductsImg.visibility =  View.GONE
        }
        else
        {
            binding.emptyProductsImg.visibility =  View.VISIBLE
        }
        val productAdapter=ProductItemsAdapter(true, true)
        productAdapter.setProductList(mainShopItem.list)
        productAdapter.setProductClickListener(object:ProductItemsAdapter.onProductClickListener{
            override fun onProductClick(productModel: Product, transitionImageView: ImageView) {
                val extras = FragmentNavigatorExtras(transitionImageView to productModel.image)
                val action = AllProductsFragmentDirections.actionAllProductsFragmentToSpecificProductFragment(productModel)
                findNavController().navigate(action, extras)
            }

            override fun addProductToCart(product: Product) {
                viewModel.addProductToCart(product.copy())
            }

        })
        binding.categoryProductsRv.adapter=productAdapter
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeListener()
    }

    private fun observeListener() {
        viewModel.cartProductsLiveData.observe(viewLifecycleOwner) {
            when(it){
                is Resource.Success->{
                    Toast.makeText(requireContext(), "Product has been added to cart successfully.", Toast.LENGTH_SHORT).show()
                    viewModel.setCartProductValue()
                }
                is Resource.Error-> Toast.makeText(requireContext(), it.msg, Toast.LENGTH_SHORT).show()
                else -> {}
            }
        }
    }

}


