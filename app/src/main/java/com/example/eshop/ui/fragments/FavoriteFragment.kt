package com.example.eshop.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.example.eshop.ui.ViewModels.FavoriteViewModel
import com.example.eshop.R
import com.example.eshop.adapters.FavoriteAdapter
import com.example.eshop.databinding.FragmentFavoriteBinding
import com.example.eshop.db.Product
import com.example.eshop.utils.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavoriteFragment : Fragment() {

    private var _binding: FragmentFavoriteBinding? = null

    private val binding get() = _binding!!

    private val favoriteViewModel by activityViewModels<FavoriteViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        val view = binding.root
        val favoriteAdapter= FavoriteAdapter()
        favoriteAdapter.setFavoriteProductClickListener(object: FavoriteAdapter.FavoriteProductClickListener{
            override fun onFavProductClick(product: Product, favProductImage: ImageView) {
                val extras = FragmentNavigatorExtras(favProductImage to product.image)
                val action = FavoriteFragmentDirections.actionFavoriteFragmentToSpecificProductFragment(product)
                findNavController().navigate(action, extras)
            }

        })
        binding.favoriteRv.adapter=favoriteAdapter
        binding.addAllToBasketBtn.setOnClickListener(View.OnClickListener {
            val favList = favoriteAdapter.productList
            if (favList != null) {
                favoriteViewModel.addProductsToCart(favList)
            }
        })
        observeListener(favoriteAdapter)
        favoriteViewModel.getFavoriteProducts()
        return view
    }

    private fun observeListener(favoriteAdapter:FavoriteAdapter) {
        favoriteViewModel.getFavoriteProducts()?.observe(viewLifecycleOwner, object : Observer<List<Product>> {
            override fun onChanged(productList: List<Product>) {
                if (productList.isEmpty()) {
                    binding.emptyProductsImg.visibility = View.VISIBLE
                    binding.favoriteContainer.visibility = View.GONE
                } else {
                    favoriteAdapter.setAdapterProductList(productList)
                    binding.emptyProductsImg.visibility = View.GONE
                    binding.favoriteContainer.visibility = View.VISIBLE
                }
            }
        })

        favoriteViewModel.cartProductsLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    Toast.makeText(requireContext(), "Saved to cart", Toast.LENGTH_SHORT).show()
                    binding.progressBar.visibility=View.GONE
                }
                is Resource.Error -> {
                    Toast.makeText(requireContext(), it.msg, Toast.LENGTH_SHORT).show()
                    binding.progressBar.visibility=View.GONE
                }
                is Resource.Loading ->   binding.progressBar.visibility=View.VISIBLE
                else -> {}
            }
        }
    }
}