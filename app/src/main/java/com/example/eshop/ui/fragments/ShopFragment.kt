package com.example.eshop.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.eshop.R
import com.example.eshop.adapters.ProductItemsAdapter
import com.example.eshop.adapters.ShopProductAdapter
import com.example.eshop.databinding.FragmentShopBinding
import com.example.eshop.db.Product
import com.example.eshop.models.MainShopItem
import com.example.eshop.models.UserInfo
import com.example.eshop.ui.MainActivity
import com.example.eshop.ui.ViewModels.ShopViewModel
import com.example.eshop.utils.Resource
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ShopFragment : Fragment(){

    private val shopViewModel by activityViewModels<ShopViewModel>()

    private var searchedText = ""

    private val bottomNavigationView by lazy {
        (activity as MainActivity).findViewById<BottomNavigationView>(
            R.id.bottomNavigationView
        )
    }

    private var _binding: FragmentShopBinding? = null

    private val binding get() = _binding!!

    private lateinit var shopAdapter: ShopProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentShopBinding.inflate(inflater, container, false)
        val view = binding.root
        shopAdapter = ShopProductAdapter()
        shopAdapter.setProductClickListener(object:ProductItemsAdapter.onProductClickListener{
            override fun onProductClick(productModel: Product, transitionImageView: ImageView) {
                val extras = FragmentNavigatorExtras(transitionImageView to productModel.image)
                val action = ShopFragmentDirections.actionShopFragmentToSpecificProductFragment(productModel)
                findNavController().navigate(action, extras)
            }

            override fun addProductToCart(product: Product) {
                shopViewModel.addProductToCart(product.copy())
            }
        })
        shopAdapter.setSeeAllClickedListener(object:ShopProductAdapter.onSeeAllClickedListener{
            override fun onSeeAllClicked(mainShopItem: MainShopItem) {
                val action = ShopFragmentDirections.actionShopFragmentToAllProductsFragment(mainShopItem)
                findNavController().navigate(action)
            }
        })
        binding.shopRv.adapter = shopAdapter
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        shopViewModel.getUserInformation()
        observeListener()
    }


    private fun observeListener() {
        // check if user have data into firebase firestore
        shopViewModel.userInformationLiveData.observe(viewLifecycleOwner) { userInfo ->
            when (userInfo) {
                is Resource.Success -> {
                    initViews(userInfo.data!!)
                    shopViewModel.getShopList()
                }
                is Resource.Error -> {
                    findNavController().navigate(R.id.action_shopFragment_to_userInfoFragment)
                }
                is Resource.Loading -> {}
                else -> {}
            }
        }
        // get products data
        shopViewModel.shopListLiveData.observe(viewLifecycleOwner) { shopList ->
            when (shopList) {
                is Resource.Success -> {
                    shopList.data?.let { shopAdapter.setShopList(it) }
                    binding.progressBar.visibility=View.GONE
                }
                is Resource.Error -> {
                    Toast.makeText(requireContext(), shopList.msg, Toast.LENGTH_SHORT).show()
                    binding.progressBar.visibility=View.GONE

                }
                is Resource.Loading ->{
                    binding.progressBar.visibility=View.VISIBLE
                }
                else -> {}
            }
        }

        // navigate to all products fragment after get products that contain same searched value.
        shopViewModel.searchedProductsLiveData.observe(viewLifecycleOwner) { products ->
            when (products) {
                is Resource.Success -> {
                    val shopItem = MainShopItem("", searchedText, 0, false, products.data as MutableList<Product>)
                    val action = ShopFragmentDirections.actionShopFragmentToAllProductsFragment(shopItem)
                    findNavController().navigate(action)
                    binding.progressBar.visibility=View.GONE
                }
                is Resource.Loading -> {
                    binding.progressBar.visibility=View.VISIBLE
                }
                is Resource.Error -> {
                    Toast.makeText(requireContext(), products.msg, Toast.LENGTH_SHORT).show()
                    binding.progressBar.visibility=View.GONE
                }
                else -> {}
            }
        }
        shopViewModel.cartProductsLiveData.observe(viewLifecycleOwner) {
            when(it){
                is Resource.Success->{
                    Toast.makeText(requireContext(), "Product has been added to cart", Toast.LENGTH_SHORT).show()
                    shopViewModel.setCartProductValue()
                    binding.progressBar.visibility=View.GONE
                }
                is Resource.Loading -> {
                    binding.progressBar.visibility=View.VISIBLE
                }
                is Resource.Error-> {
                    Toast.makeText(requireContext(), it.msg, Toast.LENGTH_SHORT).show()
                    binding.progressBar.visibility=View.GONE
                }
                else -> {}
            }
        }
    }

    private fun initViews(data: UserInfo) {
        binding.userNameTextView.text = data.userName
        Glide.with(requireContext()).load(data.userImage).into(binding.userImageImageView)
        binding.shopContainer.visibility = View.VISIBLE
        // submit search when click on search button on keyboard.
        binding.shopSearchEditText.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val search = binding.shopSearchEditText.text.toString().trim()
                if (search.isNotEmpty()) {
                    searchedText = search
                    shopViewModel.getProductsHasContainName(searchedText)
                    binding.shopSearchEditText.text.clear()
                }
                return@OnEditorActionListener true
            }
            false
        })
    }

    private fun showBottomNavigationView() {
        bottomNavigationView.animate().scaleY(1f)
        bottomNavigationView.visibility = View.VISIBLE
    }

    override fun onResume() {
        super.onResume()
        showBottomNavigationView()
    }

    override fun onStop() {
        super.onStop()
        if (binding.progressBar.visibility==View.VISIBLE)
            binding.progressBar.visibility=View.GONE
    }

}