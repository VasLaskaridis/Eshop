package com.example.eshop.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.eshop.R
import com.example.eshop.ui.ViewModels.ExploreViewModel
import com.example.eshop.adapters.ExploreCategoryAdapter
import com.example.eshop.databinding.FragmentExploreBinding
import com.example.eshop.db.Product
import com.example.eshop.models.CategoryItem
import com.example.eshop.models.MainShopItem
import com.example.eshop.utils.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ExploreFragment : Fragment() {

    private var _binding: FragmentExploreBinding? = null

    private val binding get() = _binding!!

    private val exploreViewModel by activityViewModels<ExploreViewModel>()

    private lateinit var exploreCategoryAdapter: ExploreCategoryAdapter

    private var searchedText: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        exploreViewModel.getCategoryList()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentExploreBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeListener()
        binding.searchProductEt. setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val search = binding.searchProductEt.text.toString().trim()
                if (search.isNotEmpty()) {
                    searchedText = search
                    exploreViewModel.getProductsHasContainName(searchedText)
                    binding.searchProductEt.text.clear()
                }
                return@OnEditorActionListener true
            }
            false
        })
        exploreCategoryAdapter= ExploreCategoryAdapter()
        exploreCategoryAdapter.setCategoryClickListener(object: ExploreCategoryAdapter.CategoryClickListener{
            override fun onCategoryClick(categoryItem: CategoryItem) {
                exploreViewModel.getProductsByCategoryId(categoryItem.id)
            }

        })
        binding.searchProductRv.adapter=exploreCategoryAdapter
    }


    private fun observeListener() {
        exploreViewModel.categoryLiveData.observe(viewLifecycleOwner) { categories ->
            when (categories) {
                is Resource.Success -> {
                    categories.data?.let { exploreCategoryAdapter.setCategoryItemList(it) }
                    binding.progressBar.visibility=View.GONE
                }
                is Resource.Error -> {
                    Toast.makeText(requireContext(), categories.msg, Toast.LENGTH_SHORT).show()
                    binding.progressBar.visibility=View.GONE
                }
                is Resource.Loading -> binding.progressBar.visibility=View.VISIBLE
                else -> {}
            }
        }

        exploreViewModel.searchedProductsLiveData.observe(viewLifecycleOwner) { products ->
            when (products) {
                is Resource.Success -> {
                    val shopItem = MainShopItem(
                        "", searchedText, 0, false,
                        products.data as MutableList<Product>
                    )
                    navigateToAllProducts(shopItem)
                    binding.progressBar.visibility=View.GONE
                }
                is Resource.Loading -> binding.progressBar.visibility=View.VISIBLE
                is Resource.Error -> {
                    Toast.makeText(requireContext(), products.msg, Toast.LENGTH_SHORT).show()
                    binding.progressBar.visibility=View.GONE
                }
                else -> {}
            }
        }

        exploreViewModel.categoryProductsLiveData.observe(viewLifecycleOwner) { mainShopItem ->
            when (mainShopItem) {
                is Resource.Success -> {
                    navigateToAllProducts(mainShopItem.data!!)
                    binding.progressBar.visibility=View.GONE
                }
                is Resource.Loading -> binding.progressBar.visibility=View.VISIBLE
                is Resource.Error -> {
                    Toast.makeText(requireContext(), mainShopItem.msg, Toast.LENGTH_SHORT).show()
                    binding.progressBar.visibility=View.GONE
                }
                else -> {}
            }
        }
    }
    private fun navigateToAllProducts(item: MainShopItem) {
        val action = ExploreFragmentDirections.actionExploreFragmentToAllProductsFragment(item)
        findNavController().navigate(action)
    }
}